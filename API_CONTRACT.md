# RoomBookingApp REST API Contract

This document defines the formal REST API contract for **RoomBookingApp** when transitioning from client-side mock storage (`USE_MOCK = true`) to production backend services (`USE_MOCK = false` via `fetch('/api/...')`).

Every endpoint, parameter, JSON schema, and status code adheres strictly to the Microsoft SQL Server database schema defined in `UPDATED_SQLQuery1_v2.0.sql`.

---

## Global Standards & Conventions

### Base URL
```
/api
```

### Authentication Header
Endpoints requiring shop owner authentication expect an HTTP `Authorization` Bearer token (stored in `localStorage.getItem('auth_token')`):
```http
Authorization: Bearer <token>
Accept: application/json
Content-Type: application/json
```

### Standard Status Codes
- `200 OK`: Request succeeded; returns data payload.
- `201 Created`: Resource successfully created.
- `204 No Content`: Action succeeded with no body returned.
- `400 Bad Request`: Validation failure, missing required fields, or constraint violation.
- `401 Unauthorized`: Missing or invalid bearer token.
- `404 Not Found`: Target record ID does not exist.
- `409 Conflict`: Unique constraint violation (e.g. duplicate `username`, `room_number`, `email_message_id`, `booking_ref`, or `telegram_chat_id`).
- `500 Internal Server Error`: Unhandled database or server exception.

### Standard Error Response Format
```json
{
  "error": "Error description or database constraint message",
  "status": 400
}
```

---

