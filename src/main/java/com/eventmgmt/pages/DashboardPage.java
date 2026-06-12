package com.eventmgmt.pages;

import com.eventmgmt.base.BasePage;
import com.eventmgmt.utils.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * DashboardPage.java
 * ------------------
 * Page Object Model for the OrangeHRM dashboard and top bar.
 */
public class DashboardPage extends BasePage {

    @FindBy(xpath = "//h6[normalize-space()='Dashboard']")
    private WebElement dashboardHeading;

    @FindBy(css = "span.oxd-userdropdown-tab")
    private WebElement userDropdown;

    @FindBy(xpath = "//a[normalize-space()='Logout']")
    private WebElement logoutLink;

    public DashboardPage(WebDriver driver) {
        super(driver);
        waitUntilLoaded();
    }

    public DashboardPage waitUntilLoaded() {
        WaitUtil.waitForVisibility(driver, dashboardHeading);
        return this;
    }

    public boolean isLoaded() {
        return isDisplayed(dashboardHeading);
    }

    public String getHeadingText() {
        return getText(dashboardHeading);
    }

    public LoginPage logout() {
        click(userDropdown);
        click(logoutLink);
        return new LoginPage(driver);
    }
}