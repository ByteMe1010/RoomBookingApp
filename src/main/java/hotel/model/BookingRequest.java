package hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing the BOOKING_REQUEST table in HOTEL_DB.
 * Stores incoming reservation requests parsed from Booking.com emails.
 */

public class BookingRequest {
    private int requestId;
    private Integer customerId;
    private String emailMessageId;
    private String bookingRef;
    private String guestName;
    private String guestPhone;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int roomsWanted;
    private Integer mealPlanId;
    private Double totalPrice;
    private String specialRequest;
    private String status; // 'PENDING', 'ACCEPTED', 'DENIED', 'CANCELED'
    private LocalDateTime receivedAt;
    private Integer decidedBy;
    private LocalDateTime decidedAt;

    public BookingRequest() {
    }

    public BookingRequest(String emailMessageId, String bookingRef, String guestName, String guestPhone,
                          LocalDate checkIn, LocalDate checkOut, Integer mealPlanId, Double totalPrice,
                          String specialRequest) {
        this.emailMessageId = emailMessageId;
        this.bookingRef = bookingRef;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomsWanted = 1 ;
        this.mealPlanId = mealPlanId;
        this.totalPrice = totalPrice;
        this.specialRequest = specialRequest;
        this.status = "PENDING"; // Default status for new incoming requests
    }

    public BookingRequest(int requestId, Integer customerId, String emailMessageId, String bookingRef,
                          String guestName, String guestPhone, LocalDate checkIn, LocalDate checkOut,
                          int roomsWanted, Integer mealPlanId, Double totalPrice, String specialRequest,
                          String status, LocalDateTime receivedAt, Integer decidedBy, LocalDateTime decidedAt) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.emailMessageId = emailMessageId;
        this.bookingRef = bookingRef;
        this.guestName = guestName;
        this.guestPhone = guestPhone;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomsWanted = roomsWanted;
        this.mealPlanId = mealPlanId;
        this.totalPrice = totalPrice;
        this.specialRequest = specialRequest;
        this.status = status;
        this.receivedAt = receivedAt;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt;
    }

    // --- GETTERS AND SETTERS ---

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getEmailMessageId() {
        return emailMessageId;
    }

    public void setEmailMessageId(String emailMessageId) {
        this.emailMessageId = emailMessageId;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getRoomsWanted() {
        return roomsWanted;
    }

    public void setRoomsWanted(int roomsWanted) {
        this.roomsWanted = roomsWanted;
    }

    public Integer getMealPlanId() {
        return mealPlanId;
    }

    public void setMealPlanId(Integer mealPlanId) {
        this.mealPlanId = mealPlanId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Integer getDecidedBy() {
        return decidedBy;
    }

    public void setDecidedBy(Integer decidedBy) {
        this.decidedBy = decidedBy;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "requestId=" + requestId +
                ", bookingRef='" + bookingRef + '\'' +
                ", guestName='" + guestName + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status='" + status + '\'' +
                '}';
    }
}