package org.example.Core.GoogleModule;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class GuiSimulationTest {
    private static final String BLOB = "Subject: New reservation\nReservation ID: SIM-123\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03\nRoom: Double\nRooms: 2\nPhone: 12345";

    @Test void validAndDuplicateSimulationUsesOnlyMemory() throws Exception {
        var service = new GmailIntegrationService(null, null, null, null, true);
        try {
            var result = service.simulateMessage(BLOB);
            assertTrue(result.succeeded());
            assertEquals(new BookingNotice("SIM-123", "Sample Guest", LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 3), "Double", 2, "12345"), result.booking());
            var duplicate = service.simulateMessage(BLOB);
            assertFalse(duplicate.succeeded());
            assertNull(duplicate.booking());
        } finally { service.shutdown(); }
    }

    @Test void malformedCancelledAndEmptyMessagesDoNotSucceed() throws Exception {
        var service = new GmailIntegrationService(null, null, null, null, true);
        try {
            assertFalse(service.simulateMessage("").succeeded());
            assertFalse(service.simulateMessage(BLOB.replace("2027-01-03", "invalid")).succeeded());
            assertFalse(service.simulateMessage(BLOB.replace("New reservation", "Cancelled reservation")).succeeded());
            assertTrue(service.simulateMessage(BLOB).succeeded());
        } finally { service.shutdown(); }
    }

    @Test void constructedMessageHasRequiredHeadersAndRealFilteringStillApplies() throws Exception {
        String sender = "simulation@example.invalid";
        var message = GmailIntegrationService.buildSimulatedMessage(BLOB, sender);
        assertNotNull(message.getInternalDate());
        assertEquals("text/plain", message.getPayload().getMimeType());
        assertEquals("New reservation", message.getPayload().getHeaders().get(0).getValue());
        assertEquals(sender, message.getPayload().getHeaders().get(1).getValue());
        var service = new GmailPollingService(null, (id, booking) -> {}, sender, false, Instant.ofEpochMilli(message.getInternalDate() + 1));
        assertFalse(service.processMessage(message));
        var senderCheck = new GmailPollingService(null, (id, booking) -> {}, "other@example.invalid", false, Instant.EPOCH);
        assertFalse(senderCheck.processMessage(message));
    }
}
