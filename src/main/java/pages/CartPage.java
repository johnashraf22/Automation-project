package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Swag Labs Cart page (cart.html).
 * Contains locators and actions for viewing, removing items from,
 * and proceeding to checkout from the cart.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartItems = By.className("cart_item");
    private final By itemName = By.className("inventory_item_name");
    private final By checkoutButton = By.id("checkout");
    private final By continueShoppingButton = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Returns the names of every product currently in the cart,
     * in the order they appear on the page (i.e. the order they were added).
     */
    public List<String> getCartItemNames() {
        return driver.findElements(cartItems).stream()
                .map(item -> item.findElement(itemName).getText())
                .collect(Collectors.toList());
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    /**
     * Clicks the Remove button for the cart item matching the given product name.
     */
    public void removeProduct(String productName) {
        WebElement item = driver.findElements(cartItems).stream()
                .filter(cartItem -> cartItem.findElement(itemName).getText().equals(productName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart: " + productName));
        item.findElement(By.tagName("button")).click();
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    public void clickContinueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShoppingButton)).click();
    }
}
