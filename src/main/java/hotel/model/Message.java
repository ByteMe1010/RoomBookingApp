package hotel.model;

import java.time.LocalDateTime;

/**
 * Model class representing the MESSAGE table in HOTEL_DB.
 * Tracks Telegram interaction logs between the hotel system and guests.
 */

public class Message {
    private int messageId ;
    private int customerId ;
    private String content ;
    private String status ; // 'PENDING', 'SENT', 'FAILED'
    private LocalDateTime sentAt ;

    public Message(){

    }

    public Message(int customerId,String content,String status){
        this.customerId = customerId ;
        this.content = content ;
        this.status = status ;
    }

    public Message(int messageId,int customerId,String content,String status,LocalDateTime sentAt){
        this.messageId = messageId ;
        this.customerId = customerId ;
        this.content = content ;
        this.status = status ;
        this.sentAt = sentAt ;
    }

    // --- GETTERS AND SETTERS ---

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", customerId=" + customerId +
                ", content='" + content + '\'' +
                ", status='" + status + '\'' +
                ", sentAt=" + sentAt +
                '}';
    }

}
