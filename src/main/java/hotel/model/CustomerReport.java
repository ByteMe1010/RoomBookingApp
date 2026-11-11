package hotel.model;

import java.time.LocalDateTime;

/**
 * Model class representing the CUSTOMER_REPORT table in HOTEL_DB.
 * Tracks incident reports or policy violations flagged against a guest profile.
 */

public class CustomerReport {
    private int reportId;
    private int customerId;
    private Integer bookingId; // Integer wrapper allows null if report is independent of a specific stay
    private String reason; // Renamed from 'note' for clarity
    private LocalDateTime createdAt;

    public CustomerReport() {
    }

    public CustomerReport(int customerId, Integer bookingId, String reason) {
        this.customerId = customerId;
        this.bookingId = bookingId;
        this.reason = reason;
    }

    public CustomerReport(int reportId, int customerId, Integer bookingId, String reason, LocalDateTime createdAt) {
        this.reportId = reportId;
        this.customerId = customerId;
        this.bookingId = bookingId;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    // --- GETTERS AND SETTERS ---

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CustomerReport{" +
                "reportId=" + reportId +
                ", customerId=" + customerId +
                ", bookingId=" + bookingId +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}