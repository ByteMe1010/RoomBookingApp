package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.db.DatabaseModule;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
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
    private static final String SIMULATION_SENDER = "simulation@example.invalid";

    private final DatabaseModule db;
    private final Path clientFile;
    private final Path tokenDir;
    private final String sender;
    private final boolean testMode;
    private final java.time.Instant cutoff;
    private final String referenceFilter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "gmail-integration");
        t.setDaemon(true);
        return t;
    });

    private final Object lock = new Object();
    private Gmail gmail;
    private GmailPollingService service;          // real client (for Check Gmail)
    private GmailPollingService simService;      // null client (for Simulate)
    private BookingNotice simulatedBooking;

    public GmailIntegrationService(DatabaseModule db, Path clientFile, Path tokenDir, String sender, boolean testMode,
                                  java.time.Instant cutoff, String referenceFilter) {
        this.db = db;
        this.clientFile = clientFile;
        this.tokenDir = tokenDir;
        this.sender = sender;
        this.testMode = testMode;
        this.cutoff = (cutoff == null) ? java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS) : cutoff;
        this.referenceFilter = referenceFilter;
    }

    /** Builds a service from environment variables; returns null if not configured. */
    public static GmailIntegrationService fromEnvironment(DatabaseModule db) {
        String client = System.getenv("GMAIL_CLIENT_JSON");
        String tokens = System.getenv("GMAIL_TOKEN_DIR");
        String sender = System.getenv("GMAIL_BOOKING_SENDER");
        if (client == null || client.isBlank() || tokens == null || tokens.isBlank()
                || sender == null || sender.isBlank()) {
            LOG.info("Gmail integration not configured (missing GMAIL_CLIENT_JSON/GMAIL_TOKEN_DIR/GMAIL_BOOKING_SENDER).");
            return new GmailIntegrationService(db, null, null, "simulation@example.invalid", true);
        }
        boolean testMode = "true".equalsIgnoreCase(System.getenv().getOrDefault("GMAIL_TEST_MODE", "false"));
        java.time.Instant cutoff = null;
        String cutoffEnv = System.getenv("GMAIL_INITIAL_CUTOFF");
        if (cutoffEnv != null && !cutoffEnv.isBlank()) {
            try { cutoff = java.time.Instant.parse(cutoffEnv); } catch (Exception ignored) { }
        }
        String reference = System.getenv("GMAIL_RESERVATION_REFERENCE");
        return new GmailIntegrationService(db, Path.of(client), Path.of(tokens), sender, testMode, cutoff, reference);
    }

    /** Service that works without Gmail credentials, for the "Simulate" flow (parse + save to DB). */
    public static GmailIntegrationService forSimulate(DatabaseModule db) {
        return new GmailIntegrationService(db, null, null, "simulated@example.com", true,
                java.time.Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS), null);
    }

    public boolean isConfigured() {
        return clientFile != null && tokenDir != null && sender != null;
    }

    /** Whether the simulate flow can run (always true when a database module is available). */
    public boolean canSimulate() {
        return db != null;
    }

    public String missingConfigMessage() {
        return "Gmail is not configured. Set GMAIL_CLIENT_JSON, GMAIL_TOKEN_DIR and GMAIL_BOOKING_SENDER.";
    }

    /** Lazily creates the real Gmail client + persistent polling service. */
    private GmailPollingService ensureService() throws Exception {
        synchronized (lock) {
            if (service != null) return service;
            String cutoffValue = System.getenv("GMAIL_INITIAL_CUTOFF");
            if (cutoffValue == null || cutoffValue.isBlank()) throw new IllegalArgumentException("GMAIL_INITIAL_CUTOFF is required");
            Instant cutoff = Instant.parse(cutoffValue);
            String reference = GmailModuleMain.controlledReference(testMode, System.getenv("GMAIL_ALLOW_TEST_DB_WRITES"), System.getenv("GMAIL_RESERVATION_REFERENCE"));
            GmailPollingService.BookingSink sink = GmailModuleMain.selectSink(testMode, () -> {
                String url = System.getenv("BOOKING_JDBC_URL");
                if (url == null || url.isBlank()) throw new IllegalArgumentException("BOOKING_JDBC_URL is required");
                var repository = new BookingRequestRepository(url, System.getenv("BOOKING_DB_USER"), System.getenv("BOOKING_DB_PASSWORD"), true);
                return repository::saveIfNew;
            });
            if (gmail == null) gmail = new GmailAuthentication().connect(clientFile, tokenDir);
            service = new GmailPollingService(gmail, new DatabaseBookingSink(db), sender, false, cutoff, referenceFilter);
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
    public void simulate(String blob, java.util.function.Consumer<SimulationResult> onResult, java.util.function.Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                if (blob == null || blob.isBlank()) throw new IllegalArgumentException("Message blob is empty");
                Message message = buildSimulatedMessage(blob);
                GmailPollingService svc;
                synchronized (lock) {
                    if (simService == null) simService = new GmailPollingService(null, new DatabaseBookingSink(db), sender, false, cutoff, referenceFilter);
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

    public record SimulationResult(BookingNotice booking, String message) {
        public boolean succeeded() { return booking != null; }
    }

    public enum SaveResult { INSERTED, DUPLICATE, REJECTED, FAILED }

    private SyntheticBookingSaver testDatabaseSaver() {
        if (!(db instanceof org.example.Core.DatabaseModule.db.SqlServerDatabaseModule sql))
            return new SyntheticBookingSaver(null);
        BookingRequestRepository repository = sql.testBookingRequestRepository();
        return new SyntheticBookingSaver(new SyntheticBookingSaver.Repository() {
            public boolean isTestDatabase() throws Exception { return repository.isTestDatabase(); }
            public boolean saveIfNew(String id, BookingNotice booking) throws Exception { return repository.saveIfNew(id, booking); }
        });
    }

    public void checkTestDatabase(java.util.function.Consumer<Boolean> callback) {
        executor.submit(() -> {
            boolean available = testDatabaseSaver().isAvailable();
            javafx.application.Platform.runLater(() -> callback.accept(available));
        });
    }

    public void saveSyntheticToTestDatabase(String blob, boolean confirmed, java.util.function.Consumer<SaveResult> callback) {
        executor.submit(() -> {
            SaveResult result = SaveResult.valueOf(testDatabaseSaver().save(blob, confirmed).name());
            javafx.application.Platform.runLater(() -> callback.accept(result));
        });
    }

    /** In-memory only; serialized with the other simulation calls. */
    SimulationResult simulateMessage(String blob) throws Exception {
        synchronized (lock) {
            if (blob == null || blob.isBlank()) return new SimulationResult(null, "Enter a booking message.");
            if (simService == null) simService = new GmailPollingService(null,
                    (id, booking) -> simulatedBooking = booking, SIMULATION_SENDER, false, Instant.EPOCH);
            simulatedBooking = null;
            boolean handled = simService.processMessage(buildSimulatedMessage(blob, SIMULATION_SENDER));
            if (!handled || simulatedBooking == null)
                return new SimulationResult(null, "Message rejected: duplicate, cancelled, or invalid booking.");
            return new SimulationResult(simulatedBooking, "Booking parsed; no database changes were made.");
        }
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    /** Builds a Gmail Message from a pasted blob, extracting an optional Subject header. */
    static Message buildSimulatedMessage(String blob, String sender) {
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
        String mimeType = (bodyText.contains("<") && bodyText.contains(">")) ? "text/html" : "text/plain";
        String encoded = Base64.getUrlEncoder().encodeToString(bodyText.getBytes(StandardCharsets.UTF_8));
        return new Message().setId("sim-" + System.nanoTime())
                .setInternalDate(java.time.Instant.now().toEpochMilli())
                .setPayload(new MessagePart().setMimeType(mimeType)
                        .setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue(subject)))
                        .setBody(new MessagePartBody().setData(encoded)));
    }
}
