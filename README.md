# RoomBookingApp

Hotel management desktop application (Java 27 + JavaFX 27) with a SQL Server database, Gmail-based reservation ingestion (Booking.com-style emails), and a Telegram bot for customer notifications. This guide is written for running and evaluating the project.

## 1. Prerequisites

- **JDK 27** (Oracle/OpenJDK) and **Maven 3.9+**
- **Microsoft SQL Server** (local instance, e.g. `localhost:1433`) with SQL Server Management Studio (SSMS) or `sqlcmd`
- A modern browser (for the first-run Google OAuth consent, if Gmail is enabled)
- (Optional) A Telegram bot token from [@BotFather](https://t.me/BotFather) for Telegram features

Verify:
```powershell
java -version
mvn -version
```

## 2. Database setup (required first)

The application uses a SQL Server database named `HOTEL_DB`. Create it from the supplied script **before** running anything:

1. Open SSMS and connect to your SQL Server instance.
2. Open `UPDATED_SQLQuery1_v2.0.sql` from the repository root.
3. Execute the whole script (it creates `HOTEL_DB` and all tables: `SHOP_OWNER`, `ROOM`, `ITEM`, `MEAL_PLAN`, `CUSTOMER`, `BOOKING_REQUEST`, `BOOKING`, `BOOKING_ROOM`, `PAYMENT`, `ITEM_ORDER`, `MESSAGE`, `CUSTOMER_REPORT`).
4. The default connection used by the app is:
   ```
   jdbc:sqlserver://localhost:1433;databaseName=HOTEL_DB;user=java_dev;password=123456789;encrypt=true;trustServerCertificate=true;
   ```
   Create a SQL login `java_dev` with password `123456789` and grant it access to `HOTEL_DB`, **or** edit the connection string in `src/main/java/org/example/gui/HotelApp.java` (`databaseModule` field) to match your own credentials.

> If the database already exists and you only need the Telegram link-code columns, run the idempotent `ALTER TABLE` block at the end of `UPDATED_SQLQuery1_v2.0.sql`.

### Optional: seed mock data

To populate the database with meaningful sample data (owners, rooms, items, meal plans, customers, bookings, payments, reports):

```powershell
mvn exec:java "-Dexec.mainClass=org.example.seed.MockDataSeeder"
```

It aborts automatically if shop owners already exist (idempotent guard). Clear the tables first if you want a fresh seed. Default owner credentials after seeding: `admin` / `admin123` and `david_desk` / `desk123`.

## 3. Build

```powershell
mvn clean compile
mvn test
```

> Note: the project targets JDK 27, which is newer than Mockito/Byte Buddy officially support. The `pom.xml` already passes `-Dnet.bytebuddy.experimental=true` to Surefire, so `mvn test` works in IntelliJ and on the CLI without extra flags.

## 4. Run the desktop application

The main entry point is the JavaFX desktop app:

```powershell
mvn compile exec:java "-Dexec.mainClass=org.example.gui.HotelApp"
```

On first launch, if the `SHOP_OWNER` table is empty, a **registration screen** appears to create the first administrator account. Otherwise the login screen is shown.

The app opens a maximized window with tabs: **Booking Requests**, **Stays**, **Rooms**, **Inventory**, **Customers & Reports** (Telegram logs).

## 5. Gmail module (reservation ingestion)

The Gmail module polls a mailbox for simulated Booking.com "New reservation" emails and inserts them as `BOOKING_REQUEST` rows. It can run inside the desktop app (via the "Check Gmail" / "Simulate" buttons) or standalone.

### 5.1 Environment variables

Set these in the same PowerShell session before running (paths must be **outside** the repository):

```powershell
$env:GMAIL_CLIENT_JSON   = 'C:\private\client.json'      # Desktop OAuth client JSON from Google Cloud
$env:GMAIL_TOKEN_DIR      = 'C:\private\gmail-tokens'     # private token directory (outside repo)
$env:GMAIL_BOOKING_SENDER = 'your-verified-sender@example.com'
$env:GMAIL_INITIAL_CUTOFF = '2026-11-18T00:00:00Z'      # UTC cutoff; keep stable across restarts
$env:GMAIL_POLL_MINUTES   = '2'                          # 1..5
$env:GMAIL_TEST_MODE      = 'true'                       # optional: Gmail-only, no DB
```

Also (optional, for controlled DB writes in standalone mode):
```powershell
$env:GMAIL_ALLOW_TEST_DB_WRITES = 'true'
$env:GMAIL_RESERVATION_REFERENCE = 'VIVA-SYN-002'
```

### 5.2 Standalone run

```powershell
mvn compile exec:java "-Dexec.mainClass=org.example.Core.GoogleModule.GmailModuleMain"
```

On first run a browser opens for Google OAuth consent (read-only Gmail scope). Tokens are cached in `GMAIL_TOKEN_DIR`.

### 5.3 Test mode (no database)

Set `GMAIL_TEST_MODE=true` and the three `GMAIL_CLIENT_JSON`, `GMAIL_TOKEN_DIR`, `GMAIL_BOOKING_SENDER` variables. Send the exact simulated body below as plain text with subject `New reservation` from the configured sender. Run and watch for `Simulated reservation parsed`. Test mode never writes to the database.

Simulated body (ISO dates):
```text
Reservation ID: SIM-123
Guest name: Sample Guest
Check-in: 2027-01-01
Check-out: 2027-01-03
Room: Double
Rooms: 2
Phone: 12345
```

### 5.4 Simulate from the GUI (no Gmail needed)

In the desktop app, go to **Booking Requests** and click **Simulate**. Paste any example message from `examples/gmail/` (or your own) into the textarea and click "Run Simulation". The flow parses the message and saves a `BOOKING_REQUEST` row, then refreshes the table. Examples included: `example-1-basic.txt` (accepted), `example-6-cancelled.txt` (rejected), etc. See `examples/gmail/README.txt`.

**Check Gmail** button runs the real Gmail poll (requires the env vars above).

### 5.5 Tests (offline, no Gmail/DB)

```powershell
mvn -Dtest=BookingEmailParserTest test
mvn -Dtest=GmailPollingServiceTest test
mvn -Dtest=GmailTestModeTest test
```

## 6. Telegram module (customer notifications)

The Telegram bot links a customer's chat to a booking via a desk-generated OTP code, then sends notifications (booking confirmations, etc.).

### 6.1 Setup

1. Create a bot with [@BotFather](https://t.me/BotFather) and copy the token.
2. Set the environment variable:
   ```powershell
   $env:TELEGRAM_BOT_TOKEN = '123456:ABC-DEF...'
   ```
3. (Optional) Run the bot standalone:
   ```powershell
   mvn compile exec:java "-Dexec.mainClass=org.example.Core.TelegramModule.Main"
   ```
   The desktop app also starts the bot automatically on launch if the token is set.

### 6.2 Linking a customer (OTP flow)

1. In the app, open **Customers & Reports** and click **Add Telegram User**.
2. Select an active booking (must have a customer) and click **Generate Code**. A 6-digit code (valid 5 minutes) is shown.
3. The customer sends `/register <code>` to the bot (or taps the deep link `t.me/<bot>?start=register_<code>`).
4. The bot links the chat id to the customer; the customer now appears as "Linked" and can receive messages.
5. Use **Send Message** on a linked customer row to send a Telegram message (logged in the MESSAGE table).

### 6.3 Notes
- Bots cannot message users who never started them; the OTP/deep-link flow solves this (customer initiates at the desk).
- Telegram uses the same `HOTEL_DB` via `HotelApp.databaseModule` (no separate DB env vars).

## 7. Project structure (quick map)

```
src/main/java/org/example/gui/          # JavaFX handlers (HotelApp, Dashboard, BookingRequest, Stay, Room, Inventory, Telegram, Login)
src/main/java/org/example/Core/         # DatabaseModule, GoogleModule (Gmail), TelegramModule
src/main/java/org/example/seed/          # MockDataSeeder
src/main/resources/.../app/index.html    # HTML frontend rendered in JavaFX WebView
examples/gmail/                         # Example Gmail messages for the Simulate button
UPDATED_SQLQuery1_v2.0.sql              # Database schema (run first)
```

## 8. Troubleshooting

- **"module not found: jdk.jsobject"**: ensure `org.openjfx:jdk-jsobject` is on the module path (already declared in `pom.xml`). Use the bundled Maven/IntelliJ toolchain.
- **Mockito/Byte Buddy errors on JDK 27**: already handled via `net.bytebuddy.experimental=true` in Surefire config in `pom.xml`.
- **Database connection failures**: verify SQL Server is running, `HOTEL_DB` exists, and the credentials in `HotelApp.databaseModule` match your instance.
- **Gmail OAuth**: requires a browser on the same machine; keep the process running and complete consent. Tokens are cached in `GMAIL_TOKEN_DIR`.
- **Telegram bot not responding**: ensure `TELEGRAM_BOT_TOKEN` is set and the app/bot process is running (it long-polls `getUpdates`).

## 9. Running tests

```powershell
mvn test
mvn -Dtest=BookingEmailParserTest test
mvn -Dtest=GmailPollingServiceTest test
mvn -Dtest=GmailTestModeTest test
```

Live Gmail/SQL Server integration tests are opt-in and skipped by default.