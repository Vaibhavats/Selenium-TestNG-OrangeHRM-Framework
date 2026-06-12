package com.eventmgmt.base;

import com.eventmgmt.utils.WaitUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * BasePage.java
 * --------------
 * Abstract parent for all Page Object Model (POM) classes.
 * Initialises PageFactory, holds the driver, and exposes
 * reusable low-level browser interactions so page classes
 * never call driver directly for common operations.
 *
 * All page classes extend BasePage and call its helpers
 * (click, sendKeys, selectByText, etc.) instead of raw Selenium calls.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected static final Logger log = LogManager.getLogger(BasePage.class);

    // ----------------------------------------------------------------
    // Constructor — wires up PageFactory
    // ----------------------------------------------------------------

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Initialises all @FindBy elements declared in the subclass
        PageFactory.initElements(driver, this);
    }

    // ----------------------------------------------------------------
    // Element interaction helpers
    // ----------------------------------------------------------------

    /**
     * Waits for clickability then clicks an element.
     */
    protected void click(WebElement element) {
        WaitUtil.waitForClickability(driver, element).click();
        log.debug("Clicked element: {}", element);
    }

    /**
     * Clears a text field and types the given value.
     */
    protected void enterText(WebElement element, String text) {
        WaitUtil.waitForVisibility(driver, element).clear();
        element.sendKeys(text);
        log.debug("Entered text '{}' into element", text);
    }

    /**
     * Gets the visible text of an element (after waiting for visibility).
     */
    protected String getText(WebElement element) {
        return WaitUtil.waitForVisibility(driver, element).getText().trim();
    }

    /**
     * Gets the value attribute of a form field.
     */
    protected String getValue(WebElement element) {
        return WaitUtil.waitForVisibility(driver, element)
                .getAttribute("value");
    }

    /**
     * Returns true if the element is present and visible on the page.
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Selects a dropdown option by its visible text.
     */
    protected void selectByVisibleText(WebElement dropdown, String text) {
        WaitUtil.waitForVisibility(driver, dropdown);
        new Select(dropdown).selectByVisibleText(text);
        log.debug("Selected '{}' from dropdown", text);
    }

    /**
     * Selects a dropdown option by its value attribute.
     */
    protected void selectByValue(WebElement dropdown, String value) {
        WaitUtil.waitForVisibility(driver, dropdown);
        new Select(dropdown).selectByValue(value);
        log.debug("Selected value '{}' from dropdown", value);
    }

    /**
     * Scrolls the page until the element is in view.
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Clicks an element using JavaScript — fallback when a normal click
     * fails due to overlapping elements or animation.
     */
    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", element);
        log.debug("JS click performed on element");
    }

    /**
     * Hovers over an element using Actions (for dropdown menus etc.).
     */
    protected void hoverOver(WebElement element) {
        new Actions(driver).moveToElement(element).perform();
        log.debug("Hovered over element");
    }

    /**
     * Returns the current page URL.
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Returns the current page title.
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
}
