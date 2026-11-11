package hotel.model;

public class Room {
    private int roomId  ;
    private String roomNumber ;
    private String roomType ;
    private double pricePerNight ;
    private String status ; // 'AVAILABLE', 'BOOKED' , 'CLOSED'


    public Room() {

    }

    public Room(String roomNumber,String roomType,double pricePerNight ){
        this.roomNumber = roomNumber ;
        this.roomType = roomType ;
        this.pricePerNight = pricePerNight ;
        this.status = "AVAILABLE" ;
    }

    public Room(int roomId,String roomNumber,String roomType,double pricePerNight,String status){
        this.roomId = roomId ;
        this.roomNumber = roomNumber ;
        this.roomType = roomType ;
        this.pricePerNight = pricePerNight ;
        this.status = status ;
    }

    // --- GETTERS AND SETTERS ---

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", roomNumber='" + roomNumber + '\'' +
                ", roomType='" + roomType + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", status='" + status + '\'' +
                '}';
    }
}
