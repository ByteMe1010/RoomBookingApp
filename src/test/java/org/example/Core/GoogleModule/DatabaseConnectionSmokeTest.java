package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.JdbcConnections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionSmokeTest {
    @Test
    @EnabledIfSystemProperty(named = "booking.db.smoke", matches = "true")
    void connectsToTestDatabase() {
        boolean succeeded = false;
        try {
            String url = System.getenv("BOOKING_JDBC_URL");
            assertNotNull(url, "BOOKING_JDBC_URL must be set");
            try (var connection = JdbcConnections.open(url, System.getenv("BOOKING_DB_USER"), System.getenv("BOOKING_DB_PASSWORD"));
                 var statement = connection.createStatement();
                 var rows = statement.executeQuery("SELECT DB_NAME() AS CurrentDatabase")) {
                assertTrue(rows.next());
                String database = rows.getString("CurrentDatabase");
                System.out.println("Connected database: " + database);
                assertEquals("HOTEL_DB_TEST", database, "Unexpected database");
                succeeded = true;
            }
        } catch (SQLException e) {
            fail("Database smoke test failed (SQL details withheld). Exception type: " + e.getClass().getName());
        } finally {
            System.out.println("Connection succeeded: " + succeeded);
        }
    }
}
