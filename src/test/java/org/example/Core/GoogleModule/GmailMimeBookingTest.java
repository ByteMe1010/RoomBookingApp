package org.example.Core.GoogleModule;

import com.google.api.services.gmail.model.*;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class GmailMimeBookingTest {
    private MessagePart body(String type, String content) {
        return new MessagePart().setMimeType(type).setBody(new MessagePartBody().setData(
                Base64.getUrlEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8))));
    }
    private Message message(MessagePart part) {
        part.setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue("New booking")));
        return new Message().setId("synthetic").setInternalDate(1000L).setPayload(part);
    }
    @Test void acceptsHtmlOnlyAndNestedMultipartAlternatives() throws Exception {
        for (boolean multipart : new boolean[]{false, true}) {
            AtomicReference<BookingNotice> saved = new AtomicReference<>();
            var service = new GmailPollingService(null, (id, booking) -> saved.set(booking), "test@example.com", true, Instant.EPOCH);
            var html = body("text/html", BookingTemplateTest.fixture("new-booking.html"));
            var payload = multipart ? new MessagePart().setMimeType("multipart/mixed").setParts(List.of(
                    new MessagePart().setMimeType("multipart/alternative").setParts(List.of(
                            body("text/plain", BookingTemplateTest.fixture("new-booking.txt")), html)),
                    body("text/plain", "Booking cancelled").setFilename("attachment.txt"))) : html;
            assertTrue(service.processMessage(message(payload)));
            assertEquals("9876543210", saved.get().reference());
            assertEquals(2, saved.get().rooms());
        }
    }
    @Test void rejectsConflictingAlternativesAndDoesNotParseAttachments() throws Exception {
        var service = new GmailPollingService(null, (id, booking) -> fail("Unexpected sink call"), "test@example.com", true, Instant.EPOCH);
        String plain = BookingTemplateTest.fixture("new-booking.txt");
        var alternatives = new MessagePart().setMimeType("multipart/alternative").setParts(List.of(
                body("text/plain", plain), body("text/html", BookingTemplateTest.fixture("new-booking.html").replace("9876543210", "OTHER"))));
        assertFalse(service.processMessage(message(alternatives)));
        assertFalse(service.processMessage(message(body("text/plain", plain).setFilename("booking.txt"))));
        assertFalse(service.processMessage(message(body("text/plain", plain).setBody(new MessagePartBody().setData("%%%")))));
        assertFalse(service.processMessage(message(new MessagePart().setMimeType("multipart/alternative").setParts(List.of(
                body("text/plain", plain), body("text/html", "<p>Booking cancelled</p>"))))));
    }
}
