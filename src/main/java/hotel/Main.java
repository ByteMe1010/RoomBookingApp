package hotel;

import hotel.db.DatabaseModule;
import hotel.db.SqlServerDatabaseModule;
import hotel.model.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Read database configuration from environment variables or use generic defaults
        String dbUrl = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB;encrypt=true;trustServerCertificate=true;";
        String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "sa";
        String dbPassword = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "YOUR_PASSWORD_HERE";

        System.out.println("=== Running HOTEL_DB DAO Verification ===");
        DatabaseModule db = new SqlServerDatabaseModule(dbUrl, dbUser, dbPassword);

        try {
            // Test 1: Meal Plans
            List<MealPlan> mealPlans = db.getAllMealPlans();
            System.out.println("✅ Meal Plans retrieved: " + mealPlans.size());

            // Test 2: Items
            List<Item> items = db.getAllActiveItems();
            System.out.println("✅ Active Items retrieved: " + items.size());

            // Test 3: Rooms
            List<Room> rooms = db.getAvailableRooms();
            System.out.println("✅ Available Rooms retrieved: " + rooms.size());

        } catch (Exception e) {
            System.err.println("❌ Database Test Failed!");
            e.printStackTrace();
        }
    }
}