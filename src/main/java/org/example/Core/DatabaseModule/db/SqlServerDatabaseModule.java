package org.example.Core.DatabaseModule.db;

import org.example.Core.DatabaseModule.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Complete SQL Server JDBC implementation of DatabaseModule for HOTEL_DB.
 */
public class SqlServerDatabaseModule implements DatabaseModule {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public SqlServerDatabaseModule(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    // ==========================================
    // 1. SHOP OWNER IMPLEMENTATION
    // ==========================================

    @Override
    public int createShopOwner(ShopOwner owner) throws SQLException {
        String sql = "INSERT INTO SHOP_OWNER (username, password_hash, salt, full_name) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, owner.getUsername());
            ps.setString(2, owner.getPasswordHash());
            ps.setString(3, owner.getSalt());
            ps.setString(4, owner.getFullName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    owner.setOwnerId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public ShopOwner getShopOwnerById(int ownerId) throws SQLException {
        String sql = "SELECT owner_id, username, password_hash, salt, full_name, created_at, last_login_at FROM SHOP_OWNER WHERE owner_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapShopOwner(rs);
                }
            }
        }
        return null;
    }

    @Override
    public ShopOwner getShopOwnerByUsername(String username) throws SQLException {
        String sql = "SELECT owner_id, username, password_hash, salt, full_name, created_at, last_login_at FROM SHOP_OWNER WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapShopOwner(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<ShopOwner> getAllShopOwners() throws SQLException {
        List<ShopOwner> list = new ArrayList<>();
        String sql = "SELECT owner_id, username, password_hash, salt, full_name, created_at, last_login_at FROM SHOP_OWNER";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapShopOwner(rs));
            }
        }
        return list;
    }

    @Override
    public void updateShopOwner(ShopOwner owner) throws SQLException {
        String sql = "UPDATE SHOP_OWNER SET username = ?, password_hash = ?, salt = ?, full_name = ? WHERE owner_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner.getUsername());
            ps.setString(2, owner.getPasswordHash());
            ps.setString(3, owner.getSalt());
            ps.setString(4, owner.getFullName());
            ps.setInt(5, owner.getOwnerId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateShopOwnerLastLogin(int ownerId) throws SQLException {
        String sql = "UPDATE SHOP_OWNER SET last_login_at = SYSDATETIME() WHERE owner_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteShopOwner(int ownerId) throws SQLException {
        String sql = "DELETE FROM SHOP_OWNER WHERE owner_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.executeUpdate();
        }
    }

    private ShopOwner mapShopOwner(ResultSet rs) throws SQLException {
        Timestamp cAt = rs.getTimestamp("created_at");
        Timestamp lAt = rs.getTimestamp("last_login_at");
        return new ShopOwner(
                rs.getInt("owner_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getString("full_name"),
                (cAt != null) ? cAt.toLocalDateTime() : null,
                (lAt != null) ? lAt.toLocalDateTime() : null
        );
    }

    // ==========================================
    // 2. MEAL PLAN IMPLEMENTATION
    // ==========================================

    @Override
    public int createMealPlan(MealPlan mealPlan) throws SQLException {
        String sql = "INSERT INTO MEAL_PLAN (code, name, description, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mealPlan.getCode());
            ps.setString(2, mealPlan.getName());
            ps.setString(3, mealPlan.getDescription());
            ps.setBoolean(4, mealPlan.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    mealPlan.setMealPlanId(id);
                    return id;
                }
            }
        }
        return -1;
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

    @Override
    public List<MealPlan> getAllMealPlans() throws SQLException {
        List<MealPlan> list = new ArrayList<>();
        String sql = "SELECT meal_plan_id, code, name, description, is_active FROM MEAL_PLAN WHERE is_active = 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new MealPlan(
                        rs.getInt("meal_plan_id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active")
                ));
            }
        }
        return list;
    }

    @Override
    public void updateMealPlan(MealPlan mealPlan) throws SQLException {
        String sql = "UPDATE MEAL_PLAN SET code = ?, name = ?, description = ?, is_active = ? WHERE meal_plan_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, mealPlan.getCode());
            ps.setString(2, mealPlan.getName());
            ps.setString(3, mealPlan.getDescription());
            ps.setBoolean(4, mealPlan.isActive());
            ps.setInt(5, mealPlan.getMealPlanId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteMealPlan(int mealPlanId) throws SQLException {
        String sql = "DELETE FROM MEAL_PLAN WHERE meal_plan_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mealPlanId);
            ps.executeUpdate();
        }
    }

    // ==========================================
    // 3. ITEM & STOCK IMPLEMENTATION
    // ==========================================

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
                    int id = rs.getInt(1);
                    item.setItemId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Item getItemById(int itemId) throws SQLException {
        String sql = "SELECT item_id, name, price_per_unit, stock_amount, measuring_method, is_active FROM ITEM WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Item> getAllActiveItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_id, name, price_per_unit, stock_amount, measuring_method, is_active FROM ITEM WHERE is_active = 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapItem(rs));
            }
        }
        return items;
    }

    @Override
    public List<Item> getAllItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT item_id, name, price_per_unit, stock_amount, measuring_method, is_active FROM ITEM";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(mapItem(rs));
            }
        }
        return items;
    }

    @Override
    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE ITEM SET name = ?, price_per_unit = ?, stock_amount = ?, measuring_method = ?, is_active = ? WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPricePerUnit());
            ps.setDouble(3, item.getStockAmount());
            ps.setString(4, item.getMeasuringMethod());
            ps.setBoolean(5, item.isActive());
            ps.setInt(6, item.getItemId());
            ps.executeUpdate();
        }
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

    @Override
    public void deleteItem(int itemId) throws SQLException {
        String sql = "DELETE FROM ITEM WHERE item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        }
    }

    private Item mapItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("item_id"),
                rs.getString("name"),
                rs.getDouble("price_per_unit"),
                rs.getDouble("stock_amount"),
                rs.getString("measuring_method"),
                rs.getBoolean("is_active")
        );
    }

    // ==========================================
    // 4. ROOM IMPLEMENTATION
    // ==========================================

    @Override
    public int createRoom(Room room) throws SQLException {
        String sql = "INSERT INTO ROOM (room_number, room_type, price_per_night, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerNight());
            ps.setString(4, room.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    room.setRoomId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Room getRoomById(int roomId) throws SQLException {
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM ROOM WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRoom(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM ROOM";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }
        }
        return rooms;
    }

    @Override
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT room_id, room_number, room_type, price_per_night, status FROM ROOM WHERE status = 'AVAILABLE'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRoom(rs));
            }
        }
        return rooms;
    }

    @Override
    public void updateRoom(Room room) throws SQLException {
        String sql = "UPDATE ROOM SET room_number = ?, room_type = ?, price_per_night = ?, status = ? WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerNight());
            ps.setString(4, room.getStatus());
            ps.setInt(5, room.getRoomId());
            ps.executeUpdate();
        }
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

    @Override
    public void deleteRoom(int roomId) throws SQLException {
        String sql = "DELETE FROM ROOM WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getString("room_type"),
                rs.getDouble("price_per_night"),
                rs.getString("status")
        );
    }

    // ==========================================
    // 5. CUSTOMER IMPLEMENTATION
    // ==========================================

    @Override
    public int saveCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (full_name, phone, telegram_chat_id, times_denied) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            if (customer.getTelegramChatId() != null) {
                ps.setLong(3, customer.getTelegramChatId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setInt(4, customer.getTimesDenied());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    customer.setCustomerId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, full_name, phone, telegram_chat_id, times_denied, last_denied_date, created_at FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Customer getCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT customer_id, full_name, phone, telegram_chat_id, times_denied, last_denied_date, created_at FROM CUSTOMER WHERE phone = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, phone, telegram_chat_id, times_denied, last_denied_date, created_at FROM CUSTOMER";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCustomer(rs));
            }
        }
        return list;
    }

    @Override
    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE CUSTOMER SET full_name = ?, phone = ?, telegram_chat_id = ?, times_denied = ?, last_denied_date = ? WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            if (customer.getTelegramChatId() != null) {
                ps.setLong(3, customer.getTelegramChatId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setInt(4, customer.getTimesDenied());
            if (customer.getLastDeniedDate() != null) {
                ps.setDate(5, Date.valueOf(customer.getLastDeniedDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setInt(6, customer.getCustomerId());
            ps.executeUpdate();
        }
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

    @Override
    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM CUSTOMER WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
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

    // ==========================================
    // 6. BOOKING REQUEST IMPLEMENTATION
    // ==========================================

    @Override
    public int saveBookingRequest(BookingRequest request) throws SQLException {
        String sql = "INSERT INTO BOOKING_REQUEST (customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (request.getCustomerId() != null) {
                ps.setInt(1, request.getCustomerId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, request.getEmailMessageId());
            ps.setString(3, request.getBookingRef());
            ps.setString(4, request.getGuestName());
            ps.setString(5, request.getGuestPhone());
            ps.setDate(6, Date.valueOf(request.getCheckIn()));
            ps.setDate(7, Date.valueOf(request.getCheckOut()));
            ps.setInt(8, request.getRoomsWanted());
            if (request.getMealPlanId() != null) {
                ps.setInt(9, request.getMealPlanId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            if (request.getTotalPrice() != null) {
                ps.setDouble(10, request.getTotalPrice());
            } else {
                ps.setNull(10, Types.DECIMAL);
            }
            ps.setString(11, request.getSpecialRequest());
            ps.setString(12, request.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    request.setRequestId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public BookingRequest getBookingRequestById(int requestId) throws SQLException {
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBookingRequest(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<BookingRequest> getAllBookingRequests() throws SQLException {
        List<BookingRequest> list = new ArrayList<>();
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBookingRequest(rs));
            }
        }
        return list;
    }

    @Override
    public List<BookingRequest> getPendingBookingRequests() throws SQLException {
        List<BookingRequest> requests = new ArrayList<>();
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE status = 'PENDING'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requests.add(mapBookingRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public BookingRequest getBookingRequestByEmailMessageId(String emailMessageId) throws SQLException {
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE email_message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emailMessageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBookingRequest(rs);
            }
        }
        return null;
    }

    @Override
    public BookingRequest getBookingRequestByBookingRef(String bookingRef) throws SQLException {
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE booking_ref = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingRef);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBookingRequest(rs);
            }
        }
        return null;
    }

    @Override
    public List<BookingRequest> getBookingRequests(String status, String... searchQ) throws SQLException {
        List<BookingRequest> requests = new ArrayList<>();
        String sql = "SELECT request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted, meal_plan_id, total_price, special_request, status, received_at, decided_by, decided_at FROM BOOKING_REQUEST WHERE status = ? "+((searchQ.length>0) ? "AND guest_name LIKE ?":"");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1,status);
            if(searchQ.length>0) ps.setString(2,searchQ[0]);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(mapBookingRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public void updateBookingRequest(BookingRequest request) throws SQLException {
        String sql = "UPDATE BOOKING_REQUEST SET customer_id = ?, booking_ref = ?, guest_name = ?, guest_phone = ?, check_in = ?, check_out = ?, rooms_wanted = ?, meal_plan_id = ?, total_price = ?, special_request = ?, status = ? WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (request.getCustomerId() != null) {
                ps.setInt(1, request.getCustomerId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
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
            ps.setInt(12, request.getRequestId());
            ps.executeUpdate();
        }
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

    @Override
    public void deleteBookingRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM BOOKING_REQUEST WHERE request_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    private BookingRequest mapBookingRequest(ResultSet rs) throws SQLException {
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

        return new BookingRequest(
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
    }

    // ==========================================
    // 7. BOOKING IMPLEMENTATION
    // ==========================================

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
                    int id = rs.getInt(1);
                    booking.setBookingId(id);
                    return id;
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
                    return mapBooking(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT booking_id, request_id, customer_id, check_in, check_out, status, created_at FROM BOOKING";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapBooking(rs));
            }
        }
        return list;
    }

    @Override
    public void updateBooking(Booking booking) throws SQLException {
        String sql = "UPDATE BOOKING SET request_id = ?, customer_id = ?, check_in = ?, check_out = ?, status = ? WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getRequestId());
            ps.setInt(2, booking.getCustomerId());
            ps.setDate(3, Date.valueOf(booking.getCheckIn()));
            ps.setDate(4, Date.valueOf(booking.getCheckOut()));
            ps.setString(5, booking.getStatus());
            ps.setInt(6, booking.getBookingId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE BOOKING SET status = ? WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM BOOKING WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    @Override
    public void setTelegramLinkCode(int bookingId, String code, LocalDateTime expiresAt) throws SQLException {
        String sql = "UPDATE BOOKING SET telegram_link_code = ?, telegram_link_code_expires = ? WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setTimestamp(2, Timestamp.valueOf(expiresAt));
            ps.setInt(3, bookingId);
            ps.executeUpdate();
        }
    }

    @Override
    public Booking getBookingByTelegramLinkCode(String code) throws SQLException {
        String sql = "SELECT booking_id, request_id, customer_id, check_in, check_out, status, created_at FROM BOOKING WHERE telegram_link_code = ? AND telegram_link_code_expires > SYSDATETIME()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapBooking(rs);
            }
        }
        return null;
    }

    @Override
    public boolean consumeTelegramLinkCode(String code, long chatId) throws SQLException {
        Booking booking = getBookingByTelegramLinkCode(code);
        if (booking == null || booking.getCustomerId() <= 0) return false;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE CUSTOMER SET telegram_chat_id = ? WHERE customer_id = ?")) {
                    ps.setLong(1, chatId);
                    ps.setInt(2, booking.getCustomerId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("UPDATE BOOKING SET telegram_link_code = NULL, telegram_link_code_expires = NULL WHERE telegram_link_code = ?")) {
                    ps.setString(1, code);
                    ps.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    @Override
    public void clearExpiredTelegramLinkCodes() throws SQLException {
        String sql = "UPDATE BOOKING SET telegram_link_code = NULL, telegram_link_code_expires = NULL WHERE telegram_link_code IS NOT NULL AND telegram_link_code_expires < SYSDATETIME()";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
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

    // ==========================================
    // 8. BOOKING ROOM IMPLEMENTATION
    // ==========================================

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

    @Override
    public BookingRoom getBookingRoom(int bookingId, int roomId) throws SQLException {
        String sql = "SELECT booking_id, room_id, price_per_night FROM BOOKING_ROOM WHERE booking_id = ? AND room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BookingRoom(
                            rs.getInt("booking_id"),
                            rs.getInt("room_id"),
                            rs.getDouble("price_per_night")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<BookingRoom> getRoomsForBooking(int bookingId) throws SQLException {
        List<BookingRoom> list = new ArrayList<>();
        String sql = "SELECT booking_id, room_id, price_per_night FROM BOOKING_ROOM WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookingRoom(
                            rs.getInt("booking_id"),
                            rs.getInt("room_id"),
                            rs.getDouble("price_per_night")
                    ));
                }
            }
        }
        return list;
    }

    @Override
    public void updateBookingRoom(BookingRoom bookingRoom) throws SQLException {
        String sql = "UPDATE BOOKING_ROOM SET price_per_night = ? WHERE booking_id = ? AND room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, bookingRoom.getPricePerNight());
            ps.setInt(2, bookingRoom.getBookingId());
            ps.setInt(3, bookingRoom.getRoomId());
            ps.executeUpdate();
        }
    }

    @Override
    public void unlinkRoomFromBooking(int bookingId, int roomId) throws SQLException {
        String sql = "DELETE FROM BOOKING_ROOM WHERE booking_id = ? AND room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }

    // ==========================================
    // 9. ITEM ORDER IMPLEMENTATION
    // ==========================================

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
                    int id = rs.getInt(1);
                    order.setOrderId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public ItemOrder getOrderById(int orderId) throws SQLException {
        String sql = "SELECT order_id, booking_id, item_id, amount, unit_price, ordered_at FROM ITEM_ORDER WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItemOrder(rs);
                }
            }
        }
        return null;
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
                    orders.add(mapItemOrder(rs));
                }
            }
        }
        return orders;
    }

    @Override
    public void updateItemOrder(ItemOrder order) throws SQLException {
        String sql = "UPDATE ITEM_ORDER SET booking_id = ?, item_id = ?, amount = ?, unit_price = ? WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getBookingId());
            ps.setInt(2, order.getItemId());
            ps.setDouble(3, order.getAmount());
            ps.setDouble(4, order.getUnitPrice());
            ps.setInt(5, order.getOrderId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteItemOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM ITEM_ORDER WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    private ItemOrder mapItemOrder(ResultSet rs) throws SQLException {
        Timestamp oAt = rs.getTimestamp("ordered_at");
        return new ItemOrder(
                rs.getInt("order_id"),
                rs.getInt("booking_id"),
                rs.getInt("item_id"),
                rs.getDouble("amount"),
                rs.getDouble("unit_price"),
                (oAt != null) ? oAt.toLocalDateTime() : null
        );
    }

    // ==========================================
    // 10. PAYMENT IMPLEMENTATION
    // ==========================================

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
                    int id = rs.getInt(1);
                    payment.setPaymentId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Payment getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT payment_id, booking_id, amount, method, paid_at FROM PAYMENT WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPayment(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Payment> getPaymentsForBooking(int bookingId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT payment_id, booking_id, amount, method, paid_at FROM PAYMENT WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapPayment(rs));
                }
            }
        }
        return list;
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

    @Override
    public void updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE PAYMENT SET booking_id = ?, amount = ?, method = ? WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getBookingId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getMethod());
            ps.setInt(4, payment.getPaymentId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM PAYMENT WHERE payment_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        }
    }

    private Payment mapPayment(ResultSet rs) throws SQLException {
        Timestamp pAt = rs.getTimestamp("paid_at");
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getDouble("amount"),
                rs.getString("method"),
                (pAt != null) ? pAt.toLocalDateTime() : null
        );
    }

    // ==========================================
    // 11. MESSAGE IMPLEMENTATION
    // ==========================================

    @Override
    public int logMessage(Message message) throws SQLException {
        String sql = "INSERT INTO MESSAGE (customer_id, content, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getCustomerId());
            ps.setString(2, message.getContent());
            ps.setString(3, message.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    message.setMessageId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public Message getMessageById(int messageId) throws SQLException {
        String sql = "SELECT message_id, customer_id, content, status, sent_at FROM MESSAGE WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMessage(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Message> getMessagesForCustomer(int customerId) throws SQLException {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT message_id, customer_id, content, status, sent_at FROM MESSAGE WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMessage(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void updateMessageStatus(int messageId, String status) throws SQLException {
        String sql = "UPDATE MESSAGE SET status = ? WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, messageId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM MESSAGE WHERE message_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        }
    }

    private Message mapMessage(ResultSet rs) throws SQLException {
        Timestamp sAt = rs.getTimestamp("sent_at");
        return new Message(
                rs.getInt("message_id"),
                rs.getInt("customer_id"),
                rs.getString("content"),
                rs.getString("status"),
                (sAt != null) ? sAt.toLocalDateTime() : null
        );
    }

    // ==========================================
    // 12. CUSTOMER REPORT IMPLEMENTATION
    // ==========================================

    @Override
    public int addCustomerReport(CustomerReport report) throws SQLException {
        // Uses 'note' column to match UPDATED_SQLQuery1_v2.0.sql
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
                    int id = rs.getInt(1);
                    report.setReportId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    @Override
    public CustomerReport getCustomerReportById(int reportId) throws SQLException {
        String sql = "SELECT report_id, customer_id, booking_id, note, created_at FROM CUSTOMER_REPORT WHERE report_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomerReport(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CustomerReport> getReportsForCustomer(int customerId) throws SQLException {
        List<CustomerReport> reports = new ArrayList<>();
        String sql = "SELECT report_id, customer_id, booking_id, note, created_at FROM CUSTOMER_REPORT WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapCustomerReport(rs));
                }
            }
        }
        return reports;
    }

    @Override
    public void updateCustomerReport(CustomerReport report) throws SQLException {
        String sql = "UPDATE CUSTOMER_REPORT SET customer_id = ?, booking_id = ?, note = ? WHERE report_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, report.getCustomerId());
            if (report.getBookingId() != null) {
                ps.setInt(2, report.getBookingId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, report.getReason());
            ps.setInt(4, report.getReportId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteCustomerReport(int reportId) throws SQLException {
        String sql = "DELETE FROM CUSTOMER_REPORT WHERE report_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.executeUpdate();
        }
    }

    private CustomerReport mapCustomerReport(ResultSet rs) throws SQLException {
        int bIdVal = rs.getInt("booking_id");
        Integer bId = rs.wasNull() ? null : bIdVal;
        Timestamp cAt = rs.getTimestamp("created_at");

        return new CustomerReport(
                rs.getInt("report_id"),
                rs.getInt("customer_id"),
                bId,
                rs.getString("note"),
                (cAt != null) ? cAt.toLocalDateTime() : null
        );
    }

    @Override
    public int purchaseItemOrder(ItemOrder order) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            String itemSql = "SELECT stock_amount, is_active FROM ITEM WITH (UPDLOCK, HOLDLOCK) WHERE item_id = ?";
            double stock;
            boolean active;
            try (PreparedStatement itemPs = conn.prepareStatement(itemSql)) {
                itemPs.setInt(1, order.getItemId());
                try (ResultSet rs = itemPs.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Item was not found.");
                    stock = rs.getDouble("stock_amount");
                    active = rs.getBoolean("is_active");
                }
            }

            if (!active) throw new SQLException("Archived items cannot be ordered.");
            if (order.getAmount() <= 0) throw new SQLException("Order amount must be greater than zero.");
            if (order.getAmount() > stock) throw new SQLException("Insufficient stock. Available: " + stock);

            String insertSql = "INSERT INTO ITEM_ORDER (booking_id, item_id, amount, unit_price) VALUES (?, ?, ?, ?)";
            int orderId = -1;
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setInt(1, order.getBookingId());
                insertPs.setInt(2, order.getItemId());
                insertPs.setDouble(3, order.getAmount());
                insertPs.setDouble(4, order.getUnitPrice());
                insertPs.executeUpdate();
                try (ResultSet keys = insertPs.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                        order.setOrderId(orderId);
                    }
                }
            }

            if (orderId <= 0) throw new SQLException("Failed to record item order.");

            try (PreparedStatement stockPs = conn.prepareStatement("UPDATE ITEM SET stock_amount = stock_amount - ? WHERE item_id = ?")) {
                stockPs.setDouble(1, order.getAmount());
                stockPs.setInt(2, order.getItemId());
                stockPs.executeUpdate();
            }

            conn.commit();
            return orderId;
        } catch (SQLException exception) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        } finally {
            try {
                conn.close();
            } catch (SQLException exception) {
                // Connection cleanup failure should not mask the database operation result.
            }
        }
    }

    @Override
    public boolean cancelItemOrderAndRestock(int orderId) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            int itemId;
            double amount;
            try (PreparedStatement selectPs = conn.prepareStatement(
                    "SELECT item_id, amount FROM ITEM_ORDER WITH (UPDLOCK, HOLDLOCK) WHERE order_id = ?")) {
                selectPs.setInt(1, orderId);
                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    itemId = rs.getInt("item_id");
                    amount = rs.getDouble("amount");
                }
            }

            try (PreparedStatement deletePs = conn.prepareStatement("DELETE FROM ITEM_ORDER WHERE order_id = ?")) {
                deletePs.setInt(1, orderId);
                deletePs.executeUpdate();
            }

            try (PreparedStatement stockPs = conn.prepareStatement("UPDATE ITEM SET stock_amount = stock_amount + ? WHERE item_id = ?")) {
                stockPs.setDouble(1, amount);
                stockPs.setInt(2, itemId);
                stockPs.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException exception) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                exception.addSuppressed(rollbackException);
            }
            throw exception;
        } finally {
            try {
                conn.close();
            } catch (SQLException exception) {
                // Connection cleanup failure should not mask the database operation result.
            }
        }
    }
}