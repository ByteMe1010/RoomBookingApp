package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BookingEmailParserTest {
    private final BookingEmailParser parser = new BookingEmailParser();
    private final String email = "Reservation ID: SIM-123\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03\nRoom: Double\nRooms: 2\nPhone: 12345";
    @Test void parsesSimulatedBooking() {
        BookingNotice result = parser.parse("New reservation", email).orElseThrow();
        assertEquals("SIM-123", result.reference());
        assertEquals("Sample Guest", result.guestName());
        assertEquals(LocalDate.of(2027, 1, 1), result.checkIn());
        assertEquals(LocalDate.of(2027, 1, 3), result.checkOut());
        assertEquals("Double", result.room());
        assertEquals(2, result.rooms());
    }
    @Test void rejectsChangesAndIncompleteOrAmbiguousMessages() {
        assertTrue(parser.parse("Reservation cancelled", email).isEmpty());
        assertTrue(parser.parse("New reservation", email.replace("2027-01-03", "2026-01-01")).isEmpty());
        assertTrue(parser.parse("New reservation", email + "\nReservation ID: OTHER").isEmpty());
        assertTrue(parser.parse("New reservation", "Guest name: Sample Guest").isEmpty());
    }
}
