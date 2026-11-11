package hotel.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model class representing the CUSTOMER table in HOTEL_DB.
 * Tracks guest profiles, Telegram chat IDs, and denied request history.
 */

public class Customer {
    private int customerId ;
    private String fullName ;
    private String phone ;
    private Long telegramChatId ;
    private int timesDenied ;
    private LocalDate lastDeniedDate ;
    private LocalDateTime createdAt ;


    public Customer(){

    }

    public Customer(String fullName,String phone){
        this.fullName = fullName ;
        this.phone = phone ;
        this.timesDenied = 0 ;
    }

    public Customer(int customerId, String fullName,String phone, Long telegramChatId ,int timesDenied, LocalDate lastDeniedDate ,LocalDateTime createdAt){
        this.customerId = customerId ;
        this.fullName = fullName ;
        this.phone = phone ;
        this.telegramChatId = telegramChatId ;
        this.timesDenied = timesDenied ;
        this.lastDeniedDate = lastDeniedDate ;
        this.createdAt = createdAt ;
    }

    // --- GETTERS AND SETTERS ---

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public int getTimesDenied() {
        return timesDenied;
    }

    public void setTimesDenied(int timesDenied) {
        this.timesDenied = timesDenied;
    }

    public LocalDate getLastDeniedDate() {
        return lastDeniedDate;
    }

    public void setLastDeniedDate(LocalDate lastDeniedDate) {
        this.lastDeniedDate = lastDeniedDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", telegramChatId=" + telegramChatId +
                ", timesDenied=" + timesDenied +
                ", lastDeniedDate=" + lastDeniedDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
