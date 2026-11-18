package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.example.Core.DatabaseModule.JdbcConnections;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/** Explicit opt-in only; integration writes synthetic rows to a disposable database. */
class DatabaseIntegrationTest {
    private static String env(String name) {
        String value = System.getenv(name);
        assertNotNull(value, name + " must be set");
        assertFalse(value.isBlank(), name + " must not be blank");
        return value;
    }

    @Test
    @EnabledIfSystemProperty(named = "booking.db.integration", matches = "true")
    void insertsAndDeduplicatesSyntheticReservation() {
        assertEquals("true", System.getenv("TEST_DB_DISPOSABLE"), "Set TEST_DB_DISPOSABLE=true only for a disposable test database");
        String url = env("TEST_DB_URL");
        boolean integrated = JdbcConnections.usesIntegratedSecurity(url);
        String user = integrated ? null : env("TEST_DB_USER");
        String password = integrated ? null : env("TEST_DB_PASSWORD");
        String suffix = UUID.randomUUID().toString();
        String reference = "TEST-" + suffix, message = "TEST-" + suffix;
        var booking = new BookingNotice(reference, "Synthetic Test", LocalDate.of(2030, 1, 1), LocalDate.of(2030, 1, 2), null, 1, null);
        boolean cleanupNeeded = false;
        try {
            try (var connection = JdbcConnections.open(url, user, password)) {
                requireTestDatabase(connection);
                // Do not claim or clean up an identifier pair that already exists.
                try (var check = connection.prepareStatement("SELECT 1 FROM BOOKING_REQUEST WHERE booking_ref = ? OR email_message_id = ?")) {
                    check.setString(1, reference);
                    check.setString(2, message);
                    try (var rows = check.executeQuery()) {
                        assertFalse(rows.next(), "Synthetic identifiers already exist");
                    }
                }
            }
            cleanupNeeded = true;
            var repository = new BookingRequestRepository(url, user, password);
            assertTrue(repository.saveIfNew(message, booking));
            assertFalse(repository.saveIfNew(message, booking));
            assertFalse(repository.saveIfNew("OTHER-" + suffix, booking));
            try (var connection = JdbcConnections.open(url, user, password);
                 var statement = connection.prepareStatement("SELECT email_message_id, guest_name, check_in, check_out FROM BOOKING_REQUEST WHERE booking_ref = ?")) {
                statement.setString(1, reference);
                try (var rows = statement.executeQuery()) {
                    assertTrue(rows.next());
                    assertEquals(message, rows.getString(1));
                    assertEquals("Synthetic Test", rows.getString(2));
                    assertEquals(booking.checkIn(), rows.getDate(3).toLocalDate());
                    assertEquals(booking.checkOut(), rows.getDate(4).toLocalDate());
                    assertFalse(rows.next());
                }
            }
        } catch (SQLException e) {
            fail("Database integration test failed (SQL details withheld). Exception type: " + e.getClass().getName());
        } finally {
            if (cleanupNeeded) {
                try (var connection = JdbcConnections.open(url, user, password)) {
                    requireTestDatabase(connection);
                    try (var delete = connection.prepareStatement("DELETE FROM BOOKING_REQUEST WHERE booking_ref = ? AND email_message_id = ?")) {
                        delete.setString(1, reference);
                        delete.setString(2, message);
                        delete.executeUpdate();
                    }
                } catch (SQLException e) {
                    fail("Synthetic booking cleanup failed (SQL details withheld). Exception type: " + e.getClass().getName());
                }
            }
        }
    }

    private static void requireTestDatabase(Connection connection) throws SQLException {
        try (var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT DB_NAME()")) {
            assertTrue(rows.next(), "Database name was not returned");
            assertEquals("HOTEL_DB_TEST", rows.getString(1), "Unexpected database");
        }
    }
}
