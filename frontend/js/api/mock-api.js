/**
 * mock-api.js
 * 
 * Complete localStorage-backed mock database and API for RoomBookingApp.
 * Pre-populated with realistic sample hotel data matching every table
 * and exact column name in UPDATED_SQLQuery1_v2.0.sql:
 * 
 * Tables:
 *  1. shop_owner (owner_id, username, password_hash, salt, full_name, created_at, last_login_at)
 *  2. item (item_id, name, price_per_unit, stock_amount, measuring_method, is_active) - COUNT & CONTINUOUS
 *  3. room (room_id, room_number, room_type, price_per_night, status)
 *  4. meal_plan (meal_plan_id, code, name, description, is_active)
 *  5. customer (customer_id, full_name, phone, telegram_chat_id, times_denied, last_denied_date, created_at)
 *  6. booking_request (request_id, customer_id, email_message_id, booking_ref, guest_name, guest_phone,
 *                     meal_plan_id, special_request, check_in, check_out, rooms_wanted, total_price,
 *                     status, received_at, decided_by, decided_at)
 *  7. booking (booking_id, request_id, customer_id, check_in, check_out, status, created_at)
 *  8. booking_room (booking_id, room_id, price_per_night)
 *  9. item_order (order_id, booking_id, item_id, amount, unit_price, ordered_at)
 * 10. payment (payment_id, booking_id, amount, method, paid_at)
 * 11. message (message_id, customer_id, content, status, sent_at)
 * 12. customer_report (report_id, customer_id, booking_id, note, created_at)
 */

const STORAGE_KEY = 'HOTEL_DB_MOCK_STORAGE_v2';

// In-memory fallback if localStorage is disabled or blocked in the browser environment
let inMemoryStore = null;

function isLocalStorageAvailable() {
  try {
    if (typeof window === 'undefined' || !window.localStorage) return false;
    const testKey = '__test_storage__';
    window.localStorage.setItem(testKey, testKey);
    window.localStorage.removeItem(testKey);
    return true;
  } catch (e) {
    return false;
  }
}

/**
 * Initial seed database matching exact SQL schema v2.0
 */
