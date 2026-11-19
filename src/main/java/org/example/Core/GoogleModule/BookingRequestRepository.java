package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.JdbcConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Logger;

/** Uses only BOOKING_REQUEST columns common to both checked-in schemas. */
public final class BookingRequestRepository {
    private static final Logger LOG = Logger.getLogger(BookingRequestRepository.class.getName());
    private final String url, user, password;
    private final boolean requireTestDatabase;
    public BookingRequestRepository(String url, String user, String password) {
        this(url, user, password, false);
    }
    public BookingRequestRepository(String url, String user, String password, boolean requireTestDatabase) {
        this.url = url; this.user = user; this.password = password;
        this.requireTestDatabase = requireTestDatabase;
    }
    public boolean isTestDatabase() throws SQLException {
        try (Connection connection = JdbcConnections.open(url, user, password)) {
            verifyTestDatabase(connection);
            return true;
        }
    }
    public boolean saveIfNew(String messageId, BookingNotice booking) throws SQLException {
        if (requireTestDatabase && !GmailDemoPolicy.REFERENCE.equals(booking.reference()))
            throw new SQLException("Controlled database writes require the synthetic reservation reference");
        LOG.info("Attempting database connection for a parsed booking.");
        try (Connection c = JdbcConnections.open(url, user, password)) {
            LOG.info("Database connection established.");
            if (requireTestDatabase) verifyTestDatabase(c);
            c.setAutoCommit(false);
            c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            try {
                // Serializable range locks also protect concurrent pollers when no unique booking_ref index exists.
                try (PreparedStatement check = c.prepareStatement("SELECT request_id FROM BOOKING_REQUEST WITH (UPDLOCK, HOLDLOCK) WHERE email_message_id = ? OR booking_ref = ?")) {
                    check.setString(1, messageId); check.setString(2, booking.reference());
                    try (ResultSet rows = check.executeQuery()) { if (rows.next()) {
                        c.commit();
                        if (requireTestDatabase) LOG.info("Controlled synthetic reservation already exists; insert skipped.");
                        return false;
                    } }
                }
                try (PreparedStatement insert = c.prepareStatement("INSERT INTO BOOKING_REQUEST (email_message_id, booking_ref, guest_name, guest_phone, check_in, check_out, rooms_wanted) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    insert.setString(1, messageId); insert.setString(2, booking.reference()); insert.setNString(3, booking.guestName());
                    if (booking.phone() == null) insert.setNull(4, Types.VARCHAR); else insert.setString(4, booking.phone());
                    insert.setObject(5, booking.checkIn()); insert.setObject(6, booking.checkOut());
                    insert.setInt(7, booking.rooms() == null ? 1 : booking.rooms()); insert.executeUpdate();
                }
                c.commit();
                if (requireTestDatabase) LOG.info("Controlled synthetic reservation committed to HOTEL_DB_TEST.");
                return true;
            } catch (SQLException ex) { c.rollback(); throw ex; }
        }
    }
    static void verifyTestDatabase(Connection connection) throws SQLException {
        try (var statement = connection.createStatement(); var rows = statement.executeQuery("SELECT DB_NAME()")) {
            if (!rows.next() || !"HOTEL_DB_TEST".equals(rows.getString(1)))
                throw new SQLException("Connected database is not HOTEL_DB_TEST");
        }
    }
}
