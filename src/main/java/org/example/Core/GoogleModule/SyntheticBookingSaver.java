package org.example.Core.GoogleModule;

import java.time.Instant;

/** Validates a synthetic message before handing it to the guarded transactional repository. */
final class SyntheticBookingSaver {
    interface Repository {
        boolean isTestDatabase() throws Exception;
        boolean saveIfNew(String messageId, BookingNotice booking) throws Exception;
    }
    enum Result { INSERTED, DUPLICATE, REJECTED, FAILED }
    private static final String SENDER = "simulation@example.invalid";
    private final Repository repository;

    SyntheticBookingSaver(Repository repository) { this.repository = repository; }

    boolean isAvailable() {
        try { return repository != null && repository.isTestDatabase(); }
        catch (Exception ex) { return false; }
    }

    Result save(String blob, boolean confirmed) {
        if (!confirmed || blob == null || blob.isBlank()) return Result.REJECTED;
        try {
            BookingNotice[] parsed = { null };
            GmailPollingService validator = new GmailPollingService(null, (id, booking) -> parsed[0] = booking,
                    SENDER, false, Instant.EPOCH, GmailDemoPolicy.REFERENCE);
            boolean valid = validator.processMessage(GmailIntegrationService.buildSimulatedMessage(blob, SENDER));
            if (!valid || parsed[0] == null) return Result.REJECTED;
            if (!isAvailable()) return Result.FAILED;
            return repository.saveIfNew("synthetic-viva-VIVA-SYN-002", parsed[0]) ? Result.INSERTED : Result.DUPLICATE;
        } catch (Exception ex) { return Result.FAILED; }
    }
}
