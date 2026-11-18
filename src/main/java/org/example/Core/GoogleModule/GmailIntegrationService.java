package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Integration facade for the Gmail module. Runs Gmail polling / simulation on a
 * background executor and persists results through the application's DatabaseModule.
 * Reuses the existing module classes without reimplementing their logic.
 */
public final class GmailIntegrationService {
    private static final Logger LOG = Logger.getLogger(GmailIntegrationService.class.getName());

    private final DatabaseModule db;
    private final Path clientFile;
    private final Path tokenDir;
    private final String sender;
    private final boolean testMode;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "gmail-integration");
        t.setDaemon(true);
        return t;
    });

    private final Object lock = new Object();
    private Gmail gmail;
    private GmailPollingService service;          // real client (for Check Gmail)
    private GmailPollingService simService;      // null client (for Simulate)

    public GmailIntegrationService(DatabaseModule db, Path clientFile, Path tokenDir, String sender, boolean testMode) {
        this.db = db;
        this.clientFile = clientFile;
        this.tokenDir = tokenDir;
        this.sender = sender;
        this.testMode = testMode;
    }

    /** Builds a service from environment variables; returns null if not configured. */
    public static GmailIntegrationService fromEnvironment(DatabaseModule db) {
        String client = System.getenv("GMAIL_CLIENT_JSON");
        String tokens = System.getenv("GMAIL_TOKEN_DIR");
        String sender = System.getenv("GMAIL_BOOKING_SENDER");
        if (client == null || client.isBlank() || tokens == null || tokens.isBlank()
                || sender == null || sender.isBlank()) {
            LOG.info("Gmail integration not configured (missing GMAIL_CLIENT_JSON/GMAIL_TOKEN_DIR/GMAIL_BOOKING_SENDER).");
            return null;
        }
        boolean testMode = "true".equalsIgnoreCase(System.getenv().getOrDefault("GMAIL_TEST_MODE", "false"));
        return new GmailIntegrationService(db, Path.of(client), Path.of(tokens), sender, testMode);
    }

    public boolean isConfigured() {
        return clientFile != null && tokenDir != null && sender != null;
    }

    public String missingConfigMessage() {
        return "Gmail is not configured. Set GMAIL_CLIENT_JSON, GMAIL_TOKEN_DIR and GMAIL_BOOKING_SENDER.";
    }

    /** Lazily creates the real Gmail client + persistent polling service. */
    private GmailPollingService ensureService() throws Exception {
        synchronized (lock) {
            if (service != null) return service;
            if (gmail == null) gmail = new GmailAuthentication().connect(clientFile, tokenDir);
            service = new GmailPollingService(gmail, new DatabaseBookingSink(db), sender, false);
            return service;
        }
    }

    /** Runs a real Gmail poll on a background thread. */
    public void checkGmail(Runnable onSuccess, java.util.function.Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                GmailPollingService svc = ensureService();
                svc.poll();
                if (onSuccess != null) javafx.application.Platform.runLater(onSuccess);
            } catch (Throwable t) {
                LOG.warning("Gmail check failed: " + t.getClass().getName());
                if (onError != null) javafx.application.Platform.runLater(() -> onError.accept(t));
            }
        });
    }

    /** Runs the simulated flow for a pasted message blob on a background thread. */
    public void simulate(String blob, Runnable onSuccess, java.util.function.Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                if (blob == null || blob.isBlank()) throw new IllegalArgumentException("Message blob is empty");
                Message message = buildSimulatedMessage(blob);
                GmailPollingService svc;
                synchronized (lock) {
                    if (simService == null) simService = new GmailPollingService(null, new DatabaseBookingSink(db), sender, false);
                    svc = simService;
                }
                boolean handled = svc.processMessage(message);
                if (!handled) LOG.info("Simulated message was not a new valid booking (duplicate or invalid).");
                if (onSuccess != null) javafx.application.Platform.runLater(onSuccess);
            } catch (Throwable t) {
                LOG.warning("Gmail simulate failed: " + t.getClass().getName());
                if (onError != null) javafx.application.Platform.runLater(() -> onError.accept(t));
            }
        });
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    /** Builds a Gmail Message from a pasted blob, extracting an optional Subject header. */
    static Message buildSimulatedMessage(String blob) {
        String subject = "New reservation";
        StringBuilder body = new StringBuilder();
        for (String line : blob.split("\\R", -1)) {
            String trimmed = line.trim();
            if (trimmed.toLowerCase().startsWith("subject:")) {
                subject = trimmed.substring("subject:".length()).trim();
                if (subject.isEmpty()) subject = "New reservation";
            } else {
                body.append(line).append('\n');
            }
        }
        String bodyText = body.toString().trim();
        if (bodyText.isEmpty()) bodyText = blob.trim();
        String encoded = Base64.getUrlEncoder().encodeToString(bodyText.getBytes(StandardCharsets.UTF_8));
        return new Message().setId("sim-" + System.nanoTime())
                .setPayload(new MessagePart().setMimeType("text/plain")
                        .setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue(subject)))
                        .setBody(new MessagePartBody().setData(encoded)));
    }
}