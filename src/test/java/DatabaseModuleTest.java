import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseModuleTest {

    @Mock
    private DatabaseModule databaseModule;

    private ShopOwner shopOwner;
    private MealPlan mealPlan;
    private Item item;
    private Room room;
    private Customer customer;
    private BookingRequest bookingRequest;
    private Booking booking;
    private BookingRoom bookingRoom;
    private ItemOrder itemOrder;
    private Payment payment;
    private Message message;
    private CustomerReport customerReport;

    @BeforeEach
    void setUp() {
        // ඔයාගේ Model constructors / setters වලට අනුව objects customize කරගන්න
        shopOwner = mock(ShopOwner.class);
        mealPlan = mock(MealPlan.class);
        item = mock(Item.class);
        room = mock(Room.class);
        customer = mock(Customer.class);
        bookingRequest = mock(BookingRequest.class);
        booking = mock(Booking.class);
        bookingRoom = mock(BookingRoom.class);
        itemOrder = mock(ItemOrder.class);
        payment = mock(Payment.class);
        message = mock(Message.class);
        customerReport = mock(CustomerReport.class);
    }

    // 1. SHOP OWNER OPERATIONS
    @Test
    void shopOwnerCrudAndLogin_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.createShopOwner(shopOwner)).thenReturn(1);
        when(databaseModule.getShopOwnerById(1)).thenReturn(shopOwner);
        when(databaseModule.getShopOwnerByUsername("admin")).thenReturn(shopOwner);
        when(databaseModule.getAllShopOwners()).thenReturn(List.of(shopOwner));

        assertEquals(1, databaseModule.createShopOwner(shopOwner));
        assertEquals(shopOwner, databaseModule.getShopOwnerById(1));
        assertEquals(shopOwner, databaseModule.getShopOwnerByUsername("admin"));
        assertEquals(1, databaseModule.getAllShopOwners().size());

        databaseModule.updateShopOwner(shopOwner);
        databaseModule.updateShopOwnerLastLogin(1);
        databaseModule.deleteShopOwner(1);

        verify(databaseModule).updateShopOwner(shopOwner);
        verify(databaseModule).updateShopOwnerLastLogin(1);
        verify(databaseModule).deleteShopOwner(1);
    }

    // 2. MEAL PLAN OPERATIONS
    @Test
    void mealPlanCrud_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.createMealPlan(mealPlan)).thenReturn(10);
        when(databaseModule.getMealPlanById(10)).thenReturn(mealPlan);
        when(databaseModule.getAllMealPlans()).thenReturn(List.of(mealPlan));

        assertEquals(10, databaseModule.createMealPlan(mealPlan));
        assertEquals(mealPlan, databaseModule.getMealPlanById(10));
        assertEquals(1, databaseModule.getAllMealPlans().size());

        databaseModule.updateMealPlan(mealPlan);
        databaseModule.deleteMealPlan(10);

        verify(databaseModule).updateMealPlan(mealPlan);
        verify(databaseModule).deleteMealPlan(10);
    }

    // 3. ITEM & STOCK OPERATIONS
    @Test
    void itemAndStockOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.addNewItem(item)).thenReturn(20);
        when(databaseModule.getItemById(20)).thenReturn(item);
        when(databaseModule.getAllActiveItems()).thenReturn(List.of(item));
        when(databaseModule.getAllItems()).thenReturn(List.of(item));

        assertEquals(20, databaseModule.addNewItem(item));
        assertEquals(item, databaseModule.getItemById(20));
        assertEquals(1, databaseModule.getAllActiveItems().size());
        assertEquals(1, databaseModule.getAllItems().size());

        databaseModule.updateItem(item);
        databaseModule.updateItemStock(20, 50.0);
        databaseModule.deleteItem(20);

        verify(databaseModule).updateItem(item);
        verify(databaseModule).updateItemStock(20, 50.0);
        verify(databaseModule).deleteItem(20);
    }

    // 4. ROOM OPERATIONS
    @Test
    void roomOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.createRoom(room)).thenReturn(101);
        when(databaseModule.getRoomById(101)).thenReturn(room);
        when(databaseModule.getAllRooms()).thenReturn(List.of(room));
        when(databaseModule.getAvailableRooms()).thenReturn(List.of(room));

        assertEquals(101, databaseModule.createRoom(room));
        assertEquals(room, databaseModule.getRoomById(101));
        assertEquals(1, databaseModule.getAllRooms().size());
        assertEquals(1, databaseModule.getAvailableRooms().size());

        databaseModule.updateRoom(room);
        databaseModule.updateRoomStatus(101, "OCCUPIED");
        databaseModule.deleteRoom(101);

        verify(databaseModule).updateRoom(room);
        verify(databaseModule).updateRoomStatus(101, "OCCUPIED");
        verify(databaseModule).deleteRoom(101);
    }

    // 5. CUSTOMER OPERATIONS
    @Test
    void customerOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.saveCustomer(customer)).thenReturn(5);
        when(databaseModule.getCustomerById(5)).thenReturn(customer);
        when(databaseModule.getCustomerByPhone("+94771234567")).thenReturn(customer);
        when(databaseModule.getAllCustomers()).thenReturn(List.of(customer));

        assertEquals(5, databaseModule.saveCustomer(customer));
        assertEquals(customer, databaseModule.getCustomerById(5));
        assertEquals(customer, databaseModule.getCustomerByPhone("+94771234567"));
        assertEquals(1, databaseModule.getAllCustomers().size());

        databaseModule.updateCustomer(customer);
        databaseModule.updateTelegramChatId(5, 123456789L);
        databaseModule.incrementCustomerDeniedCount(5);
        databaseModule.deleteCustomer(5);

        verify(databaseModule).updateCustomer(customer);
        verify(databaseModule).updateTelegramChatId(5, 123456789L);
        verify(databaseModule).incrementCustomerDeniedCount(5);
        verify(databaseModule).deleteCustomer(5);
    }

    // 6. BOOKING REQUEST OPERATIONS
    @Test
    void bookingRequestOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.saveBookingRequest(bookingRequest)).thenReturn(30);
        when(databaseModule.getBookingRequestById(30)).thenReturn(bookingRequest);
        when(databaseModule.getAllBookingRequests()).thenReturn(List.of(bookingRequest));
        when(databaseModule.getPendingBookingRequests()).thenReturn(List.of(bookingRequest));
        when(databaseModule.getBookingRequestByEmailMessageId("msg-123")).thenReturn(bookingRequest);
        when(databaseModule.getBookingRequestByBookingRef("REF-001")).thenReturn(bookingRequest);
        when(databaseModule.getBookingRequests("PENDING", "Tharudika")).thenReturn(List.of(bookingRequest));

        assertEquals(30, databaseModule.saveBookingRequest(bookingRequest));
        assertEquals(bookingRequest, databaseModule.getBookingRequestById(30));
        assertEquals(1, databaseModule.getAllBookingRequests().size());
        assertEquals(1, databaseModule.getPendingBookingRequests().size());
        assertEquals(bookingRequest, databaseModule.getBookingRequestByEmailMessageId("msg-123"));
        assertEquals(bookingRequest, databaseModule.getBookingRequestByBookingRef("REF-001"));
        assertEquals(1, databaseModule.getBookingRequests("PENDING", "Tharudika").size());

        databaseModule.updateBookingRequest(bookingRequest);
        databaseModule.updateBookingRequestStatus(30, "APPROVED", 1);
        databaseModule.deleteBookingRequest(30);

        verify(databaseModule).updateBookingRequest(bookingRequest);
        verify(databaseModule).updateBookingRequestStatus(30, "APPROVED", 1);
        verify(databaseModule).deleteBookingRequest(30);
    }

    // 7. BOOKING OPERATIONS
    @Test
    void bookingOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.createBooking(booking)).thenReturn(200);
        when(databaseModule.getBookingById(200)).thenReturn(booking);
        when(databaseModule.getAllBookings()).thenReturn(List.of(booking));

        assertEquals(200, databaseModule.createBooking(booking));
        assertEquals(booking, databaseModule.getBookingById(200));
        assertEquals(1, databaseModule.getAllBookings().size());

        databaseModule.updateBooking(booking);
        databaseModule.updateBookingStatus(200, "CONFIRMED");
        databaseModule.deleteBooking(200);

        verify(databaseModule).updateBooking(booking);
        verify(databaseModule).updateBookingStatus(200, "CONFIRMED");
        verify(databaseModule).deleteBooking(200);
    }

    // 13. TELEGRAM LINK CODE OPERATIONS
    @Test
    void telegramLinkCodeOperations_shouldWorkAsExpected() throws SQLException {
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);
        when(databaseModule.getBookingByTelegramLinkCode("TG123")).thenReturn(booking);
        when(databaseModule.consumeTelegramLinkCode("TG123", 123456789L)).thenReturn(true);

        databaseModule.setTelegramLinkCode(200, "TG123", expiry);
        assertEquals(booking, databaseModule.getBookingByTelegramLinkCode("TG123"));
        assertTrue(databaseModule.consumeTelegramLinkCode("TG123", 123456789L));
        databaseModule.clearExpiredTelegramLinkCodes();

        verify(databaseModule).setTelegramLinkCode(200, "TG123", expiry);
        verify(databaseModule).clearExpiredTelegramLinkCodes();
    }

    // 8. BOOKING ROOM OPERATIONS
    @Test
    void bookingRoomOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.getBookingRoom(200, 101)).thenReturn(bookingRoom);
        when(databaseModule.getRoomsForBooking(200)).thenReturn(List.of(bookingRoom));

        databaseModule.linkRoomToBooking(bookingRoom);
        assertEquals(bookingRoom, databaseModule.getBookingRoom(200, 101));
        assertEquals(1, databaseModule.getRoomsForBooking(200).size());

        databaseModule.updateBookingRoom(bookingRoom);
        databaseModule.unlinkRoomFromBooking(200, 101);

        verify(databaseModule).linkRoomToBooking(bookingRoom);
        verify(databaseModule).updateBookingRoom(bookingRoom);
        verify(databaseModule).unlinkRoomFromBooking(200, 101);
    }

    // 9. ITEM ORDER OPERATIONS
    @Test
    void itemOrderOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.addOrderToBooking(itemOrder)).thenReturn(70);
        when(databaseModule.purchaseItemOrder(itemOrder)).thenReturn(71);
        when(databaseModule.cancelItemOrderAndRestock(71)).thenReturn(true);
        when(databaseModule.getOrderById(70)).thenReturn(itemOrder);
        when(databaseModule.getOrdersForBooking(200)).thenReturn(List.of(itemOrder));

        assertEquals(70, databaseModule.addOrderToBooking(itemOrder));
        assertEquals(71, databaseModule.purchaseItemOrder(itemOrder));
        assertTrue(databaseModule.cancelItemOrderAndRestock(71));
        assertEquals(itemOrder, databaseModule.getOrderById(70));
        assertEquals(1, databaseModule.getOrdersForBooking(200).size());

        databaseModule.updateItemOrder(itemOrder);
        databaseModule.deleteItemOrder(70);

        verify(databaseModule).updateItemOrder(itemOrder);
        verify(databaseModule).deleteItemOrder(70);
    }

    // 10. PAYMENT OPERATIONS
    @Test
    void paymentOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.recordPayment(payment)).thenReturn(80);
        when(databaseModule.getPaymentById(80)).thenReturn(payment);
        when(databaseModule.getPaymentsForBooking(200)).thenReturn(List.of(payment));
        when(databaseModule.getTotalPaidForBooking(200)).thenReturn(25000.0);

        assertEquals(80, databaseModule.recordPayment(payment));
        assertEquals(payment, databaseModule.getPaymentById(80));
        assertEquals(1, databaseModule.getPaymentsForBooking(200).size());
        assertEquals(25000.0, databaseModule.getTotalPaidForBooking(200), 0.001);

        databaseModule.updatePayment(payment);
        databaseModule.deletePayment(80);

        verify(databaseModule).updatePayment(payment);
        verify(databaseModule).deletePayment(80);
    }

    // 11. MESSAGE OPERATIONS
    @Test
    void messageOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.logMessage(message)).thenReturn(90);
        when(databaseModule.getMessageById(90)).thenReturn(message);
        when(databaseModule.getMessagesForCustomer(5)).thenReturn(List.of(message));

        assertEquals(90, databaseModule.logMessage(message));
        assertEquals(message, databaseModule.getMessageById(90));
        assertEquals(1, databaseModule.getMessagesForCustomer(5).size());

        databaseModule.updateMessageStatus(90, "READ");
        databaseModule.deleteMessage(90);

        verify(databaseModule).updateMessageStatus(90, "READ");
        verify(databaseModule).deleteMessage(90);
    }

    // 12. CUSTOMER REPORT OPERATIONS
    @Test
    void customerReportOperations_shouldWorkAsExpected() throws SQLException {
        when(databaseModule.addCustomerReport(customerReport)).thenReturn(95);
        when(databaseModule.getCustomerReportById(95)).thenReturn(customerReport);
        when(databaseModule.getReportsForCustomer(5)).thenReturn(List.of(customerReport));

        assertEquals(95, databaseModule.addCustomerReport(customerReport));
        assertEquals(customerReport, databaseModule.getCustomerReportById(95));
        assertEquals(1, databaseModule.getReportsForCustomer(5).size());

        databaseModule.updateCustomerReport(customerReport);
        databaseModule.deleteCustomerReport(95);

        verify(databaseModule).updateCustomerReport(customerReport);
        verify(databaseModule).deleteCustomerReport(95);
    }

    // ERROR HANDLING EXAMPLE
    @Test
    void databaseError_shouldPropagateSqlException() throws SQLException {
        when(databaseModule.getBookingById(-1)).thenThrow(new SQLException("Booking not found"));

        assertThrows(SQLException.class, () -> databaseModule.getBookingById(-1));
    }
}