const INITIAL_DATABASE = {
  shop_owner: [
    {
      owner_id: 1,
      username: 'admin',
      password_hash: 'c8b6b23d9b626f9872e452a32c694179e8a002b55f7bc86e06b9b2c28be581e2',
      salt: 'e92bc491f0984812',
      full_name: 'Eleanor Vance',
      created_at: '2026-01-10T08:00:00',
      last_login_at: '2026-11-16T22:30:00'
    },
    {
      owner_id: 2,
      username: 'david_desk',
      password_hash: '9a842138ebda21f98bc1982302e1c4598129ca098bbde9123891caeb239108e4',
      salt: 'f498c1902834bca8',
      full_name: 'David Miller',
      created_at: '2026-02-15T09:30:00',
      last_login_at: '2026-11-16T18:45:00'
    }
  ],

  meal_plan: [
    {
      meal_plan_id: 1,
      code: 'RO',
      name: 'Room Only',
      description: 'Room accommodation only without breakfast or meals included.',
      is_active: 1
    },
    {
      meal_plan_id: 2,
      code: 'BB',
      name: 'Breakfast Included',
      description: 'Full international breakfast buffet included daily at the Grand Dining Room.',
      is_active: 1
    },
    {
      meal_plan_id: 3,
      code: 'HB',
      name: 'Half Board',
      description: 'Breakfast buffet plus choice of lunch or dinner at Azure Restaurant.',
      is_active: 1
    },
    {
      meal_plan_id: 4,
      code: 'FB',
      name: 'Full Board',
      description: 'All three daily meals (breakfast, lunch, and dinner) included with selected drinks.',
      is_active: 1
    }
  ],

  room: [
    {
      room_id: 1,
      room_number: '101',
      room_type: 'Single Standard',
      price_per_night: 65.00,
      status: 'AVAILABLE'
    },
    {
      room_id: 2,
      room_number: '102',
      room_type: 'Single Standard',
      price_per_night: 65.00,
      status: 'BOOKED'
    },
    {
      room_id: 3,
      room_number: '103',
      room_type: 'Double Standard',
      price_per_night: 85.00,
      status: 'AVAILABLE'
    },
    {
      room_id: 4,
      room_number: '104',
      room_type: 'Double Standard',
      price_per_night: 85.00,
      status: 'CLOSED'
    },
    {
      room_id: 5,
      room_number: '201',
      room_type: 'Double Superior Ocean View',
      price_per_night: 120.00,
      status: 'BOOKED'
    },
    {
      room_id: 6,
      room_number: '202',
      room_type: 'Double Superior Ocean View',
      price_per_night: 120.00,
      status: 'AVAILABLE'
    },
    {
      room_id: 7,
      room_number: '203',
      room_type: 'Family Suite',
      price_per_night: 180.00,
      status: 'BOOKED'
    },
    {
      room_id: 8,
      room_number: '204',
      room_type: 'Family Suite',
      price_per_night: 180.00,
      status: 'AVAILABLE'
    },
    {
      room_id: 9,
      room_number: '301',
      room_type: 'Executive Penthouse Suite',
      price_per_night: 280.00,
      status: 'BOOKED'
    },
    {
      room_id: 10,
      room_number: '302',
      room_type: 'Executive Penthouse Suite',
      price_per_night: 280.00,
      status: 'AVAILABLE'
    }
  ],

  item: [
    // COUNT items
    {
      item_id: 1,
      name: 'Mineral Water (500ml)',
      price_per_unit: 2.50,
      stock_amount: 145.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    {
      item_id: 2,
      name: 'Extra Luxury Bath Towel',
      price_per_unit: 6.00,
      stock_amount: 35.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    {
      item_id: 3,
      name: 'Organic Snack Pack (Nuts & Berries)',
      price_per_unit: 4.50,
      stock_amount: 48.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    {
      item_id: 4,
      name: 'Soft Drink Can (Cola 330ml)',
      price_per_unit: 3.00,
      stock_amount: 80.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    {
      item_id: 5,
      name: 'Cabernet Sauvignon Red Wine (750ml)',
      price_per_unit: 38.00,
      stock_amount: 18.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    {
      item_id: 6,
      name: 'Hygiene & Shaving Travel Kit',
      price_per_unit: 5.00,
      stock_amount: 50.00,
      measuring_method: 'COUNT',
      is_active: 1
    },
    // CONTINUOUS items
    {
      item_id: 7,
      name: 'Draft Craft Lager Beer (L)',
      price_per_unit: 8.50,
      stock_amount: 58.50,
      measuring_method: 'CONTINUOUS',
      is_active: 1
    },
    {
      item_id: 8,
      name: 'Specialty Espresso Coffee Beans (kg)',
      price_per_unit: 24.00,
      stock_amount: 14.75,
      measuring_method: 'CONTINUOUS',
      is_active: 1
    },
    {
      item_id: 9,
      name: 'Express Laundry Service (kg)',
      price_per_unit: 7.00,
      stock_amount: 100.00,
      measuring_method: 'CONTINUOUS',
      is_active: 1
    },
    {
      item_id: 10,
      name: 'Freshly Pressed Orange Juice (L)',
      price_per_unit: 9.50,
      stock_amount: 22.80,
      measuring_method: 'CONTINUOUS',
      is_active: 1
    }
  ],

  customer: [
    {
      customer_id: 1,
      full_name: 'Alexander Wright',
      phone: '+1-555-019-2834',
      telegram_chat_id: 849201928,
      times_denied: 0,
      last_denied_date: null,
      created_at: '2026-10-01T10:00:00'
    },
    {
      customer_id: 2,
      full_name: 'Elena Rostova',
      phone: '+44-7700-900451',
      telegram_chat_id: 593820112,
      times_denied: 0,
      last_denied_date: null,
      created_at: '2026-10-12T14:15:00'
    },
    {
      customer_id: 3,
      full_name: 'Marcus Vance',
      phone: '+1-555-018-7721',
      telegram_chat_id: null,
      times_denied: 1,
      last_denied_date: '2026-10-15',
      created_at: '2026-10-14T16:00:00'
    },
    {
      customer_id: 4,
      full_name: 'Sophia Chen',
      phone: '+65-8123-4567',
      telegram_chat_id: 712903485,
      times_denied: 0,
      last_denied_date: null,
      created_at: '2026-10-20T09:40:00'
    },
    {
      customer_id: 5,
      full_name: 'Liam O\'Connor',
      phone: '+353-87-123-4567',
      telegram_chat_id: null,
      times_denied: 2,
      last_denied_date: '2026-11-01',
      created_at: '2026-10-25T11:20:00'
    },
    {
      customer_id: 6,
      full_name: 'Camila Rodriguez',
      phone: '+34-612-345-678',
      telegram_chat_id: 923847162,
      times_denied: 0,
      last_denied_date: null,
      created_at: '2026-11-05T13:30:00'
    }
  ],

  booking_request: [
    {
      request_id: 1,
      customer_id: 1,
      email_message_id: 'msg_bkg_20261110_001@booking.com',
      booking_ref: 'BK-2026-98124',
      guest_name: 'Alexander Wright',
      guest_phone: '+1-555-019-2834',
      meal_plan_id: 2,
      special_request: 'Quiet high-floor room preferred away from lift. Late check-in expected around 10:30 PM.',
      check_in: '2026-11-15',
      check_out: '2026-11-18',
      rooms_wanted: 1,
      total_price: 360.00,
      status: 'ACCEPTED',
      received_at: '2026-11-10T14:22:00',
      decided_by: 1,
      decided_at: '2026-11-10T15:05:00'
    },
    {
      request_id: 2,
      customer_id: 2,
      email_message_id: 'msg_bkg_20261112_002@booking.com',
      booking_ref: 'BK-2026-98452',
      guest_name: 'Elena Rostova',
      guest_phone: '+44-7700-900451',
      meal_plan_id: 4,
      special_request: 'Celebrating 5th anniversary. Honeymoon arrangement and extra synthetic pillows requested.',
      check_in: '2026-11-16',
      check_out: '2026-11-20',
      rooms_wanted: 1,
      total_price: 1120.00,
      status: 'ACCEPTED',
      received_at: '2026-11-12T09:15:30',
      decided_by: 2,
      decided_at: '2026-11-12T10:00:15'
    },
    {
      request_id: 3,
      customer_id: 4,
      email_message_id: 'msg_bkg_20261114_003@booking.com',
      booking_ref: 'BK-2026-99015',
      guest_name: 'Sophia Chen',
      guest_phone: '+65-8123-4567',
      meal_plan_id: 2,
      special_request: 'Traveling with 18-month-old infant. Baby crib/cot and ground floor room needed.',
      check_in: '2026-11-16',
      check_out: '2026-11-19',
      rooms_wanted: 1,
      total_price: 195.00,
      status: 'ACCEPTED',
      received_at: '2026-11-14T11:45:10',
      decided_by: 1,
      decided_at: '2026-11-14T12:20:00'
    },
    {
      request_id: 4,
      customer_id: 3,
      email_message_id: 'msg_bkg_20261115_004@booking.com',
      booking_ref: 'BK-2026-99238',
      guest_name: 'Marcus Vance',
      guest_phone: '+1-555-018-7721',
      meal_plan_id: 1,
      special_request: 'Early morning arrival at 06:30 AM. Need room ready immediately without surcharge.',
      check_in: '2026-11-22',
      check_out: '2026-11-25',
      rooms_wanted: 1,
      total_price: 255.00,
      status: 'PENDING',
      received_at: '2026-11-15T18:00:00',
      decided_by: null,
      decided_at: null
    },
    {
      request_id: 5,
      customer_id: 6,
      email_message_id: 'msg_bkg_20261116_005@booking.com',
      booking_ref: 'BK-2026-99411',
      guest_name: 'Camila Rodriguez',
      guest_phone: '+34-612-345-678',
      meal_plan_id: 3,
      special_request: 'Airport shuttle pickup requested at 14:00 terminal 2. Vegetarian half-board options.',
      check_in: '2026-11-20',
      check_out: '2026-11-24',
      rooms_wanted: 1,
      total_price: 480.00,
      status: 'ACCEPTED',
      received_at: '2026-11-16T10:15:00',
      decided_by: 1,
      decided_at: '2026-11-16T11:00:00'
    },
    {
      request_id: 6,
      customer_id: 3,
      email_message_id: 'msg_bkg_20261014_099@booking.com',
      booking_ref: 'BK-2026-91102',
      guest_name: 'Marcus Vance',
      guest_phone: '+1-555-018-7721',
      meal_plan_id: 1,
      special_request: 'Insisted on smoking allowed in room despite non-smoking property policy.',
      check_in: '2026-10-18',
      check_out: '2026-10-20',
      rooms_wanted: 1,
      total_price: 170.00,
      status: 'DENIED',
      received_at: '2026-10-14T16:00:00',
      decided_by: 1,
      decided_at: '2026-10-15T09:00:00'
    },
    {
      request_id: 7,
      customer_id: 5,
      email_message_id: 'msg_bkg_20261028_105@booking.com',
      booking_ref: 'BK-2026-92455',
      guest_name: 'Liam O\'Connor',
      guest_phone: '+353-87-123-4567',
      meal_plan_id: 2,
      special_request: 'Requires third-party unbacked voucher payment upon departure.',
      check_in: '2026-11-03',
      check_out: '2026-11-06',
      rooms_wanted: 1,
      total_price: 255.00,
      status: 'DENIED',
      received_at: '2026-10-28T14:10:00',
      decided_by: 2,
      decided_at: '2026-10-29T11:00:00'
    },
    {
      request_id: 8,
      customer_id: null,
      email_message_id: 'msg_bkg_20261117_008@booking.com',
      booking_ref: 'BK-2026-99580',
      guest_name: 'Lucas Dupont',
      guest_phone: '+33-6-12-34-56-78',
      meal_plan_id: null,
      special_request: null,
      check_in: '2026-11-25',
      check_out: '2026-11-28',
      rooms_wanted: 1,
      total_price: 240.00,
      status: 'PENDING',
      received_at: '2026-11-17T01:30:00',
      decided_by: null,
      decided_at: null
    }
  ],

  booking: [
    {
      booking_id: 1,
      request_id: 1,
      customer_id: 1,
      check_in: '2026-11-15',
      check_out: '2026-11-18',
      status: 'ACTIVE',
      created_at: '2026-11-10T15:05:00'
    },
    {
      booking_id: 2,
      request_id: 2,
      customer_id: 2,
      check_in: '2026-11-16',
      check_out: '2026-11-20',
      status: 'ACTIVE',
      created_at: '2026-11-12T10:00:15'
    },
    {
      booking_id: 3,
      request_id: 3,
      customer_id: 4,
      check_in: '2026-11-16',
      check_out: '2026-11-19',
      status: 'ACTIVE',
      created_at: '2026-11-14T12:20:00'
    },
    {
      booking_id: 4,
      request_id: 5,
      customer_id: 6,
      check_in: '2026-11-20',
      check_out: '2026-11-24',
      status: 'PENDING',
      created_at: '2026-11-16T11:00:00'
    }
  ],

  booking_room: [
    {
      booking_id: 1,
      room_id: 5,
      price_per_night: 120.00
    },
    {
      booking_id: 2,
      room_id: 9,
      price_per_night: 280.00
    },
    {
      booking_id: 3,
      room_id: 2,
      price_per_night: 65.00
    },
    {
      booking_id: 4,
      room_id: 7,
      price_per_night: 180.00
    }
  ],

  item_order: [
    {
      order_id: 1,
      booking_id: 1,
      item_id: 1,
      amount: 2.00,
      unit_price: 2.50,
      ordered_at: '2026-11-15T19:30:00'
    },
    {
      order_id: 2,
      booking_id: 1,
      item_id: 7,
      amount: 1.50,
      unit_price: 8.50,
      ordered_at: '2026-11-15T21:10:00'
    },
    {
      order_id: 3,
      booking_id: 2,
      item_id: 5,
      amount: 1.00,
      unit_price: 38.00,
      ordered_at: '2026-11-16T18:40:00'
    },
    {
      order_id: 4,
      booking_id: 2,
      item_id: 9,
      amount: 3.40,
      unit_price: 7.00,
      ordered_at: '2026-11-16T10:15:00'
    },
    {
      order_id: 5,
      booking_id: 3,
      item_id: 10,
      amount: 1.25,
      unit_price: 9.50,
      ordered_at: '2026-11-16T08:50:00'
    },
    {
      order_id: 6,
      booking_id: 3,
      item_id: 3,
      amount: 2.00,
      unit_price: 4.50,
      ordered_at: '2026-11-16T15:20:00'
    }
  ],

  payment: [
    {
      payment_id: 1,
      booking_id: 1,
      amount: 200.00,
      method: 'CARD',
      paid_at: '2026-11-15T15:10:00'
    },
    {
      payment_id: 2,
      booking_id: 2,
      amount: 600.00,
      method: 'CARD',
      paid_at: '2026-11-16T12:00:00'
    },
    {
      payment_id: 3,
      booking_id: 3,
      amount: 195.00,
      method: 'CASH',
      paid_at: '2026-11-16T14:30:00'
    }
  ],

  message: [
    {
      message_id: 1,
      customer_id: 1,
      content: 'Your reservation BK-2026-98124 at Grand Azure Hotel has been confirmed! We look forward to welcoming you on Nov 15.',
      status: 'SENT',
      sent_at: '2026-11-10T15:06:00'
    },
    {
      message_id: 2,
      customer_id: 2,
      content: 'Welcome to Grand Azure Hotel, Elena! Room 301 is ready for your arrival. Honeymoon amenities have been placed.',
      status: 'SENT',
      sent_at: '2026-11-16T11:30:00'
    },
    {
      message_id: 3,
      customer_id: 4,
      content: 'Reservation BK-2026-99015 confirmed. A baby cot has been prepared in Room 102.',
      status: 'SENT',
      sent_at: '2026-11-14T12:21:00'
    },
    {
      message_id: 4,
      customer_id: 3,
      content: 'Regarding your reservation request BK-2026-91102: Unfortunately we could not accommodate non-standard smoking room requirements.',
      status: 'SENT',
      sent_at: '2026-10-15T09:05:00'
    },
    {
      message_id: 5,
      customer_id: 5,
      content: 'Telegram notification dispatch failed: Invalid chat ID or user has not initiated bot chat.',
      status: 'FAILED',
      sent_at: '2026-10-29T11:01:00'
    }
  ],

  customer_report: [
    {
      report_id: 1,
      customer_id: 1,
      booking_id: 1,
      note: 'Guest commended fast check-in and complimentary water. Inquired about late checkout on Nov 18.',
      created_at: '2026-11-15T16:00:00'
    },
    {
      report_id: 2,
      customer_id: 2,
      booking_id: 2,
      note: 'Guest requested extra synthetic pillows and bucket of ice for wine. Housekeeping fulfilled within 10 minutes.',
      created_at: '2026-11-16T19:00:00'
    },
    {
      report_id: 3,
      customer_id: 4,
      booking_id: 3,
      note: 'Baby cot delivered and safety inspected. Guest satisfied with quiet location of room 102.',
      created_at: '2026-11-16T16:15:00'
    },
    {
      report_id: 4,
      customer_id: 3,
      booking_id: null,
      note: 'Customer called inquiring why previous booking was denied. Explained hotel-wide smoke-free policy.',
      created_at: '2026-10-15T10:30:00'
    }
  ]
};

// Primary key definition for each table
const TABLE_PRIMARY_KEYS = {
  shop_owner: 'owner_id',
  item: 'item_id',
  room: 'room_id',
  meal_plan: 'meal_plan_id',
  customer: 'customer_id',
  booking_request: 'request_id',
  booking: 'booking_id',
  item_order: 'order_id',
  payment: 'payment_id',
  message: 'message_id',
  customer_report: 'report_id'
};

/**
 * Storage helpers
 */
function clone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

function nowISO() {
  const d = new Date();
  // Return YYYY-MM-DDTHH:mm:ss without milliseconds for SQL DATETIME2(0) precision
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
}

function todayDate() {
  const d = new Date();
  const pad = (n) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

function getDatabase() {
  if (isLocalStorageAvailable()) {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      const initial = clone(INITIAL_DATABASE);
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(initial));
      return initial;
    }
    try {
      const parsed = JSON.parse(raw);
      // Auto-refresh if new seed items (booking 4 or request 8) are missing
      if (
        !parsed.booking ||
        !parsed.booking.some((b) => b.booking_id === 4) ||
        !parsed.booking_request ||
        !parsed.booking_request.some((r) => r.request_id === 8)
      ) {
        const initial = clone(INITIAL_DATABASE);
        window.localStorage.setItem(STORAGE_KEY, JSON.stringify(initial));
        return initial;
      }
      return parsed;
    } catch (e) {
      console.error('Failed to parse mock database from localStorage, re-initializing...', e);
      const initial = clone(INITIAL_DATABASE);
      window.localStorage.setItem(STORAGE_KEY, JSON.stringify(initial));
      return initial;
    }
  } else {
    if (!inMemoryStore || !inMemoryStore.booking.some((b) => b.booking_id === 4)) {
      inMemoryStore = clone(INITIAL_DATABASE);
    }
    return inMemoryStore;
  }
}

function saveDatabase(db) {
  if (isLocalStorageAvailable()) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(db));
  } else {
    inMemoryStore = clone(db);
  }
}

function resetDatabase() {
  const initial = clone(INITIAL_DATABASE);
  saveDatabase(initial);
  return clone(initial);
}

function getNextId(db, tableName, pkField) {
  const table = db[tableName] || [];
  if (!table.length) return 1;
  const max = table.reduce((acc, row) => Math.max(acc, Number(row[pkField]) || 0), 0);
  return max + 1;
}

/**
 * Core Mock API implementation
 */
export const MockAPI = {
  // Utility & lifecycle
  getDatabase() {
    return clone(getDatabase());
  },

  resetDatabase() {
    return resetDatabase();
  },

  exportDatabase() {
    return JSON.stringify(getDatabase(), null, 2);
  },

  importDatabase(jsonOrObj) {
    const data = typeof jsonOrObj === 'string' ? JSON.parse(jsonOrObj) : jsonOrObj;
    saveDatabase(data);
    return clone(data);
  },

  // ---------------------------------------------------------------------------
  // Generic Table Operations
  // ---------------------------------------------------------------------------
  getAll(tableName, filterFn = null) {
    const db = getDatabase();
    const rows = db[tableName] || [];
    const result = filterFn ? rows.filter(filterFn) : rows;
    return clone(result);
  },

  getById(tableName, id) {
    const db = getDatabase();
    const pk = TABLE_PRIMARY_KEYS[tableName];
    if (!pk) throw new Error(`Primary key for table ${tableName} is not defined`);
    const row = (db[tableName] || []).find((r) => String(r[pk]) === String(id));
    return row ? clone(row) : null;
  },

  create(tableName, itemData) {
    const db = getDatabase();
    const pk = TABLE_PRIMARY_KEYS[tableName];
    if (!db[tableName]) db[tableName] = [];

    const newRecord = { ...itemData };
    if (pk && (newRecord[pk] === undefined || newRecord[pk] === null)) {
      newRecord[pk] = getNextId(db, tableName, pk);
    }
    db[tableName].push(newRecord);
    saveDatabase(db);
    return clone(newRecord);
  },

  update(tableName, id, updates) {
    const db = getDatabase();
    const pk = TABLE_PRIMARY_KEYS[tableName];
    if (!pk) throw new Error(`Primary key for table ${tableName} is not defined`);

    const index = (db[tableName] || []).findIndex((r) => String(r[pk]) === String(id));
    if (index === -1) {
      throw new Error(`Record with id ${id} not found in ${tableName}`);
    }

    const updated = { ...db[tableName][index], ...updates };
    // Ensure primary key doesn't get accidentally overwritten
    updated[pk] = db[tableName][index][pk];
    db[tableName][index] = updated;
    saveDatabase(db);
    return clone(updated);
  },

  delete(tableName, id) {
    const db = getDatabase();
    const pk = TABLE_PRIMARY_KEYS[tableName];
    if (!pk) throw new Error(`Primary key for table ${tableName} is not defined`);

    const prevLength = (db[tableName] || []).length;
    db[tableName] = (db[tableName] || []).filter((r) => String(r[pk]) !== String(id));

    const deleted = prevLength !== db[tableName].length;
    if (deleted) {
      saveDatabase(db);
    }
    return { success: deleted, id };
  },

  // ---------------------------------------------------------------------------
  // 1. SHOP_OWNER & Auth
  // ---------------------------------------------------------------------------
  login(username, password) {
    const db = getDatabase();
    const owner = (db.shop_owner || []).find((o) => o.username === username);
    if (!owner) {
      throw new Error('Invalid username or password');
    }

    // In mock mode, allow matching username or standard admin password
    const updatedOwner = this.update('shop_owner', owner.owner_id, {
      last_login_at: nowISO()
    });

    const token = `mock-token-${updatedOwner.owner_id}-${Date.now()}`;
    const user = {
      owner_id: updatedOwner.owner_id,
      username: updatedOwner.username,
      full_name: updatedOwner.full_name,
      last_login_at: updatedOwner.last_login_at
    };

    if (isLocalStorageAvailable()) {
      window.localStorage.setItem('auth_token', token);
      window.localStorage.setItem('auth_user', JSON.stringify(user));
    }

    return { token, user };
  },

  logout() {
    if (isLocalStorageAvailable()) {
      window.localStorage.removeItem('auth_token');
      window.localStorage.removeItem('auth_user');
    }
    return { success: true };
  },

  getCurrentUser() {
    if (isLocalStorageAvailable()) {
      const raw = window.localStorage.getItem('auth_user');
      if (raw) {
        try {
          return JSON.parse(raw);
        } catch (e) {
          return null;
        }
      }
    }
    // Default to the first owner in mock mode
    const db = getDatabase();
    const first = (db.shop_owner || [])[0];
    return first
      ? {
          owner_id: first.owner_id,
          username: first.username,
          full_name: first.full_name,
          last_login_at: first.last_login_at
        }
      : null;
  },

  getShopOwners() {
    return this.getAll('shop_owner').map((o) => {
      const { password_hash, salt, ...safe } = o;
      return safe;
    });
  },

  // ---------------------------------------------------------------------------
  // 2. MEAL_PLAN
  // ---------------------------------------------------------------------------
  getMealPlans(filterFn = null) {
    return this.getAll('meal_plan', filterFn);
  },

  getMealPlanById(id) {
    return this.getById('meal_plan', id);
  },

  createMealPlan(data) {
    return this.create('meal_plan', {
      is_active: 1,
      ...data
    });
  },

  updateMealPlan(id, data) {
    return this.update('meal_plan', id, data);
  },

  deleteMealPlan(id) {
    return this.delete('meal_plan', id);
  },

  // ---------------------------------------------------------------------------
  // 3. ROOM
  // ---------------------------------------------------------------------------
  getRooms(filterFn = null) {
    return this.getAll('room', filterFn);
  },

  getRoomById(id) {
    return this.getById('room', id);
  },

  createRoom(data) {
    return this.create('room', {
      status: 'AVAILABLE',
      price_per_night: Number(data.price_per_night) || 0,
      ...data
    });
  },

  updateRoom(id, data) {
    const payload = { ...data };
    if (payload.price_per_night !== undefined) {
      payload.price_per_night = Number(payload.price_per_night);
    }
    return this.update('room', id, payload);
  },

  deleteRoom(id) {
    return this.delete('room', id);
  },

  updateRoomStatus(roomId, status) {
    const valid = ['AVAILABLE', 'BOOKED', 'CLOSED'];
    if (!valid.includes(status)) {
      throw new Error(`Invalid room status: ${status}. Must be one of: ${valid.join(', ')}`);
    }
    return this.update('room', roomId, { status });
  },

  getAvailableRooms(checkIn, checkOut, roomType = null) {
    const db = getDatabase();
    const rooms = db.room || [];

    // If dates are provided, find rooms that are not in conflicting active/pending bookings
    let bookedRoomIds = new Set();
    if (checkIn && checkOut) {
      const activeBookings = (db.booking || []).filter(
        (b) => ['ACTIVE', 'PENDING'].includes(b.status) && !(b.check_out <= checkIn || b.check_in >= checkOut)
      );
      const activeBookingIds = new Set(activeBookings.map((b) => b.booking_id));

      (db.booking_room || []).forEach((br) => {
        if (activeBookingIds.has(br.booking_id)) {
          bookedRoomIds.add(br.room_id);
        }
      });
    }

    return rooms.filter((r) => {
      if (r.status === 'CLOSED') return false;
      if (bookedRoomIds.has(r.room_id)) return false;
      if (r.status === 'BOOKED' && (!checkIn || !checkOut)) return false;
      if (roomType && r.room_type !== roomType) return false;
      return true;
    });
  },

  // ---------------------------------------------------------------------------
  // 4. ITEM (COUNT & CONTINUOUS)
  // ---------------------------------------------------------------------------
  getItems(filterFn = null) {
    return this.getAll('item', filterFn);
  },

  getItemById(id) {
    return this.getById('item', id);
  },

  createItem(data) {
    const method = data.measuring_method || 'COUNT';
    if (!['COUNT', 'CONTINUOUS'].includes(method)) {
      throw new Error("measuring_method must be either 'COUNT' or 'CONTINUOUS'");
    }
    return this.create('item', {
      stock_amount: Number(data.stock_amount) || 0,
      price_per_unit: Number(data.price_per_unit) || 0,
      measuring_method: method,
      is_active: data.is_active !== undefined ? (data.is_active ? 1 : 0) : 1,
      ...data
    });
  },

  updateItem(id, data) {
    const payload = { ...data };
    if (payload.price_per_unit !== undefined) payload.price_per_unit = Number(payload.price_per_unit);
    if (payload.stock_amount !== undefined) payload.stock_amount = Number(payload.stock_amount);
    if (payload.measuring_method && !['COUNT', 'CONTINUOUS'].includes(payload.measuring_method)) {
      throw new Error("measuring_method must be either 'COUNT' or 'CONTINUOUS'");
    }
    if (payload.is_active !== undefined) payload.is_active = payload.is_active ? 1 : 0;
    return this.update('item', id, payload);
  },

  deleteItem(id) {
    return this.delete('item', id);
  },

  adjustStock(itemId, deltaAmount) {
    const item = this.getItemById(itemId);
    if (!item) throw new Error(`Item ${itemId} not found`);

    const newStock = parseFloat((item.stock_amount + Number(deltaAmount)).toFixed(2));
    if (newStock < 0) {
      throw new Error(`Insufficient stock for item "${item.name}". Available: ${item.stock_amount}, requested adjustment: ${deltaAmount}`);
    }

    return this.update('item', itemId, { stock_amount: newStock });
  },

  getLowStockItems(threshold = 20) {
    return this.getAll('item', (i) => i.is_active && i.stock_amount <= threshold);
  },

  // ---------------------------------------------------------------------------
  // 5. CUSTOMER
  // ---------------------------------------------------------------------------
  getCustomers(filterFn = null) {
    return this.getAll('customer', filterFn);
  },

  getCustomerById(id) {
    return this.getById('customer', id);
  },

  createCustomer(data) {
    return this.create('customer', {
      times_denied: 0,
      last_denied_date: null,
      created_at: nowISO(),
      ...data
    });
  },

  updateCustomer(id, data) {
    return this.update('customer', id, data);
  },

  deleteCustomer(id) {
    return this.delete('customer', id);
  },

  getCustomerHistory(customerId) {
    const db = getDatabase();
    const customer = (db.customer || []).find((c) => String(c.customer_id) === String(customerId));
    if (!customer) return null;

    const requests = (db.booking_request || []).filter((r) => String(r.customer_id) === String(customerId));
    const bookings = (db.booking || []).filter((b) => String(b.customer_id) === String(customerId));
    const reports = (db.customer_report || []).filter((cr) => String(cr.customer_id) === String(customerId));
    const messages = (db.message || []).filter((m) => String(m.customer_id) === String(customerId));

    return {
      customer: clone(customer),
      requests: clone(requests),
      bookings: clone(bookings),
      reports: clone(reports),
      messages: clone(messages)
    };
  },

  // ---------------------------------------------------------------------------
  // 6. BOOKING_REQUEST & Lifecycle Workflows
  // ---------------------------------------------------------------------------
  getBookingRequests(filterFn = null) {
    const db = getDatabase();
    const requests = db.booking_request || [];
    const list = filterFn ? requests.filter(filterFn) : requests;

    // Join meal plan and customer details
    return clone(
      list.map((r) => {
        const customer = (db.customer || []).find((c) => c.customer_id === r.customer_id);
        const mealPlan = (db.meal_plan || []).find((m) => m.meal_plan_id === r.meal_plan_id);
        return {
          ...r,
          customer: customer ? clone(customer) : null,
          meal_plan: mealPlan ? clone(mealPlan) : null
        };
      })
    );
  },

  getBookingRequestById(id) {
    const request = this.getById('booking_request', id);
    if (!request) return null;

    const db = getDatabase();
    const customer = (db.customer || []).find((c) => c.customer_id === request.customer_id);
    const mealPlan = (db.meal_plan || []).find((m) => m.meal_plan_id === request.meal_plan_id);
    const owner = (db.shop_owner || []).find((o) => o.owner_id === request.decided_by);

    return {
      ...request,
      customer: customer ? clone(customer) : null,
      meal_plan: mealPlan ? clone(mealPlan) : null,
      decided_by_user: owner ? { owner_id: owner.owner_id, full_name: owner.full_name } : null
    };
  },

  createBookingRequest(data) {
    const db = getDatabase();

    // Generate unique email_message_id if not passed
    const email_message_id = data.email_message_id || `msg_bkg_${Date.now()}@booking.com`;
    const booking_ref = data.booking_ref || `BK-${new Date().getFullYear()}-${Math.floor(10000 + Math.random() * 90000)}`;

    let customerId = data.customer_id;
    // Auto-create or find customer by phone or guest name if customer_id not passed
    if (!customerId && data.guest_name) {
      const existing = (db.customer || []).find(
        (c) => (data.guest_phone && c.phone === data.guest_phone) || c.full_name.toLowerCase() === data.guest_name.toLowerCase()
      );
      if (existing) {
        customerId = existing.customer_id;
      } else {
        const newCust = this.createCustomer({
          full_name: data.guest_name,
          phone: data.guest_phone || null,
          telegram_chat_id: null
        });
        customerId = newCust.customer_id;
      }
    }

    return this.create('booking_request', {
      customer_id: customerId || null,
      email_message_id,
      booking_ref,
      guest_name: data.guest_name,
      guest_phone: data.guest_phone || null,
      meal_plan_id: data.meal_plan_id || null,
      special_request: data.special_request || null,
      check_in: data.check_in,
      check_out: data.check_out,
      rooms_wanted: Number(data.rooms_wanted) || 1,
      total_price: Number(data.total_price) || 0,
      status: 'PENDING',
      received_at: nowISO(),
      decided_by: null,
      decided_at: null
    });
  },

  updateBookingRequest(id, data) {
    return this.update('booking_request', id, data);
  },

  deleteBookingRequest(id) {
    return this.delete('booking_request', id);
  },

  /**
   * Workflow: Accept a booking request.
   * - Marks request ACCEPTED
   * - Optionally updates req.meal_plan_id if mealPlanId provided
   * - Sets decided_by and decided_at
   * - Creates a BOOKING record with status 'PENDING'
   * - Assigns requested rooms in BOOKING_ROOM
   * - Marks assigned rooms as BOOKED
   * - Sends automated confirmation MESSAGE to customer (status 'SENT' if telegram_chat_id else 'FAILED')
   */
  acceptBookingRequest(requestId, { roomIds = [], decidedBy = 1, specialNotes = '', mealPlanId = null } = {}) {
    const db = getDatabase();
    const req = (db.booking_request || []).find((r) => String(r.request_id) === String(requestId));
    if (!req) throw new Error(`Booking request #${requestId} not found`);
    if (req.status !== 'PENDING') {
      throw new Error(`Only PENDING requests can be accepted. Current status: ${req.status}`);
    }

    const timestamp = nowISO();

    // Update meal_plan_id if provided
    if (mealPlanId !== null && mealPlanId !== undefined) {
      req.meal_plan_id = Number(mealPlanId);
    }

    // 1. Ensure customer exists
    let customerId = req.customer_id;
    let customer = customerId ? (db.customer || []).find((c) => c.customer_id === customerId) : null;
    if (!customer) {
      const existing = (db.customer || []).find(
        (c) => (req.guest_phone && c.phone === req.guest_phone) || (c.full_name && req.guest_name && c.full_name.toLowerCase() === req.guest_name.toLowerCase())
      );
      if (existing) {
        customer = existing;
      } else {
        customer = this.createCustomer({
          full_name: req.guest_name,
          phone: req.guest_phone || null,
          telegram_chat_id: null
        });
      }
      customerId = customer.customer_id;
    }

    // 2. Update booking_request status
    req.status = 'ACCEPTED';
    req.decided_by = decidedBy;
    req.decided_at = timestamp;
    req.customer_id = customerId;

    // 3. Create BOOKING record with status 'PENDING'
    const newBookingId = getNextId(db, 'booking', 'booking_id');
    const newBooking = {
      booking_id: newBookingId,
      request_id: req.request_id,
      customer_id: customerId,
      check_in: req.check_in,
      check_out: req.check_out,
      status: 'PENDING',
      created_at: timestamp
    };
    db.booking.push(newBooking);

    // 4. Assign rooms in BOOKING_ROOM & mark rooms BOOKED
    if (!db.booking_room) db.booking_room = [];
    const assignedRooms = [];
    roomIds.forEach((roomId) => {
      const room = (db.room || []).find((r) => String(r.room_id) === String(roomId));
      if (room) {
        db.booking_room.push({
          booking_id: newBookingId,
          room_id: room.room_id,
          price_per_night: room.price_per_night
        });
        room.status = 'BOOKED';
        assignedRooms.push(room);
      }
    });

    // 5. Send automated confirmation MESSAGE with status based on telegram_chat_id
    const messageStatus = (customer && customer.telegram_chat_id) ? 'SENT' : 'FAILED';
    const roomNums = assignedRooms.map((r) => r.room_number).join(', ') || 'assigned room';
    const msgId = getNextId(db, 'message', 'message_id');
    db.message.push({
      message_id: msgId,
      customer_id: customerId,
      content: `Reservation ${req.booking_ref} confirmed! Room(s): ${roomNums}. Check-in: ${req.check_in}, Check-out: ${req.check_out}.`,
      status: messageStatus,
      sent_at: timestamp
    });

    // 6. Optionally log customer report note
    if (specialNotes || req.special_request) {
      const repId = getNextId(db, 'customer_report', 'report_id');
      db.customer_report.push({
        report_id: repId,
        customer_id: customerId,
        booking_id: newBookingId,
        note: `Booking accepted. Request notes: ${req.special_request || 'None'}. Staff note: ${specialNotes || 'None'}`,
        created_at: timestamp
      });
    }

    saveDatabase(db);

    return {
      request: clone(req),
      booking: clone(newBooking),
      rooms: clone(assignedRooms)
    };
  },

  /**
   * Workflow: Deny a booking request.
   * - Marks request DENIED
   * - If customer_id is null, finds or creates CUSTOMER first and links req.customer_id
   * - Increments customer.times_denied and sets customer.last_denied_date to today
   * - Logs note in CUSTOMER_REPORT
   * - Sends notification MESSAGE (status 'SENT' if telegram_chat_id else 'FAILED')
   */
  denyBookingRequest(requestId, { reason = 'Room unavailable for requested dates', decidedBy = 1 } = {}) {
    const db = getDatabase();
    const req = (db.booking_request || []).find((r) => String(r.request_id) === String(requestId));
    if (!req) throw new Error(`Booking request #${requestId} not found`);
    if (req.status !== 'PENDING') {
      throw new Error(`Only PENDING requests can be denied. Current status: ${req.status}`);
    }

    const timestamp = nowISO();
    const today = todayDate();

    req.status = 'DENIED';
    req.decided_by = decidedBy;
    req.decided_at = timestamp;

    let customer = null;
    if (!req.customer_id) {
      // Find or create customer first and link req.customer_id
      const existing = (db.customer || []).find(
        (c) => (req.guest_phone && c.phone === req.guest_phone) || (c.full_name && req.guest_name && c.full_name.toLowerCase() === req.guest_name.toLowerCase())
      );
      if (existing) {
        customer = existing;
      } else {
        customer = this.createCustomer({
          full_name: req.guest_name,
          phone: req.guest_phone || null,
          telegram_chat_id: null
        });
      }
      req.customer_id = customer.customer_id;
    } else {
      customer = (db.customer || []).find((c) => c.customer_id === req.customer_id);
    }

    if (customer) {
      customer.times_denied = (customer.times_denied || 0) + 1;
      customer.last_denied_date = today;

      // Log report note
      const repId = getNextId(db, 'customer_report', 'report_id');
      db.customer_report.push({
        report_id: repId,
        customer_id: customer.customer_id,
        booking_id: null,
        note: `Booking request ${req.booking_ref} denied by staff. Reason: ${reason}`,
        created_at: timestamp
      });

      // Send denial message with status based on customer.telegram_chat_id
      const messageStatus = customer.telegram_chat_id ? 'SENT' : 'FAILED';
      const msgId = getNextId(db, 'message', 'message_id');
      db.message.push({
        message_id: msgId,
        customer_id: customer.customer_id,
        content: `Regarding your booking request ${req.booking_ref}: Unfortunately, we are unable to accept this reservation (${reason}).`,
        status: messageStatus,
        sent_at: timestamp
      });
    }

    saveDatabase(db);

    return {
      request: clone(req),
      customer: customer ? clone(customer) : null
    };
  },

  cancelBookingRequest(requestId) {
    const req = this.getBookingRequestById(requestId);
    if (!req) throw new Error(`Booking request #${requestId} not found`);
    return this.update('booking_request', requestId, { status: 'CANCELED' });
  },

  // ---------------------------------------------------------------------------
  // 7. BOOKING & Operations
  // ---------------------------------------------------------------------------
  getBookings(filterFn = null) {
    const db = getDatabase();
    const bookings = db.booking || [];
    const list = filterFn ? bookings.filter(filterFn) : bookings;

    return clone(
      list.map((b) => {
        const customer = (db.customer || []).find((c) => c.customer_id === b.customer_id);
        const req = (db.booking_request || []).find((r) => r.request_id === b.request_id);
        const assignedRooms = (db.booking_room || [])
          .filter((br) => br.booking_id === b.booking_id)
          .map((br) => {
            const rm = (db.room || []).find((r) => r.room_id === br.room_id);
            return { ...rm, price_per_night: br.price_per_night };
          });
        return {
          ...b,
          customer: customer ? clone(customer) : null,
          booking_request: req ? clone(req) : null,
          rooms: clone(assignedRooms)
        };
      })
    );
  },

  getBookingById(id) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(id));
    if (!booking) return null;

    const customer = (db.customer || []).find((c) => c.customer_id === booking.customer_id);
    const request = (db.booking_request || []).find((r) => r.request_id === booking.request_id);
    const mealPlan = request ? (db.meal_plan || []).find((m) => m.meal_plan_id === request.meal_plan_id) : null;

    const rooms = (db.booking_room || [])
      .filter((br) => br.booking_id === booking.booking_id)
      .map((br) => {
        const r = (db.room || []).find((rm) => rm.room_id === br.room_id);
        return {
          room_id: br.room_id,
          room_number: r ? r.room_number : 'Unknown',
          room_type: r ? r.room_type : 'Unknown',
          price_per_night: br.price_per_night
        };
      });

    const item_orders = (db.item_order || [])
      .filter((io) => io.booking_id === booking.booking_id)
      .map((io) => {
        const itm = (db.item || []).find((i) => i.item_id === io.item_id);
        return {
          ...io,
          item_name: itm ? itm.name : 'Unknown Item',
          measuring_method: itm ? itm.measuring_method : 'COUNT',
          total_price: parseFloat((io.amount * io.unit_price).toFixed(2))
        };
      });

    const payments = (db.payment || []).filter((p) => p.booking_id === booking.booking_id);
    const reports = (db.customer_report || []).filter((cr) => cr.booking_id === booking.booking_id);

    // Calculate nights & financial summary
    const checkInDate = new Date(booking.check_in);
    const checkOutDate = new Date(booking.check_out);
    const diffTime = Math.abs(checkOutDate - checkInDate);
    const nights = Math.max(1, Math.ceil(diffTime / (1000 * 60 * 60 * 24)));

    const roomCharges = rooms.reduce((sum, r) => sum + r.price_per_night * nights, 0);
    const orderCharges = item_orders.reduce((sum, io) => sum + io.total_price, 0);
    const totalCharges = parseFloat((roomCharges + orderCharges).toFixed(2));
    const totalPaid = parseFloat(payments.reduce((sum, p) => sum + Number(p.amount), 0).toFixed(2));
    const balanceDue = parseFloat((totalCharges - totalPaid).toFixed(2));

    return {
      ...clone(booking),
      customer: customer ? clone(customer) : null,
      booking_request: request ? clone(request) : null,
      meal_plan: mealPlan ? clone(mealPlan) : null,
      rooms: clone(rooms),
      item_orders: clone(item_orders),
      payments: clone(payments),
      customer_reports: clone(reports),
      billing: {
        nights,
        room_charges: roomCharges,
        order_charges: orderCharges,
        total_charges: totalCharges,
        total_paid: totalPaid,
        balance_due: balanceDue
      }
    };
  },

  createBooking(bookingData, roomAssignments = []) {
    const db = getDatabase();
    const newBookingId = getNextId(db, 'booking', 'booking_id');
    const newBooking = {
      booking_id: newBookingId,
      request_id: bookingData.request_id,
      customer_id: bookingData.customer_id,
      check_in: bookingData.check_in,
      check_out: bookingData.check_out,
      status: bookingData.status || 'ACTIVE',
      created_at: nowISO()
    };
    db.booking.push(newBooking);

    if (!db.booking_room) db.booking_room = [];
    roomAssignments.forEach((assign) => {
      const room = (db.room || []).find((r) => r.room_id === assign.room_id);
      db.booking_room.push({
        booking_id: newBookingId,
        room_id: assign.room_id,
        price_per_night: assign.price_per_night !== undefined ? assign.price_per_night : (room ? room.price_per_night : 0)
      });
      if (room && newBooking.status === 'ACTIVE') {
        room.status = 'BOOKED';
      }
    });

    saveDatabase(db);
    return this.getBookingById(newBookingId);
  },

  updateBooking(id, data) {
    return this.update('booking', id, data);
  },

  deleteBooking(id) {
    const db = getDatabase();
    // Cascade delete booking_room, item_order, payment, customer_report
    db.booking_room = (db.booking_room || []).filter((br) => String(br.booking_id) !== String(id));
    db.item_order = (db.item_order || []).filter((io) => String(io.booking_id) !== String(id));
    db.payment = (db.payment || []).filter((p) => String(p.booking_id) !== String(id));
    saveDatabase(db);
    return this.delete('booking', id);
  },

  checkInBooking(bookingId) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    booking.status = 'ACTIVE';

    // Mark assigned rooms as BOOKED
    const assigned = (db.booking_room || []).filter((br) => br.booking_id === booking.booking_id);
    assigned.forEach((br) => {
      const r = (db.room || []).find((rm) => rm.room_id === br.room_id);
      if (r) r.status = 'BOOKED';
    });

    saveDatabase(db);
    return this.getBookingById(bookingId);
  },

  checkOutBooking(bookingId) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    booking.status = 'FINISHED';

    // Free assigned rooms back to AVAILABLE
    const assigned = (db.booking_room || []).filter((br) => br.booking_id === booking.booking_id);
    assigned.forEach((br) => {
      const r = (db.room || []).find((rm) => rm.room_id === br.room_id);
      if (r && r.status === 'BOOKED') {
        r.status = 'AVAILABLE';
      }
    });

    saveDatabase(db);
    return this.getBookingById(bookingId);
  },

  cancelBooking(bookingId) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    booking.status = 'CANCELED';

    // Free assigned rooms
    const assigned = (db.booking_room || []).filter((br) => br.booking_id === booking.booking_id);
    assigned.forEach((br) => {
      const r = (db.room || []).find((rm) => rm.room_id === br.room_id);
      if (r && r.status === 'BOOKED') {
        r.status = 'AVAILABLE';
      }
    });

    saveDatabase(db);
    return this.getBookingById(bookingId);
  },

  getBookingBilling(bookingId) {
    const full = this.getBookingById(bookingId);
    return full ? full.billing : null;
  },

  // ---------------------------------------------------------------------------
  // 8. BOOKING_ROOM
  // ---------------------------------------------------------------------------
  getBookingRooms(bookingId) {
    const db = getDatabase();
    const rows = (db.booking_room || []).filter((br) => String(br.booking_id) === String(bookingId));
    return clone(
      rows.map((br) => {
        const room = (db.room || []).find((r) => r.room_id === br.room_id);
        return {
          ...br,
          room: room ? clone(room) : null
        };
      })
    );
  },

  assignRoomToBooking(bookingId, roomId, pricePerNight = null) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    const room = (db.room || []).find((r) => String(r.room_id) === String(roomId));
    if (!room) throw new Error(`Room #${roomId} not found`);

    if (!db.booking_room) db.booking_room = [];
    const exists = db.booking_room.some((br) => br.booking_id === booking.booking_id && br.room_id === room.room_id);
    if (exists) throw new Error(`Room #${room.room_number} is already assigned to this booking`);

    const rate = pricePerNight !== null ? Number(pricePerNight) : room.price_per_night;
    const brRecord = {
      booking_id: booking.booking_id,
      room_id: room.room_id,
      price_per_night: rate
    };
    db.booking_room.push(brRecord);

    if (booking.status === 'ACTIVE') {
      room.status = 'BOOKED';
    }

    saveDatabase(db);
    return clone(brRecord);
  },

  removeRoomFromBooking(bookingId, roomId) {
    const db = getDatabase();
    const prev = (db.booking_room || []).length;
    db.booking_room = (db.booking_room || []).filter(
      (br) => !(String(br.booking_id) === String(bookingId) && String(br.room_id) === String(roomId))
    );

    const room = (db.room || []).find((r) => String(r.room_id) === String(roomId));
    if (room && room.status === 'BOOKED') {
      room.status = 'AVAILABLE';
    }

    saveDatabase(db);
    return { success: (db.booking_room || []).length !== prev };
  },

  // ---------------------------------------------------------------------------
  // 9. ITEM_ORDER (Supports COUNT and CONTINUOUS decimals)
  // ---------------------------------------------------------------------------
  getItemOrders(bookingId = null) {
    const db = getDatabase();
    let orders = db.item_order || [];
    if (bookingId !== null) {
      orders = orders.filter((io) => String(io.booking_id) === String(bookingId));
    }
    return clone(
      orders.map((io) => {
        const item = (db.item || []).find((i) => i.item_id === io.item_id);
        return {
          ...io,
          item_name: item ? item.name : 'Unknown Item',
          measuring_method: item ? item.measuring_method : 'COUNT',
          total_price: parseFloat((io.amount * io.unit_price).toFixed(2))
        };
      })
    );
  },

  createItemOrder({ bookingId, itemId, amount, unitPrice = null }) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    const item = (db.item || []).find((i) => String(i.item_id) === String(itemId));
    if (!item) throw new Error(`Item #${itemId} not found`);

    const orderAmount = parseFloat(Number(amount).toFixed(2));
    if (orderAmount <= 0) throw new Error('Order amount must be greater than zero');

    if (item.stock_amount < orderAmount) {
      throw new Error(`Insufficient stock for "${item.name}". In stock: ${item.stock_amount}, requested: ${orderAmount}`);
    }

    // Deduct stock
    item.stock_amount = parseFloat((item.stock_amount - orderAmount).toFixed(2));

    const rate = unitPrice !== null ? Number(unitPrice) : item.price_per_unit;
    const newOrderId = getNextId(db, 'item_order', 'order_id');
    const orderRecord = {
      order_id: newOrderId,
      booking_id: booking.booking_id,
      item_id: item.item_id,
      amount: orderAmount,
      unit_price: rate,
      ordered_at: nowISO()
    };

    if (!db.item_order) db.item_order = [];
    db.item_order.push(orderRecord);
    saveDatabase(db);

    return {
      ...clone(orderRecord),
      item_name: item.name,
      measuring_method: item.measuring_method,
      total_price: parseFloat((orderAmount * rate).toFixed(2))
    };
  },

  deleteItemOrder(orderId) {
    const db = getDatabase();
    const orderIndex = (db.item_order || []).findIndex((io) => String(io.order_id) === String(orderId));
    if (orderIndex === -1) throw new Error(`Order #${orderId} not found`);

    const order = db.item_order[orderIndex];
    // Return stock to item
    const item = (db.item || []).find((i) => i.item_id === order.item_id);
    if (item) {
      item.stock_amount = parseFloat((item.stock_amount + order.amount).toFixed(2));
    }

    db.item_order.splice(orderIndex, 1);
    saveDatabase(db);
    return { success: true, order_id: orderId };
  },

  // ---------------------------------------------------------------------------
  // 10. PAYMENT
  // ---------------------------------------------------------------------------
  getPayments(bookingId = null) {
    const db = getDatabase();
    let payments = db.payment || [];
    if (bookingId !== null) {
      payments = payments.filter((p) => String(p.booking_id) === String(bookingId));
    }
    return clone(payments);
  },

  createPayment({ bookingId, amount, method = 'CASH', paidAt = null }) {
    const db = getDatabase();
    const booking = (db.booking || []).find((b) => String(b.booking_id) === String(bookingId));
    if (!booking) throw new Error(`Booking #${bookingId} not found`);

    const payAmount = Number(amount);
    if (isNaN(payAmount) || payAmount <= 0) {
      throw new Error('Payment amount must be greater than zero');
    }

    const payMethod = String(method).toUpperCase();
    if (!['CASH', 'CARD', 'OTHER'].includes(payMethod)) {
      throw new Error("Payment method must be 'CASH', 'CARD', or 'OTHER'");
    }

    const newPaymentId = getNextId(db, 'payment', 'payment_id');
    const paymentRecord = {
      payment_id: newPaymentId,
      booking_id: booking.booking_id,
      amount: parseFloat(payAmount.toFixed(2)),
      method: payMethod,
      paid_at: paidAt || nowISO()
    };

    if (!db.payment) db.payment = [];
    db.payment.push(paymentRecord);
    saveDatabase(db);

    return clone(paymentRecord);
  },

  deletePayment(paymentId) {
    return this.delete('payment', paymentId);
  },

  // ---------------------------------------------------------------------------
  // 11. MESSAGE
  // ---------------------------------------------------------------------------
  getMessages(customerId = null) {
    const db = getDatabase();
    let messages = db.message || [];
    if (customerId !== null) {
      messages = messages.filter((m) => String(m.customer_id) === String(customerId));
    }
    return clone(
      messages.map((m) => {
        const customer = (db.customer || []).find((c) => c.customer_id === m.customer_id);
        return {
          ...m,
          customer_name: customer ? customer.full_name : 'Unknown Customer'
        };
      })
    );
  },

  sendMessage({ customerId, content, status = 'SENT' }) {
    const db = getDatabase();
    const customer = (db.customer || []).find((c) => String(c.customer_id) === String(customerId));
    if (!customer) throw new Error(`Customer #${customerId} not found`);

    const newMsgId = getNextId(db, 'message', 'message_id');
    const msgRecord = {
      message_id: newMsgId,
      customer_id: customer.customer_id,
      content,
      status: ['SENT', 'FAILED'].includes(status) ? status : 'SENT',
      sent_at: nowISO()
    };

    if (!db.message) db.message = [];
    db.message.push(msgRecord);
    saveDatabase(db);

    return clone(msgRecord);
  },

  // ---------------------------------------------------------------------------
  // 12. CUSTOMER_REPORT
  // ---------------------------------------------------------------------------
  getCustomerReports(customerId = null, bookingId = null) {
    const db = getDatabase();
    let reports = db.customer_report || [];
    if (customerId !== null) {
      reports = reports.filter((cr) => String(cr.customer_id) === String(customerId));
    }
    if (bookingId !== null) {
      reports = reports.filter((cr) => String(cr.booking_id) === String(bookingId));
    }

    return clone(
      reports.map((cr) => {
        const customer = (db.customer || []).find((c) => c.customer_id === cr.customer_id);
        const booking = (db.booking || []).find((b) => b.booking_id === cr.booking_id);
        return {
          ...cr,
          customer_name: customer ? customer.full_name : 'Unknown Customer',
          booking_ref: booking ? `Booking #${booking.booking_id}` : null
        };
      })
    );
  },

  createCustomerReport({ customerId, bookingId = null, note }) {
    const db = getDatabase();
    const customer = (db.customer || []).find((c) => String(c.customer_id) === String(customerId));
    if (!customer) throw new Error(`Customer #${customerId} not found`);

    if (!note || !note.trim()) {
      throw new Error('Report note content cannot be empty');
    }

    const newRepId = getNextId(db, 'customer_report', 'report_id');
    const reportRecord = {
      report_id: newRepId,
      customer_id: customer.customer_id,
      booking_id: bookingId ? Number(bookingId) : null,
      note: note.trim(),
      created_at: nowISO()
    };

    if (!db.customer_report) db.customer_report = [];
    db.customer_report.push(reportRecord);
    saveDatabase(db);

    return clone(reportRecord);
  },

  updateCustomerReport(id, data) {
    return this.update('customer_report', id, data);
  },

  deleteCustomerReport(id) {
    return this.delete('customer_report', id);
  },

  // ---------------------------------------------------------------------------
  // Dashboard & Analytics
  // ---------------------------------------------------------------------------
  getDashboardSummary() {
    const db = getDatabase();
    const rooms = db.room || [];
    const requests = db.booking_request || [];
    const bookings = db.booking || [];
    const items = db.item || [];
    const payments = db.payment || [];

    const roomsBreakdown = {
      total: rooms.length,
      available: rooms.filter((r) => r.status === 'AVAILABLE').length,
      booked: rooms.filter((r) => r.status === 'BOOKED').length,
      closed: rooms.filter((r) => r.status === 'CLOSED').length
    };

    const requestsBreakdown = {
      total: requests.length,
      pending: requests.filter((r) => r.status === 'PENDING').length,
      accepted: requests.filter((r) => r.status === 'ACCEPTED').length,
      denied: requests.filter((r) => r.status === 'DENIED').length,
      canceled: requests.filter((r) => r.status === 'CANCELED').length
    };

    const bookingsBreakdown = {
      total: bookings.length,
      pending: bookings.filter((b) => b.status === 'PENDING').length,
      active: bookings.filter((b) => b.status === 'ACTIVE').length,
      finished: bookings.filter((b) => b.status === 'FINISHED').length,
      canceled: bookings.filter((b) => b.status === 'CANCELED').length
    };

    const totalRevenue = parseFloat(payments.reduce((sum, p) => sum + Number(p.amount), 0).toFixed(2));
    const lowStockItems = items.filter((i) => i.is_active && i.stock_amount <= 20);

    return {
      rooms: roomsBreakdown,
      booking_requests: requestsBreakdown,
      bookings: bookingsBreakdown,
      total_revenue: totalRevenue,
      low_stock_items_count: lowStockItems.length,
      low_stock_items: clone(lowStockItems),
      recent_pending_requests: clone(requests.filter((r) => r.status === 'PENDING').slice(0, 5))
    };
  }
};

// Support browser window global
if (typeof window !== 'undefined') {
  window.MockAPI = MockAPI;
}

// Support CommonJS
if (typeof module !== 'undefined' && module.exports) {
  module.exports = MockAPI;
}

export default MockAPI;
