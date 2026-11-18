package org.example.Core.GoogleModule;

import com.google.api.services.gmail.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Offline viva scenarios: only in-memory messages and a recording sink, no Gmail client or JDBC. */
class VivaSyntheticBookingTest {
    private final BookingEmailParser parser = new BookingEmailParser();
    private String fixture(String name) throws Exception { return BookingTemplateTest.fixture("viva/" + name); }
    private BookingNotice garden() {
        return new BookingNotice("VIVA-SYN-001", "Mira Testwell", LocalDate.of(2026,11,22),
                LocalDate.of(2026,11,25), "Garden Double", 2, "+00 000 0101");
    }
    private BookingNotice family() {
        return new BookingNotice("VIVA-SYN-002", "Arun Demo & Family", LocalDate.of(2026,11,28),
                LocalDate.of(2026,12,2), "Family Suite", 1, null);
    }
    private MessagePart part(String mime, String text) {
        return new MessagePart().setMimeType(mime).setBody(new MessagePartBody().setData(
                Base64.getUrlEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8))));
    }
    private Message message(String id, String subject, MessagePart payload) {
        payload.setHeaders(List.of(new MessagePartHeader().setName("Subject").setValue(subject),
                new MessagePartHeader().setName("From").setValue("Synthetic Demo <demo@example.invalid>")));
        return new Message().setId(id).setInternalDate(2000L).setPayload(payload);
    }
    private GmailPollingService service(List<BookingNotice> output) {
        return new GmailPollingService(null, (id, booking) -> output.add(booking),
                "demo@example.invalid", true, Instant.ofEpochMilli(1000));
    }
    @Test void plainTextContainsEveryExpectedField() throws Exception {
        assertEquals(garden(), parser.parse("Booking.com: New reservation - VIVA-SYN-001", fixture("garden.txt")).orElseThrow());
    }
    @Test void applicationSinkHasNoDemoReferenceRestrictionButStillRequiresExactSender() throws Exception {
        List<BookingNotice> output = new ArrayList<>();
        var service = new GmailPollingService(null, (id, booking) -> output.add(booking),
                "demo@example.invalid", false, Instant.ofEpochMilli(1000));
        var wrongSender = message("wrong-sender", "New reservation", part("text/plain", fixture("garden.txt")));
        wrongSender.getPayload().getHeaders().get(1).setValue("other@example.invalid");
        assertFalse(service.processMessage(wrongSender));
        assertTrue(service.processMessage(message("normal", "New reservation", part("text/plain", fixture("garden.txt")))));
        assertEquals(List.of(garden()), output);
    }
    @Test void htmlTableDecodesEntitiesAndOptionalPhone() throws Exception {
        assertEquals(family(), parser.parseHtml("New booking", fixture("family.html")).orElseThrow());
    }
    @Test void missingOptionalFieldsStayNullWithoutInventedQuantity() throws Exception {
        var result = parser.parse("New reservation", fixture("twin.txt")).orElseThrow();
        assertEquals(new BookingNotice("VIVA-SYN-003", "Lena Exampleton", LocalDate.of(2027,1,13),
                LocalDate.of(2027,1,16), "Courtyard Twin", null, null), result);
    }
    @Test void multipleReservationsTraverseSyntheticGmailObjects() throws Exception {
        List<BookingNotice> output = new ArrayList<>();
        var service = service(output);
        assertTrue(service.processMessage(message("one", "New reservation", part("text/plain", fixture("garden.txt")))));
        assertTrue(service.processMessage(message("two", "New booking", part("text/html", fixture("family.html")))));
        assertTrue(service.processMessage(message("three", "New reservation", part("text/plain", fixture("twin.txt")))));
        assertEquals(3, output.size());
        assertEquals(garden(), output.get(0));
        assertEquals(family(), output.get(1));
        assertNull(output.get(2).rooms());
    }
    @Test void sameMessageIdWithDifferentReferenceIsNotProcessedTwice() throws Exception {
        List<BookingNotice> output = new ArrayList<>();
        var service = service(output);
        assertTrue(service.processMessage(message("same-id", "New reservation", part("text/plain", fixture("garden.txt")))));
        assertFalse(service.processMessage(message("same-id", "New booking", part("text/plain", fixture("family.txt")))));
        assertEquals(List.of(garden()), output);
    }
    @Test void sameReferenceWithDifferentMessageIdsIsNotProcessedTwice() throws Exception {
        List<BookingNotice> output = new ArrayList<>();
        var service = service(output);
        assertTrue(service.processMessage(message("first-id", "New reservation", part("text/plain", fixture("garden.txt")))));
        assertFalse(service.processMessage(message("second-id", "New reservation", part("text/plain", fixture("garden.txt")))));
        assertEquals(List.of(garden()), output);
    }
    @ParameterizedTest @ValueSource(strings={"cancelled.txt", "modified.txt", "newsletter.txt", "malformed.html"})
    void nonBookingAndMalformedFixturesNeverReachSink(String file) throws Exception {
        List<BookingNotice> output = new ArrayList<>();
        assertFalse(service(output).processMessage(message("rejected", "New reservation",
                part(file.endsWith("html") ? "text/html" : "text/plain", fixture(file)))));
        assertTrue(output.isEmpty());
    }
    @ParameterizedTest @ValueSource(strings={"Reservation number", "Guest name", "Check-in", "Check-out"})
    void requiredFieldCannotBeOmitted(String label) throws Exception {
        String body = fixture("garden.txt").replaceAll("(?m)^" + label + ":.*\\R?", "");
        assertTrue(parser.parse("New reservation", body).isEmpty());
    }
    @ParameterizedTest @ValueSource(strings={"2026-02-30", "2026-13-01", "02/03/2026", "22 November", "2026-11-26"})
    void invalidAmbiguousOrReversedDatesAreRejected(String date) throws Exception {
        assertTrue(parser.parse("New reservation", fixture("garden.txt").replace("22 November 2026", date)).isEmpty());
    }
    @Test void nestedMultipartAcceptsMatchingRepresentationsAndSkipsAttachment() throws Exception {
        var multipart = new MessagePart().setMimeType("multipart/mixed").setParts(List.of(
                new MessagePart().setMimeType("multipart/alternative").setParts(List.of(
                        part("text/plain", fixture("family.txt")), part("text/html", fixture("family.html")))),
                part("text/plain", fixture("cancelled.txt")).setFilename("ignored-attachment.txt")));
        List<BookingNotice> output = new ArrayList<>();
        assertTrue(service(output).processMessage(message("multipart", "New booking", multipart)));
        assertEquals(List.of(family()), output);
    }
    @Test void conflictingMimeAlternativesAreRejected() throws Exception {
        var multipart = new MessagePart().setMimeType("multipart/alternative").setParts(List.of(
                part("text/plain", fixture("garden.txt")), part("text/html", fixture("family.html"))));
        List<BookingNotice> output = new ArrayList<>();
        assertFalse(service(output).processMessage(message("conflict", "New reservation", multipart)));
        assertTrue(output.isEmpty());
    }
    @Test void optionalPhoneCannotConsumeFollowingFieldWhenBlank() throws Exception {
        String body = fixture("garden.txt").replace("Guest phone: +00 000 0101", "Guest phone:\nRoom quantity: 3");
        assertTrue(parser.parse("New reservation", body).isEmpty());
    }
}
