package com.eventmgmt.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtil.java
 * --------------------
 * Utility class for capturing screenshots.
 * Screenshots are saved to the path defined in config.properties and are
 * automatically embedded in ExtentReports on test failure.
 *
 * File naming format: TestName_yyyy-MM-dd_HH-mm-ss.png
 */
public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);

    private ScreenshotUtil() {}

    // ----------------------------------------------------------------
    // Core capture method
    // ----------------------------------------------------------------

    /**
     * Takes a screenshot of the current browser state and saves it to disk.
     *
     * @param driver    active WebDriver instance
     * @param testName  used as part of the filename for easy identification
     * @return          absolute path to the saved screenshot file, or null on failure
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Cannot capture screenshot — WebDriver is null.");
            return null;
        }

        // Build timestamped filename
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String fileName  = sanitiseFileName(testName) + "_" + timestamp + ".png";

        // Resolve output directory from config
        String screenshotDir = ConfigReader.getInstance().getScreenshotPath();
        File destFile = new File(screenshotDir + fileName);

        try {
            // Cast driver to TakesScreenshot and capture
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);
            log.info("Screenshot saved: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();

        } catch (IOException e) {
            log.error("Failed to save screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }

    /**
     * Captures the screenshot and returns it as a Base64 string
     * (useful for embedding directly in HTML reports without file I/O).
     *
     * @param driver  active WebDriver instance
     * @return        Base64-encoded PNG string, or null on failure
     */
    public static String captureScreenshotAsBase64(WebDriver driver) {
        if (driver == null) {
            log.warn("Cannot capture screenshot — WebDriver is null.");
            return null;
        }
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            log.error("Failed to capture Base64 screenshot: {}", e.getMessage());
            return null;
        }
    }

    // ----------------------------------------------------------------
    // Helper: strip characters that are illegal in file names
    // ----------------------------------------------------------------
    private static String sanitiseFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}
