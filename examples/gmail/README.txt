Example Gmail messages for the "Simulate" button (Booking Requests page).

How to use:
  1. Open the app -> Booking Requests -> click "Simulate".
  2. Paste the full contents of any example file into the textarea.
  3. Click "Run Simulation". A new booking request is created in the DB.

Files:
  example-1-basic.txt        -> accepted (plain text, all fields)
  example-2-full-labels.txt  -> accepted (alternative labels + Booking.com subject + named dates)
  example-3-minimal.txt      -> accepted (only required fields; rooms defaults to 1)
  example-4-family.txt       -> accepted (family suite, 3 rooms)
  example-5-html.txt         -> accepted (HTML body)
  example-6-cancelled.txt    -> rejected (cancellation)
  example-7-modified.txt     -> rejected (modification)
  example-8-duplicate-key.txt-> rejected (duplicate reservation id key)
  example-9-bad-dates.txt    -> rejected (check-out before check-in)

Notes:
  - The first "Subject: ..." line is optional; if omitted, "New reservation" is assumed.
  - Required fields: Reservation ID, Guest name, Check-in, Check-out (ISO date preferred).
  - Optional: Room, Rooms (>=1), Phone (<=30 chars). Room text is parsed but not persisted.