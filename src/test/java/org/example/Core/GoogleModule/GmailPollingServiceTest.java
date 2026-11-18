package org.example.Core.GoogleModule;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class GmailPollingServiceTest {
    @Test void cutoffRejectsOldOrUndatedMessages() throws Exception {
        var calls = new AtomicInteger();
        var service = new GmailPollingService(null, (id, booking) -> calls.incrementAndGet(), "test@example.com", true, java.time.Instant.ofEpochMilli(2000));
        var message = new com.google.api.services.gmail.model.Message().setId("old").setInternalDate(1999L);
        assertFalse(service.processMessage(message));
        assertFalse(service.processMessage(message.setInternalDate(null)));
        assertEquals(0, calls.get());
    }
    @Test void duplicateReservationsAndRepeatedPollsAreHandledOnce() throws Exception {
        String body = "Reservation ID: SIM-123\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03";
        String encoded = Base64.getUrlEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8));
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String json = path.endsWith("/messages")
                    ? "{\"messages\":[{\"id\":\"one\"},{\"id\":\"two\"},{\"id\":\"one\"}]}"
                    : "{\"id\":\"" + path.substring(path.lastIndexOf('/') + 1)
                    + "\",\"internalDate\":\"1000\",\"payload\":{\"mimeType\":\"text/plain\",\"headers\":[{\"name\":\"Subject\",\"value\":\"New reservation\"}],\"body\":{\"data\":\"" + encoded + "\"}}}";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (var output = exchange.getResponseBody()) { output.write(bytes); }
        });
        server.start();
        try {
            Gmail gmail = new Gmail.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
                    .setRootUrl("http://127.0.0.1:" + server.getAddress().getPort() + "/").build();
            AtomicInteger calls = new AtomicInteger();
            var service = new GmailPollingService(gmail, (id, booking) -> calls.incrementAndGet(), "test@example.com", true, java.time.Instant.EPOCH);
            service.poll();
            service.poll();
            assertEquals(1, calls.get());
        } finally { server.stop(0); }
    }

    @Test void failedSinkIsRetried() throws Exception {
        String body = "Reservation ID: SIM-123\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03";
        var message = new com.google.api.services.gmail.model.Message().setId("one").setInternalDate(1000L)
                .setPayload(new com.google.api.services.gmail.model.MessagePart().setMimeType("text/plain")
                        .setHeaders(java.util.List.of(new com.google.api.services.gmail.model.MessagePartHeader().setName("Subject").setValue("New reservation")))
                        .setBody(new com.google.api.services.gmail.model.MessagePartBody().setData(Base64.getUrlEncoder().encodeToString(body.getBytes(StandardCharsets.UTF_8)))));
        AtomicInteger calls = new AtomicInteger();
        var service = new GmailPollingService(null, (id, booking) -> { if (calls.incrementAndGet() == 1) throw new Exception(); }, "test@example.com", true, java.time.Instant.EPOCH);
        assertThrows(Exception.class, () -> service.processMessage(message));
        assertTrue(service.processMessage(message));
        assertFalse(service.processMessage(message));
        assertEquals(2, calls.get());
    }
}
