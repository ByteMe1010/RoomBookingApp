package hotel.model;

import java.time.LocalDateTime;

/**
 * Model class representing the ITEM_ORDER table in HOTEL_DB.
 * Tracks individual item purchases added to a customer's room bill cart.
 */

public class ItemOrder {
    private int orderId ;
    private int bookingId ;
    private int itemId ;
    private double amount ;
    private double unitPrice ;
    private LocalDateTime orderedAt;

    public ItemOrder(){

    }

    public ItemOrder(int bookingId, int itemId,double amount, double unitPrice ){
        this.bookingId = bookingId ;
        this.itemId = itemId ;
        this.amount = amount ;
        this.unitPrice = unitPrice ;
    }

    public ItemOrder(int orderId, int bookingId, int itemId,double amount, double unitPrice,LocalDateTime orderedAt ){
        this.orderId = orderId ;
        this.bookingId = bookingId ;
        this.itemId = itemId ;
        this.amount = amount ;
        this.unitPrice = unitPrice ;
        this.orderedAt = orderedAt ;
    }

    // --- GETTERS AND SETTERS ---

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    @Override
    public String toString() {
        return "ItemOrder{" +
                "orderId=" + orderId +
                ", bookingId=" + bookingId +
                ", itemId=" + itemId +
                ", amount=" + amount +
                ", unitPrice=" + unitPrice +
                ", orderedAt=" + orderedAt +
                '}';
    }
}
