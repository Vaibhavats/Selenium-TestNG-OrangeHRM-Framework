package com.eventmgmt.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestListener.java
 * ------------------
 * TestNG ITestListener implementation.
 *
 * Hooks into the TestNG lifecycle to:
 *   - Log test start/pass/fail/skip to Extent Reports and Log4j
 *   - Auto-capture screenshots on test failure
 *   - Track overall suite statistics
 *
 * Registered in testng.xml under <listeners> — fires automatically
 * for every test without any changes to test classes.
 */
public class TestListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    // ----------------------------------------------------------------
    // Suite-level events
    // ----------------------------------------------------------------

    @Override
    public void onStart(ITestContext context) {
        log.info("========================================");
        log.info("Test Suite Started: {}", context.getName());
        log.info("========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed  = context.getPassedTests().size();
        int failed  = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();

        log.info("========================================");
        log.info("Test Suite Finished: {}", context.getName());
        log.info("  PASSED  : {}", passed);
        log.info("  FAILED  : {}", failed);
        log.info("  SKIPPED : {}", skipped);
        log.info("========================================");

        // Flush the report when the suite ends (belt-and-suspenders)
        ExtentReportManager.flushReport();
    }

    // ----------------------------------------------------------------
    // Test-level events
    // ----------------------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getFullTestName(result);
        log.info("▶ Starting: {}", testName);
        ExtentReportManager.logInfo("Starting test: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getFullTestName(result);
        log.info("✔ PASSED : {}", testName);
        ExtentReportManager.logPass("Test PASSED: " + testName);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getFullTestName(result);
        log.error("✘ FAILED : {}", testName);
        log.error("  Reason : {}", result.getThrowable().getMessage());

        // --- Screenshot on failure ---
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            // Embed Base64 screenshot in the Extent report
            String base64 = ScreenshotUtil.captureScreenshotAsBase64(driver);
            ExtentReportManager.attachScreenshot(base64);

            // Also write the file to disk (useful as a CI artefact)
            ScreenshotUtil.captureScreenshot(driver, testName);
        }

        ExtentReportManager.logFail("Test FAILED: " + testName
                + "\nException: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getFullTestName(result);
        log.warn("⚠ SKIPPED: {}", testName);
        ExtentReportManager.logWarning("Test SKIPPED: " + testName);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.warn("Test failed but within success percentage: {}",
                getFullTestName(result));
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    /**
     * Returns "ClassName :: methodName" for clear log output.
     */
    private String getFullTestName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName()
                + " :: "
                + result.getMethod().getMethodName();
    }
}
