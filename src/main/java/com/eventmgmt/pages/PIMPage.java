package com.eventmgmt.pages;

import com.eventmgmt.base.BasePage;
import com.eventmgmt.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * PIMPage.java
 * ------------
 * Page Object Model for the OrangeHRM PIM module.
 */
public class PIMPage extends BasePage {

    @FindBy(xpath = "//span[normalize-space()='PIM']")
    private WebElement pimMenu;

    @FindBy(xpath = "//h6[normalize-space()='PIM']")
    private WebElement pimHeading;

    @FindBy(xpath = "//button[normalize-space()='Add']")
    private WebElement addEmployeeButton;

    @FindBy(name = "firstName")
    private WebElement firstNameField;

    @FindBy(name = "lastName")
    private WebElement lastNameField;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement saveButton;

    @FindBy(xpath = "//label[normalize-space()='Employee Name']/../following-sibling::div//input")
    private WebElement employeeNameField;

    @FindBy(xpath = "//button[normalize-space()='Search']")
    private WebElement searchButton;

    @FindBy(xpath = "//button[normalize-space()='Yes, Delete']")
    private WebElement confirmDeleteButton;

    public PIMPage(WebDriver driver) {
        super(driver);
    }

    public PIMPage navigateToPIM() {
        click(pimMenu);
        WaitUtil.waitForVisibility(driver, pimHeading);
        return this;
    }

    public PIMPage clickAddEmployee() {
        navigateToPIM();
        click(addEmployeeButton);
        WaitUtil.waitForVisibility(driver, firstNameField);
        return this;
    }

    public PIMPage enterFirstName(String firstName) {
        enterText(firstNameField, firstName);
        return this;
    }

    public PIMPage enterLastName(String lastName) {
        enterText(lastNameField, lastName);
        return this;
    }

    public PIMPage clickSave() {
        click(saveButton);
        WaitUtil.waitForUrlToContain(driver, "viewPersonalDetails");
        return this;
    }

    public PIMPage searchEmployee(String employeeName) {
        navigateToPIM();
        WaitUtil.waitForVisibility(driver, employeeNameField);
        enterText(employeeNameField, employeeName);
        click(searchButton);
        return this;
    }

    public PIMPage deleteEmployee(String employeeName) {
        searchEmployee(employeeName);

        By deleteButton = By.xpath("//div[@role='row'][contains(., '" + employeeName + "')]//button[contains(@class,'oxd-icon-button')][last()]");
        List<WebElement> deleteButtons = driver.findElements(deleteButton);

        if (!deleteButtons.isEmpty()) {
            click(deleteButtons.get(0));
            click(confirmDeleteButton);
        }

        return this;
    }

    public boolean isEmployeeListed(String employeeName) {
        By employeeRow = By.xpath("//div[@role='row'][contains(., '" + employeeName + "')]");
        return !driver.findElements(employeeRow).isEmpty();
    }
}