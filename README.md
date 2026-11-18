# RoomBookingApp — Gmail module

University submission on `feature/google-services`. Requires JDK 27 and Maven. Dependency versions are pinned in [pom.xml](./pom.xml); this is a regular Maven JAR, not an executable or dependency-bundled JAR. Run the main class through Maven or an IDE with the Maven classpath.

## Status and scope

The team reports a successful controlled Gmail ? parser ? SQL Server insertion and restart/duplicate test against `HOTEL_DB_TEST`. Current automated fixtures are synthetic. **Real Booking.com compatibility has not been validated.** Obtain a redacted genuine new-reservation email and verify sender, subject, language, MIME layout and field labels before enabling real booking ingestion. Cancellations and modifications are intentionally rejected.

No OAuth client files, tokens or passwords belong in Git. Ignore rules cover common credential names, `.env` files, `StoredCredential`, token directories, local configuration, Maven output and JVM crash/replay logs. Arbitrary secret filenames cannot be recognized automatically: always keep secrets outside the repository. Existing crash dumps may contain sensitive process data and must not be shared.

## Architecture and team integration

- `GmailAuthentication`: Desktop OAuth with Gmail read-only scope, cached refresh credentials and loopback callback.
- `GmailPollingService`: exact sender search, initial cutoff, MIME decoding, parsing and an injected `BookingSink`. It tracks successfully handled Gmail IDs and reservation references in memory; failed sink operations are retried.
- `BookingEmailParser` / `BookingNotice`: testable parsing independent of Gmail and JDBC.
- `BookingRequestRepository`: prepared statements and a serializable transaction with SQL Server `UPDLOCK, HOLDLOCK` for an atomic ID/reference check and insert.
- `SimulatedBookingSink` / `GmailDemoPolicy`: synthetic demonstration behavior only. The hardcoded `VIVA-SYN-002` reference is isolated in the demo policy, not a normal booking validation rule.
- `GoogleSpesificationInterface` remains the existing empty team placeholder; no contract was invented for it.

### Demo versus normal application behavior

The supplied CLI intentionally has only two safe demonstration paths:

| Configuration | Behavior |
| --- | --- |
| `GMAIL_TEST_MODE=true` | Gmail-only, no database configuration or connection; displays only the exact public `SIM-123` fixture below |
| Test mode absent/false, without explicit write opt-in | Fails closed; does not authorize unrestricted writes |
| Test mode absent/false, `GMAIL_ALLOW_TEST_DB_WRITES=true`, exact demo reference | Controlled writes only to `HOTEL_DB_TEST` and `VIVA-SYN-002` |

**There is no unrestricted-write CLI mode.** Setting test mode to false does not select normal production behavior.

For normal application wiring, the public `GmailPollingService` constructor accepts a public `BookingSink`. After the team explicitly approves the target database and write policy, it can supply its own sink or deliberately supply the normal three-argument `BookingRequestRepository` method `saveIfNew`. That constructor has no demo-reference restriction or database-name guard; authorization and target validation are the calling application's responsibility. The CLI never selects it. Pass `testMode=false` to the poller so the exact `From` mailbox is checked in addition to Gmail search. Do not remove demo guards to integrate the normal sink.

The current repository matches fields in the team's `DatabaseModule.model.BookingRequest`: email message ID, booking reference, guest name/phone, dates and room quantity. It reuses `JdbcConnections` for SQL or Windows Authentication. The existing DAO's `saveBookingRequest` opens its own connection and does not atomically deduplicate, so replacing the repository with that call would lose transaction guarantees. No teammate code was changed during finalization.

The Gmail repository inserts columns common to both checked-in SQL schemas. The model/DAO targets the newer schema. Customer, meal plan, total price, special request and decision fields are not inferred. Status and received time use database defaults; a missing room count retains the existing insertion default of one. Room type is parsed but has no BOOKING_REQUEST destination column. There is no schema change.

## OAuth setup and configuration

1. Enable Gmail API in Google Cloud, configure consent and add the authorized account as a test user when required.
2. Create a **Desktop app** OAuth client. Store its JSON outside the repository in a private, non-synced location.
3. Create a private token directory outside the repository/current working directory; restrict it to the running OS account using ACLs. Token files are sensitive refresh credentials stored on disk, not encrypted by this module. The runtime path check is based on the working directory, not a complete symlink/repository security boundary.
4. Set the variables below in the same PowerShell session used to run Maven. Example paths are placeholders.

