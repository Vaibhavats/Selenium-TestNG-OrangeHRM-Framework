package com.eventmgmt.tests;

import com.eventmgmt.base.BaseTest;
import com.eventmgmt.pages.DashboardPage;
import com.eventmgmt.pages.LoginPage;
import com.eventmgmt.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Simplified Login Tests for Demo Site (OrangeHRM)
 * Focus: Make framework stable and working
 */

public class LoginTest extends BaseTest {

    // ------------------------------------------------------------
    // ✅ TC_01 — Valid Login
    // ------------------------------------------------------------
        @Test(priority = 1, groups = "smoke")
    public void testValidLogin() {
        ExtentReportManager.logInfo("Test: Valid Login");

        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);

        DashboardPage dashboardPage = loginPage.loginAndWaitForDashboard(
            config.getValidUsername(),
            config.getValidPassword());

        Assert.assertTrue(dashboardPage.isLoaded(),
            "Dashboard should load after valid login");
        Assert.assertEquals(dashboardPage.getHeadingText(), "Dashboard",
            "Dashboard heading should be visible after login");

        ExtentReportManager.logPass("Valid login passed");
    }

    // ------------------------------------------------------------
    // ❌ TC_02 — Invalid Login
    // ------------------------------------------------------------
    @Test(priority = 2)
    public void testInvalidLogin() {
        ExtentReportManager.logInfo("Test: Invalid Login");

        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("Admin", "wrongpassword");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be shown for invalid login");

        ExtentReportManager.logPass("Invalid login test passed");
    }

    // ------------------------------------------------------------
    // ⚠️ TC_03 — Empty Fields
    // ------------------------------------------------------------
    @Test(priority = 3)
    public void testEmptyFields() {
        ExtentReportManager.logInfo("Test: Empty Fields Login");

        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);

        loginPage.clickLoginButton();

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be shown when fields are empty");

        ExtentReportManager.logPass("Empty field validation works");
    }
}