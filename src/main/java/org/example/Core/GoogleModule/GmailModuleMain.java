package org.example.Core.GoogleModule;

import org.example.Core.DatabaseModule.JdbcConnections;

import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Supplier;

public final class GmailModuleMain {
    static final String CONTROLLED_REFERENCE = GmailDemoPolicy.REFERENCE;
    private static final Logger LOG = Logger.getLogger(GmailModuleMain.class.getName());
    // Third-party exception messages may contain HTTP bodies, credentials, or emails.
    // Preserve stack frames and exception types without passing those messages to the logger.
    private static Throwable safeDiagnostic(Throwable error) {
        String message = error.getMessage();
        if (!"Token directory must be outside the working directory".equals(message)
                && !"Configure an exact sender address".equals(message)) {
            message = "Exception message withheld because it may contain sensitive data";
        }
        Exception safe = new Exception(error.getClass().getName() + ": " + message);
        safe.setStackTrace(error.getStackTrace());
        return safe;
    }
    private static String env(String name) { String value = System.getenv(name); if (value == null || value.isBlank()) throw new IllegalArgumentException("Missing environment variable " + name); return value; }
    static GmailPollingService.BookingSink selectSink(boolean testMode, Supplier<GmailPollingService.BookingSink> databaseSink) {
        return testMode ? new SimulatedBookingSink() : databaseSink.get();
    }
    static String controlledReference(boolean testMode, String writeOptIn, String reference) {
        if (testMode) return null;
        return GmailDemoPolicy.requireReference(writeOptIn, reference);
    }
    public static void main(String[] args) {
        int minutes = Integer.parseInt(System.getenv().getOrDefault("GMAIL_POLL_MINUTES", "2"));
        if (minutes < 1 || minutes > 5) throw new IllegalArgumentException("GMAIL_POLL_MINUTES must be 1 to 5");
        Path client = Path.of(env("GMAIL_CLIENT_JSON")); Path tokens = Path.of(env("GMAIL_TOKEN_DIR"));
        String sender = env("GMAIL_BOOKING_SENDER");
        Instant cutoff = Instant.parse(env("GMAIL_INITIAL_CUTOFF"));
        boolean testMode = "true".equalsIgnoreCase(System.getenv().getOrDefault("GMAIL_TEST_MODE", "false"));
        String reference = controlledReference(testMode, System.getenv("GMAIL_ALLOW_TEST_DB_WRITES"), System.getenv("GMAIL_RESERVATION_REFERENCE"));
        if (testMode) LOG.info("Gmail test mode enabled; database is disabled.");
        GmailPollingService.BookingSink sink = selectSink(testMode, () -> {
            String url = env("BOOKING_JDBC_URL");
            boolean integrated = JdbcConnections.usesIntegratedSecurity(url);
            var repository = new BookingRequestRepository(url, integrated ? null : env("BOOKING_DB_USER"), integrated ? null : env("BOOKING_DB_PASSWORD"), true);
            return repository::saveIfNew;
        });
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdownNow));
        LOG.info("Gmail polling scheduler started; interval is " + minutes + " minute(s).");
        scheduler.scheduleWithFixedDelay(new Runnable() {
            private GmailPollingService service;
            public void run() {
                try {
                    if (service == null) service = new GmailPollingService(new GmailAuthentication().connect(client, tokens), sink, sender, testMode, cutoff, reference);
                    service.poll();
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Gmail polling or authentication failed; retrying on the next poll.", safeDiagnostic(ex));
                    var seen = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<Throwable, Boolean>());
                    seen.add(ex);
                    for (Throwable cause = ex.getCause(); cause != null && seen.add(cause); cause = cause.getCause()) {
                        LOG.log(Level.WARNING, "Underlying cause:", safeDiagnostic(cause));
                    }
                }
            }
        }, 0, minutes, TimeUnit.MINUTES);
    }
}
