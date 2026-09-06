package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By Username = By.id("user-name");
    private final By Password = By.id("password");
    private final By LoginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    public void usernameAndPasswordInput(String username, String password){
        wait.until(ExpectedConditions.visibilityOfElementLocated(Username)).sendKeys(username);
        driver.findElement(Password).sendKeys(password);
    }
    public void clickOnLoginButton(){
        driver.findElement(LoginButton).click();
    }
    public void login(String username, String password) {
        usernameAndPasswordInput(username,password);
        clickOnLoginButton();
    }
    public String getErrorMessage(){
        return driver.findElement(errorMessage).getText();
    }



}