```powershell
$env:GMAIL_CLIENT_JSON = 'C:\private\client.json'
$env:GMAIL_TOKEN_DIR = 'C:\private\gmail-tokens'
$env:GMAIL_BOOKING_SENDER = 'your-verified-test-sender@example.com'
$env:GMAIL_INITIAL_CUTOFF = '2026-11-18T00:00:00Z'
$env:GMAIL_POLL_MINUTES = '2'
$env:GMAIL_TEST_MODE = 'true'
mvn compile exec:java '-Dexec.mainClass=org.example.Core.GoogleModule.GmailModuleMain'
```

Choose an intentional UTC cutoff before your demonstration message and keep it unchanged across restarts. Poll interval must be 1–5 minutes. The listener starts before browser authorization and closes afterward. Stored credentials can skip the browser; logs distinguish that case. Use the browser on the same machine as Java, keep the process running, and complete the fresh authorization attempt. No complete authorization URL, code, token or email body is logged. An interactive desktop browser is required for initial login; headless first-login is not supported. A stored credential is not proof that the next Gmail API call will succeed; revoked tokens/consent and API/network errors still require diagnosis.

The scheduler uses one non-daemon worker and remains running after authorization; operational exceptions are logged without arbitrary third-party messages and retried on the next cycle. Stop with Ctrl+C. A stalled network request or browser authorization can delay later polls; there is no custom watchdog or unattended reauthorization workflow.

Send this plain-text body from the configured sender to the authorized inbox, subject `New reservation`, to see the Gmail-only dummy output:

```text
Reservation ID: SIM-123
Guest name: Sample Guest
Check-in: 2027-01-01
Check-out: 2027-01-03
Room: Double
Rooms: 2
Phone: 12345
```

Other valid synthetic templates contribute to parsing counts but do not display guest fields. Successful handling is remembered only for the current process. No Gmail labels, read flags or bodies are modified.

## Controlled SQL demonstration (explicit opt-in only)

Not required for offline submission checks. Use the previously validated SQL Server instance and existing schema. Keep the native authentication DLL matching JDBC driver **12.6.1** and the JDK architecture on `PATH`, e.g. `mssql-jdbc_auth-12.6.1.x64.dll` for x64 Windows Authentication. No database username/password is required with `integratedSecurity=true`. SQL authentication uses `BOOKING_DB_USER` and `BOOKING_DB_PASSWORD` from the environment.

```powershell
# Use your real, previously verified DLL directory and server settings.
$env:PATH = 'C:\path\to\jdbc-driver\auth\x64;' + $env:PATH
$env:BOOKING_JDBC_URL = 'jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB_TEST;integratedSecurity=true;encrypt=true;trustServerCertificate=true;'
$env:GMAIL_TEST_MODE = 'false'
$env:GMAIL_ALLOW_TEST_DB_WRITES = 'true'
$env:GMAIL_RESERVATION_REFERENCE = 'VIVA-SYN-002'
mvn compile exec:java '-Dexec.mainClass=org.example.Core.GoogleModule.GmailModuleMain'
```

The certificate-trust bypass in this example is for a local self-signed test instance; use verified TLS for deployment. Retain the existing OAuth, sender and cutoff settings. The application validates the opt-in/reference before OAuth, and verifies `SELECT DB_NAME()` on every write connection before the write transaction. Both application and repository enforce the demo reference.

Use subject `Booking.com: New reservation` and the rendered HTML in [family.html](./src/test/resources/booking-emails/viva/family.html). Send as inline `text/html`, not source text or an attachment. If there is a plain-text alternative, use identical fields from [family.txt](./src/test/resources/booking-emails/viva/family.txt). Check for zero rows beforehand; do not delete existing rows to reset the demonstration.

```sql
SELECT COUNT(*) AS synthetic_rows FROM HOTEL_DB_TEST.dbo.BOOKING_REQUEST
WHERE booking_ref = 'VIVA-SYN-002';
SELECT request_id, email_message_id, booking_ref, check_in, check_out, rooms_wanted, status
FROM HOTEL_DB_TEST.dbo.BOOKING_REQUEST WHERE booking_ref = 'VIVA-SYN-002';
```

After success expect one row, dates 2026-11-28 through 2026-12-02, one room and PENDING. Restart with the same cutoff for message-ID deduplication; resend the reference under a new message ID for reference deduplication. The row count must remain one. Stop, then disable writes:

