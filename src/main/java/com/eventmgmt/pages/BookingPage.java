package com.eventmgmt.pages;

import com.eventmgmt.base.BasePage;
import com.eventmgmt.utils.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * BookingPage.java
 * -----------------
 * Page Object Model for the Event / Venue Booking page.
 *
 * Workflow modelled:
 *   1. User selects an event from a catalogue
 *   2. User selects a date/time slot
 *   3. User fills in attendee count and special requests
 *   4. User confirms the booking
 *   5. Confirmation number is shown on success
 */
public class BookingPage extends BasePage {

    // ----------------------------------------------------------------
    // Element Locators
    // ----------------------------------------------------------------

    // Event search / filter
    @FindBy(id = "eventSearchInput")
    private WebElement eventSearchInput;

    @FindBy(id = "searchEventsBtn")
    private WebElement searchEventsButton;

    // List of event cards returned in the catalogue
    @FindBy(css = ".event-card")
    private List<WebElement> eventCards;

    // First available event card
    @FindBy(css = ".event-card:first-child .book-event-btn")
    private WebElement firstBookButton;

    // Date/time slot picker
    @FindBy(id = "eventDate")
    private WebElement eventDateInput;

    @FindBy(id = "timeSlot")
    private WebElement timeSlotDropdown;

    // Booking form fields
    @FindBy(id = "attendeeCount")
    private WebElement attendeeCountField;

    @FindBy(id = "specialRequests")
    private WebElement specialRequestsField;

    @FindBy(id = "venuePreference")
    private WebElement venuePreferenceDropdown;

    // Payment section
    @FindBy(id = "cardNumber")
    private WebElement cardNumberField;

    @FindBy(id = "cardExpiry")
    private WebElement cardExpiryField;

    @FindBy(id = "cardCvv")
    private WebElement cardCvvField;

    // Buttons
    @FindBy(id = "confirmBookingBtn")
    private WebElement confirmBookingButton;

    @FindBy(id = "cancelBookingBtn")
    private WebElement cancelBookingButton;

    // Result elements
    @FindBy(css = ".booking-confirmation")
    private WebElement bookingConfirmation;

    @FindBy(css = ".confirmation-number")
    private WebElement confirmationNumber;

    @FindBy(css = ".booking-error")
    private WebElement bookingError;

    @FindBy(css = ".slot-unavailable-msg")
    private WebElement slotUnavailableMessage;

    // Capacity-full warning
    @FindBy(css = ".capacity-warning")
    private WebElement capacityWarning;

    // Loading spinner (shown while booking is being processed)
    @FindBy(css = ".booking-spinner")
    private WebElement bookingSpinner;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------

    public BookingPage(WebDriver driver) {
        super(driver);
    }

    // ----------------------------------------------------------------
    // Page actions
    // ----------------------------------------------------------------

    /**
     * Searches the event catalogue for events matching the given name.
     */
    public BookingPage searchEvent(String eventName) {
        log.info("Searching for event: {}", eventName);
        enterText(eventSearchInput, eventName);
        click(searchEventsButton);
        return this;
    }

    /**
     * Returns the number of event cards currently displayed.
     */
    public int getEventCardCount() {
        return eventCards.size();
    }

    /**
     * Clicks the "Book" button on the first event card in the catalogue.
     */
    public BookingPage clickFirstBookButton() {
        log.info("Clicking first event's Book button");
        click(firstBookButton);
        return this;
    }

    /**
     * Clicks the "Book" button on the event card at the given index (0-based).
     */
    public BookingPage clickBookButtonAt(int index) {
        log.info("Clicking Book button at index {}", index);
        WebElement bookBtn = eventCards.get(index)
                .findElement(By.cssSelector(".book-event-btn"));
        click(bookBtn);
        return this;
    }

    /**
     * Selects the booking date.
     * @param date  format: yyyy-MM-dd  (e.g. "2025-12-25")
     */
    public BookingPage selectDate(String date) {
        log.info("Selecting date: {}", date);
        enterText(eventDateInput, date);
        return this;
    }

    /**
     * Selects a time slot from the dropdown.
     */
    public BookingPage selectTimeSlot(String timeSlot) {
        log.info("Selecting time slot: {}", timeSlot);
        selectByVisibleText(timeSlotDropdown, timeSlot);
        return this;
    }

    /**
     * Enters the number of attendees.
     */
    public BookingPage enterAttendeeCount(String count) {
        log.info("Entering attendee count: {}", count);
        enterText(attendeeCountField, count);
        return this;
    }

    /**
     * Enters any special requests or notes.
     */
    public BookingPage enterSpecialRequests(String requests) {
        log.info("Entering special requests");
        enterText(specialRequestsField, requests);
        return this;
    }

    /**
     * Selects a preferred venue from the dropdown.
     */
    public BookingPage selectVenuePreference(String venue) {
        log.info("Selecting venue: {}", venue);
        selectByVisibleText(venuePreferenceDropdown, venue);
        return this;
    }

    /**
     * Fills in payment card details.
     */
    public BookingPage enterPaymentDetails(String cardNumber,
                                            String expiry,
                                            String cvv) {
        log.info("Entering payment details");
        enterText(cardNumberField, cardNumber);
        enterText(cardExpiryField, expiry);
        enterText(cardCvvField, cvv);
        return this;
    }

    /**
     * Submits the booking and waits for the spinner to clear.
     */
    public BookingPage confirmBooking() {
        log.info("Confirming booking");
        click(confirmBookingButton);
        try {
            WaitUtil.waitForInvisibility(driver, bookingSpinner);
        } catch (Exception ignored) {
            // Spinner may be absent on fast responses
        }
        return this;
    }

    /**
     * Cancels the booking in progress.
     */
    public void cancelBooking() {
        log.info("Cancelling booking");
        click(cancelBookingButton);
    }

    /**
     * End-to-end helper: completes the full booking flow.
     */
    public void completeBooking(String eventName, String date, String timeSlot,
                                 String attendeeCount, String venue) {
        searchEvent(eventName);
        clickFirstBookButton();
        selectDate(date);
        selectTimeSlot(timeSlot);
        enterAttendeeCount(attendeeCount);
        selectVenuePreference(venue);
        confirmBooking();
    }

    // ----------------------------------------------------------------
    // Validation helpers
    // ----------------------------------------------------------------

    public boolean isBookingConfirmed() {
        return isDisplayed(bookingConfirmation);
    }

    public String getConfirmationNumber() {
        return getText(confirmationNumber);
    }

    public boolean isBookingErrorDisplayed() {
        return isDisplayed(bookingError);
    }

    public String getBookingError() {
        return getText(bookingError);
    }

    public boolean isSlotUnavailable() {
        return isDisplayed(slotUnavailableMessage);
    }

    public String getSlotUnavailableMessage() {
        return getText(slotUnavailableMessage);
    }

    public boolean isCapacityWarningDisplayed() {
        return isDisplayed(capacityWarning);
    }

    public String getCapacityWarning() {
        return getText(capacityWarning);
    }

    /**
     * Returns true if the confirmation number text is non-empty.
     */
    public boolean hasValidConfirmationNumber() {
        String number = getConfirmationNumber();
        return number != null && !number.trim().isEmpty();
    }
}
