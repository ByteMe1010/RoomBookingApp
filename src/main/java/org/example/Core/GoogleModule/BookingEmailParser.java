package org.example.Core.GoogleModule;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Parser for the documented simulated format; real provider formats require verification. */
public final class BookingEmailParser {
    public Optional<BookingNotice> parse(String subject, String body) {
        if (subject == null || body == null || !subject.trim().equalsIgnoreCase("New reservation")) return Optional.empty();
        Map<String, String> fields = new HashMap<>();
        for (String line : body.split("\\R")) {
            int separator = line.indexOf(':');
            if (separator > 0) {
                String key = line.substring(0, separator).trim().toLowerCase(Locale.ROOT);
                if (fields.putIfAbsent(key, line.substring(separator + 1).trim()) != null) return Optional.empty();
            }
        }
        try {
            String reference = required(fields, "reservation id", 50);
            String name = required(fields, "guest name", 100);
            LocalDate in = LocalDate.parse(required(fields, "check-in", 10));
            LocalDate out = LocalDate.parse(required(fields, "check-out", 10));
            String room = fields.get("room");
            String phone = fields.get("phone");
            Integer rooms = fields.containsKey("rooms") ? Integer.valueOf(fields.get("rooms")) : null;
            if (!out.isAfter(in) || (rooms != null && rooms < 1) || (phone != null && phone.length() > 30)) return Optional.empty();
            return Optional.of(new BookingNotice(reference, name, in, out, room, rooms, phone));
        } catch (IllegalArgumentException ex) { return Optional.empty(); }
    }
    private static String required(Map<String, String> fields, String key, int max) {
        String value = fields.get(key);
        if (value == null || value.isBlank() || value.length() > max) throw new IllegalArgumentException();
        return value;
    }
}
