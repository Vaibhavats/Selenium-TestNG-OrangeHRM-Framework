package com.eventmgmt.tests;

import com.eventmgmt.base.BaseTest;
import com.eventmgmt.pages.DashboardPage;
import com.eventmgmt.pages.LoginPage;
import com.eventmgmt.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LogoutTest.java
 * ---------------
 * OrangeHRM logout smoke coverage.
 */
public class LogoutTest extends BaseTest {

    @Test(priority = 1, groups = "smoke")
    public void testLogoutReturnsToLoginPage() {
        ExtentReportManager.logInfo("Test: Logout Flow");

        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAndWaitForDashboard(
                config.getValidUsername(),
                config.getValidPassword());

        LoginPage returnedLoginPage = dashboardPage.logout();

        Assert.assertTrue(returnedLoginPage.isOnLoginPage(),
                "Logout should return the user to the login page");

        ExtentReportManager.logPass("Logout flow passed");
    }
}