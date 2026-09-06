package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    protected WebDriver driver;
    protected static final String baseUrl = "https://www.saucedemo.com/";

    @BeforeMethod
    public void setUp(){
        driver = new ChromeDriver(buildChromeOptions());
        driver.manage().window().maximize();
        driver.get(baseUrl);
    }

    /**
     * SauceDemo's demo credentials (standard_user / secret_sauce) are public and
     * well-known, so Chrome's built-in "Password Manager" leak/breach checker
     * flags them on login and pops up a native "Change your password" /
     * "Your password was found in a data breach" bubble. That bubble is chrome://
     * UI, not part of the page, so Selenium can't see or dismiss it - and while
     * it's open it can grab focus/steal clicks on whatever is underneath it
     * (e.g. the Continue button on checkout, or the login form right after a
     * fresh navigation), which is what was causing the NoSuchElementException /
     * TimeoutExceptions. Disabling the password manager and its leak-detection
     * service (plus the "Save password?" bubble and notifications, which can
     * cause the same kind of interference) stops the popup from ever appearing.
     */
    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-features=PasswordLeakDetection,PasswordCheck,AutofillServerCommunication");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        return options;
    }

    @AfterMethod
    public void tearDown(){
        if (driver!= null){
            driver.quit();
        }

    }





}
