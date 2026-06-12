package com.eventmgmt.pages;

import com.eventmgmt.base.BasePage;
import com.eventmgmt.utils.WaitUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * LoginPage.java
 * -----------------
 * Page Object Model for OrangeHRM Login Page
 */
public class LoginPage extends BasePage {

    // ------------------------------------------------------------
    // Locators (OrangeHRM)
    // ------------------------------------------------------------

    @FindBy(name = "username")
    private WebElement usernameField;

    @FindBy(name = "password")
    private WebElement passwordField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    @FindBy(xpath = "//h6[text()='Dashboard']")
    private WebElement dashboardText;

    @FindBy(xpath = "//p[contains(@class,'alert-content-text')]")
    private WebElement errorMessage;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------

    public LoginPage(WebDriver driver) {
        super(driver);
        waitUntilLoaded();
    }

    // ------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------

    public LoginPage enterUsername(String username) {
        enterText(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        enterText(passwordField, password);
        return this;
    }

    public LoginPage clickLoginButton() {
        click(loginButton);
        return this;
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    public DashboardPage loginAndWaitForDashboard(String username, String password) {
        login(username, password);
        return new DashboardPage(driver);
    }

    public LoginPage waitUntilLoaded() {
        WaitUtil.waitForVisibility(driver, usernameField);
        WaitUtil.waitForVisibility(driver, loginButton);
        return this;
    }

    // ------------------------------------------------------------
    // Validations
    // ------------------------------------------------------------

    public boolean isLoginSuccessful() {
        return isDisplayed(dashboardText);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }

    public boolean isOnLoginPage() {
        return isDisplayed(usernameField) && isDisplayed(loginButton);
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }
}