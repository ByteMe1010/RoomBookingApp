package hotel.model;

import java.time.LocalDateTime;

/**
 * Model class representing the PAYMENT table in HOTEL_DB.
 * Tracks cash, card, and other money transactions made against a booking.
 */

public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String method; // 'CASH', 'CARD', 'OTHER'
    private LocalDateTime paidAt;

    public Payment() {
    }

    public Payment(int bookingId, double amount, String method) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
    }

    public Payment(int paymentId, int bookingId, double amount, String method, LocalDateTime paidAt) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt;
    }

    // --- GETTERS AND SETTERS ---

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", bookingId=" + bookingId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", paidAt=" + paidAt +
                '}';
    }
}