package org.example.Core.GoogleModule;

import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Supplier;

public final class GmailModuleMain {
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
    public static void main(String[] args) {
        int minutes = Integer.parseInt(System.getenv().getOrDefault("GMAIL_POLL_MINUTES", "2"));
        if (minutes < 1 || minutes > 5) throw new IllegalArgumentException("GMAIL_POLL_MINUTES must be 1 to 5");
        Path client = Path.of(env("GMAIL_CLIENT_JSON")); Path tokens = Path.of(env("GMAIL_TOKEN_DIR"));
        String sender = env("GMAIL_BOOKING_SENDER");
        boolean testMode = "true".equalsIgnoreCase(System.getenv().getOrDefault("GMAIL_TEST_MODE", "false"));
        if (testMode) LOG.info("Gmail test mode enabled; database is disabled.");
        GmailPollingService.BookingSink sink = selectSink(testMode, () -> {
            var dbModule = new org.example.Core.DatabaseModule.db.SqlServerDatabaseModule(
                    "jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB;user=java_dev;password=123456789;encrypt=true;trustServerCertificate=true;",
                    "java_dev", "123456789");
            return new DatabaseBookingSink(dbModule);
        });
        var scheduler = Executors.newSingleThreadScheduledExecutor();
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::shutdownNow));
        LOG.info("Gmail polling scheduler started; interval is " + minutes + " minute(s).");
        scheduler.scheduleWithFixedDelay(new Runnable() {
            private GmailPollingService service;
            public void run() {
                try {
                    if (service == null) service = new GmailPollingService(new GmailAuthentication().connect(client, tokens), sink, sender, testMode);
                    service.poll();
                } catch (Exception ex) {
                    LOG.log(Level.WARNING, "Gmail polling or authentication failed; retrying on the next poll.", safeDiagnostic(ex));
                    for (Throwable cause = ex.getCause(); cause != null && cause != ex; cause = cause.getCause()) {
                        LOG.log(Level.WARNING, "Underlying cause:", safeDiagnostic(cause));
                    }
                }
            }
        }, 0, minutes, TimeUnit.MINUTES);
    }
}
