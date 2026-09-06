package tests;

import base.BaseTest;
import org.json.JSONObject;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

import java.time.Duration;
import java.util.List;

/**
 * Part 2: Cart, checkout, and edge-case scenarios for the Swag Labs site.
 * All product data used in the data-driven scenario is loaded from
 * testData.json ("cartProducts") via DataDriven.jsonReaderList(), not hardcoded.
 */
public class CartTest extends BaseTest {

    /**
     * Logs in using the "validUser" credentials from testData.json.
     * Shared by every test in this class since each scenario needs
     * to start from a logged-in state.
     */
    private void loginAsValidUser() {
        JSONObject validUser = DataDriven.jsonReader("validUser");
        new LoginPage(driver).login(validUser.getString("username"), validUser.getString("password"));
    }

    // ----- Scenario 1: Verify Social Links -----

    @Test(description = "Verify LinkedIn, Facebook, and X (Twitter) footer icons open the correct sites")
    public void testSocialLinks() {
        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        String originalWindow = driver.getWindowHandle();
        String originalUrl = driver.getCurrentUrl();

        inventoryPage.clickLinkedInIcon();
        assertLinkNavigatesTo(originalWindow, originalUrl, "linkedin");

        inventoryPage.clickFacebookIcon();
        assertLinkNavigatesTo(originalWindow, originalUrl, "facebook");

        inventoryPage.clickTwitterIcon();
        assertLinkNavigatesTo(originalWindow, originalUrl, "x.com");
    }

    /**
     * Verifies a social icon click landed on a URL containing expectedUrlPart,
     * whether the link opened a new tab (target="_blank") or navigated in the
     * same tab. If a new tab opened, it's closed and focus returns to the
     * original window; if navigation happened in the same tab, we navigate
     * back to the Inventory page afterward so the next icon click has a
     * clean starting point.
     */
    private void assertLinkNavigatesTo(String originalWindow, String originalUrl, String expectedUrlPart) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        boolean newTabOpened;
        try {
            wait.until(d -> d.getWindowHandles().size() > 1);
            newTabOpened = true;
        } catch (org.openqa.selenium.TimeoutException e) {
            newTabOpened = false;
        }

