package hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing the BOOKING table in HOTEL_DB.
 * Tracks active, pending, finished, and canceled guest stays.
 */

public class Booking {
    private int bookingId;
    private int requestId;
    private int customerId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String status; // 'PENDING', 'ACTIVE', 'FINISHED', 'CANCELED'
    private LocalDateTime createdAt;

    public Booking() {
    }


    public Booking(int requestId, int customerId, LocalDate checkIn, LocalDate checkOut) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = "PENDING"; // Default initial status
    }


    public Booking(int bookingId, int requestId, int customerId, LocalDate checkIn, LocalDate checkOut, String status, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.requestId = requestId;
        this.customerId = customerId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- GETTERS AND SETTERS ---

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", requestId=" + requestId +
                ", customerId=" + customerId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}