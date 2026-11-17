# RoomBookingApp

## Gmail reservation prototype

This module polls Gmail with the read-only scope and inserts new requests into the columns shared by both checked-in SQL Server schemas. There is no implemented database service in the current project. Set up one of the supplied database scripts first. The module uses serializable transactions and SQL Server update locks to check both Gmail message ID and booking reference before inserting. All writers that insert booking references must follow the same locking protocol; the schema does not enforce uniqueness of `booking_ref`. A team-approved unique filtered index on that column would provide a stronger database-wide guarantee after existing duplicate references are resolved.

**Integration limitation:** No real Booking.com message sample or verified sender address was supplied. The parser accepts only the simulated format below, in a `text/plain` MIME part with subject exactly `New reservation`. It is not yet a production Booking.com parser. Obtain a redacted genuine new-booking email and verify the sender and format before enabling this against a live inbox. Sender search is a filter, not cryptographic proof of origin. Messages that do not match are left untouched and retried on later polls. Cancellations and modifications are not handled. Room text is parsed but neither checked-in BOOKING_REQUEST schema has a room-description column, so it is not saved. When the simulated message omits the room count, the database's documented default of one is used. Other optional fields are not inferred.

Example simulated body (ISO dates):

```text
Reservation ID: SIM-123
Guest name: Sample Guest
Check-in: 2027-01-01
Check-out: 2027-01-03
Room: Double
Rooms: 2
Phone: 12345
```

### Configuration

Enable the Gmail API in Google Cloud, configure the OAuth consent screen and any required test users, then create a Desktop OAuth client. Store its JSON outside the repository. Create a separate token directory outside the repository, accessible only to the account running the app. Tokens include refresh credentials; protect the directory with operating-system ACLs and never share or commit it. The Google client library also restricts its token-store permissions where supported. Do not use a shared or synced directory.

Set these environment variables in the launch environment:

- `GMAIL_CLIENT_JSON`: absolute path to the desktop client JSON.
- `GMAIL_TOKEN_DIR`: absolute path to a private directory outside the repository and current working directory.
- `GMAIL_BOOKING_SENDER`: the exact verified sender email address. This is required and must be confirmed using a genuine notification.
- `GMAIL_TEST_MODE`: set to `true` for the database-free test mode described below.
- `BOOKING_JDBC_URL`: SQL Server JDBC URL, required outside test mode.
- `BOOKING_DB_USER`, `BOOKING_DB_PASSWORD`: database credentials, required outside test mode.
- `GMAIL_POLL_MINUTES`: optional integer from 1 to 5; default 2.

### Build and run

Use JDK 27 and Maven, from the repository root:

```powershell
mvn test
mvn compile exec:java -Dexec.mainClass=org.example.Core.GoogleModule.GmailModuleMain
```

On first run the Google authorization URL opens in the browser. Sign in to the intended mailbox and consent to Gmail read-only access. The callback advertises a loopback address. Tokens are reused and refreshed on subsequent runs. Browser launch is required; the application does not print the authorization URL. The service retries failed polls; stop it with Ctrl+C.

### Gmail-only test mode

Set `GMAIL_TEST_MODE=true`, the three `GMAIL_CLIENT_JSON`, `GMAIL_TOKEN_DIR`, and `GMAIL_BOOKING_SENDER` variables above, and optionally `GMAIL_POLL_MINUTES=1`. No database variables are required. Send the exact simulated body above as plain text with subject `New reservation` from the configured sender to the authorized inbox. Run the command above and watch for `Simulated reservation parsed`. To avoid displaying real guest information, test mode displays only the exact fixture above, including its optional fields; other parsed messages produce no booking output. Successfully handled message IDs and reservation IDs are remembered for the lifetime of the process, so the fixture is not displayed again on subsequent polls. Test mode never writes bookings.

Each cycle reports messages found and valid bookings parsed, including messages that are not the printable fixture; already handled message IDs are skipped before parsing. On an API failure the totals are partial. The in-memory deduplication is reset on restart and grows with the number of handled bookings. Database mode relies on the database check across restarts. Set `GMAIL_TEST_MODE=false` or remove the variable to disable test mode; the database configuration is then required.

`mvn -Dtest=GmailTestModeTest test` exercises message decoding, parsing, and the test sink without Gmail or a database. It is not a live Gmail integration test.

Run the simulated parser tests with `mvn -Dtest=BookingEmailParserTest test`. These tests do not connect to Google or SQL Server. A live Gmail/SQL Server integration test requires your configured mailbox, a verified sender and a database; neither is supplied here.
