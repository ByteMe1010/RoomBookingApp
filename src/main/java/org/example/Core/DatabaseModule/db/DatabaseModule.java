package org.example.Core.DatabaseModule.db;

import org.example.Core.DatabaseModule.model.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining complete CRUD and utility database operations for HOTEL_DB.
 */
public interface DatabaseModule {

    // ==========================================
    // 1. SHOP OWNER OPERATIONS (CRUD)
    // ==========================================
    int createShopOwner(ShopOwner owner) throws SQLException;
    ShopOwner getShopOwnerById(int ownerId) throws SQLException;
    ShopOwner getShopOwnerByUsername(String username) throws SQLException;
    List<ShopOwner> getAllShopOwners() throws SQLException;
    void updateShopOwner(ShopOwner owner) throws SQLException;
    void updateShopOwnerLastLogin(int ownerId) throws SQLException;
    void deleteShopOwner(int ownerId) throws SQLException;

    // ==========================================
    // 2. MEAL PLAN OPERATIONS (CRUD)
    // ==========================================
    int createMealPlan(MealPlan mealPlan) throws SQLException;
    MealPlan getMealPlanById(int mealPlanId) throws SQLException;
    List<MealPlan> getAllMealPlans() throws SQLException;
    void updateMealPlan(MealPlan mealPlan) throws SQLException;
    void deleteMealPlan(int mealPlanId) throws SQLException;

    // ==========================================
    // 3. ITEM & STOCK OPERATIONS (CRUD + Utility)
    // ==========================================
    int addNewItem(Item item) throws SQLException;
    Item getItemById(int itemId) throws SQLException;
    List<Item> getAllActiveItems() throws SQLException;
    List<Item> getAllItems() throws SQLException;
    void updateItem(Item item) throws SQLException;
    void updateItemStock(int itemId, double newStockAmount) throws SQLException;
    void deleteItem(int itemId) throws SQLException;

    // ==========================================
    // 4. ROOM OPERATIONS (CRUD + Utility)
    // ==========================================
    int createRoom(Room room) throws SQLException;
    Room getRoomById(int roomId) throws SQLException;
    List<Room> getAllRooms() throws SQLException;
    List<Room> getAvailableRooms() throws SQLException;
    void updateRoom(Room room) throws SQLException;
    void updateRoomStatus(int roomId, String newStatus) throws SQLException;
    void deleteRoom(int roomId) throws SQLException;

    // ==========================================
    // 5. CUSTOMER OPERATIONS (CRUD + Utility)
    // ==========================================
    int saveCustomer(Customer customer) throws SQLException;
    Customer getCustomerById(int customerId) throws SQLException;
    Customer getCustomerByPhone(String phone) throws SQLException;
    List<Customer> getAllCustomers() throws SQLException;
    void updateCustomer(Customer customer) throws SQLException;
    void updateTelegramChatId(int customerId, long telegramChatId) throws SQLException;
    void incrementCustomerDeniedCount(int customerId) throws SQLException;
    void deleteCustomer(int customerId) throws SQLException;

    // ==========================================
    // 6. BOOKING REQUEST OPERATIONS (CRUD + Utility)
    // ==========================================
    int saveBookingRequest(BookingRequest request) throws SQLException;
    BookingRequest getBookingRequestById(int requestId) throws SQLException;
    List<BookingRequest> getAllBookingRequests() throws SQLException;
    List<BookingRequest> getPendingBookingRequests() throws SQLException;
    void updateBookingRequest(BookingRequest request) throws SQLException;
    void updateBookingRequestStatus(int requestId, String status, int decidedBy) throws SQLException;
    void deleteBookingRequest(int requestId) throws SQLException;

    // ==========================================
    // 7. BOOKING OPERATIONS (CRUD + Utility)
    // ==========================================
    int createBooking(Booking booking) throws SQLException;
    Booking getBookingById(int bookingId) throws SQLException;
    List<Booking> getAllBookings() throws SQLException;
    void updateBooking(Booking booking) throws SQLException;
    void updateBookingStatus(int bookingId, String status) throws SQLException;
    void deleteBooking(int bookingId) throws SQLException;

    // ==========================================
    // 8. BOOKING ROOM OPERATIONS (CRUD)
    // ==========================================
    void linkRoomToBooking(BookingRoom bookingRoom) throws SQLException;
    BookingRoom getBookingRoom(int bookingId, int roomId) throws SQLException;
    List<BookingRoom> getRoomsForBooking(int bookingId) throws SQLException;
    void updateBookingRoom(BookingRoom bookingRoom) throws SQLException;
    void unlinkRoomFromBooking(int bookingId, int roomId) throws SQLException;

    // ==========================================
    // 9. ITEM ORDER OPERATIONS (CRUD)
    // ==========================================
    int addOrderToBooking(ItemOrder order) throws SQLException;
    ItemOrder getOrderById(int orderId) throws SQLException;
    List<ItemOrder> getOrdersForBooking(int bookingId) throws SQLException;
    void updateItemOrder(ItemOrder order) throws SQLException;
    void deleteItemOrder(int orderId) throws SQLException;

    // ==========================================
    // 10. PAYMENT OPERATIONS (CRUD + Utility)
    // ==========================================
    int recordPayment(Payment payment) throws SQLException;
    Payment getPaymentById(int paymentId) throws SQLException;
    List<Payment> getPaymentsForBooking(int bookingId) throws SQLException;
    double getTotalPaidForBooking(int bookingId) throws SQLException;
    void updatePayment(Payment payment) throws SQLException;
    void deletePayment(int paymentId) throws SQLException;

    // ==========================================
    // 11. MESSAGE OPERATIONS (CRUD)
    // ==========================================
    int logMessage(Message message) throws SQLException;
    Message getMessageById(int messageId) throws SQLException;
    List<Message> getMessagesForCustomer(int customerId) throws SQLException;
    void updateMessageStatus(int messageId, String status) throws SQLException;
    void deleteMessage(int messageId) throws SQLException;

    // ==========================================
    // 12. CUSTOMER REPORT OPERATIONS (CRUD)
    // ==========================================
    int addCustomerReport(CustomerReport report) throws SQLException;
    CustomerReport getCustomerReportById(int reportId) throws SQLException;
    List<CustomerReport> getReportsForCustomer(int customerId) throws SQLException;
    void updateCustomerReport(CustomerReport report) throws SQLException;
    void deleteCustomerReport(int reportId) throws SQLException;
}