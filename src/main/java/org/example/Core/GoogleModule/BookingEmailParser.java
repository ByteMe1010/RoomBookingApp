package org.example.Core.GoogleModule;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.regex.Pattern;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/** Conservative labelled English templates; not validated against real provider emails. */
public final class BookingEmailParser {
    private static final Pattern EXCLUDED = Pattern.compile(
            "(?i)\\b(cancel(?:led|ed|lation)?|modif(?:ied|ication|y)|amended|amendment|updated|update|changed|change)\\b");
    private static final Map<String, String> LABELS = Map.ofEntries(
            Map.entry("reservation id", "reference"), Map.entry("reservation number", "reference"),
            Map.entry("booking number", "reference"), Map.entry("booking reference", "reference"),
            Map.entry("guest name", "guest"), Map.entry("guest", "guest"),
            Map.entry("check-in", "in"), Map.entry("check in", "in"), Map.entry("arrival date", "in"),
            Map.entry("check-out", "out"), Map.entry("check out", "out"), Map.entry("departure date", "out"),
            Map.entry("room", "room"), Map.entry("room type", "room"),
            Map.entry("rooms", "rooms"), Map.entry("number of rooms", "rooms"), Map.entry("room quantity", "rooms"),
            Map.entry("phone", "phone"), Map.entry("guest phone", "phone"), Map.entry("telephone", "phone"));

    public Optional<BookingNotice> parse(String subject, String body) {
        if (subject == null || body == null) return Optional.empty();
        if (body.matches("(?s).*&(?:#\\d+|#x[0-9a-fA-F]+|[A-Za-z]+);.*"))
            body = htmlText(body.replace("<", "&lt;").replace(">", "&gt;").replaceAll("\\R", "<br>"));
        String heading = normalize(subject).replaceFirst("(?i)^Booking\\.com\\s*:\\s*", "");
        if (!heading.matches("(?i)New (?:reservation|booking)(?:\\s*[-:#].+)?")
                || EXCLUDED.matcher(subject + "\n" + body).find()) return Optional.empty();
        String[] lines = body.replace('\u00a0', ' ').split("\\R");
        Map<String, String> fields = new HashMap<>();
        try {
            for (int i = 0; i < lines.length; i++) {
                String line = normalize(lines[i]);
                int colon = line.indexOf(':');
                String label = colon < 0 ? line : line.substring(0, colon);
                String key = LABELS.get(normalize(label).toLowerCase(Locale.ROOT));
                if (key == null) continue;
                String value = colon < 0 ? "" : normalize(line.substring(colon + 1));
                if (value.isEmpty()) {
                    while (++i < lines.length && normalize(lines[i]).isEmpty()) { }
                    if (i >= lines.length) return Optional.empty();
                    value = normalize(lines[i]);
                    // A following field is not a continuation value for a blank field.
                    String nextLabel = value.contains(":") ? value.substring(0, value.indexOf(':')) : value;
                    if (LABELS.containsKey(normalize(nextLabel).toLowerCase(Locale.ROOT))) return Optional.empty();
                }
                if (fields.putIfAbsent(key, value) != null) return Optional.empty();
            }
            String reference = required(fields, "reference", 50), guest = required(fields, "guest", 100);
            LocalDate in = date(required(fields, "in", 60)), out = date(required(fields, "out", 60));
            Integer rooms = fields.containsKey("rooms") ? Integer.valueOf(fields.get("rooms")) : null;
            String phone = fields.get("phone");
            if (!out.isAfter(in) || (rooms != null && rooms < 1) || (phone != null && phone.length() > 30)) return Optional.empty();
            return Optional.of(new BookingNotice(reference, guest, in, out, fields.get("room"), rooms, phone));
        } catch (RuntimeException ex) { return Optional.empty(); }
    }

    public Optional<BookingNotice> parseHtml(String subject, String html) {
        return parse(subject, htmlText(html));
    }

    Optional<BookingNotice> parseAlternatives(String subject, List<String> bodies) {
        BookingNotice result = null;
        for (String body : bodies) {
            if (EXCLUDED.matcher(body).find()) return Optional.empty();
            var candidate = parse(subject, body);
            if (candidate.isPresent()) {
                if (result != null && !result.equals(candidate.get())) return Optional.empty();
                result = candidate.get();
            }
        }
        return Optional.ofNullable(result);
    }

    static String htmlText(String html) {
        if (html == null) return "";
        StringBuilder text = new StringBuilder();
        try {
            new ParserDelegator().parse(new StringReader(html), new HTMLEditorKit.ParserCallback() {
                private int hidden;
                private void boundary(HTML.Tag tag) {
                    if (Set.of(HTML.Tag.P, HTML.Tag.DIV, HTML.Tag.TR, HTML.Tag.TD, HTML.Tag.TH,
                            HTML.Tag.LI, HTML.Tag.BR, HTML.Tag.H1, HTML.Tag.H2, HTML.Tag.H3).contains(tag)) text.append('\n');
                }
                public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
                    if (tag == HTML.Tag.SCRIPT || tag == HTML.Tag.STYLE) hidden++;
                    boundary(tag);
                }
                public void handleEndTag(HTML.Tag tag, int pos) {
                    if (tag == HTML.Tag.SCRIPT || tag == HTML.Tag.STYLE) hidden = Math.max(0, hidden - 1);
                    boundary(tag);
                }
                public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) { boundary(tag); }
                public void handleText(char[] data, int pos) { if (hidden == 0) text.append(data); }
            }, true);
        } catch (java.io.IOException ex) { return ""; }
        return text.toString();
    }

    private static String normalize(String value) { return value.replace('\u00a0', ' ').replaceAll("\\s+", " ").trim(); }
    private static LocalDate date(String value) {
        // Slash dates are day-first, but ambiguous values are rejected rather than guessed.
        if (value.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
            String[] bits = value.split("/");
            int day = Integer.parseInt(bits[0]), month = Integer.parseInt(bits[1]);
            if (day <= 12 && month <= 12 && day != month) throw new IllegalArgumentException();
        }
        for (String pattern : List.of("uuuu-MM-dd", "d MMM uuuu", "d MMMM uuuu", "EEEE, d MMMM uuuu", "d/M/uuuu")) {
            try {
                DateTimeFormatter format = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(pattern)
                        .toFormatter(Locale.ENGLISH).withResolverStyle(ResolverStyle.STRICT);
                return LocalDate.parse(value, format);
            } catch (java.time.DateTimeException ignored) { }
        }
        throw new IllegalArgumentException();
    }
    private static String required(Map<String, String> fields, String key, int max) {
        String value = fields.get(key);
        if (value == null || value.isBlank() || value.length() > max) throw new IllegalArgumentException();
        return value;
    }
}
