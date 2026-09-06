package tests;


import base.BaseTest;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

public class LoginTest extends BaseTest {


    @Test
    public void testSuccessfulLogin(){
        JSONObject validUser = DataDriven.jsonReader("validUser");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(validUser.getString("username"), validUser.getString("password"));

        InventoryPage inventoryPage = new InventoryPage(driver);
        Assert.assertTrue(inventoryPage.isOnInventoryPage(),
                "User was not redirected to the Inventory page after a valid login.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "URL does not contain '/inventory.html' after successful login.");
    }
    @Test(description = "Verify invalid login shows the correct error message")
    public void testInvalidLogin() {
        JSONObject invalidUser = DataDriven.jsonReader("invalidUser");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(invalidUser.getString("username"), invalidUser.getString("password"));

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains("Username and password do not match"),
                "Error message did not contain expected text. Actual: " + actualError);
    }

    @Test(description = "Verify login without a password shows the correct error message")
    public void testLoginWithoutPassword() {
        JSONObject userWithoutPassword = DataDriven.jsonReader("userWithoutPassword");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(userWithoutPassword.getString("username"), userWithoutPassword.getString("password"));

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains("Password is required"),
                "Error message did not contain expected text. Actual: " + actualError);
    }


}
