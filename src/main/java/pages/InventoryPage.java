package pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartIcon = By.className("shopping_cart_link");
    private final By inventoryItems = By.className("inventory_item");
    private final By inventoryContainer = By.id("inventory_container");

    // Burger menu / logout
    private final By burgerMenuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    // Footer social links
    private final By linkedInIcon = By.className("social_linkedin");
    private final By facebookIcon = By.className("social_facebook");
    private final By twitterIcon = By.className("social_twitter");

    public InventoryPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }


    public boolean isOnInventoryPage() {
        return driver.getCurrentUrl().contains("/inventory.html");
    }
    public String getPageTitle() {
        return driver.getTitle();
    }
    public boolean isCartIconDisplayed() {
        return driver.findElement(cartIcon).isDisplayed();
    }
    public int getProductCount() {
        List<WebElement> products = driver.findElements(inventoryItems);
        return products.size();
    }

    /**
     * Clicks the shopping cart icon in the header, navigating to the Cart page.
     */
    public void clickCartIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(cartIcon)).click();
    }

    /**
     * Opens the burger menu and clicks Logout, returning the user to the Login page.
     */
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(burgerMenuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        // Make sure we're actually back on the login page before returning control -
        // otherwise an immediate re-login can race the navigation and fail to find
        // the username field.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
    }

    // ----- Social footer links -----

    public void clickLinkedInIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(linkedInIcon)).click();
    }

    public void clickFacebookIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(facebookIcon)).click();
    }

    public void clickTwitterIcon() {
        wait.until(ExpectedConditions.elementToBeClickable(twitterIcon)).click();
    }

    // ----- Per-product locators/actions (used for cart data-driven scenarios) -----

    /**
     * Builds an XPath that scopes down to the single .inventory_item container
     * whose product name matches, so we can look up its button/price without
     * depending on product order or a hand-built data-test slug.
     *
     * Uses contains(@class, ...) instead of an exact @class= match (tolerant of
     * extra/alternate CSS classes on the element), and normalize-space(.) instead
     * of text()= (tolerant of surrounding whitespace/newlines and of the name
     * living in a nested child element rather than directly in the text node).
     */
    private By productContainer(String productName) {
        return By.xpath(String.format(
                "//div[contains(@class,'inventory_item')]"
                        + "[.//*[contains(@class,'inventory_item_name') and normalize-space(.)='%s']]",
                productName));
    }

    /**
     * Clicks the Add to cart / Remove button for the given product by name.
     */
    public void clickProductButton(String productName) {
        WebElement container = wait.until(visibilityOfElementLocated(productContainer(productName)));
        container.findElement(By.tagName("button")).click();
    }

    /**
     * Returns the current text of a product's action button:
     * "Add to cart" or "Remove".
     */
    public String getProductButtonText(String productName) {
        WebElement container = wait.until(visibilityOfElementLocated(productContainer(productName)));
        return container.findElement(By.tagName("button")).getText();
    }

    /**
     * Reads a product's displayed price from the Inventory page (e.g. "$29.99")
     * and returns it as a double (e.g. 29.99).
     */
    public double getProductPrice(String productName) {
        WebElement container = wait.until(visibilityOfElementLocated(productContainer(productName)));
        String priceText = container.findElement(By.xpath(".//*[contains(@class,'inventory_item_price')]")).getText();
        return Double.parseDouble(priceText.replace("$", "").trim());
    }
}
