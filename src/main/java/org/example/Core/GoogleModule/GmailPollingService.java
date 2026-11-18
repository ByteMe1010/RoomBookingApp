package org.example.Core.GoogleModule;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class GmailPollingService {
    private static final Logger LOG = Logger.getLogger(GmailPollingService.class.getName());
    private final Gmail gmail;
    @FunctionalInterface
    public interface BookingSink { void accept(String messageId, BookingNotice booking) throws Exception; }
    private final BookingSink sink;
    private final boolean testMode;
    private final String query;
    private final Instant cutoff;
    private final String referenceFilter;
    private final String sender;
    private final BookingEmailParser parser = new BookingEmailParser();
    private final Set<String> processedMessages = new HashSet<>();
    private final Set<String> processedReservations = new HashSet<>();
    public GmailPollingService(Gmail gmail, BookingSink sink, String sender, boolean testMode, Instant cutoff) {
        this(gmail, sink, sender, testMode, cutoff, null);
    }
    GmailPollingService(Gmail gmail, BookingSink sink, String sender, boolean testMode, Instant cutoff, String referenceFilter) {
        if (!sender.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+") || cutoff == null) throw new IllegalArgumentException("Configure an exact sender address and cutoff");
        this.gmail = gmail; this.sink = sink; this.testMode = testMode; this.cutoff = cutoff;
        this.referenceFilter = referenceFilter;
        this.sender = sender;
        this.query = "from:" + sender + " after:" + (cutoff.getEpochSecond() - 1);
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
        if (message == null || message.getId() == null || message.getInternalDate() == null
                || message.getInternalDate() < cutoff.toEpochMilli() || message.getPayload() == null
                || message.getPayload().getHeaders() == null) return false;
        if (processedMessages.contains(message.getId())) return false;
        if (!testMode || referenceFilter != null) {
            var from = message.getPayload().getHeaders().stream()
                    .filter(h -> "From".equalsIgnoreCase(h.getName())).toList();
            if (from.size() != 1 || from.get(0).getValue() == null) return false;
            String address = from.get(0).getValue().trim();
            var mailbox = java.util.regex.Pattern.compile("^[^<>]*<([^<>]+)>$").matcher(address);
            if (mailbox.matches()) address = mailbox.group(1).trim();
            if (!sender.equalsIgnoreCase(address)) return false;
        }
        String subject = message.getPayload().getHeaders().stream().filter(h -> "subject".equalsIgnoreCase(h.getName())).map(h -> h.getValue()).findFirst().orElse("");
        var bodies = new java.util.ArrayList<String>();
        collectBodies(message.getPayload(), bodies);
        var booking = parser.parseAlternatives(subject, bodies);
        if (booking.isPresent()) {
            onParsed.run();
            if (referenceFilter != null && !referenceFilter.equals(booking.get().reference())) return false;
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
    private void collectBodies(MessagePart part, java.util.List<String> bodies) {
        if (part == null || (part.getFilename() != null && !part.getFilename().isBlank())) return;
        if (part.getHeaders() != null && part.getHeaders().stream().anyMatch(h ->
                "Content-Disposition".equalsIgnoreCase(h.getName()) && h.getValue() != null
                        && h.getValue().toLowerCase(Locale.ROOT).startsWith("attachment"))) return;
        if (("text/plain".equalsIgnoreCase(part.getMimeType()) || "text/html".equalsIgnoreCase(part.getMimeType()))
                && part.getBody() != null && part.getBody().getData() != null) {
            try {
                String charset = "UTF-8";
                if (part.getHeaders() != null) for (var header : part.getHeaders()) {
                    if ("Content-Type".equalsIgnoreCase(header.getName()) && header.getValue() != null) {
                        var matcher = java.util.regex.Pattern.compile("(?i)charset\\s*=\\s*\"?([^;\\s\"]+)").matcher(header.getValue());
                        if (matcher.find()) charset = matcher.group(1);
                    }
                }
                String decoded = java.nio.charset.Charset.forName(charset).newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(Base64.getUrlDecoder().decode(part.getBody().getData()))).toString();
                bodies.add("text/html".equalsIgnoreCase(part.getMimeType()) ? BookingEmailParser.htmlText(decoded) : decoded);
            } catch (IllegalArgumentException | CharacterCodingException ex) { /* Unsupported or malformed body is not parsed. */ }
        }
        if (part.getMimeType() != null && part.getMimeType().toLowerCase(Locale.ROOT).startsWith("multipart/")
                && part.getParts() != null) for (MessagePart child : part.getParts()) collectBodies(child, bodies);
    }
}