```powershell
$env:GMAIL_ALLOW_TEST_DB_WRITES = 'false'
$env:GMAIL_TEST_MODE = 'true'
Remove-Item Env:GMAIL_RESERVATION_REFERENCE -ErrorAction SilentlyContinue
```

## Supported synthetic parser formats and limitations

- English subjects `New reservation` / `New booking`, optional `Booking.com:` prefix and reference suffix starting `-`, `:`, or `#`; replies/unrelated subjects rejected.
- Required explicit labels: reservation ID/number or booking number/reference; guest/guest name; check-in/check in/arrival date; check-out/check out/departure date. Values may follow a colon or appear on the next nonblank line. Duplicate aliases, blank fields followed by another label and missing required values are rejected.
- Optional room/room type, rooms/number of rooms/room quantity (positive integer), phone/guest phone/telephone. Missing fields stay null in the parser. Multiple room categories are not aggregated.
- Strict dates: `2026-11-18`, `18 Nov 2026`, `18 November 2026`, `Wednesday, 18 November 2026`, unambiguous day-first `18/11/2026`. Ambiguous slash dates, missing years, impossible dates and mismatched weekdays are rejected.
- Inline HTML tables/block elements/entities and whitespace are normalized with the JDK HTML parser. Nested multipart inline plain-text/HTML is supported with declared charset decoding (UTF-8 default). Attachments, forwarded `message/rfc822` and Gmail attachment-ID-only bodies are not parsed.
- Conflicting valid alternatives are rejected; an unsupported alternative may fall back to a valid one. Cancellation/modification/change/update markers veto a message conservatively, including policy wording in a legitimate new-booking email. Localization and actual provider templates remain unvalidated.
- Exact sender checks are filtering, not cryptographic authentication. The cutoff uses Gmail internal timestamps and an approximate search prefilter; messages with missing/older timestamps are rejected.
- In-memory deduplication grows with handled messages and resets at restart. The repository provides persistence via both IDs; no unique `booking_ref` index exists. Other writers must follow the same locking protocol for database-wide consistency. Deadlocks/network/SQL failures are retried; no connection pool or durable Gmail history checkpoint is implemented.
- OAuth library APIs produce a deprecation warning on JDK 27. Its listener advertises loopback but the installed library binds all local interfaces. Changing OAuth libraries/listener implementation is deferred; use a trusted local machine. OAuth re-consent, scope migrations and full production hardening are outside this submission.

## Offline university viva and validation

Fixtures under [booking-emails/viva](./src/test/resources/booking-emails/viva/) are invented, including hotel/guest names and invalid `+00` phone placeholders. Show the plain-text, HTML and optional-field examples, then the matching multipart and duplicate tests. The new demo suite invokes processing with in-memory Gmail `Message` objects and a recording sink; it does not contact Gmail or SQL Server or print guest records.

```powershell
$env:GMAIL_ALLOW_TEST_DB_WRITES = 'false'
$env:TEST_DB_DISPOSABLE = 'false'
mvn '-Dtest=VivaSyntheticBookingTest' test
mvn clean test '-Dbooking.db.integration=false' '-Dbooking.db.smoke=false'
mvn clean package '-DskipTests'
```

Some regression tests use only local loopback HTTP/socket listeners. Two live SQL tests are skipped by default: `DatabaseConnectionSmokeTest` requires `-Dbooking.db.smoke=true`; `DatabaseIntegrationTest` requires `-Dbooking.db.integration=true`, `TEST_DB_DISPOSABLE=true` and a separately provisioned `TEST_DB_URL` for HOTEL_DB_TEST (plus TEST_DB_USER/PASSWORD for SQL auth). Do not enable them for the offline submission. They require explicit separate authorization and are not proof of real Booking.com compatibility.

If JVM native memory is constrained, set temporary session options before Maven:

```powershell
$env:JAVA_TOOL_OPTIONS = '-Xmx128m -Xint -XX:ActiveProcessorCount=1 -XX:ReservedCodeCacheSize=32m'
```

Packaging creates `target/RoomBookApp-1.0-SNAPSHOT.jar`. `clean package` removes earlier test report files; retain the console test summary if needed. No executable manifest or bundled dependency distribution is provided. The team must review the existing DatabaseModule Windows Authentication/helper edits separately before staging; do not blindly stage the entire working tree.
