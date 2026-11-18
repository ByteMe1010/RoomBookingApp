/**
 * api.js
 * 
 * Unified async adapter for RoomBookingApp.
 * Routes all CRUD operations and workflow calls to either:
 *  - mock-api.js (in-browser localStorage database) when USE_MOCK is true
 *  - fetch('/api/...') (real backend REST endpoints) when USE_MOCK is false
 */

import { MockAPI } from './mock-api.js';

// Main switch: true = use localStorage mock database, false = use real backend fetch
export const USE_MOCK = true;

const API_BASE_URL = '/api';

/**
 * Helper to retrieve MockAPI instance
 */
function getMock() {
  if (typeof MockAPI !== 'undefined' && MockAPI) return MockAPI;
  if (typeof window !== 'undefined' && window.MockAPI) return window.MockAPI;
  throw new Error('MockAPI implementation is not available');
}

/**
 * Wraps mock execution in a Promise with a micro delay to simulate network latency
 */
function mockCall(fn) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      try {
        const result = fn(getMock());
        resolve(result);
      } catch (err) {
        reject(err);
      }
    }, 20);
  });
}

/**
 * HTTP fetch wrapper with authorization, headers, and error handling
 */
async function httpRequest(endpoint, options = {}) {
  const { method = 'GET', body = null, params = null, headers = {} } = options;

  let url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : `/${endpoint}`}`;

  if (params && typeof params === 'object') {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, val]) => {
      if (val !== null && val !== undefined) {
        query.append(key, val);
      }
    });
    const queryString = query.toString();
    if (queryString) {
      url += `${url.includes('?') ? '&' : '?'}${queryString}`;
    }
  }

  const reqHeaders = {
    'Accept': 'application/json',
    ...headers
  };

  if (body !== null && !(body instanceof FormData)) {
    reqHeaders['Content-Type'] = 'application/json';
  }

  // Attach auth token if stored
  if (typeof window !== 'undefined' && window.localStorage) {
    const token = window.localStorage.getItem('auth_token');
    if (token) {
      reqHeaders['Authorization'] = `Bearer ${token}`;
    }
  }

  const fetchOptions = {
    method,
    headers: reqHeaders
  };

  if (body !== null) {
    fetchOptions.body = body instanceof FormData ? body : JSON.stringify(body);
  }

  const response = await fetch(url, fetchOptions);

  if (!response.ok) {
    let errorMsg = `HTTP Error ${response.status}: ${response.statusText}`;
    try {
      const errData = await response.json();
      if (errData && (errData.message || errData.error)) {
        errorMsg = errData.message || errData.error;
      }
    } catch (e) {
      // Body not JSON
    }
    const error = new Error(errorMsg);
    error.status = response.status;
    throw error;
  }

  if (response.status === 204) {
    return null;
  }

  try {
    return await response.json();
  } catch (e) {
    return null;
  }
}

/**
 * Unified API Adapter
 */
export const api = {
  // ---------------------------------------------------------------------------
  // Authentication & Shop Owners
  // ---------------------------------------------------------------------------
  auth: {
    async login(username, password) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.login(username, password));
      }
      return httpRequest('/auth/login', {
        method: 'POST',
        body: { username, password }
      });
    },

    async logout() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.logout());
      }
      return httpRequest('/auth/logout', { method: 'POST' });
    },

    async getCurrentUser() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getCurrentUser());
      }
      return httpRequest('/auth/me');
    },

    async getShopOwners() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getShopOwners());
      }
      return httpRequest('/shop-owners');
    }
  },

  // ---------------------------------------------------------------------------
  // Rooms
  // ---------------------------------------------------------------------------
  rooms: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          if (!params) return mock.getRooms();
          return mock.getRooms((r) => {
            if (params.status && r.status !== params.status) return false;
            if (params.room_type && r.room_type !== params.room_type) return false;
            return true;
          });
        });
      }
      return httpRequest('/rooms', { params });
    },

    async getById(roomId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getRoomById(roomId));
      }
      return httpRequest(`/rooms/${roomId}`);
    },

    async create(roomData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createRoom(roomData));
      }
      return httpRequest('/rooms', {
        method: 'POST',
        body: roomData
      });
    },

    async update(roomId, roomData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateRoom(roomId, roomData));
      }
      return httpRequest(`/rooms/${roomId}`, {
        method: 'PUT',
        body: roomData
      });
    },

    async delete(roomId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteRoom(roomId));
      }
      return httpRequest(`/rooms/${roomId}`, { method: 'DELETE' });
    },

    async updateStatus(roomId, status) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateRoomStatus(roomId, status));
      }
      return httpRequest(`/rooms/${roomId}/status`, {
        method: 'PATCH',
        body: { status }
      });
    },

    async getAvailable(checkIn, checkOut, roomType = null) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getAvailableRooms(checkIn, checkOut, roomType));
      }
      return httpRequest('/rooms/available', {
        params: {
          check_in: checkIn,
          check_out: checkOut,
          room_type: roomType
        }
      });
    }
  },

  // ---------------------------------------------------------------------------
  // Meal Plans
  // ---------------------------------------------------------------------------
  mealPlans: {
    async getAll() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getMealPlans());
      }
      return httpRequest('/meal-plans');
    },

    async getById(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getMealPlanById(id));
      }
      return httpRequest(`/meal-plans/${id}`);
    },

    async create(mealPlanData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createMealPlan(mealPlanData));
      }
      return httpRequest('/meal-plans', {
        method: 'POST',
        body: mealPlanData
      });
    },

    async update(id, mealPlanData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateMealPlan(id, mealPlanData));
      }
      return httpRequest(`/meal-plans/${id}`, {
        method: 'PUT',
        body: mealPlanData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteMealPlan(id));
      }
      return httpRequest(`/meal-plans/${id}`, { method: 'DELETE' });
    }
  },

  // ---------------------------------------------------------------------------
  // Items (Inventory with COUNT & CONTINUOUS)
  // ---------------------------------------------------------------------------
  items: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          if (!params) return mock.getItems();
          return mock.getItems((i) => {
            if (params.measuring_method && i.measuring_method !== params.measuring_method) return false;
            if (params.is_active !== undefined && Number(i.is_active) !== Number(params.is_active)) return false;
            return true;
          });
        });
      }
      return httpRequest('/items', { params });
    },

    async getById(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getItemById(id));
      }
      return httpRequest(`/items/${id}`);
    },

    async create(itemData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createItem(itemData));
      }
      return httpRequest('/items', {
        method: 'POST',
        body: itemData
      });
    },

    async update(id, itemData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateItem(id, itemData));
      }
      return httpRequest(`/items/${id}`, {
        method: 'PUT',
        body: itemData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteItem(id));
      }
      return httpRequest(`/items/${id}`, { method: 'DELETE' });
    },

    async adjustStock(itemId, deltaAmount) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.adjustStock(itemId, deltaAmount));
      }
      return httpRequest(`/items/${itemId}/stock`, {
        method: 'POST',
        body: { delta_amount: deltaAmount }
      });
    },

    async getLowStock(threshold = 20) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getLowStockItems(threshold));
      }
      return httpRequest('/items/low-stock', { params: { threshold } });
    }
  },

  // ---------------------------------------------------------------------------
  // Customers
  // ---------------------------------------------------------------------------
  customers: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          if (!params) return mock.getCustomers();
          return mock.getCustomers((c) => {
            if (params.search) {
              const q = params.search.toLowerCase();
              return c.full_name.toLowerCase().includes(q) || (c.phone && c.phone.includes(q));
            }
            return true;
          });
        });
      }
      return httpRequest('/customers', { params });
    },

    async getById(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getCustomerById(id));
      }
      return httpRequest(`/customers/${id}`);
    },

    async create(customerData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createCustomer(customerData));
      }
      return httpRequest('/customers', {
        method: 'POST',
        body: customerData
      });
    },

    async update(id, customerData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateCustomer(id, customerData));
      }
      return httpRequest(`/customers/${id}`, {
        method: 'PUT',
        body: customerData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteCustomer(id));
      }
      return httpRequest(`/customers/${id}`, { method: 'DELETE' });
    },

    async getHistory(customerId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getCustomerHistory(customerId));
      }
      return httpRequest(`/customers/${customerId}/history`);
    }
  },

  // ---------------------------------------------------------------------------
  // Booking Requests & Workflows
  // ---------------------------------------------------------------------------
  bookingRequests: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          if (!params) return mock.getBookingRequests();
          return mock.getBookingRequests((r) => {
            if (params.status && r.status !== params.status) return false;
            if (params.search) {
              const q = params.search.toLowerCase();
              return (
                (r.guest_name && r.guest_name.toLowerCase().includes(q)) ||
                (r.booking_ref && r.booking_ref.toLowerCase().includes(q))
              );
            }
            return true;
          });
        });
      }
      return httpRequest('/booking-requests', { params });
    },

    async getById(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getBookingRequestById(id));
      }
      return httpRequest(`/booking-requests/${id}`);
    },

    async create(requestData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createBookingRequest(requestData));
      }
      return httpRequest('/booking-requests', {
        method: 'POST',
        body: requestData
      });
    },

    async update(id, requestData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateBookingRequest(id, requestData));
      }
      return httpRequest(`/booking-requests/${id}`, {
        method: 'PUT',
        body: requestData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteBookingRequest(id));
      }
      return httpRequest(`/booking-requests/${id}`, { method: 'DELETE' });
    },

    async accept(requestId, options = {}) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.acceptBookingRequest(requestId, options));
      }
      return httpRequest(`/booking-requests/${requestId}/accept`, {
        method: 'POST',
        body: options
      });
    },

    async deny(requestId, options = {}) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.denyBookingRequest(requestId, options));
      }
      return httpRequest(`/booking-requests/${requestId}/deny`, {
        method: 'POST',
        body: options
      });
    },

    async cancel(requestId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.cancelBookingRequest(requestId));
      }
      return httpRequest(`/booking-requests/${requestId}/cancel`, {
        method: 'POST'
      });
    }
  },

  // ---------------------------------------------------------------------------
  // Bookings & Lifecycle Operations
  // ---------------------------------------------------------------------------
  bookings: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          if (!params) return mock.getBookings();
          return mock.getBookings((b) => {
            if (params.status && b.status !== params.status) return false;
            return true;
          });
        });
      }
      return httpRequest('/bookings', { params });
    },

    async getById(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getBookingById(id));
      }
      return httpRequest(`/bookings/${id}`);
    },

    async create(bookingData, roomAssignments = []) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createBooking(bookingData, roomAssignments));
      }
      return httpRequest('/bookings', {
        method: 'POST',
        body: { ...bookingData, roomAssignments }
      });
    },

    async update(id, bookingData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateBooking(id, bookingData));
      }
      return httpRequest(`/bookings/${id}`, {
        method: 'PUT',
        body: bookingData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteBooking(id));
      }
      return httpRequest(`/bookings/${id}`, { method: 'DELETE' });
    },

    async checkIn(bookingId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.checkInBooking(bookingId));
      }
      return httpRequest(`/bookings/${bookingId}/check-in`, { method: 'POST' });
    },

    async checkOut(bookingId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.checkOutBooking(bookingId));
      }
      return httpRequest(`/bookings/${bookingId}/check-out`, { method: 'POST' });
    },

    async cancel(bookingId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.cancelBooking(bookingId));
      }
      return httpRequest(`/bookings/${bookingId}/cancel`, { method: 'POST' });
    },

    async getBilling(bookingId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getBookingBilling(bookingId));
      }
      return httpRequest(`/bookings/${bookingId}/billing`);
    }
  },

  // ---------------------------------------------------------------------------
  // Booking Rooms (Room Assignments)
  // ---------------------------------------------------------------------------
  bookingRooms: {
    async getByBooking(bookingId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getBookingRooms(bookingId));
      }
      return httpRequest(`/bookings/${bookingId}/rooms`);
    },

    async assign(bookingId, roomId, pricePerNight = null) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.assignRoomToBooking(bookingId, roomId, pricePerNight));
      }
      return httpRequest(`/bookings/${bookingId}/rooms`, {
        method: 'POST',
        body: { roomId, pricePerNight }
      });
    },

    async remove(bookingId, roomId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.removeRoomFromBooking(bookingId, roomId));
      }
      return httpRequest(`/bookings/${bookingId}/rooms/${roomId}`, {
        method: 'DELETE'
      });
    }
  },

  // ---------------------------------------------------------------------------
  // Item Orders (Count & Continuous inventory purchases)
  // ---------------------------------------------------------------------------
  itemOrders: {
    async getAll(bookingId = null) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getItemOrders(bookingId));
      }
      return httpRequest('/item-orders', { params: bookingId ? { booking_id: bookingId } : null });
    },

    async create(orderData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createItemOrder(orderData));
      }
      return httpRequest('/item-orders', {
        method: 'POST',
        body: orderData
      });
    },

    async delete(orderId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteItemOrder(orderId));
      }
      return httpRequest(`/item-orders/${orderId}`, { method: 'DELETE' });
    }
  },

  // ---------------------------------------------------------------------------
  // Payments
  // ---------------------------------------------------------------------------
  payments: {
    async getAll(bookingId = null) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getPayments(bookingId));
      }
      return httpRequest('/payments', { params: bookingId ? { booking_id: bookingId } : null });
    },

    async create(paymentData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createPayment(paymentData));
      }
      return httpRequest('/payments', {
        method: 'POST',
        body: paymentData
      });
    },

    async delete(paymentId) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deletePayment(paymentId));
      }
      return httpRequest(`/payments/${paymentId}`, { method: 'DELETE' });
    }
  },

  // ---------------------------------------------------------------------------
  // Messages (Customer communications & Telegram)
  // ---------------------------------------------------------------------------
  messages: {
    async getAll(customerId = null) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getMessages(customerId));
      }
      return httpRequest('/messages', { params: customerId ? { customer_id: customerId } : null });
    },

    async send(messageData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.sendMessage(messageData));
      }
      return httpRequest('/messages', {
        method: 'POST',
        body: messageData
      });
    }
  },

  // ---------------------------------------------------------------------------
  // Customer Reports
  // ---------------------------------------------------------------------------
  customerReports: {
    async getAll(params = null) {
      if (USE_MOCK) {
        return mockCall((mock) => {
          const customerId = params ? params.customer_id : null;
          const bookingId = params ? params.booking_id : null;
          return mock.getCustomerReports(customerId, bookingId);
        });
      }
      return httpRequest('/customer-reports', { params });
    },

    async create(reportData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.createCustomerReport(reportData));
      }
      return httpRequest('/customer-reports', {
        method: 'POST',
        body: reportData
      });
    },

    async update(id, reportData) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.updateCustomerReport(id, reportData));
      }
      return httpRequest(`/customer-reports/${id}`, {
        method: 'PUT',
        body: reportData
      });
    },

    async delete(id) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.deleteCustomerReport(id));
      }
      return httpRequest(`/customer-reports/${id}`, { method: 'DELETE' });
    }
  },

  // ---------------------------------------------------------------------------
  // Dashboard & Analytics
  // ---------------------------------------------------------------------------
  dashboard: {
    async getSummary() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.getDashboardSummary());
      }
      return httpRequest('/dashboard/summary');
    }
  },

  // ---------------------------------------------------------------------------
  // System / Database Utilities
  // ---------------------------------------------------------------------------
  system: {
    async resetDatabase() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.resetDatabase());
      }
      return httpRequest('/system/reset', { method: 'POST' });
    },

    async exportDatabase() {
      if (USE_MOCK) {
        return mockCall((mock) => mock.exportDatabase());
      }
      return httpRequest('/system/export');
    },

    async importDatabase(data) {
      if (USE_MOCK) {
        return mockCall((mock) => mock.importDatabase(data));
      }
      return httpRequest('/system/import', {
        method: 'POST',
        body: { data }
      });
    }
  },

  // ---------------------------------------------------------------------------
  // Flat Shortcut Methods (for developer ergonomics)
  // ---------------------------------------------------------------------------
  getRooms(params) { return this.rooms.getAll(params); },
  getRoomById(id) { return this.rooms.getById(id); },
  getAvailableRooms(checkIn, checkOut, type) { return this.rooms.getAvailable(checkIn, checkOut, type); },
  getMealPlans() { return this.mealPlans.getAll(); },
  getItems(params) { return this.items.getAll(params); },
  getCustomers(params) { return this.customers.getAll(params); },
  getCustomerHistory(id) { return this.customers.getHistory(id); },
  getBookingRequests(params) { return this.bookingRequests.getAll(params); },
  getBookingRequestById(id) { return this.bookingRequests.getById(id); },
  acceptBookingRequest(id, opts) { return this.bookingRequests.accept(id, opts); },
  denyBookingRequest(id, opts) { return this.bookingRequests.deny(id, opts); },
  getBookings(params) { return this.bookings.getAll(params); },
  getBookingById(id) { return this.bookings.getById(id); },
  checkInBooking(id) { return this.bookings.checkIn(id); },
  checkOutBooking(id) { return this.bookings.checkOut(id); },
  cancelBooking(id) { return this.bookings.cancel(id); },
  getBookingBilling(id) { return this.bookings.getBilling(id); },
  orderItem(orderData) { return this.itemOrders.create(orderData); },
  addPayment(paymentData) { return this.payments.create(paymentData); },
  sendMessage(messageData) { return this.messages.send(messageData); },
  addCustomerReport(reportData) { return this.customerReports.create(reportData); },
  getDashboardSummary() { return this.dashboard.getSummary(); },
  resetDatabase() { return this.system.resetDatabase(); }
};

// Global browser window attachment
if (typeof window !== 'undefined') {
  window.api = api;
  window.USE_MOCK = USE_MOCK;
}

// CommonJS support
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { api, USE_MOCK };
}

export default api;
