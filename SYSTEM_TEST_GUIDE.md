# RoomBookingApp system test guide

These steps are preparation, not evidence of a completed system test. Use synthetic data and an isolated SQL Server instance. Do not enable Gmail or Telegram during offline testing.

## Disposable database setup

1. Install JDK 27, Maven, SQL Server with TCP/IP enabled, and a SQL Server client that understands `GO` (for example SSMS or sqlcmd). Obtain the Maven dependencies before disconnecting from the internet.
2. Provision an isolated SQL Server instance and a least-privilege test login. Confirm its identity and that it contains no valuable data. In that instance only, create a new empty database named `HOTEL_DB_TEST`.
3. The current application schema is `UPDATED_SQLQuery1_v2.0.sql`. It begins with `CREATE DATABASE HOTEL_DB` and `USE HOTEL_DB`. Do **not** execute it unchanged. Open a temporary copy, remove those initial database-creation and selection batches, select `HOTEL_DB_TEST` explicitly in the SQL client, verify `SELECT DB_NAME()` returns `HOTEL_DB_TEST`, and execute the remaining DDL in that database. Review the script first. `SQLQuery_for_creatingDB.sql` is the older schema; the newer one includes fields used by the GUI, including Telegram fields. No schema file was modified here.
4. Set `DB_URL` to a JDBC URL explicitly naming that isolated database, e.g. `jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB_TEST;encrypt=true;trustServerCertificate=true;` (trusting the certificate is appropriate only for a controlled local test). Set `DB_USER` and `DB_PASSWORD` outside the repository, or use a configured integrated-security URL and its SQL Server native authentication prerequisites. Never include passwords in commands saved to a file or in the URL.
5. Keep `TELEGRAM_BOT_TOKEN` unset and leave Gmail OAuth settings unset. On an empty database the GUI offers owner registration; create a synthetic owner and use the GUI to create test rooms and other data. The checked-in seeder writes many records and must not be run without explicit confirmation; if separately approved it requires `SEED_ADMIN_PASSWORD` and `SEED_DESK_PASSWORD` in the environment. Meal plans and booking requests may require controlled synthetic fixture setup; the script's example meal-plan inserts are commented out.
6. Intended JavaFX launch from the project directory, after the build dependencies are available: `mvn org.openjfx:javafx-maven-plugin:0.0.8:run "-Djavafx.mainClass=com.codehack.nngf/org.example.gui.HotelApp"`. This command has not been verified by launching the GUI. The app performs a periodic database cleanup even when Telegram is disabled; only launch it against the disposable instance.

## Checklist

Record screenshots, expected and actual results, and the matching SQL rows on the isolated database after each step. Verify that UI refreshes and a restart retain the same values. Watch for mock data overriding or duplicating database-backed results.

- [ ] Register the first synthetic owner, log out, reject an incorrect password (including username `tester`), sign in correctly, and verify the owner and last-login record.
- [ ] Check the pending-request badge and dashboard counts against SQL after changing a request.
- [ ] Create a room, reject duplicate number and invalid price, change availability, refresh, and inspect `ROOM`.
- [ ] Check customer list and history, and compare with `CUSTOMER` and linked bookings. Check missing/duplicate phone handling.
- [ ] Create a synthetic pending booking request in the isolated database using an approved fixture, then filter, search, refresh, and deny a separate request. Verify status and audit fields.
- [ ] Accept a request, choose available rooms and a meal plan, and verify request status, customer, booking, and `BOOKING_ROOM` assignments. Try insufficient rooms and conflicting dates; verify no partial changes or double allocation.
- [ ] Check in, inspect status and room availability; check out and verify room release and final booking status. Test invalid transitions and repeat actions.
- [ ] Add an item order and record a synthetic payment; verify folio totals, stock changes, `ITEM_ORDER`, and `PAYMENT`. Check invalid amounts and repeat submission.
- [ ] Create and edit inventory, restock, deactivate and test insufficient stock; compare values with SQL.
- [ ] With Gmail and Telegram disabled, use Gmail simulation with the sample text shown in the GUI. Verify a valid message parses and an invalid or repeated message is rejected. Simulation intentionally does not insert a database row.
- [ ] Only with separate explicit authorization for live integrations: use a dedicated synthetic Gmail account and a test Telegram bot/customer. Verify sender and cutoff filtering, duplicate handling and controlled writes, linking, sending, failure handling and message logs. Never use real guest data. The current instructions do not authorize such a test.

## Known boundaries

The standalone `frontend/` and the HTML/JavaScript embedded in JavaFX both ship with `USE_MOCK = true`. The latter also has Java bridge handlers for several database-backed actions. These paths overlap and can conflict; a successful screen interaction is not proof of persistence. The API contract describes a proposed REST service, not proof that one exists. No GUI, SQL Server, Gmail, or Telegram end-to-end test was performed in this preparation. Booking acceptance currently creates a customer, booking, room links and status updates in separate operations rather than one transaction; failures or concurrent acceptance may leave partial data or double allocations. Address and test that before relying on a live demonstration.

GUI Gmail checks default to in-memory test mode. To permit the existing restricted write path in a later authorized test, configure `GMAIL_TEST_MODE=false`, `GMAIL_ALLOW_TEST_DB_WRITES=true`, `GMAIL_RESERVATION_REFERENCE=VIVA-SYN-002`, `GMAIL_INITIAL_CUTOFF` as an ISO-8601 instant, `BOOKING_JDBC_URL`, and, for SQL authentication, `BOOKING_DB_USER` and `BOOKING_DB_PASSWORD`; configure `GMAIL_CLIENT_JSON`, `GMAIL_TOKEN_DIR` outside the repository, and `GMAIL_BOOKING_SENDER`. The repository verifies the connected database is exactly `HOTEL_DB_TEST` before writing and only accepts the synthetic reference. This is not a general production Gmail ingestion workflow. It inserts a request without creating its customer, so acceptance must be separately verified.

The repository schema uniquely constrains email message IDs but not booking references. The controlled Gmail repository checks and inserts under a SQL Server serializable transaction and locks, protecting cooperating repository writers; other insertion paths do not necessarily honor its locking protocol. Database-wide booking-reference uniqueness would require a reviewed schema/index change and duplicate-data audit.
