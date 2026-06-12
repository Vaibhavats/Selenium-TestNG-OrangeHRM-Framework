package com.eventmgmt.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ExtentReportManager.java
 * -------------------------
 * Manages the ExtentReports singleton and provides per-thread ExtentTest
 * instances (required for parallel test execution).
 *
 * How it works:
 *   1. ExtentReports (the report file) is a singleton — one per test run.
 *   2. ExtentTest (a single test node) is stored in ThreadLocal so each
 *      parallel test thread writes to its own node without conflicts.
 *   3. BaseTest calls startTest() / endReport(), and logs pass/fail/info.
 */
public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);

    // Singleton report instance
    private static ExtentReports extentReports;

    // One test node per thread (thread-safe for parallel execution)
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    private ExtentReportManager() {}

    // ----------------------------------------------------------------
    // Initialise the report (call once before any tests run)
    // ----------------------------------------------------------------

    /**
     * Creates the ExtentReports instance and configures the HTML reporter.
     * Should be called in a @BeforeSuite method.
     */
    public static synchronized ExtentReports getExtentReports() {
        if (extentReports == null) {
            ConfigReader config = ConfigReader.getInstance();
            String reportPath = config.getReportPath();

            // Spark reporter produces a modern, interactive HTML file
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle(config.getReportTitle());
            sparkReporter.config().setReportName(config.getReportName());
            sparkReporter.config().setEncoding("UTF-8");
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            // System info shown at the top of the report
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("Base URL", config.getBaseUrl());
            extentReports.setSystemInfo("Tester", System.getProperty("user.name"));

            log.info("ExtentReports initialised. Report will be saved to: {}", reportPath);
        }
        return extentReports;
    }

    // ----------------------------------------------------------------
    // Per-test node management
    // ----------------------------------------------------------------

    /**
     * Creates a new test node in the report for the current thread.
     * Call at the start of each test (@BeforeMethod or inside @Test).
     *
     * @param testName    name displayed in the report
     * @param description brief description of what the test validates
     */
    public static void startTest(String testName, String description) {
        ExtentTest test = getExtentReports().createTest(testName, description);
        extentTestThreadLocal.set(test);
        log.info("ExtentTest started: {}", testName);
    }

    /**
     * Returns the ExtentTest node for the current thread.
     */
    public static ExtentTest getTest() {
        ExtentTest test = extentTestThreadLocal.get();
        if (test == null) {
            throw new IllegalStateException(
                    "ExtentTest not started. Call startTest() before logging.");
        }
        return test;
    }

    // ----------------------------------------------------------------
    // Logging helpers (delegated from BaseTest)
    // ----------------------------------------------------------------

    public static void logPass(String message) {
        getTest().pass(message);
        log.info("[PASS] {}", message);
    }

    public static void logFail(String message) {
        getTest().fail(message);
        log.error("[FAIL] {}", message);
    }

    public static void logInfo(String message) {
        getTest().info(message);
        log.info("[INFO] {}", message);
    }

    public static void logWarning(String message) {
        getTest().warning(message);
        log.warn("[WARN] {}", message);
    }

    /**
     * Embeds a Base64 screenshot into the report for the current test.
     */
    public static void attachScreenshot(String base64Screenshot) {
        if (base64Screenshot != null) {
            getTest().addScreenCaptureFromBase64String(base64Screenshot,
                    "Failure Screenshot");
        }
    }

    // ----------------------------------------------------------------
    // Flush report (call once after all tests complete)
    // ----------------------------------------------------------------

    /**
     * Writes all buffered test results to the HTML file.
     * Must be called in @AfterSuite — otherwise the file is empty.
     */
    public static void flushReport() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("ExtentReports flushed. Open report at: {}",
                    ConfigReader.getInstance().getReportPath());
        }
    }
}
