package org.example.Core.GoogleModule;

import java.time.LocalDate;
import java.util.logging.Logger;

/** Only the exact public fixture may be displayed, so real guest data cannot be logged. */
final class SimulatedBookingSink implements GmailPollingService.BookingSink {
    private static final Logger LOG = Logger.getLogger(SimulatedBookingSink.class.getName());
    static final BookingNotice FIXTURE = new BookingNotice("SIM-123", "Sample Guest",
            LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 3), "Double", 2, "12345");

    public void accept(String messageId, BookingNotice booking) {
        if (!FIXTURE.equals(booking)) return;
        LOG.info("Simulated reservation parsed: reservation ID=" + booking.reference()
                + ", guest=" + booking.guestName() + ", check-in=" + booking.checkIn()
                + ", check-out=" + booking.checkOut() + ", room=" + booking.room()
                + ", rooms=" + booking.rooms() + ", phone=" + booking.phone());
    }
}
