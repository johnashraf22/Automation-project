package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object covering the Checkout flow (checkout-step-one.html and
 * checkout-step-two.html). Kept as a single class since both steps are
 * one continuous, tightly-coupled flow and neither step is large enough
 * to justify its own class for this assignment's scope.
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Step One - "Your Information"
    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueButton = By.id("continue");

    // Step Two - "Overview"
    private final By itemTotalLabel = By.className("summary_subtotal_label");

    // Shown on either step if something goes wrong (e.g. missing required field)
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Fills out the Step One "Your Information" form and clicks Continue,
     * landing on the Step Two Overview page.
     */
    public void fillInfoAndContinue(String firstName, String lastName, String postalCode) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).sendKeys(firstName);
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(postalCodeField).sendKeys(postalCode);
        driver.findElement(continueButton).click();
    }

    /**
     * Reads the "Item total: $XX.XX" label on the Overview page and
     * returns it as a double (e.g. 63.97).
     */
    public double getItemTotal() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(itemTotalLabel)).getText();
        String amount = text.substring(text.indexOf("$") + 1).trim();
        return Double.parseDouble(amount);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    public boolean isOnCheckoutStepOne() {
        return driver.getCurrentUrl().contains("checkout-step-one.html");
    }
}
