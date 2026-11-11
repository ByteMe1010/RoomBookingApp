package hotel.model;

import java.time.LocalDateTime;

/**
 * Model class representing the SHOP_OWNER table in HOTEL_DB.
 * Stores login credentials and profile details for hotel management.
 */

public class ShopOwner {
    private int ownerId;
    private String username;
    private String passwordHash;
    private String salt;
    private String fullName;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public ShopOwner(){
    }

    public ShopOwner(String username, String passwordHash, String salt, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
    }

    public ShopOwner(int ownerId, String username, String passwordHash, String salt, String fullName, LocalDateTime createdAt, LocalDateTime lastLoginAt) {
        this.ownerId = ownerId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.lastLoginAt = lastLoginAt;
    }

    // --- GETTERS AND SETTERS ---

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Override
    public String toString() {
        return "ShopOwner{" +
                "ownerId=" + ownerId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}


