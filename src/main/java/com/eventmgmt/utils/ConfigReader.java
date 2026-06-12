package com.eventmgmt.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader.java
 * ------------------
 * Singleton utility class that loads and provides access to all properties
 * defined in config.properties. Centralises all configuration so no
 * hard-coded values appear in page or test classes.
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);

    // Singleton instance
    private static ConfigReader instance;

    // Holds all loaded properties
    private final Properties properties;

    // Path to the config file (relative to project root)
    private static final String CONFIG_PATH =
            "src/test/resources/config/config.properties";

    // ----------------------------------------------------------------
    // Private constructor — reads the file once on first access
    // ----------------------------------------------------------------
    private ConfigReader() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Configuration loaded successfully from: {}", CONFIG_PATH);
        } catch (IOException e) {
            log.error("Failed to load config file at: {}", CONFIG_PATH, e);
            throw new RuntimeException("Cannot load config.properties. " +
                    "Ensure the file exists at: " + CONFIG_PATH, e);
        }
    }

    // ----------------------------------------------------------------
    // Thread-safe singleton accessor
    // ----------------------------------------------------------------

    /**
     * Returns the singleton ConfigReader instance.
     * Thread-safe via double-checked locking.
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    // ----------------------------------------------------------------
    // Generic accessor
    // ----------------------------------------------------------------

    /**
     * Returns the value for the given key, or throws if missing.
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException("Property '" + key +
                    "' not found in config.properties");
        }
        return value.trim();
    }

    // ----------------------------------------------------------------
    // Typed convenience methods
    // ----------------------------------------------------------------

    public String getBaseUrl()           { return getProperty("base.url"); }
    public String getLoginUrl()          { return getProperty("login.url"); }
    public String getRegisterUrl()       { return getProperty("register.url"); }
    public String getBookingUrl()        { return getProperty("booking.url"); }

    public String getBrowser()           { return getProperty("browser"); }
    public boolean isHeadless()          { return Boolean.parseBoolean(getProperty("headless")); }

    public int getImplicitWait()         { return Integer.parseInt(getProperty("implicit.wait")); }
    public int getExplicitWait()         { return Integer.parseInt(getProperty("explicit.wait")); }
    public int getPageLoadTimeout()      { return Integer.parseInt(getProperty("page.load.timeout")); }

    public String getScreenshotPath()    { return getProperty("screenshot.path"); }
    public String getReportPath()        { return getProperty("report.path"); }
    public String getReportTitle()       { return getProperty("report.title"); }
    public String getReportName()        { return getProperty("report.name"); }

    public String getLoginTestData()     { return getProperty("login.testdata.path"); }
    public String getRegistrationTestData() { return getProperty("registration.testdata.path"); }
    public String getBookingTestData()   { return getProperty("booking.testdata.path"); }

    public String getValidUsername()     { return getProperty("valid.username"); }
    public String getValidPassword()     { return getProperty("valid.password"); }
}
