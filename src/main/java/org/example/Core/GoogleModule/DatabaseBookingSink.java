package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import org.example.Core.DatabaseModule.model.BookingRequest;
import org.example.Core.DatabaseModule.model.Customer;

/** Booking sink backed by the application's DatabaseModule instead of a direct JDBC connection. */
final class DatabaseBookingSink implements GmailPollingService.BookingSink {
    private final DatabaseModule db;

    DatabaseBookingSink(DatabaseModule db) {
        this.db = db;
    }

    @Override
    public void accept(String messageId, BookingNotice booking) throws Exception {
        // Dedup by message id and booking ref (mirrors BookingRequestRepository locking intent).
        if (db.getBookingRequestByEmailMessageId(messageId) != null) return;
        if (booking.reference() != null && !booking.reference().isBlank()
                && db.getBookingRequestByBookingRef(booking.reference()) != null) return;

        // Customer upsert by phone (fall back to name-only when no phone).
        Customer customer = null;
        if (booking.phone() != null && !booking.phone().isBlank()) {
            customer = db.getCustomerByPhone(booking.phone());
        }
        if (customer == null) {
            customer = new Customer(booking.guestName(), booking.phone());
            db.saveCustomer(customer);
        }

        BookingRequest request = new BookingRequest(
                messageId,
                booking.reference(),
                booking.guestName(),
                booking.phone(),
                booking.checkIn(),
                booking.checkOut(),
                null,
                null,
                booking.room() == null ? null : "Room: " + booking.room()
        );
        request.setCustomerId(customer.getCustomerId());
        request.setRoomsWanted(booking.rooms() == null ? 1 : booking.rooms());
        db.saveBookingRequest(request);
    }
}