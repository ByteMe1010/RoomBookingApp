package org.example.Core.GoogleModule;

/** Synthetic demonstration restrictions only; never a production booking validation rule. */
final class GmailDemoPolicy {
    static final String REFERENCE = "VIVA-SYN-002";
    private GmailDemoPolicy() { }

    static String requireReference(String writeOptIn, String reference) {
        if (!"true".equalsIgnoreCase(writeOptIn) || !REFERENCE.equals(reference))
            throw new IllegalArgumentException("Controlled database mode requires explicit write opt-in and the exact synthetic reference");
        return reference;
    }
}
