package tests;

import base.BaseTest;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

public class InventoryTest extends BaseTest {

    private static final int EXPECTED_PRODUCT_COUNT = 6;

    @Test
    public void testInventoryPageElements() {

        JSONObject validUser = DataDriven.jsonReader("validUser");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(validUser.getString("username"), validUser.getString("password"));

        InventoryPage inventoryPage = new InventoryPage(driver);
        Assert.assertTrue(inventoryPage.isOnInventoryPage(), "Login did not land on the Inventory page.");


        Assert.assertEquals(inventoryPage.getPageTitle(), "Swag Labs",
                "Page title does not match expected value.");


        Assert.assertTrue(inventoryPage.isCartIconDisplayed(), "Cart icon is not displayed.");


        Assert.assertEquals(inventoryPage.getProductCount(), EXPECTED_PRODUCT_COUNT,
                "Number of products displayed does not match expected count.");
    }






}
