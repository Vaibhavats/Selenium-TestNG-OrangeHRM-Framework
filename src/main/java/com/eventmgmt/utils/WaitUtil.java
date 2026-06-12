package com.eventmgmt.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtil.java
 * --------------
 * Centralised explicit wait utility.
 * Avoids duplicating WebDriverWait boilerplate throughout page classes.
 * All methods use the explicit.wait timeout from config.properties unless
 * a custom timeout is provided.
 */
public class WaitUtil {

    private static final Logger log = LogManager.getLogger(WaitUtil.class);

    // Default timeout sourced from config
    private static final int DEFAULT_TIMEOUT =
            ConfigReader.getInstance().getExplicitWait();

    private WaitUtil() {}

    // ----------------------------------------------------------------
    // Visibility
    // ----------------------------------------------------------------

    /**
     * Waits until an element is visible on screen.
     */
    public static WebElement waitForVisibility(WebDriver driver, WebElement element) {
        return waitForVisibility(driver, element, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForVisibility(WebDriver driver,
                                               WebElement element,
                                               int timeoutSeconds) {
        log.debug("Waiting for visibility of element (timeout={}s)", timeoutSeconds);
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until an element located by a By-locator is visible.
     */
    public static WebElement waitForVisibility(WebDriver driver, By locator) {
        log.debug("Waiting for visibility of locator: {}", locator);
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // ----------------------------------------------------------------
    // Clickability
    // ----------------------------------------------------------------

    /**
     * Waits until an element is both visible and enabled (clickable).
     */
    public static WebElement waitForClickability(WebDriver driver, WebElement element) {
        return waitForClickability(driver, element, DEFAULT_TIMEOUT);
    }

    public static WebElement waitForClickability(WebDriver driver,
                                                  WebElement element,
                                                  int timeoutSeconds) {
        log.debug("Waiting for element to be clickable (timeout={}s)", timeoutSeconds);
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(element));
    }

    // ----------------------------------------------------------------
    // Presence
    // ----------------------------------------------------------------

    /**
     * Waits until an element is present in the DOM (may not be visible).
     */
    public static WebElement waitForPresence(WebDriver driver, By locator) {
        log.debug("Waiting for presence of locator: {}", locator);
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // ----------------------------------------------------------------
    // Text / Attribute
    // ----------------------------------------------------------------

    /**
     * Waits until an element's text contains the expected substring.
     */
    public static boolean waitForTextToContain(WebDriver driver,
                                               WebElement element,
                                               String expectedText) {
        log.debug("Waiting for text '{}' in element", expectedText);
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.textToBePresentInElement(element, expectedText));
    }

    // ----------------------------------------------------------------
    // URL / Title
    // ----------------------------------------------------------------

    /**
     * Waits until the current page URL contains the given fragment.
     */
    public static boolean waitForUrlToContain(WebDriver driver, String urlFragment) {
        log.debug("Waiting for URL to contain: {}", urlFragment);
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.urlContains(urlFragment));
    }

    // ----------------------------------------------------------------
    // Alert
    // ----------------------------------------------------------------

    /**
     * Waits until a browser alert is present and returns it.
     */
    public static org.openqa.selenium.Alert waitForAlert(WebDriver driver) {
        log.debug("Waiting for alert to appear");
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.alertIsPresent());
    }

    // ----------------------------------------------------------------
    // Invisibility
    // ----------------------------------------------------------------

    /**
     * Waits until an element is no longer visible (e.g. loading spinner gone).
     */
    public static boolean waitForInvisibility(WebDriver driver, WebElement element) {
        log.debug("Waiting for element to become invisible");
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(ExpectedConditions.invisibilityOf(element));
    }
}
