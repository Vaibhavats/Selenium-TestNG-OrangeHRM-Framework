package com.eventmgmt.tests;

import com.eventmgmt.base.BaseTest;
import com.eventmgmt.pages.BookingPage;
import com.eventmgmt.pages.LoginPage;
import com.eventmgmt.utils.ExcelReader;
import com.eventmgmt.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * BookingTest.java
 * -----------------
 * End-to-end test scenarios for the Event Booking module.
 *
 * Pre-condition:  All booking tests require an authenticated user.
 *                 @BeforeMethod logs in before each test.
 *
 * Test cases:
 *  TC_B_01 : Valid booking → confirmation number shown
 *  TC_B_02 : Book an unavailable slot → error message shown
 *  TC_B_03 : Zero attendees → validation error
 *  TC_B_04 : Exceed venue capacity → capacity warning shown
 *  TC_B_05 : Past date booking → validation error
 *  TC_B_06 : Cancel booking mid-flow → user returned to catalogue
 *  TC_B_07 : Data-driven booking from Excel
 *  TC_B_08 : Booking confirmation number format validation
 *  TC_B_09 : Event search returns relevant results
 */
public class BookingTest extends BaseTest {

    // ----------------------------------------------------------------
    // @BeforeMethod override — log in before every booking test
    // ----------------------------------------------------------------

    /**
     * Overrides BaseTest.setUp() to also perform a login after the
     * browser is launched, since booking requires authentication.
     */
    @BeforeMethod(alwaysRun = true)
    @Override
    public void setUp(Method method) {
        // Call parent to initialise driver, navigate to base URL, start report node
        super.setUp(method);

        // Log in so the booking page is accessible
        navigateToLogin();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(config.getValidUsername(), config.getValidPassword());

        // Verify login succeeded before proceeding
        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "Pre-condition FAILED: Could not log in before booking test.");

