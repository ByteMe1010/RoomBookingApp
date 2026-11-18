package org.example.Core.GoogleModule;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.*;

class GmailTestModeTest {
    @Test void databaseModeRequiresOptInAndExactReference() {
        String ref = GmailModuleMain.CONTROLLED_REFERENCE;
        assertNull(GmailModuleMain.controlledReference(true, null, null));
        assertThrows(IllegalArgumentException.class, () -> GmailModuleMain.controlledReference(false, null, ref));
        assertThrows(IllegalArgumentException.class, () -> GmailModuleMain.controlledReference(false, "true", "other"));
        assertThrows(IllegalArgumentException.class, () -> GmailModuleMain.controlledReference(false, "true", "BK-TEST-20261118-001"));
        assertEquals(ref, GmailModuleMain.controlledReference(false, "true", ref));
    }
    @Test void referenceFilterOnlyPassesExactMatch() throws Exception {
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        var service = new GmailPollingService(null, (id, booking) -> calls.incrementAndGet(), "test@example.com", false,
                java.time.Instant.EPOCH, GmailModuleMain.CONTROLLED_REFERENCE);
        String body = "Reservation ID: VIVA-SYN-002\nGuest name: Synthetic Test\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03";
        assertFalse(service.processMessage(message(body.replace("VIVA-SYN-002", "OTHER"))));
        assertTrue(service.processMessage(message(body)));
        assertEquals(1, calls.get());
    }
    @Test void testModeNeverInitializesOrCallsDatabaseSink() throws Exception {
        var sink = GmailModuleMain.selectSink(true, () -> { throw new AssertionError("Database accessed"); });
        sink.accept("test", SimulatedBookingSink.FIXTURE);
    }
    @Test void controlledModeRejectsWrongSenderAndBothKindsOfDuplicate() throws Exception {
        var calls = new java.util.concurrent.atomic.AtomicInteger();
        var service = new GmailPollingService(null, (id, booking) -> calls.incrementAndGet(), "test@example.com", false,
                java.time.Instant.EPOCH, GmailModuleMain.CONTROLLED_REFERENCE);
        String body = "Reservation ID: VIVA-SYN-002\nGuest name: Synthetic Test\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03";
        var wrongSender = message(body);
        wrongSender.getPayload().getHeaders().get(1).setValue("other@example.com");
        assertFalse(service.processMessage(wrongSender));
        assertTrue(service.processMessage(message(body)));
        assertFalse(service.processMessage(message(body)));
        assertFalse(service.processMessage(message(body).setId("second-message")));
        assertEquals(1, calls.get());
    }
    private Message message(String body) {
        return new Message().setId("dummy-message").setInternalDate(1000L).setPayload(new MessagePart().setMimeType("text/plain")
                .setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue("New reservation"),
                        new MessagePartHeader().setName("From").setValue("test@example.com")))
                .setBody(new MessagePartBody().setData(Base64.getUrlEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8)))));
    }
    @Test void parsesGmailMessageWithoutDatabaseAndSuppressesOtherGuestData() throws Exception {
        String fixture = "Reservation ID: SIM-123\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03\nRoom: Double\nRooms: 2\nPhone: 12345";
        Logger logger = Logger.getLogger(SimulatedBookingSink.class.getName());
        AtomicReference<String> output = new AtomicReference<>();
        Handler handler = new Handler() {
            public void publish(LogRecord record) { output.set(record.getMessage()); }
            public void flush() { }
            public void close() { }
        };
        logger.addHandler(handler);
        try {
            var service = new GmailPollingService(null, new SimulatedBookingSink(), "test@example.com", true, java.time.Instant.EPOCH);
            service.processMessage(message(fixture));
            assertNotNull(output.get());
            assertTrue(output.get().contains("Simulated reservation parsed"));
            assertTrue(output.get().contains("check-out=2027-01-03"));
            output.set(null);
            service.processMessage(message(fixture.replace("Sample Guest", "Private Person")));
            assertNull(output.get());
        } finally { logger.removeHandler(handler); }
    }
}
