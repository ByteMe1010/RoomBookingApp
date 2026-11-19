package org.example.seed;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.db.SqlServerDatabaseModule;
import org.example.Core.DatabaseModule.model.*;
import org.example.Core.Utils.PasswordManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds HOTEL_DB with meaningful, internally-consistent mock data.
 * Run: mvn compile exec:java -Dexec.mainClass=org.example.seed.MockDataSeeder
 * Aborts if shop owners already exist (idempotent guard).
 */
public class MockDataSeeder {
    public static void main(String[] args) {
        String adminPassword = System.getenv("SEED_ADMIN_PASSWORD");
        String deskPassword = System.getenv("SEED_DESK_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank() || deskPassword == null || deskPassword.isBlank()) {
            throw new IllegalArgumentException("Set SEED_ADMIN_PASSWORD and SEED_DESK_PASSWORD before seeding");
        }
        DatabaseModule db = new SqlServerDatabaseModule(
                System.getenv("DB_URL"), System.getenv("DB_USER"), System.getenv("DB_PASSWORD"));

        try {
            List<ShopOwner> existing = db.getAllShopOwners();
            if (existing != null && !existing.isEmpty()) {
                System.out.println("Shop owners already exist (" + existing.size() + "). Aborting to avoid duplicates. " +
                        "Clear tables manually if you want a fresh seed.");
                return;
            }

            System.out.println("Seeding mock data...");

            // ---------- Shop owners ----------
            PasswordManager.HashResult hr1 = PasswordManager.hashPassword(adminPassword);
            ShopOwner owner1 = new ShopOwner("admin", hr1.hash, hr1.salt, "Eleanor Vance");
            int ownerId1 = db.createShopOwner(owner1);
            PasswordManager.HashResult hr2 = PasswordManager.hashPassword(deskPassword);
            ShopOwner owner2 = new ShopOwner("david_desk", hr2.hash, hr2.salt, "David Miller");
            int ownerId2 = db.createShopOwner(owner2);
            System.out.println("  owners: " + ownerId1 + ", " + ownerId2);

            // ---------- Meal plans ----------
            db.createMealPlan(new MealPlan("RO", "Room Only", "No meals included"));
            db.createMealPlan(new MealPlan("BB", "Breakfast Included", "Daily breakfast"));
            db.createMealPlan(new MealPlan("HB", "Half Board", "Breakfast + dinner"));
            db.createMealPlan(new MealPlan("FB", "Full Board", "All meals included"));
            List<MealPlan> plans = db.getAllMealPlans();
            int bbId = plans.stream().filter(p -> "BB".equals(p.getCode())).findFirst().get().getMealPlanId();
            int roId = plans.stream().filter(p -> "RO".equals(p.getCode())).findFirst().get().getMealPlanId();
            System.out.println("  meal plans: " + plans.size());

            // ---------- Items ----------
            db.addNewItem(new Item("Bottled Water", 2.50, 50, "COUNT"));
            db.addNewItem(new Item("Soft Drink", 3.00, 40, "COUNT"));
            db.addNewItem(new Item("Chocolate Bar", 4.00, 30, "COUNT"));
            db.addNewItem(new Item("Bath Towel", 15.00, 20, "COUNT"));
            db.addNewItem(new Item("Minibar Snack Mix", 6.50, 25, "COUNT"));
            db.addNewItem(new Item("Coffee Capsule", 1.50, 100, "COUNT"));
            List<Item> items = db.getAllItems();
            System.out.println("  items: " + items.size());

            // ---------- Rooms ----------
            String[] types = {"Single Standard", "Double Standard", "Double Superior", "Family Suite", "Executive Suite"};
            double[] prices = {65.00, 85.00, 110.00, 160.00, 220.00};
            for (int i = 0; i < types.length; i++) {
                for (int n = 1; n <= 2; n++) {
                    int num = 100 + (i + 1) * 10 + n;
                    db.createRoom(new Room(String.valueOf(num), types[i], prices[i]));
                }
            }
            List<Room> rooms = db.getAllRooms();
            System.out.println("  rooms: " + rooms.size());

            // ---------- Customers ----------
            int c1 = db.saveCustomer(new Customer("Kamal Perera", "0771234567"));
            int c2 = db.saveCustomer(new Customer("Nimal Silva", "0779876543"));
            int c3 = db.saveCustomer(new Customer("Anoma Jayasinghe", "0715551234"));
            int c4 = db.saveCustomer(new Customer("Ruwan Fernando", "0723456789"));
            System.out.println("  customers: 4");

            // Link a telegram chat to one customer (simulating a registered user)
            db.updateTelegramChatId(c1, 1718525359L);

            // ---------- Booking requests ----------
            // 1. PENDING request (recent)
            BookingRequest r1 = new BookingRequest("msg-001", "BKG-1001", "Kamal Perera", "0771234567",
                    LocalDate.now().plusDays(5), LocalDate.now().plusDays(8), bbId, 240.00, "Late check-in requested");
            r1.setCustomerId(c1);
            r1.setRoomsWanted(1);
            int req1 = db.saveBookingRequest(r1);

            // 2. PENDING request
            BookingRequest r2 = new BookingRequest("msg-002", "BKG-1002", "Nimal Silva", "0779876543",
                    LocalDate.now().plusDays(10), LocalDate.now().plusDays(12), roId, 170.00, null);
            r2.setCustomerId(c2);
            r2.setRoomsWanted(2);
            int req2 = db.saveBookingRequest(r2);

            // 3. ACCEPTED request (will create booking)
            BookingRequest r3 = new BookingRequest("msg-003", "BKG-1003", "Anoma Jayasinghe", "0715551234",
                    LocalDate.now().plusDays(2), LocalDate.now().plusDays(4), bbId, 220.00, "Quiet room preferred");
            r3.setCustomerId(c3);
            r3.setRoomsWanted(1);
            int req3 = db.saveBookingRequest(r3);

            // 4. DENIED request
            BookingRequest r4 = new BookingRequest("msg-004", "BKG-1004", "Ruwan Fernando", "0723456789",
                    LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), roId, 130.00, null);
            r4.setCustomerId(c4);
            r4.setRoomsWanted(1);
            r4.setStatus("DENIED");
            int req4 = db.saveBookingRequest(r4);
            db.incrementCustomerDeniedCount(c4);

            System.out.println("  booking requests: 4 (2 pending, 1 accepted, 1 denied)");

            // ---------- Accept request 3 -> create booking + assign room ----------
            Booking booking3 = new Booking(req3, c3, LocalDate.now().plusDays(2), LocalDate.now().plusDays(4));
            int bookingId3 = db.createBooking(booking3);
            // assign first available double room
            List<Room> avail = db.getAvailableRooms();
            Room assignedRoom = avail.isEmpty() ? null : avail.get(0);
            if (assignedRoom != null) {
                db.linkRoomToBooking(new BookingRoom(bookingId3, assignedRoom.getRoomId(), assignedRoom.getPricePerNight()));
                db.updateRoomStatus(assignedRoom.getRoomId(), "BOOKED");
            }
            db.updateBookingRequestStatus(req3, "ACCEPTED", ownerId1);
            System.out.println("  booking created: #" + bookingId3);

            // ---------- Payments ----------
            if (bookingId3 > 0) {
                db.recordPayment(new Payment(bookingId3, 110.00, "CARD"));
            }

            // ---------- Item orders ----------
            if (bookingId3 > 0 && !items.isEmpty()) {
                Item water = items.get(0);
                db.addOrderToBooking(new ItemOrder(bookingId3, water.getItemId(), 2, water.getPricePerUnit()));
                // purchase (deduct stock)
                ItemOrder order = db.getOrdersForBooking(bookingId3).get(0);
                db.purchaseItemOrder(order);
            }

            // ---------- Messages (telegram log) ----------
            db.logMessage(new Message(c1, "Booking confirmation sent for BKG-1003", "SENT"));

            // ---------- Customer report ----------
            if (bookingId3 > 0) {
                db.addCustomerReport(new CustomerReport(c3, bookingId3, "Guest requested extra pillows at check-in."));
            }

            // ---------- A finished past booking for history ----------
            BookingRequest r5 = new BookingRequest("msg-005", "BKG-1005", "Kamal Perera", "0771234567",
                    LocalDate.now().minusDays(10), LocalDate.now().minusDays(7), roId, 195.00, null);
            r5.setCustomerId(c1);
            r5.setRoomsWanted(1);
            r5.setStatus("ACCEPTED");
            int req5 = db.saveBookingRequest(r5);
            Booking pastBooking = new Booking(req5, c1, LocalDate.now().minusDays(10), LocalDate.now().minusDays(7));
            int pastBookingId = db.createBooking(pastBooking);
            pastBooking.setStatus("FINISHED");
            db.updateBooking(pastBooking);
            if (!avail.isEmpty() && avail.size() > 1) {
                Room r = avail.get(1);
                db.linkRoomToBooking(new BookingRoom(pastBookingId, r.getRoomId(), r.getPricePerNight()));
            }
            db.recordPayment(new Payment(pastBookingId, 195.00, "CASH"));

            System.out.println("Seeding complete.");
        } catch (SQLException e) {
            System.err.println("Seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
