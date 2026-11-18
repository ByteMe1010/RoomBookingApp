package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** Unit-only JDBC proxies: no driver connection or database write is performed. */
class ControlledDatabaseGuardTest {
    @Test void rejectsOtherReferencesBeforeOpeningConnection() {
        var repository = new BookingRequestRepository("jdbc:no-connection-allowed", null, null, true);
        var booking = new BookingNotice("OTHER", "Synthetic Test", LocalDate.of(2027, 1, 1),
                LocalDate.of(2027, 1, 3), null, 1, null);
        SQLException error = assertThrows(SQLException.class, () -> repository.saveIfNew("test", booking));
        assertEquals("Controlled database writes require the synthetic reservation reference", error.getMessage());
    }

    @Test void checksActualDatabaseNameAndFailsClosed() throws Exception {
        BookingRequestRepository.verifyTestDatabase(connection("HOTEL_DB_TEST", true));
        assertThrows(SQLException.class, () -> BookingRequestRepository.verifyTestDatabase(connection("HOTEL_DB", true)));
        assertThrows(SQLException.class, () -> BookingRequestRepository.verifyTestDatabase(connection(null, true)));
        assertThrows(SQLException.class, () -> BookingRequestRepository.verifyTestDatabase(connection(null, false)));
    }

    private Connection connection(String database, boolean hasRow) {
        ResultSet rows = (ResultSet) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ResultSet.class}, (p, m, a) -> {
            return switch (m.getName()) {
                case "next" -> hasRow;
                case "getString" -> database;
                case "close" -> null;
                default -> throw new AssertionError("Unexpected result-set operation");
            };
        });
        Statement statement = (Statement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Statement.class}, (p, m, a) -> {
            if (m.getName().equals("close")) return null;
            assertEquals("executeQuery", m.getName());
            assertEquals("SELECT DB_NAME()", a[0]);
            return rows;
        });
        return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Connection.class}, (p, m, a) -> {
            assertEquals("createStatement", m.getName());
            return statement;
        });
    }
}
