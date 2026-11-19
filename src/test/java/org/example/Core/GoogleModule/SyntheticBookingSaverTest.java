package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class SyntheticBookingSaverTest {
    private static final String VALID = "Subject: New reservation\nReservation ID: VIVA-SYN-002\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03";

    private static final class FakeRepository implements SyntheticBookingSaver.Repository {
        boolean testDatabase = true, inserted = true, fail;
        int calls;
        BookingNotice booking;
        public boolean isTestDatabase() throws Exception { if (fail) throw new SQLException(); return testDatabase; }
        public boolean saveIfNew(String id, BookingNotice value) throws Exception {
            calls++; booking = value;
            if (fail) throw new SQLException();
            return inserted;
        }
    }

    @Test void insertAndDuplicateAreReportedAccurately() {
        var repo = new FakeRepository(); var saver = new SyntheticBookingSaver(repo);
        assertEquals(SyntheticBookingSaver.Result.INSERTED, saver.save(VALID, true));
        assertEquals("VIVA-SYN-002", repo.booking.reference());
        repo.inserted = false;
        assertEquals(SyntheticBookingSaver.Result.DUPLICATE, saver.save(VALID, true));
        assertEquals(2, repo.calls);
    }

    @Test void confirmationValidationAndReferenceGuardPreventWrites() {
        var repo = new FakeRepository(); var saver = new SyntheticBookingSaver(repo);
        assertEquals(SyntheticBookingSaver.Result.REJECTED, saver.save(VALID, false));
        assertEquals(SyntheticBookingSaver.Result.REJECTED, saver.save(VALID.replace("VIVA-SYN-002", "OTHER"), true));
        assertEquals(SyntheticBookingSaver.Result.REJECTED, saver.save(VALID.replace("New reservation", "Cancelled reservation"), true));
        assertEquals(SyntheticBookingSaver.Result.REJECTED, saver.save("invalid", true));
        assertEquals(0, repo.calls);
    }

    @Test void unavailableOrFailedDatabaseCannotBeSaved() {
        var repo = new FakeRepository(); var saver = new SyntheticBookingSaver(repo);
        repo.testDatabase = false;
        assertFalse(saver.isAvailable());
        assertEquals(SyntheticBookingSaver.Result.FAILED, saver.save(VALID, true));
        assertEquals(0, repo.calls);
        repo.fail = true;
        assertFalse(saver.isAvailable());
        assertEquals(SyntheticBookingSaver.Result.FAILED, saver.save(VALID, true));
        assertEquals(0, repo.calls);
    }
}
