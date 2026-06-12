package com.eventmgmt.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory.java
 * -------------------
 * Manages WebDriver lifecycle using ThreadLocal — supports parallel test
 * execution safely. Each thread gets its own WebDriver instance so tests
 * never interfere with each other.
 *
 * Usage:
 *   DriverFactory.initDriver("chrome");
 *   WebDriver driver = DriverFactory.getDriver();
 *   DriverFactory.quitDriver();
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);

    // ThreadLocal ensures each test thread has its own WebDriver
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Private constructor — utility class should not be instantiated
    private DriverFactory() {}

    // ----------------------------------------------------------------
    // Driver Initialisation
    // ----------------------------------------------------------------

    /**
     * Creates and stores a WebDriver instance for the calling thread.
     *
     * @param browser  "chrome" | "firefox" | "edge" (case-insensitive)
     * @param headless true to run without a visible browser window
     */
    public static void initDriver(String browser, boolean headless) {
        WebDriver driver;

        switch (browser.toLowerCase().trim()) {

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                log.info("Firefox WebDriver initialised (headless={})", headless);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless");
                driver = new EdgeDriver(edgeOptions);
                log.info("Edge WebDriver initialised (headless={})", headless);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                // Useful flags for stable CI execution
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-extensions");
                chromeOptions.addArguments("--disable-notifications");
                driver = new ChromeDriver(chromeOptions);
                log.info("Chrome WebDriver initialised (headless={})", headless);
                break;
        }

        // Timeouts
        ConfigReader config = ConfigReader.getInstance();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().window().maximize();

        driverThreadLocal.set(driver);
        log.info("WebDriver stored in ThreadLocal for thread: {}",
                Thread.currentThread().getName());
    }

    // ----------------------------------------------------------------
    // Driver Accessor
    // ----------------------------------------------------------------

    /**
     * Returns the WebDriver for the current thread.
     * Throws if initDriver() was not called first.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver not initialised. Call DriverFactory.initDriver() first.");
        }
        return driver;
    }

    // ----------------------------------------------------------------
    // Driver Teardown
    // ----------------------------------------------------------------

    /**
     * Quits the WebDriver and removes it from ThreadLocal.
     * Must be called in @AfterMethod to prevent browser leaks.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove(); // critical: prevent memory leak
            log.info("WebDriver quit and removed from ThreadLocal.");
        }
    }
}
