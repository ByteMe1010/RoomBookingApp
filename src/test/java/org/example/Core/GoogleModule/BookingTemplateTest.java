package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class BookingTemplateTest {
    private final BookingEmailParser parser = new BookingEmailParser();
    static String fixture(String name) throws Exception {
        try (var input = BookingTemplateTest.class.getResourceAsStream("/booking-emails/" + name)) {
            assertNotNull(input);
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
    @Test void textAndHtmlFixturesProduceSameFields() throws Exception {
        var plain = parser.parse("Booking.com: New reservation", fixture("new-booking.txt")).orElseThrow();
        assertEquals(plain, parser.parseHtml("New booking", fixture("new-booking.html")).orElseThrow());
        assertEquals("9876543210", plain.reference());
        assertEquals("Synthetic & Example", plain.guestName());
        assertEquals(LocalDate.of(2026, 11, 18), plain.checkIn());
        assertEquals(LocalDate.of(2026, 11, 21), plain.checkOut());
        assertEquals("Deluxe Double", plain.room());
        assertEquals(2, plain.rooms());
        assertEquals("+00 000 0000", plain.phone());
    }
    @Test void supportsStrictDatesAndRejectsAmbiguousOrImpossibleDates() throws Exception {
        String body = fixture("new-booking.txt");
        for (String date : new String[]{"2026-11-18", "18/11/2026", "Wednesday, 18 November 2026"})
            assertTrue(parser.parse("New reservation", body.replace("18 November 2026", date)).isPresent(), date);
        for (String date : new String[]{"02/03/2026", "2026-02-30", "18 November", "Monday, 18 November 2026", "nonsense"})
            assertTrue(parser.parse("New reservation", body.replace("18 November 2026", date)).isEmpty(), date);
    }
    @Test void missingRequiredOrConflictingFieldsAreRejected() throws Exception {
        String body = fixture("new-booking.txt");
        for (String label : new String[]{"Reservation number", "Guest name", "Check-in", "Check-out"})
            assertTrue(parser.parse("New reservation", body.replaceAll("(?m)^" + label + ":.*\\R?", "")).isEmpty(), label);
        assertTrue(parser.parse("New reservation", body + "\nBooking reference: DIFFERENT").isEmpty());
        assertTrue(parser.parse("New reservation", body.replace("rooms: 2", "rooms: 0")).isEmpty());
        assertTrue(parser.parseHtml("New reservation", "<table><tr><td>Guest name</td></tr></table>").isEmpty());
        assertTrue(parser.parse(null, body).isEmpty());
        assertTrue(parser.parse("New reservation", null).isEmpty());
    }
    @Test void rejectsNonNewNotificationsEvenWithValidFields() throws Exception {
        String body = fixture("new-booking.txt");
        for (String subject : new String[]{"Reservation cancelled", "New reservation - modified", "Invoice", "Re: New reservation"})
            assertTrue(parser.parse(subject, body).isEmpty());
        for (String status : new String[]{"Reservation cancelled", "Booking modification", "Booking updated"})
            assertTrue(parser.parse("New reservation", body + "\n" + status).isEmpty());
    }
}
