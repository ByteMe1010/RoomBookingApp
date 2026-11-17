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
    @Test void testModeNeverInitializesOrCallsDatabaseSink() throws Exception {
        var sink = GmailModuleMain.selectSink(true, () -> { throw new AssertionError("Database accessed"); });
        sink.accept("test", SimulatedBookingSink.FIXTURE);
    }
    private Message message(String body) {
        return new Message().setId("dummy-message").setPayload(new MessagePart().setMimeType("text/plain")
                .setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue("New reservation")))
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
            var service = new GmailPollingService(null, new SimulatedBookingSink(), "test@example.com", true);
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
