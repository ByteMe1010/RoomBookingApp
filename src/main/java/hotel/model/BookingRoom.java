package hotel.model;

/**
 * Model class representing the BOOKING_ROOM link table in HOTEL_DB.
 * Maps the many-to-many relationship between bookings and physical rooms,
 * copying the price per night at booking time.
 */

public class BookingRoom {
    private int bookingId ;
    private int roomId ;
    private double pricePerNight ;

    public BookingRoom(){

    }

    public BookingRoom(int bookingId,int roomId,double pricePerNight ){
        this.bookingId = bookingId ;
        this.roomId = roomId ;
        this.pricePerNight = pricePerNight ;
    }

    // --- GETTERS AND SETTERS ---

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    @Override
    public String toString() {
        return "BookingRoom{" +
                "bookingId=" + bookingId +
                ", roomId=" + roomId +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