        // Navigate to booking page
        navigateToBooking();
        ExtentReportManager.logInfo("Logged in and navigated to booking page.");
    }

    // ----------------------------------------------------------------
    // Data Provider
    // ----------------------------------------------------------------

    /**
     * Supplies booking test data from BookingTestData.xlsx.
     * Sheet: "BookingData"
     * Columns: eventName | date | timeSlot | attendeeCount | venue |
     *          expectedResult | description
     */
    @DataProvider(name = "bookingDataProvider")
    public Object[][] bookingDataProvider() {
        List<Map<String, String>> data =
                ExcelReader.getSheetData(config.getBookingTestData(), "BookingData");
        return ExcelReader.toTestNGDataProvider(data);
    }

    // ----------------------------------------------------------------
    // TC_B_01 — Valid end-to-end booking
    // ----------------------------------------------------------------

    @Test(priority = 1,
          description = "TC_B_01: Complete a valid booking and verify confirmation number",
          groups = {"smoke", "regression"})
    public void testValidBooking() {
        ExtentReportManager.logInfo("Test: Valid end-to-end event booking");

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.completeBooking(
                "Annual Tech Conference",   // event name
                "2025-12-20",              // date (future)
                "10:00 AM",               // time slot
                "5",                       // attendees
                "Main Hall"                // venue
        );

        Assert.assertTrue(bookingPage.isBookingConfirmed(),
                "Expected a booking confirmation after valid booking.");
        Assert.assertTrue(bookingPage.hasValidConfirmationNumber(),
                "Expected a non-empty confirmation number.");

        String confNum = bookingPage.getConfirmationNumber();
        ExtentReportManager.logInfo("Confirmation number: " + confNum);
        ExtentReportManager.logPass("Valid booking completed. Confirmation: " + confNum);
    }

    // ----------------------------------------------------------------
    // TC_B_02 — Unavailable time slot
    // ----------------------------------------------------------------

    @Test(priority = 2,
          description = "TC_B_02: Attempt to book an already-taken time slot",
          groups = {"regression"})
    public void testBookingUnavailableSlot() {
        ExtentReportManager.logInfo("Test: Booking a slot that is already taken");

        BookingPage bookingPage = new BookingPage(driver);

        // Assume this specific slot is marked as fully booked in the test environment
        bookingPage.completeBooking(
                "Sold Out Gala",
                "2025-11-30",
                "07:00 PM",
                "2",
                "Banquet Hall"
        );

        Assert.assertTrue(bookingPage.isSlotUnavailable(),
                "Expected a 'slot unavailable' message for a fully booked slot.");

        String msg = bookingPage.getSlotUnavailableMessage();
        ExtentReportManager.logInfo("Slot unavailable message: " + msg);
        Assert.assertFalse(msg.isEmpty(),
                "Unavailable slot message must not be empty.");

        ExtentReportManager.logPass("Unavailable slot correctly communicated to user.");
    }

    // ----------------------------------------------------------------
    // TC_B_03 — Zero attendees
    // ----------------------------------------------------------------

    @Test(priority = 3,
          description = "TC_B_03: Booking with zero attendees should be rejected",
          groups = {"regression"})
    public void testBookingWithZeroAttendees() {
        ExtentReportManager.logInfo("Test: Booking with attendee count of 0");

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.searchEvent("Tech Summit")
                .clickFirstBookButton()
                .selectDate("2025-12-15")
                .selectTimeSlot("02:00 PM")
                .enterAttendeeCount("0")   // ← invalid
                .selectVenuePreference("Conference Room A")
                .confirmBooking();

        // Booking must not succeed
        Assert.assertFalse(bookingPage.isBookingConfirmed(),
                "Booking with 0 attendees should NOT be confirmed.");
        Assert.assertTrue(bookingPage.isBookingErrorDisplayed(),
                "Expected an error message for 0-attendee booking.");

        ExtentReportManager.logPass("Zero-attendee booking correctly rejected.");
    }

    // ----------------------------------------------------------------
    // TC_B_04 — Exceed venue capacity
    // ----------------------------------------------------------------

    @Test(priority = 4,
          description = "TC_B_04: Booking with attendee count exceeding venue capacity",
          groups = {"regression"})
    public void testBookingExceedingCapacity() {
        ExtentReportManager.logInfo("Test: Attendee count exceeds venue capacity");

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.searchEvent("Intimate Workshop")
                .clickFirstBookButton()
                .selectDate("2025-12-18")
                .selectTimeSlot("11:00 AM")
                .enterAttendeeCount("9999")   // ← massively exceeds capacity
                .selectVenuePreference("Small Meeting Room")
                .confirmBooking();

        Assert.assertTrue(bookingPage.isCapacityWarningDisplayed(),
                "Expected a capacity warning for excessive attendee count.");

        String warning = bookingPage.getCapacityWarning();
        ExtentReportManager.logInfo("Capacity warning: " + warning);
        Assert.assertFalse(warning.isEmpty(),
                "Capacity warning message must not be empty.");
    }

    // ----------------------------------------------------------------
    // TC_B_05 — Past date booking
    // ----------------------------------------------------------------

    @Test(priority = 5,
          description = "TC_B_05: Booking for a past date should be rejected",
          groups = {"regression"})
    public void testBookingWithPastDate() {
        ExtentReportManager.logInfo("Test: Booking attempted for a past date");

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.searchEvent("Annual Gala")
                .clickFirstBookButton()
                .selectDate("2020-01-01")   // ← past date
                .selectTimeSlot("06:00 PM")
                .enterAttendeeCount("10")
                .selectVenuePreference("Grand Ballroom")
                .confirmBooking();

        Assert.assertFalse(bookingPage.isBookingConfirmed(),
                "Booking for a past date should NOT be confirmed.");
        Assert.assertTrue(bookingPage.isBookingErrorDisplayed(),
                "Expected a validation error for a past booking date.");

        ExtentReportManager.logPass("Past date booking correctly rejected.");
    }

    // ----------------------------------------------------------------
    // TC_B_06 — Cancel booking mid-flow
    // ----------------------------------------------------------------

    @Test(priority = 6,
          description = "TC_B_06: Cancel booking mid-flow and verify navigation",
          groups = {"regression"})
    public void testCancelBooking() {
        ExtentReportManager.logInfo("Test: Cancel booking in progress");

        BookingPage bookingPage = new BookingPage(driver);

        // Start a booking but cancel before confirming
        bookingPage.searchEvent("Tech Conference")
                .clickFirstBookButton()
                .selectDate("2025-12-20")
                .selectTimeSlot("10:00 AM")
                .enterAttendeeCount("3")
                .selectVenuePreference("Main Hall")
                .cancelBooking();   // ← cancel

        // After cancellation the user should be back on a catalogue/listing page
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                currentUrl.contains("booking") || currentUrl.contains("events"),
                "Expected user to remain on the booking/events page after cancellation."
        );
        Assert.assertFalse(bookingPage.isBookingConfirmed(),
                "No confirmation should be shown after cancelling.");

        ExtentReportManager.logPass("Booking cancellation handled correctly.");
    }

    // ----------------------------------------------------------------
    // TC_B_07 — Data-driven booking
    // ----------------------------------------------------------------

    @Test(priority = 7,
          dataProvider = "bookingDataProvider",
          description = "TC_B_07: Data-driven booking tests from Excel",
          groups = {"data-driven", "regression"})
    public void testBookingDataDriven(Map<String, String> testData) {
        String eventName      = testData.get("eventName");
        String date           = testData.get("date");
        String timeSlot       = testData.get("timeSlot");
        String attendeeCount  = testData.get("attendeeCount");
        String venue          = testData.get("venue");
        String expectedResult = testData.get("expectedResult"); // "success" | "failure"
        String description    = testData.get("description");

        ExtentReportManager.logInfo("Data-driven test: " + description);

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.completeBooking(eventName, date, timeSlot, attendeeCount, venue);

        if ("success".equalsIgnoreCase(expectedResult)) {
            Assert.assertTrue(bookingPage.isBookingConfirmed(),
                    "Expected booking to succeed: " + description);
            ExtentReportManager.logPass("Booking succeeded as expected.");
        } else {
            Assert.assertFalse(bookingPage.isBookingConfirmed(),
                    "Expected booking to fail: " + description);
            ExtentReportManager.logPass("Booking failed as expected.");
        }
    }

    // ----------------------------------------------------------------
    // TC_B_08 — Confirmation number format validation
    // ----------------------------------------------------------------

    @Test(priority = 8,
          description = "TC_B_08: Verify confirmation number matches expected pattern",
          groups = {"regression"})
    public void testConfirmationNumberFormat() {
        ExtentReportManager.logInfo("Test: Confirmation number format check");

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.completeBooking(
                "Annual Tech Conference",
                "2025-12-22",
                "10:00 AM",
                "3",
                "Main Hall"
        );

        Assert.assertTrue(bookingPage.isBookingConfirmed(),
                "Booking must be confirmed before checking the confirmation number.");

        String confNum = bookingPage.getConfirmationNumber();
        ExtentReportManager.logInfo("Confirmation number: " + confNum);

        // Expected format: alphanumeric string of 6–12 characters (e.g. "EVT-20251222-001")
        Assert.assertNotNull(confNum,
                "Confirmation number must not be null.");
        Assert.assertTrue(confNum.length() >= 6,
                "Confirmation number must be at least 6 characters long.");
        Assert.assertTrue(confNum.matches("[A-Za-z0-9\\-]+"),
                "Confirmation number must only contain alphanumeric characters and hyphens.");

        ExtentReportManager.logPass("Confirmation number format is valid: " + confNum);
    }

    // ----------------------------------------------------------------
    // TC_B_09 — Event search results
    // ----------------------------------------------------------------

    @Test(priority = 9,
          description = "TC_B_09: Event search returns relevant results",
          groups = {"regression"})
    public void testEventSearchReturnsResults() {
        ExtentReportManager.logInfo("Test: Event search returns at least one result");

        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.searchEvent("Conference");

        int cardCount = bookingPage.getEventCardCount();
        ExtentReportManager.logInfo("Event cards found: " + cardCount);

        Assert.assertTrue(cardCount > 0,
                "Expected at least one event card for search term 'Conference'.");

        ExtentReportManager.logPass("Event search returned " + cardCount + " result(s).");
    }
}