## Table of Contents
1. [Authentication & Shop Owners (`SHOP_OWNER`)](#1-authentication--shop-owners-shop_owner)
2. [Rooms & Availability (`ROOM`)](#2-rooms--availability-room)
3. [Meal Plans (`MEAL_PLAN`)](#3-meal-plans-meal_plan)
4. [Items & Supplies Inventory (`ITEM`)](#4-items--supplies-inventory-item)
5. [Customers & Telegram Directory (`CUSTOMER`)](#5-customers--telegram-directory-customer)
6. [Booking Requests & Triage (`BOOKING_REQUEST`)](#6-booking-requests--triage-booking_request)
7. [Bookings & Lifecycle Stays (`BOOKING`)](#7-bookings--lifecycle-stays-booking)
8. [Room Assignments (`BOOKING_ROOM`)](#8-room-assignments-booking_room)
9. [Item Orders & Minibar Purchases (`ITEM_ORDER`)](#9-item-orders--minibar-purchases-item_order)
10. [Payments & Settlement (`PAYMENT`)](#10-payments--settlement-payment)
11. [Telegram Communications Log (`MESSAGE`)](#11-telegram-communications-log-message)
12. [Customer Reports & Incident Dossier (`CUSTOMER_REPORT`)](#12-customer-reports--incident-dossier-customer_report)
13. [Dashboard & Metrics (`DASHBOARD`)](#13-dashboard--metrics-dashboard)
14. [System Database Utilities (`SYSTEM`)](#14-system-database-utilities-system)

---

## 1. Authentication & Shop Owners (`SHOP_OWNER`)

### 1.1 Owner Login
Authenticates a shop owner using credentials. Sets session token.

- **Method**: `POST`
- **URL**: `/api/auth/login`
- **Request Body**:
```json
{
  "username": "admin",
  "password": "secret_password"
}
```
- **Response** (`200 OK`):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "owner_id": 1,
    "username": "admin",
    "full_name": "Eleanor Vance",
    "last_login_at": "2026-11-17T02:30:00"
  }
}
```

### 1.2 Owner Logout
Terminates active session token.

- **Method**: `POST`
- **URL**: `/api/auth/logout`
- **Request Body**: None
- **Response** (`200 OK`):
```json
{
  "success": true
}
```

### 1.3 Get Current User Profile
Retrieves the logged-in owner profile via Bearer token.

- **Method**: `GET`
- **URL**: `/api/auth/me`
- **Headers**: `Authorization: Bearer <token>`
- **Response** (`200 OK`):
```json
{
  "owner_id": 1,
  "username": "admin",
  "full_name": "Eleanor Vance",
  "last_login_at": "2026-11-17T02:30:00"
}
```

### 1.4 List All Shop Owners
Returns list of registered owners (excluding `password_hash` and `salt`).

- **Method**: `GET`
- **URL**: `/api/shop-owners`
- **Response** (`200 OK`):
```json
[
  {
    "owner_id": 1,
    "username": "admin",
    "full_name": "Eleanor Vance",
    "created_at": "2026-01-10T08:00:00",
    "last_login_at": "2026-11-17T02:30:00"
  },
  {
    "owner_id": 2,
    "username": "david_desk",
    "full_name": "David Miller",
    "created_at": "2026-02-15T09:30:00",
    "last_login_at": "2026-11-16T18:45:00"
  }
]
```

---

## 2. Rooms & Availability (`ROOM`)

### 2.1 List All Rooms
Returns all hotel rooms. Supports optional query filters.

- **Method**: `GET`
- **URL**: `/api/rooms`
- **Query Parameters**:
  - `status` (`AVAILABLE` | `BOOKED` | `CLOSED` - optional)
  - `room_type` (string - optional)
- **Response** (`200 OK`):
```json
[
  {
    "room_id": 1,
    "room_number": "101",
    "room_type": "Single Standard",
    "price_per_night": 65.00,
    "status": "AVAILABLE"
  },
  {
    "room_id": 2,
    "room_number": "102",
    "room_type": "Single Standard",
    "price_per_night": 65.00,
    "status": "BOOKED"
  },
  {
    "room_id": 4,
    "room_number": "104",
    "room_type": "Double Standard",
    "price_per_night": 85.00,
    "status": "CLOSED"
  }
]
```

### 2.2 Get Room by ID
- **Method**: `GET`
- **URL**: `/api/rooms/:roomId`
- **Response** (`200 OK`):
```json
{
  "room_id": 1,
  "room_number": "101",
  "room_type": "Single Standard",
  "price_per_night": 65.00,
  "status": "AVAILABLE"
}
```

### 2.3 Create New Room
Enforces `price_per_night >= 0` and unique `room_number`.

- **Method**: `POST`
- **URL**: `/api/rooms`
- **Request Body**:
```json
{
  "room_number": "303",
  "room_type": "Executive Penthouse Suite",
  "price_per_night": 290.00,
  "status": "AVAILABLE"
}
```
- **Response** (`201 Created`):
```json
{
  "room_id": 11,
  "room_number": "303",
  "room_type": "Executive Penthouse Suite",
  "price_per_night": 290.00,
  "status": "AVAILABLE"
}
```

### 2.4 Update Room Details
- **Method**: `PUT`
- **URL**: `/api/rooms/:roomId`
- **Request Body**:
```json
{
  "room_number": "303",
  "room_type": "Executive Penthouse Suite",
  "price_per_night": 310.00,
  "status": "AVAILABLE"
}
```
- **Response** (`200 OK`):
```json
{
  "room_id": 11,
  "room_number": "303",
  "room_type": "Executive Penthouse Suite",
  "price_per_night": 310.00,
  "status": "AVAILABLE"
}
```

### 2.5 Quick Patch Room Status (Maintenance Toggle)
Switches room status between `AVAILABLE`, `CLOSED`, and `BOOKED`.

- **Method**: `PATCH`
- **URL**: `/api/rooms/:roomId/status`
- **Request Body**:
```json
{
  "status": "CLOSED"
}
```
- **Response** (`200 OK`):
```json
{
  "room_id": 11,
  "room_number": "303",
  "room_type": "Executive Penthouse Suite",
  "price_per_night": 310.00,
  "status": "CLOSED"
}
```

### 2.6 Delete Room
- **Method**: `DELETE`
- **URL**: `/api/rooms/:roomId`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "room_id": 11
}
```

### 2.7 Search Available Rooms by Stay Dates
Returns rooms that have status `AVAILABLE` and are not assigned to conflicting `PENDING` or `ACTIVE` bookings over the requested date range (`check_in` < `check_out`).

- **Method**: `GET`
- **URL**: `/api/rooms/available`
- **Query Parameters**:
  - `check_in` (`YYYY-MM-DD` - required)
  - `check_out` (`YYYY-MM-DD` - required)
  - `room_type` (string - optional)
- **Response** (`200 OK`):
```json
[
  {
    "room_id": 1,
    "room_number": "101",
    "room_type": "Single Standard",
    "price_per_night": 65.00,
    "status": "AVAILABLE"
  },
  {
    "room_id": 3,
    "room_number": "103",
    "room_type": "Double Standard",
    "price_per_night": 85.00,
    "status": "AVAILABLE"
  }
]
```

---

## 3. Meal Plans (`MEAL_PLAN`)

### 3.1 List All Meal Plans
- **Method**: `GET`
- **URL**: `/api/meal-plans`
- **Response** (`200 OK`):
```json
[
  {
    "meal_plan_id": 1,
    "code": "RO",
    "name": "Room Only",
    "description": "Room accommodation only without breakfast or meals included.",
    "is_active": 1
  },
  {
    "meal_plan_id": 2,
    "code": "BB",
    "name": "Breakfast Included",
    "description": "Full international breakfast buffet included daily.",
    "is_active": 1
  },
  {
    "meal_plan_id": 3,
    "code": "HB",
    "name": "Half Board",
    "description": "Breakfast buffet plus choice of lunch or dinner.",
    "is_active": 1
  },
  {
    "meal_plan_id": 4,
    "code": "FB",
    "name": "Full Board",
    "description": "All three daily meals included with selected drinks.",
    "is_active": 1
  }
]
```

### 3.2 Get Meal Plan by ID
- **Method**: `GET`
- **URL**: `/api/meal-plans/:id`
- **Response** (`200 OK`):
```json
{
  "meal_plan_id": 2,
  "code": "BB",
  "name": "Breakfast Included",
  "description": "Full international breakfast buffet included daily.",
  "is_active": 1
}
```

### 3.3 Create Meal Plan
- **Method**: `POST`
- **URL**: `/api/meal-plans`
- **Request Body**:
```json
{
  "code": "AI",
  "name": "All Inclusive",
  "description": "Unlimited meals, snacks, and alcoholic/non-alcoholic beverages.",
  "is_active": 1
}
```
- **Response** (`201 Created`):
```json
{
  "meal_plan_id": 5,
  "code": "AI",
  "name": "All Inclusive",
  "description": "Unlimited meals, snacks, and alcoholic/non-alcoholic beverages.",
  "is_active": 1
}
```

### 3.4 Update Meal Plan
- **Method**: `PUT`
- **URL**: `/api/meal-plans/:id`
- **Request Body**:
```json
{
  "code": "AI",
  "name": "All Inclusive Premium",
  "description": "Unlimited meals and premium drinks.",
  "is_active": 1
}
```
- **Response** (`200 OK`):
```json
{
  "meal_plan_id": 5,
  "code": "AI",
  "name": "All Inclusive Premium",
  "description": "Unlimited meals and premium drinks.",
  "is_active": 1
}
```

### 3.5 Delete Meal Plan
- **Method**: `DELETE`
- **URL**: `/api/meal-plans/:id`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "meal_plan_id": 5
}
```

---

## 4. Items & Supplies Inventory (`ITEM`)

Measuring method is strictly `'COUNT'` (discrete units) or `'CONTINUOUS'` (decimal metric weight/volume). Enforces `price_per_unit >= 0` and `stock_amount >= 0`.

### 4.1 List All Inventory Items
- **Method**: `GET`
- **URL**: `/api/items`
- **Query Parameters**:
  - `measuring_method` (`COUNT` | `CONTINUOUS` - optional)
  - `is_active` (`1` | `0` - optional)
- **Response** (`200 OK`):
```json
[
  {
    "item_id": 1,
    "name": "Mineral Water (500ml)",
    "price_per_unit": 2.50,
    "stock_amount": 145.00,
    "measuring_method": "COUNT",
    "is_active": 1
  },
  {
    "item_id": 7,
    "name": "Draft Craft Lager Beer (L)",
    "price_per_unit": 8.50,
    "stock_amount": 58.50,
    "measuring_method": "CONTINUOUS",
    "is_active": 1
  }
]
```

### 4.2 Get Item by ID
- **Method**: `GET`
- **URL**: `/api/items/:id`
- **Response** (`200 OK`):
```json
{
  "item_id": 7,
  "name": "Draft Craft Lager Beer (L)",
  "price_per_unit": 8.50,
  "stock_amount": 58.50,
  "measuring_method": "CONTINUOUS",
  "is_active": 1
}
```

### 4.3 Create New Item
- **Method**: `POST`
- **URL**: `/api/items`
- **Request Body**:
```json
{
  "name": "Sparkling Artesian Water (750ml)",
  "price_per_unit": 4.50,
  "stock_amount": 60.00,
  "measuring_method": "COUNT",
  "is_active": 1
}
```
- **Response** (`201 Created`):
```json
{
  "item_id": 11,
  "name": "Sparkling Artesian Water (750ml)",
  "price_per_unit": 4.50,
  "stock_amount": 60.00,
  "measuring_method": "COUNT",
  "is_active": 1
}
```

### 4.4 Update Item / Toggle Active Status
Used to update pricing or deactivate (`is_active = 0`) instead of deleting, preserving historical `ITEM_ORDER` ledgers.

- **Method**: `PUT`
- **URL**: `/api/items/:id`
- **Request Body**:
```json
{
  "name": "Sparkling Artesian Water (750ml)",
  "price_per_unit": 4.75,
  "stock_amount": 60.00,
  "measuring_method": "COUNT",
  "is_active": 0
}
```
- **Response** (`200 OK`):
```json
{
  "item_id": 11,
  "name": "Sparkling Artesian Water (750ml)",
  "price_per_unit": 4.75,
  "stock_amount": 60.00,
  "measuring_method": "COUNT",
  "is_active": 0
}
```

### 4.5 Adjust / Restock Item Stock
Adds (positive `delta_amount`) or writes off (negative `delta_amount`) stock. Enforces resulting `stock_amount >= 0`.

- **Method**: `POST`
- **URL**: `/api/items/:itemId/stock`
- **Request Body**:
```json
{
  "delta_amount": 20.00
}
```
- **Response** (`200 OK`):
```json
{
  "item_id": 11,
  "name": "Sparkling Artesian Water (750ml)",
  "price_per_unit": 4.75,
  "stock_amount": 80.00,
  "measuring_method": "COUNT",
  "is_active": 1
}
```

### 4.6 List Low-Stock Supplies
- **Method**: `GET`
- **URL**: `/api/items/low-stock`
- **Query Parameters**: `threshold` (number, default: 20)
- **Response** (`200 OK`):
```json
[
  {
    "item_id": 5,
    "name": "Cabernet Sauvignon Red Wine (750ml)",
    "price_per_unit": 38.00,
    "stock_amount": 18.00,
    "measuring_method": "COUNT",
    "is_active": 1
  },
  {
    "item_id": 8,
    "name": "Specialty Espresso Coffee Beans (kg)",
    "price_per_unit": 24.00,
    "stock_amount": 14.75,
    "measuring_method": "CONTINUOUS",
    "is_active": 1
  }
]
```

### 4.7 Delete Item
*(Note: Prefer `is_active: 0` in production to prevent FK violation with `ITEM_ORDER`).*

- **Method**: `DELETE`
- **URL**: `/api/items/:id`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "item_id": 11
}
```

---

## 5. Customers & Telegram Directory (`CUSTOMER`)

### 5.1 List / Search Customers
Supports real-time search by full name or phone number.

- **Method**: `GET`
- **URL**: `/api/customers`
- **Query Parameters**: `search` (string - optional)
- **Response** (`200 OK`):
```json
[
  {
    "customer_id": 1,
    "full_name": "Alexander Wright",
    "phone": "+1-555-019-2834",
    "telegram_chat_id": 849201928,
    "times_denied": 0,
    "last_denied_date": null,
    "created_at": "2026-10-01T10:00:00"
  },
  {
    "customer_id": 5,
    "full_name": "Liam O'Connor",
    "phone": "+353-87-123-4567",
    "telegram_chat_id": null,
    "times_denied": 2,
    "last_denied_date": "2026-11-01",
    "created_at": "2026-10-25T11:20:00"
  }
]
```

### 5.2 Get Customer by ID
- **Method**: `GET`
- **URL**: `/api/customers/:id`
- **Response** (`200 OK`):
```json
{
  "customer_id": 1,
  "full_name": "Alexander Wright",
  "phone": "+1-555-019-2834",
  "telegram_chat_id": 849201928,
  "times_denied": 0,
  "last_denied_date": null,
  "created_at": "2026-10-01T10:00:00"
}
```

### 5.3 Create Customer
- **Method**: `POST`
- **URL**: `/api/customers`
- **Request Body**:
```json
{
  "full_name": "Lucas Dupont",
  "phone": "+33-6-12-34-56-78",
  "telegram_chat_id": null
}
```
- **Response** (`201 Created`):
```json
{
  "customer_id": 7,
  "full_name": "Lucas Dupont",
  "phone": "+33-6-12-34-56-78",
  "telegram_chat_id": null,
  "times_denied": 0,
  "last_denied_date": null,
  "created_at": "2026-11-17T02:00:00"
}
```

### 5.4 Update Customer Details / Link Telegram Chat ID
Links a Telegram account or updates telephone/contact info.

- **Method**: `PUT`
- **URL**: `/api/customers/:id`
- **Request Body**:
```json
{
  "full_name": "Lucas Dupont",
  "phone": "+33-6-12-34-56-78",
  "telegram_chat_id": 928374123
}
```
- **Response** (`200 OK`):
```json
{
  "customer_id": 7,
  "full_name": "Lucas Dupont",
  "phone": "+33-6-12-34-56-78",
  "telegram_chat_id": 928374123,
  "times_denied": 0,
  "last_denied_date": null,
  "created_at": "2026-11-17T02:00:00"
}
```

### 5.5 Get Comprehensive Customer Dossier & History
Retrieves full CRM context including requests, confirmed bookings, staff incident notes, and message logs.

- **Method**: `GET`
- **URL**: `/api/customers/:customerId/history`
- **Response** (`200 OK`):
```json
{
  "customer": {
    "customer_id": 1,
    "full_name": "Alexander Wright",
    "phone": "+1-555-019-2834",
    "telegram_chat_id": 849201928,
    "times_denied": 0,
    "last_denied_date": null,
    "created_at": "2026-10-01T10:00:00"
  },
  "requests": [
    {
      "request_id": 1,
      "booking_ref": "BK-2026-98124",
      "check_in": "2026-11-15",
      "check_out": "2026-11-18",
      "rooms_wanted": 1,
      "total_price": 360.00,
      "status": "ACCEPTED"
    }
  ],
  "bookings": [
    {
      "booking_id": 1,
      "check_in": "2026-11-15",
      "check_out": "2026-11-18",
      "status": "ACTIVE"
    }
  ],
  "reports": [
    {
      "report_id": 1,
      "booking_id": 1,
      "note": "VIP guest prefers extra down feather pillows and early check-in.",
      "created_at": "2026-11-15T15:10:00"
    }
  ],
  "messages": [
    {
      "message_id": 1,
      "content": "Reservation BK-2026-98124 confirmed! Room(s): Room 201.",
      "status": "SENT",
      "sent_at": "2026-11-10T15:05:00"
    }
  ]
}
```

### 5.6 Delete Customer
- **Method**: `DELETE`
- **URL**: `/api/customers/:id`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "customer_id": 7
}
```

---

## 6. Booking Requests & Triage (`BOOKING_REQUEST`)

### 6.1 List Booking Requests
Returns reservations received via email/web channels. Supports filtering by status (`PENDING`, `ACCEPTED`, `DENIED`, `CANCELED`).

- **Method**: `GET`
- **URL**: `/api/booking-requests`
- **Query Parameters**:
  - `status` (`PENDING` | `ACCEPTED` | `DENIED` | `CANCELED` - optional)
  - `search` (guest_name, booking_ref - optional)
- **Response** (`200 OK`):
```json
[
  {
    "request_id": 4,
    "customer_id": 3,
    "email_message_id": "msg_bkg_20261115_004@booking.com",
    "booking_ref": "BK-2026-99238",
    "guest_name": "Marcus Vance",
    "guest_phone": "+1-555-018-7721",
    "meal_plan_id": 1,
    "special_request": "Early morning arrival at 06:30 AM. Need room ready immediately without surcharge.",
    "check_in": "2026-11-22",
    "check_out": "2026-11-25",
    "rooms_wanted": 1,
    "total_price": 255.00,
    "status": "PENDING",
    "received_at": "2026-11-15T18:00:00",
    "decided_by": null,
    "decided_at": null,
    "customer": {
      "customer_id": 3,
      "full_name": "Marcus Vance",
      "phone": "+1-555-018-7721",
      "telegram_chat_id": null,
      "times_denied": 1,
      "last_denied_date": "2026-10-15"
    },
    "meal_plan": {
      "meal_plan_id": 1,
      "code": "RO",
      "name": "Room Only"
    }
  }
]
```

### 6.2 Get Request by ID
- **Method**: `GET`
- **URL**: `/api/booking-requests/:id`
- **Response** (`200 OK`):
```json
{
  "request_id": 4,
  "customer_id": 3,
  "email_message_id": "msg_bkg_20261115_004@booking.com",
  "booking_ref": "BK-2026-99238",
  "guest_name": "Marcus Vance",
  "guest_phone": "+1-555-018-7721",
  "meal_plan_id": 1,
  "special_request": "Early morning arrival at 06:30 AM. Need room ready immediately without surcharge.",
  "check_in": "2026-11-22",
  "check_out": "2026-11-25",
  "rooms_wanted": 1,
  "total_price": 255.00,
  "status": "PENDING",
  "received_at": "2026-11-15T18:00:00",
  "decided_by": null,
  "decided_at": null,
  "customer": { ... },
  "meal_plan": { ... }
}
```

### 6.3 Create Inbound Booking Request
- **Method**: `POST`
- **URL**: `/api/booking-requests`
- **Request Body**:
```json
{
  "email_message_id": "msg_bkg_20261117_010@booking.com",
  "booking_ref": "BK-2026-99640",
  "guest_name": "Camila Rodriguez",
  "guest_phone": "+34-612-345-678",
  "meal_plan_id": 3,
  "special_request": "Ground floor room requested.",
  "check_in": "2026-12-01",
  "check_out": "2026-12-05",
  "rooms_wanted": 1,
  "total_price": 540.00
}
```
- **Response** (`201 Created`):
```json
{
  "request_id": 9,
  "customer_id": 6,
  "email_message_id": "msg_bkg_20261117_010@booking.com",
  "booking_ref": "BK-2026-99640",
  "guest_name": "Camila Rodriguez",
  "guest_phone": "+34-612-345-678",
  "meal_plan_id": 3,
  "special_request": "Ground floor room requested.",
  "check_in": "2026-12-01",
  "check_out": "2026-12-05",
  "rooms_wanted": 1,
  "total_price": 540.00,
  "status": "PENDING",
  "received_at": "2026-11-17T03:00:00",
  "decided_by": null,
  "decided_at": null
}
```

### 6.4 Accept Booking Request Workflow
Atomically executes the acceptance transaction:
1. Enforces selecting **exactly `rooms_wanted` rooms** where status is `'AVAILABLE'`.
2. Updates `meal_plan_id` on the request if provided.
3. Finds or creates the `CUSTOMER` record if `customer_id` was `NULL`.
4. Sets `decided_by = owner_id` and `decided_at = SYSDATETIME()`.
5. Updates request `status = 'ACCEPTED'`.
6. Inserts a new `BOOKING` record with `status = 'PENDING'`.
7. Inserts `BOOKING_ROOM` rows (copying each room's current `price_per_night`).
8. Updates assigned rooms status to `'BOOKED'`.
9. Logs automated confirmation `MESSAGE` (`SENT` if guest has `telegram_chat_id`, else `FAILED`).

- **Method**: `POST`
- **URL**: `/api/booking-requests/:requestId/accept`
- **Request Body**:
```json
{
  "roomIds": [1],
  "decidedBy": 1,
  "mealPlanId": 2,
  "specialNotes": "Room assigned away from elevator as requested."
}
```
- **Response** (`200 OK`):
```json
{
  "request": {
    "request_id": 4,
    "customer_id": 3,
    "booking_ref": "BK-2026-99238",
    "guest_name": "Marcus Vance",
    "status": "ACCEPTED",
    "decided_by": 1,
    "decided_at": "2026-11-17T03:15:00"
  },
  "booking": {
    "booking_id": 5,
    "request_id": 4,
    "customer_id": 3,
    "check_in": "2026-11-22",
    "check_out": "2026-11-25",
    "status": "PENDING",
    "created_at": "2026-11-17T03:15:00"
  },
  "rooms": [
    {
      "room_id": 1,
      "room_number": "101",
      "room_type": "Single Standard",
      "price_per_night": 65.00,
      "status": "BOOKED"
    }
  ]
}
```

### 6.5 Deny Booking Request Workflow
Atomically executes the denial transaction:
1. If `customer_id` is null, finds or creates `CUSTOMER` by phone/name.
2. Increments `customer.times_denied = times_denied + 1`.
3. Sets `customer.last_denied_date = CAST(GETDATE() AS DATE)`.
4. Updates request `status = 'DENIED'`, `decided_by = owner_id`, and `decided_at = SYSDATETIME()`.
5. Logs denial incident record into `CUSTOMER_REPORT`.
6. Dispatches denial `MESSAGE` (`SENT` if guest has `telegram_chat_id`, else `FAILED`).

- **Method**: `POST`
- **URL**: `/api/booking-requests/:requestId/deny`
- **Request Body**:
```json
{
  "reason": "No available rooms matching criteria",
  "decidedBy": 1
}
```
- **Response** (`200 OK`):
```json
{
  "request": {
    "request_id": 4,
    "customer_id": 3,
    "booking_ref": "BK-2026-99238",
    "status": "DENIED",
    "decided_by": 1,
    "decided_at": "2026-11-17T03:20:00"
  },
  "customer": {
    "customer_id": 3,
    "full_name": "Marcus Vance",
    "times_denied": 2,
    "last_denied_date": "2026-11-17"
  }
}
```

### 6.6 Cancel Booking Request
Sets request status to `'CANCELED'`.

- **Method**: `POST`
- **URL**: `/api/booking-requests/:requestId/cancel`
- **Response** (`200 OK`):
```json
{
  "request_id": 4,
  "status": "CANCELED"
}
```

---

## 7. Bookings & Lifecycle Stays (`BOOKING`)

Booking status flow: `PENDING` $\rightarrow$ `ACTIVE` $\rightarrow$ `FINISHED` (or `PENDING` $\rightarrow$ `CANCELED`).

### 7.1 List All Bookings
- **Method**: `GET`
- **URL**: `/api/bookings`
- **Query Parameters**: `status` (`PENDING` | `ACTIVE` | `FINISHED` | `CANCELED` - optional)
- **Response** (`200 OK`):
```json
[
  {
    "booking_id": 1,
    "request_id": 1,
    "customer_id": 1,
    "check_in": "2026-11-15",
    "check_out": "2026-11-18",
    "status": "ACTIVE",
    "created_at": "2026-11-10T15:05:00",
    "customer": {
      "customer_id": 1,
      "full_name": "Alexander Wright",
      "phone": "+1-555-019-2834"
    },
    "booking_request": {
      "booking_ref": "BK-2026-98124",
      "meal_plan_id": 2
    },
    "rooms": [
      {
        "room_id": 5,
        "room_number": "201",
        "room_type": "Double Superior Ocean View",
        "price_per_night": 120.00
      }
    ]
  }
]
```

### 7.2 Get Booking Details & Live Folio
Returns complete stay dossier with room charges, item purchases, payments, and balance due.

- **Method**: `GET`
- **URL**: `/api/bookings/:id`
- **Response** (`200 OK`):
```json
{
  "booking_id": 1,
  "request_id": 1,
  "customer_id": 1,
  "check_in": "2026-11-15",
  "check_out": "2026-11-18",
  "status": "ACTIVE",
  "created_at": "2026-11-10T15:05:00",
  "customer": {
    "customer_id": 1,
    "full_name": "Alexander Wright",
    "phone": "+1-555-019-2834",
    "telegram_chat_id": 849201928
  },
  "booking_request": {
    "request_id": 1,
    "booking_ref": "BK-2026-98124",
    "special_request": "Quiet room away from lift."
  },
  "meal_plan": {
    "meal_plan_id": 2,
    "code": "BB",
    "name": "Breakfast Included"
  },
  "rooms": [
    {
      "room_id": 5,
      "room_number": "201",
      "room_type": "Double Superior Ocean View",
      "price_per_night": 120.00
    }
  ],
  "item_orders": [
    {
      "order_id": 1,
      "booking_id": 1,
      "item_id": 1,
      "amount": 2.00,
      "unit_price": 2.50,
      "item_name": "Mineral Water (500ml)",
      "measuring_method": "COUNT",
      "total_price": 5.00,
      "ordered_at": "2026-11-15T18:30:00"
    }
  ],
  "payments": [
    {
      "payment_id": 1,
      "booking_id": 1,
      "amount": 200.00,
      "method": "CARD",
      "paid_at": "2026-11-15T15:20:00"
    }
  ],
  "billing": {
    "nights": 3,
    "room_charges": 360.00,
    "order_charges": 5.00,
    "total_charges": 365.00,
    "total_paid": 200.00,
    "balance_due": 165.00
  }
}
```

### 7.3 Check In Guest (`PENDING` $\rightarrow$ `ACTIVE`)
Updates `BOOKING.status` to `'ACTIVE'` and confirms rooms remain `'BOOKED'`.

- **Method**: `POST`
- **URL**: `/api/bookings/:bookingId/check-in`
- **Response** (`200 OK`): Full booking payload with `"status": "ACTIVE"`.

### 7.4 Check Out Guest (`ACTIVE` $\rightarrow$ `FINISHED`)
Updates `BOOKING.status` to `'FINISHED'` and resets assigned rooms back to `'AVAILABLE'`.

- **Method**: `POST`
- **URL**: `/api/bookings/:bookingId/check-out`
- **Response** (`200 OK`): Full booking payload with `"status": "FINISHED"`.

### 7.5 Cancel No-Show Reservation (`PENDING` $\rightarrow$ `CANCELED`)
Sets `BOOKING.status = 'CANCELED'` and immediately resets assigned rooms back to `'AVAILABLE'`.

- **Method**: `POST`
- **URL**: `/api/bookings/:bookingId/cancel`
- **Response** (`200 OK`): Full booking payload with `"status": "CANCELED"`.

### 7.6 Get Booking Billing Summary
- **Method**: `GET`
- **URL**: `/api/bookings/:bookingId/billing`
- **Response** (`200 OK`):
```json
{
  "nights": 3,
  "room_charges": 360.00,
  "order_charges": 5.00,
  "total_charges": 365.00,
  "total_paid": 200.00,
  "balance_due": 165.00
}
```

---

## 8. Room Assignments (`BOOKING_ROOM`)

### 8.1 List Assigned Rooms for a Booking
- **Method**: `GET`
- **URL**: `/api/bookings/:bookingId/rooms`
- **Response** (`200 OK`):
```json
[
  {
    "booking_id": 1,
    "room_id": 5,
    "price_per_night": 120.00,
    "room": {
      "room_id": 5,
      "room_number": "201",
      "room_type": "Double Superior Ocean View",
      "price_per_night": 120.00,
      "status": "BOOKED"
    }
  }
]
```

### 8.2 Assign Room to Booking
- **Method**: `POST`
- **URL**: `/api/bookings/:bookingId/rooms`
- **Request Body**:
```json
{
  "roomId": 6,
  "pricePerNight": 120.00
}
```
- **Response** (`201 Created`):
```json
{
  "booking_id": 1,
  "room_id": 6,
  "price_per_night": 120.00
}
```

### 8.3 Remove Room from Booking
- **Method**: `DELETE`
- **URL**: `/api/bookings/:bookingId/rooms/:roomId`
- **Response** (`200 OK`):
```json
{
  "success": true
}
```

---

## 9. Item Orders & Minibar Purchases (`ITEM_ORDER`)

Enforces `amount > 0` and `amount <= stock_amount`. Supports integer quantities for `COUNT` items and decimal quantities (e.g. `1.50`) for `CONTINUOUS` items. Deducts inventory automatically.

### 9.1 List Orders by Booking
- **Method**: `GET`
- **URL**: `/api/item-orders`
- **Query Parameters**: `booking_id` (integer - optional)
- **Response** (`200 OK`):
```json
[
  {
    "order_id": 1,
    "booking_id": 1,
    "item_id": 1,
    "amount": 2.00,
    "unit_price": 2.50,
    "ordered_at": "2026-11-15T18:30:00",
    "item_name": "Mineral Water (500ml)",
    "measuring_method": "COUNT",
    "total_price": 5.00
  },
  {
    "order_id": 2,
    "booking_id": 1,
    "item_id": 7,
    "amount": 1.50,
    "unit_price": 8.50,
    "ordered_at": "2026-11-16T21:15:00",
    "item_name": "Draft Craft Lager Beer (L)",
    "measuring_method": "CONTINUOUS",
    "total_price": 12.75
  }
]
```

### 9.2 Record New Item Order
- **Method**: `POST`
- **URL**: `/api/item-orders`
- **Request Body**:
```json
{
  "bookingId": 1,
  "itemId": 7,
  "amount": 1.50,
  "unitPrice": 8.50
}
```
- **Response** (`201 Created`):
```json
{
  "order_id": 3,
  "booking_id": 1,
  "item_id": 7,
  "amount": 1.50,
  "unit_price": 8.50,
  "ordered_at": "2026-11-17T03:30:00",
  "item_name": "Draft Craft Lager Beer (L)",
  "measuring_method": "CONTINUOUS",
  "total_price": 12.75
}
```

### 9.3 Delete / Cancel Item Order (Restocks Inventory)
Removes the charge from the folio and restores the ordered amount back into `ITEM.stock_amount`.

- **Method**: `DELETE`
- **URL**: `/api/item-orders/:orderId`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "order_id": 3
}
```

---

## 10. Payments & Settlement (`PAYMENT`)

Enforces `amount > 0` and `method IN ('CASH', 'CARD', 'OTHER')`.

### 10.1 List Payments by Booking
- **Method**: `GET`
- **URL**: `/api/payments`
- **Query Parameters**: `booking_id` (integer - optional)
- **Response** (`200 OK`):
```json
[
  {
    "payment_id": 1,
    "booking_id": 1,
    "amount": 200.00,
    "method": "CARD",
    "paid_at": "2026-11-15T15:20:00"
  }
]
```

### 10.2 Record Payment
- **Method**: `POST`
- **URL**: `/api/payments`
- **Request Body**:
```json
{
  "bookingId": 1,
  "amount": 165.00,
  "method": "CASH"
}
```
- **Response** (`201 Created`):
```json
{
  "payment_id": 5,
  "booking_id": 1,
  "amount": 165.00,
  "method": "CASH",
  "paid_at": "2026-11-17T03:35:00"
}
```

### 10.3 Delete Payment
- **Method**: `DELETE`
- **URL**: `/api/payments/:paymentId`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "payment_id": 5
}
```

---

## 11. Telegram Communications Log (`MESSAGE`)

Tracks automated notices dispatched to guests. Status is strictly `'SENT'` (when guest has a linked `telegram_chat_id`) or `'FAILED'` (when chat ID is null).

### 11.1 List Message Delivery Logs
- **Method**: `GET`
- **URL**: `/api/messages`
- **Query Parameters**: `customer_id` (integer - optional)
- **Response** (`200 OK`):
```json
[
  {
    "message_id": 1,
    "customer_id": 1,
    "customer_name": "Alexander Wright",
    "content": "Reservation BK-2026-98124 confirmed! Room(s): Room 201. Check-in: 2026-11-15, Check-out: 2026-11-18.",
    "status": "SENT",
    "sent_at": "2026-11-10T15:05:00"
  },
  {
    "message_id": 4,
    "customer_id": 3,
    "customer_name": "Marcus Vance",
    "content": "Regarding your booking request BK-2026-99238: Unfortunately, we are unable to accept this reservation (No available rooms matching criteria).",
    "status": "FAILED",
    "sent_at": "2026-11-15T18:30:00"
  }
]
```

### 11.2 Dispatch Message
- **Method**: `POST`
- **URL**: `/api/messages`
- **Request Body**:
```json
{
  "customerId": 1,
  "content": "Welcome to Grand Horizon Hotel! Your room keycard is ready at reception.",
  "status": "SENT"
}
```
- **Response** (`201 Created`):
```json
{
  "message_id": 10,
  "customer_id": 1,
  "content": "Welcome to Grand Horizon Hotel! Your room keycard is ready at reception.",
  "status": "SENT",
  "sent_at": "2026-11-17T03:40:00"
}
```

---

## 12. Customer Reports & Incident Dossier (`CUSTOMER_REPORT`)

Stores staff notes, no-show incident records, damage assessments, or special guest observations.

### 12.1 List Customer Reports
- **Method**: `GET`
- **URL**: `/api/customer-reports`
- **Query Parameters**:
  - `customer_id` (integer - optional)
  - `booking_id` (integer - optional)
- **Response** (`200 OK`):
```json
[
  {
    "report_id": 1,
    "customer_id": 3,
    "booking_id": null,
    "note": "Booking request BK-2026-91102 denied by staff. Reason: Insisted on smoking allowed in room.",
    "created_at": "2026-10-15T09:00:00",
    "customer_name": "Marcus Vance",
    "booking_ref": null
  },
  {
    "report_id": 2,
    "customer_id": 1,
    "booking_id": 1,
    "note": "[No-Show Cancellation] Guest failed to arrive by midnight cutoff; telephone contact unanswered.",
    "created_at": "2026-11-16T00:15:00",
    "customer_name": "Alexander Wright",
    "booking_ref": "Booking #1"
  }
]
```

### 12.2 Create Customer Report Note
- **Method**: `POST`
- **URL**: `/api/customer-reports`
- **Request Body**:
```json
{
  "customerId": 3,
  "bookingId": 4,
  "note": "VIP guest requested feather-free synthetic bedding and extra hangers."
}
```
- **Response** (`201 Created`):
```json
{
  "report_id": 5,
  "customer_id": 3,
  "booking_id": 4,
  "note": "VIP guest requested feather-free synthetic bedding and extra hangers.",
  "created_at": "2026-11-17T03:45:00"
}
```

### 12.3 Update Customer Report Note
- **Method**: `PUT`
- **URL**: `/api/customer-reports/:id`
- **Request Body**:
```json
{
  "note": "Updated note: Guest allergy confirmed with reception."
}
```
- **Response** (`200 OK`):
```json
{
  "report_id": 5,
  "customer_id": 3,
  "booking_id": 4,
  "note": "Updated note: Guest allergy confirmed with reception.",
  "created_at": "2026-11-17T03:45:00"
}
```

### 12.4 Delete Customer Report Note
- **Method**: `DELETE`
- **URL**: `/api/customer-reports/:id`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "report_id": 5
}
```

---

## 13. Dashboard & Metrics (`DASHBOARD`)

### 13.1 Get Comprehensive Property Metrics
Provides property health metrics, live room availability, booking request backlog, and inventory warnings.

- **Method**: `GET`
- **URL**: `/api/dashboard/summary`
- **Response** (`200 OK`):
```json
{
  "rooms": {
    "total": 10,
    "available": 5,
    "booked": 4,
    "closed": 1
  },
  "booking_requests": {
    "total": 8,
    "pending": 2,
    "accepted": 4,
    "denied": 2,
    "canceled": 0
  },
  "bookings": {
    "total": 4,
    "pending": 1,
    "active": 3,
    "finished": 0,
    "canceled": 0
  },
  "total_revenue": 1450.00,
  "low_stock_items_count": 2,
  "low_stock_items": [
    {
      "item_id": 5,
      "name": "Cabernet Sauvignon Red Wine (750ml)",
      "price_per_unit": 38.00,
      "stock_amount": 18.00,
      "measuring_method": "COUNT",
      "is_active": 1
    }
  ],
  "recent_pending_requests": [
    {
      "request_id": 4,
      "booking_ref": "BK-2026-99238",
      "guest_name": "Marcus Vance",
      "check_in": "2026-11-22",
      "check_out": "2026-11-25",
      "rooms_wanted": 1,
      "total_price": 255.00,
      "status": "PENDING"
    }
  ]
}
```

---

## 14. System Database Utilities (`SYSTEM`)

Administrative utilities for database seeding and backup migration.

### 14.1 Reset Database
Re-seeds database tables to factory test data.

- **Method**: `POST`
- **URL**: `/api/system/reset`
- **Response** (`200 OK`):
```json
{
  "success": true,
  "message": "Database reset to seed data"
}
```

### 14.2 Export Database Backup
- **Method**: `GET`
- **URL**: `/api/system/export`
- **Response** (`200 OK`): Full JSON database dump containing all 12 tables.

### 14.3 Import Database Backup
- **Method**: `POST`
- **URL**: `/api/system/import`
- **Request Body**:
```json
{
  "data": {
    "shop_owner": [ ... ],
    "room": [ ... ],
    "meal_plan": [ ... ],
    "customer": [ ... ],
    "booking_request": [ ... ],
    "booking": [ ... ],
    "booking_room": [ ... ],
    "item": [ ... ],
    "item_order": [ ... ],
    "payment": [ ... ],
    "message": [ ... ],
    "customer_report": [ ... ]
  }
}
```
- **Response** (`200 OK`):
```json
{
  "success": true,
  "imported_at": "2026-11-17T03:50:00"
}
```
