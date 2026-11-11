package hotel.db;

import hotel.model.*;
import java.util.List;

/**
 * Interface defining all database Operations for HOTEL_DB.
 */

public interface DatabaseModule {

    // --- MEAL PLAN OPERATIONS ---
    List<MealPlan> getAllMealPlans() throws Exception;
    MealPlan getMealPlanById(int mealPlanId) throws Exception;

    // --- ITEM & STOCK OPERATIONS ---
    int addNewItem(Item item) throws Exception;
    List<Item> getAllActiveItems() throws Exception;
    void updateItemStock(int itemId, double newStockAmount) throws Exception;

    // --- ROOM OPERATIONS ---
    List<Room> getAvailableRooms() throws Exception;
    void updateRoomStatus(int roomId, String newStatus) throws Exception;

    // --- CUSTOMER OPERATIONS ---
    int saveCustomer(Customer customer) throws Exception;
    Customer getCustomerByPhone(String phone) throws Exception;
    void updateTelegramChatId(int customerId, long telegramChatId) throws Exception;
    void incrementCustomerDeniedCount(int customerId) throws Exception;

    // --- BOOKING REQUEST OPERATIONS ---
    int saveBookingRequest(BookingRequest request) throws Exception;
    List<BookingRequest> getPendingBookingRequests() throws Exception;
    void updateBookingRequestStatus(int requestId, String status, int decidedBy) throws Exception;

    // --- BOOKING OPERATIONS ---
    int createBooking(Booking booking) throws Exception;
    Booking getBookingById(int bookingId) throws Exception;
    void linkRoomToBooking(BookingRoom bookingRoom) throws Exception;

    // --- ITEM ORDER OPERATIONS ---
    int addOrderToBooking(ItemOrder order) throws Exception;
    List<ItemOrder> getOrdersForBooking(int bookingId) throws Exception;

    // --- PAYMENT OPERATIONS ---
    int recordPayment(Payment payment) throws Exception;
    double getTotalPaidForBooking(int bookingId) throws Exception;

    // --- CUSTOMER REPORT OPERATIONS ---
    int addCustomerReport(CustomerReport report) throws Exception;
    List<CustomerReport> getReportsForCustomer(int customerId) throws Exception;
}