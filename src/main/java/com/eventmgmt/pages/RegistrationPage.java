package com.eventmgmt.pages;

import com.eventmgmt.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * RegistrationPage.java
 * ----------------------
 * Page Object Model for the Event Management System Registration page.
 *
 * Form fields:
 *   First Name | Last Name | Email | Phone | Password | Confirm Password
 *   Date of Birth | Role (dropdown: Attendee / Organiser) | Terms checkbox
 */
public class RegistrationPage extends BasePage {

    // ----------------------------------------------------------------
    // Element Locators
    // ----------------------------------------------------------------

    @FindBy(id = "firstName")
    private WebElement firstNameField;

    @FindBy(id = "lastName")
    private WebElement lastNameField;

    @FindBy(id = "email")
    private WebElement emailField;

    @FindBy(id = "phone")
    private WebElement phoneField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordField;

    @FindBy(id = "dob")
    private WebElement dobField;

    @FindBy(id = "role")
    private WebElement roleDropdown;

    @FindBy(id = "termsCheckbox")
    private WebElement termsCheckbox;

    @FindBy(id = "registerBtn")
    private WebElement registerButton;

    // Error messages under individual fields
    @FindBy(id = "firstNameError")
    private WebElement firstNameError;

    @FindBy(id = "lastNameError")
    private WebElement lastNameError;

    @FindBy(id = "emailError")
    private WebElement emailError;

    @FindBy(id = "phoneError")
    private WebElement phoneError;

    @FindBy(id = "passwordError")
    private WebElement passwordError;

    @FindBy(id = "confirmPasswordError")
    private WebElement confirmPasswordError;

    // Top-level alert messages
    @FindBy(css = ".alert-success")
    private WebElement successAlert;

    @FindBy(css = ".alert-danger")
    private WebElement errorAlert;

    // "Email already in use" specific error
    @FindBy(css = ".duplicate-email-error")
    private WebElement duplicateEmailError;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    // ----------------------------------------------------------------
    // Field setters (fluent — return this for chaining)
    // ----------------------------------------------------------------

    public RegistrationPage enterFirstName(String firstName) {
        log.info("Entering first name: {}", firstName);
        enterText(firstNameField, firstName);
        return this;
    }

    public RegistrationPage enterLastName(String lastName) {
        log.info("Entering last name: {}", lastName);
        enterText(lastNameField, lastName);
        return this;
    }

    public RegistrationPage enterEmail(String email) {
        log.info("Entering email: {}", email);
        enterText(emailField, email);
        return this;
    }

    public RegistrationPage enterPhone(String phone) {
        log.info("Entering phone: {}", phone);
        enterText(phoneField, phone);
        return this;
    }

    public RegistrationPage enterPassword(String password) {
        log.info("Entering password");
        enterText(passwordField, password);
        return this;
    }

    public RegistrationPage enterConfirmPassword(String confirmPassword) {
        log.info("Entering confirm password");
        enterText(confirmPasswordField, confirmPassword);
        return this;
    }

    public RegistrationPage enterDateOfBirth(String dob) {
        log.info("Entering date of birth: {}", dob);
        enterText(dobField, dob);
        return this;
    }

    public RegistrationPage selectRole(String role) {
        log.info("Selecting role: {}", role);
        selectByVisibleText(roleDropdown, role);
        return this;
    }

    public RegistrationPage acceptTerms() {
        log.info("Checking Terms & Conditions checkbox");
        if (!termsCheckbox.isSelected()) {
            click(termsCheckbox);
        }
        return this;
    }

    // ----------------------------------------------------------------
    // Form submission
    // ----------------------------------------------------------------

    /**
     * Clicks the Register button.
     */
    public RegistrationPage clickRegisterButton() {
        log.info("Clicking Register button");
        click(registerButton);
        return this;
    }

    /**
     * Fills in all registration fields and submits the form.
     * Uses a fluent builder pattern — every field is optional here;
     * data-driven tests pass only the fields relevant to their scenario.
     */
    public void registerUser(String firstName, String lastName, String email,
                              String phone, String password,
                              String confirmPassword, String dob, String role) {
        enterFirstName(firstName)
                .enterLastName(lastName)
                .enterEmail(email)
                .enterPhone(phone)
                .enterPassword(password)
                .enterConfirmPassword(confirmPassword)
                .enterDateOfBirth(dob)
                .selectRole(role)
                .acceptTerms()
                .clickRegisterButton();
    }

    // ----------------------------------------------------------------
    // Validation helpers
    // ----------------------------------------------------------------

    public boolean isRegistrationSuccessful() {
        return isDisplayed(successAlert);
    }

    public String getSuccessMessage() {
        return getText(successAlert);
    }

    public String getErrorAlertMessage() {
        return getText(errorAlert);
    }

    public boolean isDuplicateEmailErrorDisplayed() {
        return isDisplayed(duplicateEmailError);
    }

    public String getDuplicateEmailError() {
        return getText(duplicateEmailError);
    }

    // Per-field error accessors
    public String getFirstNameError()       { return getText(firstNameError); }
    public String getLastNameError()        { return getText(lastNameError); }
    public String getEmailError()           { return getText(emailError); }
    public String getPhoneError()           { return getText(phoneError); }
    public String getPasswordError()        { return getText(passwordError); }
    public String getConfirmPasswordError() { return getText(confirmPasswordError); }

    public boolean isEmailErrorDisplayed()           { return isDisplayed(emailError); }
    public boolean isPasswordErrorDisplayed()        { return isDisplayed(passwordError); }
    public boolean isConfirmPasswordErrorDisplayed() { return isDisplayed(confirmPasswordError); }

    /**
     * Returns true if currently on the registration page.
     */
    public boolean isOnRegistrationPage() {
        return getCurrentUrl().contains("register");
    }
}
