package org.example.Core.GoogleModule;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class GmailPollingService {
    private static final Logger LOG = Logger.getLogger(GmailPollingService.class.getName());
    private final Gmail gmail;
    @FunctionalInterface
    interface BookingSink { void accept(String messageId, BookingNotice booking) throws Exception; }
    private final BookingSink sink;
    private final boolean testMode;
    private final String query;
    private final BookingEmailParser parser = new BookingEmailParser();
    private final Set<String> processedMessages = new HashSet<>();
    private final Set<String> processedReservations = new HashSet<>();
    GmailPollingService(Gmail gmail, BookingSink sink, String sender, boolean testMode) {
        if (!sender.matches("[A-Za-z0-9._%+@-]+") || !sender.contains("@")) throw new IllegalArgumentException("Configure an exact sender address");
        this.gmail = gmail; this.sink = sink; this.testMode = testMode; this.query = "from:" + sender;
    }
    public void poll() throws IOException {
        LOG.info("Gmail polling started.");
        int failures = 0, found = 0;
        int[] parsed = {0};
        String page = null;
        try {
        do {
            var response = gmail.users().messages().list("me").setQ(query).setPageToken(page).execute();
            if (response.getMessages() != null) found += response.getMessages().size();
            if (response.getMessages() != null) for (Message item : response.getMessages()) {
                try {
                    Message message = gmail.users().messages().get("me", item.getId()).setFormat("full").execute();
                    processMessage(message, () -> parsed[0]++);
                } catch (Exception ex) { failures++; LOG.warning("A message could not be processed; it will be retried on a later poll. Exception type: " + ex.getClass().getName()); }
            }
            page = response.getNextPageToken();
        } while (page != null);
        if (failures == 0) LOG.info("Gmail polling completed successfully.");
        else LOG.warning("Gmail polling completed with message processing failures: " + failures);
        } finally {
            LOG.info("Gmail polling cycle totals: messages found=" + found + ", valid bookings parsed=" + parsed[0] + ".");
        }
    }
    boolean processMessage(Message message) throws Exception {
        return processMessage(message, () -> { });
    }
    private boolean processMessage(Message message, Runnable onParsed) throws Exception {
        if (processedMessages.contains(message.getId())) return false;
        String subject = message.getPayload().getHeaders().stream().filter(h -> "subject".equalsIgnoreCase(h.getName())).map(h -> h.getValue()).findFirst().orElse("");
        // Test mode must never send an unmarked notification to any sink.
        if (testMode && !"New reservation".equalsIgnoreCase(subject.trim())) return false;
        var booking = parser.parse(subject, text(message.getPayload()));
        if (booking.isPresent()) {
            onParsed.run();
            if (processedReservations.contains(booking.get().reference())) {
                processedMessages.add(message.getId());
                return false;
            }
            sink.accept(message.getId(), booking.get());
            processedMessages.add(message.getId());
            processedReservations.add(booking.get().reference());
            return true;
        }
        return false;
    }
    private String text(MessagePart part) {
        if (part == null) return "";
        if ("text/plain".equalsIgnoreCase(part.getMimeType()) && part.getBody() != null && part.getBody().getData() != null)
            return new String(Base64.getUrlDecoder().decode(part.getBody().getData()), StandardCharsets.UTF_8);
        if (part.getParts() != null) for (MessagePart child : part.getParts()) { String value = text(child); if (!value.isEmpty()) return value; }
        return "";
    }
}
