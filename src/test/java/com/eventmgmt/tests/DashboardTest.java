package com.eventmgmt.tests;

import com.eventmgmt.base.BaseTest;
import com.eventmgmt.pages.DashboardPage;
import com.eventmgmt.pages.LoginPage;
import com.eventmgmt.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DashboardTest.java
 * ------------------
 * OrangeHRM dashboard validation smoke coverage.
 */
public class DashboardTest extends BaseTest {

    @Test(priority = 1, groups = "smoke")
    public void testDashboardValidation() {
        ExtentReportManager.logInfo("Test: Dashboard Validation");

        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.loginAndWaitForDashboard(
                config.getValidUsername(),
                config.getValidPassword());

        Assert.assertTrue(dashboardPage.isLoaded(),
                "Dashboard should be visible after login");
        Assert.assertEquals(dashboardPage.getHeadingText(), "Dashboard",
                "Dashboard heading should match the OrangeHRM landing page");

        ExtentReportManager.logPass("Dashboard validation passed");
    }
}