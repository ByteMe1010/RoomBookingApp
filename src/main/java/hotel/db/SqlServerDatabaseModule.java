package hotel.db;

import hotel.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL Server implementation of the DatabaseModule interface using JDBC.
 */

public class SqlServerDatabaseModule implements DatabaseModule {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    // Constructor with database credentials
    public SqlServerDatabaseModule(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    // Helper method to establish a connection
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // --- MEAL PLAN IMPLEMENTATION ---

    @Override
    public List<MealPlan> getAllMealPlans() throws SQLException {
        List<MealPlan> list = new ArrayList<>();
        String sql = "SELECT meal_plan_id, code, name, description, is_active FROM MEAL_PLAN WHERE is_active = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MealPlan plan = new MealPlan(
                        rs.getInt("meal_plan_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                );
                list.add(plan);
            }
        }
        return list;
    }

    @Override
    public MealPlan getMealPlanById(int mealPlanId) throws SQLException {
        String sql = "SELECT meal_plan_id, code, name, description, is_active FROM MEAL_PLAN WHERE meal_plan_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mealPlanId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new MealPlan(
                            rs.getInt("meal_plan_id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active")
                    );
                }
            }
        }
        return null;
    }

    // --- ITEM & STOCK IMPLEMENTATION ---

    @Override
    public int addNewItem(Item item) throws SQLException {
        String sql = "INSERT INTO ITEM (name, price_per_unit, stock_amount, measuring_method, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPricePerUnit());
            ps.setDouble(3, item.getStockAmount());
            ps.setString(4, item.getMeasuringMethod());
            ps.setBoolean(5, item.isActive());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    item.setItemId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<Item> getAllActiveItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_id, name, price_per_unit, stock_amount, measuring_method, is_active FROM ITEM WHERE is_active = 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price_per_unit"),
                        rs.getDouble("stock_amount"),
                        rs.getString("measuring_method"),
                        rs.getBoolean("is_active")
                );
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public void updateItemStock(int itemId, double newStockAmount) throws SQLException {
        String sql = "UPDATE ITEM SET stock_amount = ? WHERE item_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newStockAmount);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    // --- ROOM IMPLEMENTATION ---

    @Override
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM ROOM WHERE status = 'AVAILABLE'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night"),
                        rs.getString("status")
                );
                rooms.add(room);
            }
        }
        return rooms;
    }

    @Override
    public void updateRoomStatus(int roomId, String newStatus) throws SQLException {
        String sql = "UPDATE ROOM SET status = ? WHERE room_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }

    // --- CUSTOMER IMPLEMENTATION ---

    @Override
    public int saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (full_name, phone, times_denied) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setInt(3, customer.getTimesDenied());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    customer.setCustomerId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public Customer getCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT customer_id, full_name, phone, telegram_chat_id, times_denied, last_denied_date, created_at FROM CUSTOMER WHERE phone = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long telegramIdVal = rs.getLong("telegram_chat_id");
                    Long telegramId = rs.wasNull() ? null : telegramIdVal;

                    Date deniedDateVal = rs.getDate("last_denied_date");
                    java.time.LocalDate lastDenied = (deniedDateVal != null) ? deniedDateVal.toLocalDate() : null;

                    Timestamp createdAtVal = rs.getTimestamp("created_at");
                    java.time.LocalDateTime createdAt = (createdAtVal != null) ? createdAtVal.toLocalDateTime() : null;

                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("full_name"),
                            rs.getString("phone"),
                            telegramId,
                            rs.getInt("times_denied"),
                            lastDenied,
                            createdAt
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void updateTelegramChatId(int customerId, long telegramChatId) throws SQLException {
        String sql = "UPDATE CUSTOMER SET telegram_chat_id = ? WHERE customer_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, telegramChatId);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        }
    }

    @Override
    public void incrementCustomerDeniedCount(int customerId) throws SQLException {
        String sql = "UPDATE CUSTOMER SET times_denied = times_denied + 1, last_denied_date = CAST(GETDATE() AS DATE) WHERE customer_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }

    // --- BOOKING REQUEST IMPLEMENTATION ---

    @Override
    public int saveBookingRequest(BookingRequest request) throws SQLException {
        String sql = "INSERT INTO BOOKING_REQUEST (email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, request.getEmailMessageId());
            ps.setString(2, request.getBookingRef());
            ps.setString(3, request.getGuestName());
            ps.setString(4, request.getGuestPhone());
            ps.setDate(5, Date.valueOf(request.getCheckIn()));
            ps.setDate(6, Date.valueOf(request.getCheckOut()));
            ps.setInt(7, request.getRoomsWanted());

            if (request.getMealPlanId() != null) {
                ps.setInt(8, request.getMealPlanId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            if (request.getTotalPrice() != null) {
                ps.setDouble(9, request.getTotalPrice());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }

            ps.setString(10, request.getSpecialRequest());
            ps.setString(11, request.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    request.setRequestId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<BookingRequest> getPendingBookingRequests() throws SQLException {
        List<BookingRequest> requests = new ArrayList<>();
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE status = 'PENDING'";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int custIdVal = rs.getInt("customer_id");
                Integer custId = rs.wasNull() ? null : custIdVal;

                int mpIdVal = rs.getInt("meal_plan_id");
                Integer mpId = rs.wasNull() ? null : mpIdVal;

                double priceVal = rs.getDouble("total_price");
                Double price = rs.wasNull() ? null : priceVal;

                int decByVal = rs.getInt("decided_by");
                Integer decBy = rs.wasNull() ? null : decByVal;

                Timestamp rAt = rs.getTimestamp("received_at");
                Timestamp dAt = rs.getTimestamp("decided_at");

                BookingRequest req = new BookingRequest(
                        rs.getInt("request_id"),
                        custId,
                        rs.getString("email_message_id"),
                        rs.getString("booking_ref"),
                        rs.getString("guest_name"),
                        rs.getString("guest_phone"),
                        rs.getDate("check_in").toLocalDate(),
                        rs.getDate("check_out").toLocalDate(),
                        rs.getInt("rooms_wanted"),
                        mpId,
                        price,
                        rs.getString("special_request"),
                        rs.getString("status"),
                        (rAt != null) ? rAt.toLocalDateTime() : null,
                        decBy,
                        (dAt != null) ? dAt.toLocalDateTime() : null
                );
                requests.add(req);
            }
        }
        return requests;
    }

    @Override
    public void updateBookingRequestStatus(int requestId, String status, int decidedBy) throws SQLException {
        String sql = "UPDATE BOOKING_REQUEST SET status = ?, decided_by = ?, decided_at = SYSDATETIME() WHERE request_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, decidedBy);
            ps.setInt(3, requestId);
            ps.executeUpdate();
        }
    }

    // --- BOOKING IMPLEMENTATION ---

    @Override
    public int createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO BOOKING (request_id, customer_id, check_in, check_out, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, booking.getRequestId());
            ps.setInt(2, booking.getCustomerId());
            ps.setDate(3, Date.valueOf(booking.getCheckIn()));
            ps.setDate(4, Date.valueOf(booking.getCheckOut()));
            ps.setString(5, booking.getStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    booking.setBookingId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT booking_id, request_id, customer_id, check_in, check_out, status, created_at FROM BOOKING WHERE booking_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp cAt = rs.getTimestamp("created_at");
                    return new Booking(
                            rs.getInt("booking_id"),
                            rs.getInt("request_id"),
                            rs.getInt("customer_id"),
                            rs.getDate("check_in").toLocalDate(),
                            rs.getDate("check_out").toLocalDate(),
                            rs.getString("status"),
                            (cAt != null) ? cAt.toLocalDateTime() : null
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void linkRoomToBooking(BookingRoom bookingRoom) throws SQLException {
        String sql = "INSERT INTO BOOKING_ROOM (booking_id, room_id, price_per_night) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingRoom.getBookingId());
            ps.setInt(2, bookingRoom.getRoomId());
            ps.setDouble(3, bookingRoom.getPricePerNight());
            ps.executeUpdate();
        }
    }

    // --- ITEM ORDER IMPLEMENTATION ---

    @Override
    public int addOrderToBooking(ItemOrder order) throws SQLException {
        String sql = "INSERT INTO ITEM_ORDER (booking_id, item_id, amount, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, order.getBookingId());
            ps.setInt(2, order.getItemId());
            ps.setDouble(3, order.getAmount());
            ps.setDouble(4, order.getUnitPrice());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    order.setOrderId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<ItemOrder> getOrdersForBooking(int bookingId) throws SQLException {
        List<ItemOrder> orders = new ArrayList<>();
        String sql = "SELECT order_id, booking_id, item_id, amount, unit_price, ordered_at FROM ITEM_ORDER WHERE booking_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp oAt = rs.getTimestamp("ordered_at");
                    ItemOrder order = new ItemOrder(
                            rs.getInt("order_id"),
                            rs.getInt("booking_id"),
                            rs.getInt("item_id"),
                            rs.getDouble("amount"),
                            rs.getDouble("unit_price"),
                            (oAt != null) ? oAt.toLocalDateTime() : null
                    );
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    // --- PAYMENT IMPLEMENTATION ---

    @Override
    public int recordPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO PAYMENT (booking_id, amount, method) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getMethod());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    payment.setPaymentId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public double getTotalPaidForBooking(int bookingId) throws SQLException {
        String sql = "SELECT ISNULL(SUM(amount), 0.0) AS total_paid FROM PAYMENT WHERE booking_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_paid");
                }
            }
        }
        return 0.0;
    }

    // --- CUSTOMER REPORT IMPLEMENTATION ---

    @Override
    public int addCustomerReport(CustomerReport report) throws SQLException {
        String sql = "INSERT INTO CUSTOMER_REPORT (customer_id, booking_id, note) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, report.getCustomerId());

            if (report.getBookingId() != null) {
                ps.setInt(2, report.getBookingId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, report.getReason());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    report.setReportId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<CustomerReport> getReportsForCustomer(int customerId) throws SQLException {
        List<CustomerReport> reports = new ArrayList<>();
        String sql = "SELECT report_id, customer_id, booking_id, reason, created_at FROM CUSTOMER_REPORT WHERE customer_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int bIdVal = rs.getInt("booking_id");
                    Integer bId = rs.wasNull() ? null : bIdVal;

                    Timestamp cAt = rs.getTimestamp("created_at");

                    CustomerReport report = new CustomerReport(
                            rs.getInt("report_id"),
                            rs.getInt("customer_id"),
                            bId,
                            rs.getString("reason"),
                            (cAt != null) ? cAt.toLocalDateTime() : null
                    );
                    reports.add(report);
                }
            }
        }
        return reports;
    }
}