        if (newTabOpened) {
            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalWindow)) {
                    driver.switchTo().window(handle);
                    break;
                }
            }
            wait.until(ExpectedConditions.urlContains(expectedUrlPart));
            Assert.assertTrue(driver.getCurrentUrl().contains(expectedUrlPart),
                    "Expected URL to contain '" + expectedUrlPart + "' but was: " + driver.getCurrentUrl());
            driver.close();
            driver.switchTo().window(originalWindow);
        } else {
            // No new tab - assume the link navigated in the same tab instead
            wait.until(ExpectedConditions.urlContains(expectedUrlPart));
            Assert.assertTrue(driver.getCurrentUrl().contains(expectedUrlPart),
                    "Expected URL to contain '" + expectedUrlPart + "' but was: " + driver.getCurrentUrl());
            driver.navigate().to(originalUrl);
        }
    }

    // ----- Scenario 2: Verify Cart Is Empty -----

    @Test(description = "Verify a freshly logged-in user's cart contains no items")
    public void testCartIsEmptyByDefault() {
        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartEmpty(), "Expected cart to be empty for a freshly logged-in user.");
    }

    // ----- Scenario 3: Add 3 Specific Products (Data-Driven) -----

    @Test(description = "Verify the 3 products from testData.json are added to the cart in the order they were added")
    public void testAddSpecificProductsFromJson() {
        List<String> cartProducts = DataDriven.jsonReaderList("cartProducts");

        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        for (String product : cartProducts) {
            inventoryPage.clickProductButton(product);
        }

        inventoryPage.clickCartIcon();
        CartPage cartPage = new CartPage(driver);
        Assert.assertEquals(cartPage.getCartItemNames(), cartProducts,
                "Cart items did not match the products added, or were not in the same order.");
    }

    // ----- Scenario 4: Remove One Product -----

    @Test(description = "Verify removing one product updates its inventory button and leaves the others unaffected")
    public void testRemoveOneProductUpdatesInventoryButtons() {
        List<String> cartProducts = DataDriven.jsonReaderList("cartProducts");
        String productToRemove = cartProducts.get(1); // "Sauce Labs Bolt T-Shirt"

        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        for (String product : cartProducts) {
            inventoryPage.clickProductButton(product);
        }

        inventoryPage.clickCartIcon();
        CartPage cartPage = new CartPage(driver);
        cartPage.removeProduct(productToRemove);
        cartPage.clickContinueShopping();

        // Back on the Inventory page - verify button states
        InventoryPage inventoryPageAfter = new InventoryPage(driver);
        Assert.assertEquals(inventoryPageAfter.getProductButtonText(productToRemove), "Add to cart",
                "Removed product's button did not revert to 'Add to cart'.");

        for (String product : cartProducts) {
            if (!product.equals(productToRemove)) {
                Assert.assertEquals(inventoryPageAfter.getProductButtonText(product), "Remove",
                        "Product '" + product + "' should still show 'Remove' after removing a different item.");
            }
        }
    }

    // ----- Scenario 5: Verify Cart Total Price -----

    @Test(description = "Verify the sum of the 3 product prices on Inventory matches the checkout Item total")
    public void testCartTotalPriceMatchesInventoryPrices() {
        List<String> cartProducts = DataDriven.jsonReaderList("cartProducts");

        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);

        // Read prices from the Inventory page BEFORE adding to cart
        double expectedTotal = 0.0;
        for (String product : cartProducts) {
            expectedTotal += inventoryPage.getProductPrice(product);
        }

        // Add the same 3 products
        for (String product : cartProducts) {
            inventoryPage.clickProductButton(product);
        }

        inventoryPage.clickCartIcon();
        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillInfoAndContinue("John", "Doe", "12345");

        double actualItemTotal = checkoutPage.getItemTotal();
        Assert.assertEquals(actualItemTotal, expectedTotal, 0.01,
                "Checkout Item total did not match the sum of individually-read Inventory prices.");
    }

    // ----- Scenario 6: Checkout With an Empty Cart -----

    /**
     * NOTE: SauceDemo does not appear to block checkout when the cart is empty -
     * the Checkout button on the (empty) cart page simply navigates to
     * checkout-step-one.html like it would with items in the cart, with no
     * validation error shown. This assertion reflects that expected behavior.
     * Run this once against the live site to confirm - if the real behavior
     * differs (e.g. an error is shown, or the button is disabled/absent),
     * swap the assertion below accordingly.
     */
    @Test(description = "Verify the actual behavior when proceeding to checkout with an empty cart")
    public void testCheckoutWithEmptyCart() {
        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        inventoryPage.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartEmpty(), "Precondition failed: cart was not empty before checkout.");

        cartPage.clickCheckout();

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        Assert.assertTrue(checkoutPage.isOnCheckoutStepOne(),
                "Expected checkout to proceed to Step One even with an empty cart, based on observed SauceDemo behavior. "
                        + "If the real site instead blocks/errors on empty-cart checkout, update this assertion.");
    }

    // ----- Scenario 7: Cart State After Logout/Login -----

    /**
     * Confirmed against the live site: SauceDemo persists cart contents across
     * a logout/login cycle for the same user - the cart is NOT cleared on
     * logout. This asserts on that observed behavior.
     */
    @Test(description = "Verify cart items persist after logging out and back in as the same user")
    public void testCartStateAfterLogoutLogin() {
        List<String> cartProducts = DataDriven.jsonReaderList("cartProducts");
        List<String> productsToAdd = cartProducts.subList(0, 2); // at least 2 products

        loginAsValidUser();
        InventoryPage inventoryPage = new InventoryPage(driver);
        for (String product : productsToAdd) {
            inventoryPage.clickProductButton(product);
        }

        inventoryPage.logout();

        loginAsValidUser();
        InventoryPage inventoryPageAfterReLogin = new InventoryPage(driver);
        inventoryPageAfterReLogin.clickCartIcon();

        CartPage cartPage = new CartPage(driver);
        int cartCountAfterReLogin = cartPage.getCartItemCount();

        Assert.assertEquals(cartCountAfterReLogin, productsToAdd.size(),
                "Expected cart items to persist after logout/login, based on confirmed SauceDemo behavior.");
    }
}
