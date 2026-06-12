package com.eventmgmt.base;

import com.eventmgmt.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest.java
 * --------------
 * Abstract base class for every test class in the framework.
 *
 * Responsibilities:
 *  - @BeforeSuite  : Initialise ExtentReports (once per run)
 *  - @BeforeMethod : Start browser, navigate to base URL, start test node
 *  - @AfterMethod  : Capture screenshot on failure, log result, quit browser
 *  - @AfterSuite   : Flush and save the HTML report
 *
 * All test classes extend BaseTest and get a ready-to-use `driver` and
 * `config` reference without any boilerplate.
 */
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    // Convenience references available to all subclasses
    protected WebDriver driver;
    protected ConfigReader config;

    // ----------------------------------------------------------------
    // Suite lifecycle
    // ----------------------------------------------------------------

    /**
     * Runs once before the entire test suite.
     * Initialises ExtentReports so all tests share one report file.
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("===== Test Suite Starting =====");
        ExtentReportManager.getExtentReports(); // triggers singleton creation
    }

    /**
     * Runs once after the entire test suite.
     * Flushes ExtentReports to disk — MUST be called or the HTML file is blank.
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        ExtentReportManager.flushReport();
        log.info("===== Test Suite Complete =====");
    }

    // ----------------------------------------------------------------
    // Per-test lifecycle
    // ----------------------------------------------------------------

    /**
     * Runs before every @Test method.
     * Creates a fresh WebDriver, configures it, and opens the base URL.
     *
     * @param method  injected by TestNG — used to get the test name for the report
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(java.lang.reflect.Method method) {
        config = ConfigReader.getInstance();

        // Start a new ExtentTest node for this method
        String testName   = method.getName();
        String className  = method.getDeclaringClass().getSimpleName();
        ExtentReportManager.startTest(
                className + " :: " + testName,
                "Automated test — " + className
        );

        // Initialise driver from config
        DriverFactory.initDriver(config.getBrowser(), config.isHeadless());
        driver = DriverFactory.getDriver();

        // Navigate to application base URL
        driver.get(config.getBaseUrl());
        log.info("Browser opened. Navigated to: {}", config.getBaseUrl());
        ExtentReportManager.logInfo("Browser launched. URL: " + config.getBaseUrl());
    }

    /**
     * Runs after every @Test method.
     * Captures screenshot on failure, logs final status, quits driver.
     *
     * @param result  injected by TestNG — contains pass/fail status and exception info
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        if (result.getStatus() == ITestResult.FAILURE) {
            // --- Capture screenshot ---
            String base64Screenshot =
                    ScreenshotUtil.captureScreenshotAsBase64(driver);
            ExtentReportManager.attachScreenshot(base64Screenshot);

            // Also save to disk for CI artefact archiving
            ScreenshotUtil.captureScreenshot(driver, testName);

            // Log failure details
            ExtentReportManager.logFail("TEST FAILED: " + testName);
            if (result.getThrowable() != null) {
                ExtentReportManager.logFail(
                        "Exception: " + result.getThrowable().getMessage());
            }
            log.error("TEST FAILED: {}", testName, result.getThrowable());

        } else if (result.getStatus() == ITestResult.SKIP) {
            ExtentReportManager.logWarning("TEST SKIPPED: " + testName);
            log.warn("TEST SKIPPED: {}", testName);

        } else {
            ExtentReportManager.logPass("TEST PASSED: " + testName);
            log.info("TEST PASSED: {}", testName);
        }

        // Always quit the driver — even if the test threw an exception
        DriverFactory.quitDriver();
        log.info("Driver quit for test: {}", testName);
    }

    // ----------------------------------------------------------------
    // Convenience helper for navigating to specific app pages
    // ----------------------------------------------------------------

    protected void navigateToLogin() {
        driver.get(config.getLoginUrl());
        log.info("Navigated to login URL: {}", config.getLoginUrl());
    }

    protected void navigateToRegister() {
        driver.get(config.getRegisterUrl());
        log.info("Navigated to register URL: {}", config.getRegisterUrl());
    }

    protected void navigateToBooking() {
        driver.get(config.getBookingUrl());
        log.info("Navigated to booking URL: {}", config.getBookingUrl());
    }
}
