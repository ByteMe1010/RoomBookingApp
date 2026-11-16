/**
 * app.js
 * 
 * Vanilla Single-Page Application (SPA) Shell for RoomBookingApp.
 * 
 * Features:
 *  - Shop owner authentication & session handling
 *  - Top bar with logged-in owner details and live PENDING requests badge
 *  - Hash-based SPA routing (#requests, #stays, #rooms, #inventory, #customers)
 *  - Semantic status badges (AVAILABLE, PENDING, ACTIVE, BOOKED, CLOSED, FAILED, etc.)
 *  - Integrated with api.js async adapter & localStorage mock database
 */

import { api, USE_MOCK } from './api/api.js';

// Application State
const state = {
  currentUser: null,
  currentView: 'requests',
  requestsFilter: 'ALL',
  requestsSearch: '',
  roomsFilter: 'ALL',
  inventoryFilter: 'ALL',
  cache: {
    pendingCount: 0
  }
};

// =============================================================================
// DOM Elements
// =============================================================================
const dom = {
  // Screens
  loginScreen: document.getElementById('login-screen'),
  appShell: document.getElementById('app-shell'),
  
  // Login Form
  loginForm: document.getElementById('login-form'),
  loginUsername: document.getElementById('login-username'),
  loginPassword: document.getElementById('login-password'),
  loginErrorBox: document.getElementById('login-error-box'),
  loginErrorText: document.getElementById('login-error-text'),

  // Top Bar
  topbarPendingBtn: document.getElementById('topbar-pending-btn'),
  topbarPendingCount: document.getElementById('topbar-pending-count'),
  ownerAvatar: document.getElementById('owner-avatar'),
  ownerFullName: document.getElementById('owner-full-name'),
  ownerUsername: document.getElementById('owner-username'),
  btnLogout: document.getElementById('btn-logout'),

  // Sidebar
  navLinks: document.querySelectorAll('.sidebar-nav .nav-item'),
  sidebarPendingBadge: document.getElementById('sidebar-pending-badge'),
  btnResetMockDb: document.getElementById('btn-reset-mock-db'),

  // Views
  viewPanels: {
    requests: document.getElementById('view-requests'),
    stays: document.getElementById('view-stays'),
    rooms: document.getElementById('view-rooms'),
    inventory: document.getElementById('view-inventory'),
    customers: document.getElementById('view-customers')
  },

  // Modals & Toasts
  modalContainer: document.getElementById('modal-container'),
  modalTitle: document.getElementById('modal-title'),
  modalBody: document.getElementById('modal-body'),
  modalFooter: document.getElementById('modal-footer'),
  modalCloseBtn: document.getElementById('modal-close-btn'),
  toastContainer: document.getElementById('toast-container')
};

// =============================================================================
// Toast Notification Utility
// =============================================================================
function showToast(message, type = 'info') {
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  let iconSvg = '';
  if (type === 'success') {
    iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>`;
  } else if (type === 'error') {
    iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#ef4444" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>`;
  } else {
    iconSvg = `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="#3b82f6" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>`;
  }

  toast.innerHTML = `${iconSvg}<span>${escapeHtml(message)}</span>`;
  dom.toastContainer.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 300ms ease';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function escapeHtml(text) {
  if (text === null || text === undefined) return '';
  const div = document.createElement('div');
  div.innerText = String(text);
  return div.innerHTML;
}

// =============================================================================
// Modal Helpers
// =============================================================================
function openModal(title, bodyHtml, footerHtml) {
  dom.modalTitle.innerText = title;
  dom.modalBody.innerHTML = bodyHtml;
  dom.modalFooter.innerHTML = footerHtml;
  dom.modalContainer.classList.remove('hidden');
}

function closeModal() {
  dom.modalContainer.classList.add('hidden');
  dom.modalBody.innerHTML = '';
  dom.modalFooter.innerHTML = '';
}

// =============================================================================
// Authentication & Owner Handling
// =============================================================================
async function initAuth() {
  try {
    const user = await api.auth.getCurrentUser();
    if (user) {
      setAuthenticatedUser(user);
    } else {
      showLoginScreen();
    }
  } catch (err) {
    console.error('Auth verification error:', err);
    showLoginScreen();
  }
}

function setAuthenticatedUser(user) {
  state.currentUser = user;
  
  // Format initials for avatar
  const initials = (user.full_name || user.username || 'SO')
    .split(' ')
    .map((w) => w[0])
    .join('')
    .substring(0, 2)
    .toUpperCase();

  dom.ownerAvatar.innerText = initials;
  dom.ownerFullName.innerText = user.full_name || 'Shop Owner';
  dom.ownerUsername.innerText = `@${user.username}`;

  dom.loginScreen.classList.add('hidden');
  dom.appShell.classList.remove('hidden');

  // Trigger initial route load & badges
  handleRoute();
  updatePendingRequestsBadge();
}

function showLoginScreen() {
  state.currentUser = null;
  dom.appShell.classList.add('hidden');
  dom.loginScreen.classList.remove('hidden');
  dom.loginErrorBox.classList.add('hidden');
}

async function handleLoginSubmit(e) {
  e.preventDefault();
  dom.loginErrorBox.classList.add('hidden');

  const username = dom.loginUsername.value.trim();
  const password = dom.loginPassword.value.trim();

  try {
    const result = await api.auth.login(username, password);
    showToast(`Welcome back, ${result.user.full_name}!`, 'success');
    setAuthenticatedUser(result.user);
  } catch (err) {
    dom.loginErrorText.innerText = err.message || 'Login failed. Please check credentials.';
    dom.loginErrorBox.classList.remove('hidden');
  }
}

async function handleLogout() {
  await api.auth.logout();
  showToast('You have been signed out.', 'info');
  showLoginScreen();
}

// =============================================================================
// Badge Counter: PENDING Requests
// =============================================================================
async function updatePendingRequestsBadge() {
  try {
    const pendingRequests = await api.bookingRequests.getAll({ status: 'PENDING' });
    const count = (pendingRequests || []).length;
    state.cache.pendingCount = count;

    dom.topbarPendingCount.innerText = count;
    dom.sidebarPendingBadge.innerText = count;

    const statPending = document.getElementById('stat-requests-pending');
    if (statPending) statPending.innerText = count;
  } catch (err) {
    console.error('Failed to update pending requests count:', err);
  }
}

// =============================================================================
// Hash Router
// =============================================================================
const VALID_VIEWS = ['requests', 'stays', 'rooms', 'inventory', 'customers'];

function handleRoute() {
  let hash = window.location.hash.replace('#', '').toLowerCase();
  if (!VALID_VIEWS.includes(hash)) {
    hash = 'requests';
    window.location.hash = '#requests';
    return;
  }

  state.currentView = hash;

  // Update navigation items active state
  dom.navLinks.forEach((link) => {
    const viewName = link.getAttribute('data-view');
    if (viewName === hash) {
      link.classList.add('active');
    } else {
      link.classList.remove('active');
    }
  });

  // Toggle active view panel
  Object.entries(dom.viewPanels).forEach(([key, panel]) => {
    if (key === hash) {
      panel.classList.add('active');
    } else {
      panel.classList.remove('active');
    }
  });

  // Render content for the active view
  renderActiveView(hash);
  updatePendingRequestsBadge();
}

function renderActiveView(viewName) {
  switch (viewName) {
    case 'requests':
      renderBookingRequestsView();
      break;
    case 'stays':
      renderStaysView();
      break;
    case 'rooms':
      renderRoomsView();
      break;
    case 'inventory':
      renderInventoryView();
      break;
    case 'customers':
      renderCustomersView();
      break;
  }
}

// =============================================================================
// Semantic Status Badge Renderer
// =============================================================================
function renderStatusBadge(status) {
  const norm = (status || '').toUpperCase();
  let badgeClass = 'badge-closed';

  switch (norm) {
    case 'AVAILABLE':
      badgeClass = 'badge-available';
      break;
    case 'PENDING':
      badgeClass = 'badge-pending';
      break;
    case 'ACTIVE':
      badgeClass = 'badge-active';
      break;
    case 'BOOKED':
      badgeClass = 'badge-booked';
      break;
    case 'CLOSED':
      badgeClass = 'badge-closed';
      break;
    case 'FAILED':
      badgeClass = 'badge-failed';
      break;
    case 'DENIED':
      badgeClass = 'badge-denied';
      break;
    case 'ACCEPTED':
      badgeClass = 'badge-accepted';
      break;
    case 'FINISHED':
      badgeClass = 'badge-finished';
      break;
    case 'CANCELED':
      badgeClass = 'badge-canceled';
      break;
  }

  return `<span class="badge ${badgeClass}">${escapeHtml(norm)}</span>`;
}

// =============================================================================
// View 1: Booking Requests (#requests)
// =============================================================================
async function renderBookingRequestsView() {
  const tbody = document.getElementById('tbody-booking-requests');
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading booking requests...</td></tr>`;

  try {
    const allRequests = await api.bookingRequests.getAll();
    
    // Update overview stats
    const totalCount = allRequests.length;
    const pendingCount = allRequests.filter((r) => r.status === 'PENDING').length;
    const acceptedCount = allRequests.filter((r) => r.status === 'ACCEPTED').length;

    const elTotal = document.getElementById('stat-requests-total');
    const elPending = document.getElementById('stat-requests-pending');
    const elAccepted = document.getElementById('stat-requests-accepted');
    if (elTotal) elTotal.innerText = totalCount;
    if (elPending) elPending.innerText = pendingCount;
    if (elAccepted) elAccepted.innerText = acceptedCount;

    // Filter
    let filtered = allRequests;
    if (state.requestsFilter !== 'ALL') {
      filtered = filtered.filter((r) => r.status === state.requestsFilter);
    }
    if (state.requestsSearch) {
      const q = state.requestsSearch.toLowerCase();
      filtered = filtered.filter(
        (r) =>
          (r.guest_name && r.guest_name.toLowerCase().includes(q)) ||
          (r.booking_ref && r.booking_ref.toLowerCase().includes(q))
      );
    }

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2.5rem; color: var(--color-text-muted);">No booking requests found matching criteria.</td></tr>`;
      return;
    }

    tbody.innerHTML = filtered
      .map((req) => {
        const specialReqHtml = req.special_request
          ? `<span class="special-request-chip" title="${escapeHtml(req.special_request)}">★ ${escapeHtml(req.special_request)}</span>`
          : `<span style="color: var(--color-text-muted); font-size: 0.8rem;">None</span>`;

        const mealCode = req.meal_plan ? `<span class="badge badge-active">${escapeHtml(req.meal_plan.code)}</span> <small>${escapeHtml(req.meal_plan.name)}</small>` : 'Standard (RO)';

        let actionsHtml = '';
        if (req.status === 'PENDING') {
          actionsHtml = `
            <div style="display:flex; gap:6px;">
              <button class="btn btn-sm btn-primary btn-accept-req" data-id="${req.request_id}">Accept</button>
              <button class="btn btn-sm btn-outline btn-deny-req" data-id="${req.request_id}" style="color:var(--status-failed-text);">Deny</button>
            </div>
          `;
        } else {
          actionsHtml = `<span style="color:var(--color-text-muted); font-size:0.78rem;">Decided</span>`;
        }

        return `
          <tr>
            <td>
              <strong style="color:var(--color-primary);">${escapeHtml(req.booking_ref)}</strong><br>
              ${renderStatusBadge(req.status)}
            </td>
            <td>
              <div style="font-weight:600;">${escapeHtml(req.guest_name)}</div>
              <small style="color:var(--color-text-muted);">${escapeHtml(req.guest_phone || 'No phone')}</small>
            </td>
            <td>${mealCode}</td>
            <td>
              <div><strong>${escapeHtml(req.check_in)}</strong> → <strong>${escapeHtml(req.check_out)}</strong></div>
              <small style="color:var(--color-text-muted);">${req.rooms_wanted} Room(s)</small>
            </td>
            <td>${specialReqHtml}</td>
            <td>
              <strong style="color:var(--color-text-main);">$${Number(req.total_price || 0).toFixed(2)}</strong>
            </td>
            <td>${actionsHtml}</td>
          </tr>
        `;
      })
      .join('');

    // Attach Action Listeners
    tbody.querySelectorAll('.btn-accept-req').forEach((btn) => {
      btn.addEventListener('click', () => promptAcceptRequest(btn.getAttribute('data-id')));
    });

    tbody.querySelectorAll('.btn-deny-req').forEach((btn) => {
      btn.addEventListener('click', () => promptDenyRequest(btn.getAttribute('data-id')));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding: 2rem;">Error loading requests: ${escapeHtml(err.message)}</td></tr>`;
  }
}

/**
 * Modal Workflow: Accept Booking Request & Assign Room
 */
async function promptAcceptRequest(requestId) {
  try {
    const req = await api.bookingRequests.getById(requestId);
    if (!req) return;

    // Get available rooms for dates
    const availableRooms = await api.rooms.getAvailable(req.check_in, req.check_out);

    if (!availableRooms.length) {
      showToast('No rooms currently available for requested stay dates!', 'error');
      return;
    }

    const roomOptionsHtml = availableRooms
      .map(
        (r) => `
        <label style="display:flex; align-items:center; gap:10px; padding:8px; border:1px solid var(--color-border); border-radius:6px; margin-bottom:6px; cursor:pointer;">
          <input type="checkbox" name="assigned_room" value="${r.room_id}">
          <div>
            <strong>Room ${escapeHtml(r.room_number)}</strong> — ${escapeHtml(r.room_type)}
            <div style="font-size:0.75rem; color:var(--color-text-muted);">$${Number(r.price_per_night).toFixed(2)} / night</div>
          </div>
        </label>
      `
      )
      .join('');

    const bodyHtml = `
      <div style="margin-bottom:1rem;">
        <p><strong>Reservation:</strong> ${escapeHtml(req.booking_ref)} (${escapeHtml(req.guest_name)})</p>
        <p style="font-size:0.85rem; color:var(--color-text-muted);">Stay Dates: ${escapeHtml(req.check_in)} to ${escapeHtml(req.check_out)} (${req.rooms_wanted} Room(s) requested)</p>
      </div>
      ${req.special_request ? `<div style="background:#fffbeb; padding:8px 12px; border-radius:6px; margin-bottom:1rem; font-size:0.8rem; border:1px solid #fde68a;"><strong>Special Request:</strong> ${escapeHtml(req.special_request)}</div>` : ''}
      <div class="form-group">
        <label class="form-label">Select Room(s) to Assign:</label>
        <div style="max-height:200px; overflow-y:auto;">
          ${roomOptionsHtml}
        </div>
      </div>
    `;

    const footerHtml = `
      <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
      <button class="btn btn-primary btn-sm" id="btn-modal-confirm-accept">Confirm & Create Booking</button>
    `;

    openModal(`Accept Request #${req.booking_ref}`, bodyHtml, footerHtml);

    document.getElementById('btn-modal-cancel').onclick = closeModal;
    document.getElementById('btn-modal-confirm-accept').onclick = async () => {
      const selectedCheckboxes = document.querySelectorAll('input[name="assigned_room"]:checked');
      const roomIds = Array.from(selectedCheckboxes).map((cb) => Number(cb.value));

      if (!roomIds.length) {
        showToast('Please select at least one room to assign.', 'error');
        return;
      }

      try {
        await api.bookingRequests.accept(requestId, {
          roomIds,
          decidedBy: state.currentUser ? state.currentUser.owner_id : 1
        });
        closeModal();
        showToast(`Request ${req.booking_ref} accepted and booking created!`, 'success');
        renderBookingRequestsView();
        updatePendingRequestsBadge();
      } catch (err) {
        showToast(`Failed to accept request: ${err.message}`, 'error');
      }
    };
  } catch (err) {
    showToast(`Error: ${err.message}`, 'error');
  }
}

/**
 * Modal Workflow: Deny Booking Request
 */
async function promptDenyRequest(requestId) {
  const req = await api.bookingRequests.getById(requestId);
  if (!req) return;

  const bodyHtml = `
    <p>Are you sure you want to deny request <strong>${escapeHtml(req.booking_ref)}</strong> for <strong>${escapeHtml(req.guest_name)}</strong>?</p>
    <div class="form-group" style="margin-top:1rem;">
      <label class="form-label" for="deny-reason-input">Denial Reason (logged in customer report):</label>
      <select id="deny-reason-select" class="form-control" style="margin-bottom:8px;">
        <option value="No rooms available for requested dates">No rooms available for requested dates</option>
        <option value="Non-smoking policy conflict">Non-smoking policy conflict</option>
        <option value="Rate or minimum stay condition not met">Rate or minimum stay condition not met</option>
        <option value="Guest requested cancellation">Guest requested cancellation</option>
        <option value="Custom">Other (Specify below)...</option>
      </select>
      <input type="text" id="deny-reason-custom" class="form-control" placeholder="Specific denial notes..." style="display:none;">
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-deny" style="background:#ef4444;">Confirm Denial</button>
  `;

  openModal(`Deny Request #${req.booking_ref}`, bodyHtml, footerHtml);

  const sel = document.getElementById('deny-reason-select');
  const customInput = document.getElementById('deny-reason-custom');
  sel.onchange = () => {
    customInput.style.display = sel.value === 'Custom' ? 'block' : 'none';
  };

  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-deny').onclick = async () => {
    const reason = sel.value === 'Custom' ? (customInput.value.trim() || 'Staff policy denial') : sel.value;

    try {
      await api.bookingRequests.deny(requestId, {
        reason,
        decidedBy: state.currentUser ? state.currentUser.owner_id : 1
      });
      closeModal();
      showToast(`Request ${req.booking_ref} denied. Customer denial counter incremented.`, 'info');
      renderBookingRequestsView();
      updatePendingRequestsBadge();
    } catch (err) {
      showToast(`Failed to deny request: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 2: Active Stays & Bookings (#stays)
// =============================================================================
async function renderStaysView() {
  const tbody = document.getElementById('tbody-stays');
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading bookings...</td></tr>`;

  try {
    const bookings = await api.bookings.getAll();
    if (!bookings.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2.5rem; color:var(--color-text-muted);">No bookings recorded yet.</td></tr>`;
      return;
    }

    tbody.innerHTML = bookings
      .map((b) => {
        const guestName = b.customer ? b.customer.full_name : 'Guest';
        const roomNumbers = (b.rooms || []).map((r) => `Room ${r.room_number}`).join(', ') || 'Unassigned';

        return `
          <tr>
            <td><strong>#${b.booking_id}</strong></td>
            <td>
              <div style="font-weight:600;">${escapeHtml(guestName)}</div>
              <small style="color:var(--color-text-muted);">${b.customer ? escapeHtml(b.customer.phone || '') : ''}</small>
            </td>
            <td><span class="badge badge-booked">${escapeHtml(roomNumbers)}</span></td>
            <td>${escapeHtml(b.check_in)} → ${escapeHtml(b.check_out)}</td>
            <td>${renderStatusBadge(b.status)}</td>
            <td>
              <button class="btn btn-sm btn-outline btn-view-billing" data-id="${b.booking_id}">
                View Billing
              </button>
            </td>
            <td>
              <div style="display:flex; gap:6px;">
                ${b.status === 'ACTIVE' ? `<button class="btn btn-sm btn-primary btn-checkout-stay" data-id="${b.booking_id}">Check Out</button>` : ''}
                ${b.status === 'PENDING' ? `<button class="btn btn-sm btn-primary btn-checkin-stay" data-id="${b.booking_id}">Check In</button>` : ''}
                <button class="btn btn-sm btn-outline btn-add-order" data-id="${b.booking_id}" title="Order Room Service">+ Order</button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');

    // Attach Stay actions
    tbody.querySelectorAll('.btn-view-billing').forEach((btn) => {
      btn.addEventListener('click', () => showBillingModal(btn.getAttribute('data-id')));
    });

    tbody.querySelectorAll('.btn-checkout-stay').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const id = btn.getAttribute('data-id');
        if (confirm(`Check out booking #${id} and release assigned rooms to AVAILABLE?`)) {
          await api.bookings.checkOut(id);
          showToast(`Booking #${id} checked out. Rooms released!`, 'success');
          renderStaysView();
        }
      });
    });

    tbody.querySelectorAll('.btn-checkin-stay').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const id = btn.getAttribute('data-id');
        await api.bookings.checkIn(id);
        showToast(`Booking #${id} marked ACTIVE. Rooms set to BOOKED.`, 'success');
        renderStaysView();
      });
    });

    tbody.querySelectorAll('.btn-add-order').forEach((btn) => {
      btn.addEventListener('click', () => promptAddOrder(btn.getAttribute('data-id')));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function showBillingModal(bookingId) {
  try {
    const booking = await api.bookings.getById(bookingId);
    if (!booking) return;

    const b = booking.billing;
    const bodyHtml = `
      <div style="font-size:0.9rem; line-height:1.6;">
        <p><strong>Guest:</strong> ${escapeHtml(booking.customer ? booking.customer.full_name : 'Guest')}</p>
        <p><strong>Nights:</strong> ${b.nights} night(s)</p>
        <hr style="margin:0.8rem 0; border:none; border-top:1px solid var(--color-border);">
        <div style="display:flex; justify-content:space-between;"><span>Room Charges:</span> <strong>$${b.room_charges.toFixed(2)}</strong></div>
        <div style="display:flex; justify-content:space-between;"><span>Orders & Supplies:</span> <strong>$${b.order_charges.toFixed(2)}</strong></div>
        <div style="display:flex; justify-content:space-between; margin-top:6px; font-size:1rem;"><span>Total Bill:</span> <strong>$${b.total_charges.toFixed(2)}</strong></div>
        <div style="display:flex; justify-content:space-between; color:#10b981;"><span>Total Paid:</span> <strong>-$${b.total_paid.toFixed(2)}</strong></div>
        <div style="display:flex; justify-content:space-between; margin-top:8px; padding-top:8px; border-top:1px dashed var(--color-border); font-size:1.1rem; color:${b.balance_due > 0 ? '#b91c1c' : '#047857'};">
          <span>Balance Due:</span> <strong>$${b.balance_due.toFixed(2)}</strong>
        </div>
      </div>
    `;

    const footerHtml = `
      <button class="btn btn-outline btn-sm" id="btn-modal-close">Close</button>
      <button class="btn btn-primary btn-sm" id="btn-modal-add-payment">Record Payment</button>
    `;

    openModal(`Billing Invoice - Booking #${bookingId}`, bodyHtml, footerHtml);
    document.getElementById('btn-modal-close').onclick = closeModal;
    document.getElementById('btn-modal-add-payment').onclick = () => {
      closeModal();
      promptAddPayment(bookingId);
    };
  } catch (err) {
    showToast(`Error: ${err.message}`, 'error');
  }
}

async function promptAddPayment(bookingId) {
  const bodyHtml = `
    <div class="form-group">
      <label class="form-label" for="pay-amount">Payment Amount ($)</label>
      <input type="number" id="pay-amount" class="form-control" step="0.01" min="0.01" placeholder="e.g. 100.00" required>
    </div>
    <div class="form-group">
      <label class="form-label" for="pay-method">Payment Method</label>
      <select id="pay-method" class="form-control">
        <option value="CARD">CARD (Credit/Debit)</option>
        <option value="CASH">CASH</option>
        <option value="OTHER">OTHER (Bank Transfer / Voucher)</option>
      </select>
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-pay">Save Payment</button>
  `;

  openModal(`Record Payment for Booking #${bookingId}`, bodyHtml, footerHtml);
  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-pay').onclick = async () => {
    const amount = Number(document.getElementById('pay-amount').value);
    const method = document.getElementById('pay-method').value;

    if (!amount || amount <= 0) {
      showToast('Please enter a valid payment amount.', 'error');
      return;
    }

    try {
      await api.payments.create({ bookingId, amount, method });
      closeModal();
      showToast(`Payment of $${amount.toFixed(2)} recorded!`, 'success');
      renderStaysView();
    } catch (err) {
      showToast(`Payment error: ${err.message}`, 'error');
    }
  };
}

async function promptAddOrder(bookingId) {
  const items = await api.items.getAll({ is_active: 1 });
  const itemOptionsHtml = items
    .map((i) => `<option value="${i.item_id}" data-price="${i.price_per_unit}" data-method="${i.measuring_method}">${escapeHtml(i.name)} ($${Number(i.price_per_unit).toFixed(2)} / ${i.measuring_method === 'COUNT' ? 'unit' : 'metric'}) — Stock: ${i.stock_amount}</option>`)
    .join('');

  const bodyHtml = `
    <div class="form-group">
      <label class="form-label">Select Supply / Order Item:</label>
      <select id="order-item-id" class="form-control">
        ${itemOptionsHtml}
      </select>
    </div>
    <div class="form-group">
      <label class="form-label" for="order-amount">Quantity / Amount (supports decimals for continuous items like 1.5 L):</label>
      <input type="number" id="order-amount" class="form-control" step="0.01" min="0.01" value="1.00" required>
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-order">Add Order</button>
  `;

  openModal(`Room Service Order - Booking #${bookingId}`, bodyHtml, footerHtml);
  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-order').onclick = async () => {
    const itemId = document.getElementById('order-item-id').value;
    const amount = Number(document.getElementById('order-amount').value);

    try {
      await api.itemOrders.create({ bookingId, itemId, amount });
      closeModal();
      showToast(`Item ordered successfully! Stock updated.`, 'success');
      renderStaysView();
    } catch (err) {
      showToast(`Order failed: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 3: Room Management (#rooms)
// =============================================================================
async function renderRoomsView() {
  const grid = document.getElementById('rooms-grid-container');
  grid.innerHTML = `<div style="grid-column: 1 / -1; text-align:center; padding: 2rem;">Loading rooms...</div>`;

  try {
    const rooms = await api.rooms.getAll();

    let filtered = rooms;
    if (state.roomsFilter !== 'ALL') {
      filtered = filtered.filter((r) => r.status === state.roomsFilter);
    }

    grid.innerHTML = filtered
      .map(
        (r) => `
        <div class="room-card">
          <div>
            <div class="room-card-header">
              <span class="room-number-lg">Room ${escapeHtml(r.room_number)}</span>
              ${renderStatusBadge(r.status)}
            </div>
            <div class="room-type-text">${escapeHtml(r.room_type)}</div>
          </div>
          <div class="room-card-footer">
            <div class="room-price-val">$${Number(r.price_per_night).toFixed(2)} <span>/ night</span></div>
            <button class="btn btn-sm btn-outline btn-toggle-room-status" data-id="${r.room_id}" data-status="${r.status}">
              ${r.status === 'CLOSED' ? 'Reopen' : (r.status === 'AVAILABLE' ? 'Set Closed' : 'View')}
            </button>
          </div>
        </div>
      `
      )
      .join('');

    // Status toggle listeners
    grid.querySelectorAll('.btn-toggle-room-status').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const id = btn.getAttribute('data-id');
        const currentStatus = btn.getAttribute('data-status');
        
        if (currentStatus === 'BOOKED') {
          showToast(`Room is currently occupied by a registered stay.`, 'info');
          return;
        }

        const newStatus = currentStatus === 'AVAILABLE' ? 'CLOSED' : 'AVAILABLE';
        await api.rooms.updateStatus(id, newStatus);
        showToast(`Room status updated to ${newStatus}`, 'success');
        renderRoomsView();
      });
    });

  } catch (err) {
    grid.innerHTML = `<div style="color:red;">Error: ${escapeHtml(err.message)}</div>`;
  }
}

// =============================================================================
// View 4: Items & Inventory (#inventory)
// =============================================================================
async function renderInventoryView() {
  const tbody = document.getElementById('tbody-inventory');
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading inventory...</td></tr>`;

  try {
    const items = await api.items.getAll();

    let filtered = items;
    if (state.inventoryFilter !== 'ALL') {
      filtered = filtered.filter((i) => i.measuring_method === state.inventoryFilter);
    }

    tbody.innerHTML = filtered
      .map((item) => {
        const methodBadge = item.measuring_method === 'COUNT'
          ? `<span class="badge badge-count">COUNT (Units)</span>`
          : `<span class="badge badge-continuous">CONTINUOUS (kg / L)</span>`;

        const isLow = Number(item.stock_amount) <= 20;
        const stockDisplay = isLow
          ? `<strong style="color:var(--status-failed-text);">${Number(item.stock_amount).toFixed(2)} ⚠️ Low Stock</strong>`
          : `<strong>${Number(item.stock_amount).toFixed(2)}</strong>`;

        return `
          <tr>
            <td>#${item.item_id}</td>
            <td><strong>${escapeHtml(item.name)}</strong></td>
            <td>${methodBadge}</td>
            <td>$${Number(item.price_per_unit).toFixed(2)}</td>
            <td>${stockDisplay}</td>
            <td>${item.is_active ? renderStatusBadge('AVAILABLE') : renderStatusBadge('CLOSED')}</td>
            <td>
              <div style="display:flex; gap:6px;">
                <button class="btn btn-sm btn-outline btn-adjust-stock" data-id="${item.item_id}" data-name="${escapeHtml(item.name)}" data-stock="${item.stock_amount}">Adjust Stock</button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');

    tbody.querySelectorAll('.btn-adjust-stock').forEach((btn) => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        const name = btn.getAttribute('data-name');
        const currentStock = btn.getAttribute('data-stock');
        promptAdjustStock(id, name, currentStock);
      });
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function promptAdjustStock(itemId, itemName, currentStock) {
  const bodyHtml = `
    <p>Adjusting stock for <strong>${escapeHtml(itemName)}</strong> (Current: ${currentStock}):</p>
    <div class="form-group" style="margin-top:1rem;">
      <label class="form-label" for="stock-delta">Adjustment Amount (+ to restock, - to write off):</label>
      <input type="number" id="stock-delta" class="form-control" step="0.01" placeholder="e.g. 10 or -2.5" required>
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-stock">Apply Adjustment</button>
  `;

  openModal(`Adjust Inventory: ${itemName}`, bodyHtml, footerHtml);
  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-stock').onclick = async () => {
    const delta = Number(document.getElementById('stock-delta').value);
    if (isNaN(delta) || delta === 0) {
      showToast('Please enter a non-zero adjustment value.', 'error');
      return;
    }

    try {
      await api.items.adjustStock(itemId, delta);
      closeModal();
      showToast(`Stock updated successfully!`, 'success');
      renderInventoryView();
    } catch (err) {
      showToast(`Stock adjustment error: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 5: Customer Directory & Reports (#customers)
// =============================================================================
async function renderCustomersView() {
  const tbody = document.getElementById('tbody-customers');
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading customers...</td></tr>`;

  try {
    const customers = await api.customers.getAll();

    tbody.innerHTML = customers
      .map((c) => {
        const telegramHtml = c.telegram_chat_id
          ? `<span class="badge badge-active">Chat ID: ${c.telegram_chat_id}</span>`
          : `<span style="color:var(--color-text-muted); font-size:0.8rem;">Not linked</span>`;

        const deniedHtml = c.times_denied > 0
          ? `<span class="badge badge-failed">${c.times_denied} Denied</span> <small style="color:var(--color-text-muted);">${c.last_denied_date || ''}</small>`
          : `<span class="badge badge-available">0 (Clean)</span>`;

        return `
          <tr>
            <td>#${c.customer_id}</td>
            <td><strong>${escapeHtml(c.full_name)}</strong></td>
            <td>${escapeHtml(c.phone || 'No phone')}</td>
            <td>${telegramHtml}</td>
            <td>${deniedHtml}</td>
            <td><small style="color:var(--color-text-muted);">${(c.created_at || '').substring(0, 10)}</small></td>
            <td>
              <button class="btn btn-sm btn-outline btn-view-history" data-id="${c.customer_id}">
                View Profile & History
              </button>
            </td>
          </tr>
        `;
      })
      .join('');

    tbody.querySelectorAll('.btn-view-history').forEach((btn) => {
      btn.addEventListener('click', () => showCustomerHistoryModal(btn.getAttribute('data-id')));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function showCustomerHistoryModal(customerId) {
  try {
    const history = await api.customers.getHistory(customerId);
    if (!history) return;

    const reportsHtml = (history.reports || []).length
      ? history.reports.map((r) => `<li style="padding:4px 0; border-bottom:1px solid var(--color-border); font-size:0.82rem;"><strong>${r.created_at.substring(0, 10)}:</strong> ${escapeHtml(r.note)}</li>`).join('')
      : '<li style="color:var(--color-text-muted); font-size:0.82rem;">No incident notes on file.</li>';

    const messagesHtml = (history.messages || []).length
      ? history.messages.map((m) => `<li style="padding:4px 0; font-size:0.82rem;"><strong>[${m.status}] ${m.sent_at.substring(0, 10)}:</strong> ${escapeHtml(m.content)}</li>`).join('')
      : '<li style="color:var(--color-text-muted); font-size:0.82rem;">No messages sent.</li>';

    const bodyHtml = `
      <div style="font-size:0.9rem;">
        <h4>${escapeHtml(history.customer.full_name)}</h4>
        <p style="color:var(--color-text-muted); font-size:0.85rem;">Phone: ${escapeHtml(history.customer.phone || 'None')} | Telegram: ${history.customer.telegram_chat_id || 'None'}</p>
        <p style="margin-top:6px;"><strong>Denials Count:</strong> ${history.customer.times_denied} (Last denied: ${history.customer.last_denied_date || 'N/A'})</p>

        <h5 style="margin-top:1rem; margin-bottom:0.4rem; color:var(--color-primary);">Customer Reports & Incident Log:</h5>
        <ul style="list-style:none; padding-left:0;">${reportsHtml}</ul>

        <h5 style="margin-top:1rem; margin-bottom:0.4rem; color:var(--color-primary);">Communications History:</h5>
        <ul style="list-style:none; padding-left:0;">${messagesHtml}</ul>
      </div>
    `;

    const footerHtml = `<button class="btn btn-outline btn-sm" id="btn-modal-close">Close</button>`;

    openModal(`Guest Dossier - ${history.customer.full_name}`, bodyHtml, footerHtml);
    document.getElementById('btn-modal-close').onclick = closeModal;
  } catch (err) {
    showToast(`Error: ${err.message}`, 'error');
  }
}

// =============================================================================
// Event Listeners & Initialization
// =============================================================================
function setupEventListeners() {
  // Hash Routing
  window.addEventListener('hashchange', handleRoute);

  // Login & Logout
  dom.loginForm.addEventListener('submit', handleLoginSubmit);
  dom.btnLogout.addEventListener('click', handleLogout);

  // Top Bar pending button -> Jump to #requests
  dom.topbarPendingBtn.addEventListener('click', () => {
    window.location.hash = '#requests';
  });

  // Modal close handlers
  dom.modalCloseBtn.addEventListener('click', closeModal);
  dom.modalContainer.addEventListener('click', (e) => {
    if (e.target === dom.modalContainer) closeModal();
  });

  // Reset Mock Database button
  dom.btnResetMockDb.addEventListener('click', async () => {
    if (confirm('Reset entire mock database in localStorage back to original seed data?')) {
      await api.system.resetDatabase();
      showToast('Mock database re-seeded to factory default.', 'success');
      handleRoute();
      updatePendingRequestsBadge();
    }
  });

  // Filter Buttons for Requests View
  const reqFilterContainer = document.getElementById('requests-status-filters');
  if (reqFilterContainer) {
    reqFilterContainer.addEventListener('click', (e) => {
      if (e.target.classList.contains('filter-pill')) {
        reqFilterContainer.querySelectorAll('.filter-pill').forEach((p) => p.classList.remove('active'));
        e.target.classList.add('active');
        state.requestsFilter = e.target.getAttribute('data-status');
        renderBookingRequestsView();
      }
    });
  }

  // Search input for requests
  const reqSearchInput = document.getElementById('requests-search-input');
  if (reqSearchInput) {
    reqSearchInput.addEventListener('input', (e) => {
      state.requestsSearch = e.target.value.trim();
      renderBookingRequestsView();
    });
  }

  // Filter Buttons for Rooms View
  const roomFilterContainer = document.getElementById('rooms-status-filters');
  if (roomFilterContainer) {
    roomFilterContainer.addEventListener('click', (e) => {
      if (e.target.classList.contains('filter-pill')) {
        roomFilterContainer.querySelectorAll('.filter-pill').forEach((p) => p.classList.remove('active'));
        e.target.classList.add('active');
        state.roomsFilter = e.target.getAttribute('data-status');
        renderRoomsView();
      }
    });
  }

  // Filter Buttons for Inventory View
  const invFilterContainer = document.getElementById('inventory-method-filters');
  if (invFilterContainer) {
    invFilterContainer.addEventListener('click', (e) => {
      if (e.target.classList.contains('filter-pill')) {
        invFilterContainer.querySelectorAll('.filter-pill').forEach((p) => p.classList.remove('active'));
        e.target.classList.add('active');
        state.inventoryFilter = e.target.getAttribute('data-method');
        renderInventoryView();
      }
    });
  }

  // View Refresh Buttons
  document.getElementById('btn-refresh-requests')?.addEventListener('click', () => {
    renderBookingRequestsView();
    updatePendingRequestsBadge();
    showToast('Refreshed requests.', 'info');
  });

  document.getElementById('btn-refresh-stays')?.addEventListener('click', () => {
    renderStaysView();
    showToast('Refreshed stays.', 'info');
  });

  document.getElementById('btn-refresh-rooms')?.addEventListener('click', () => {
    renderRoomsView();
    showToast('Refreshed rooms.', 'info');
  });

  document.getElementById('btn-refresh-inventory')?.addEventListener('click', () => {
    renderInventoryView();
    showToast('Refreshed inventory.', 'info');
  });

  document.getElementById('btn-refresh-customers')?.addEventListener('click', () => {
    renderCustomersView();
    showToast('Refreshed customers.', 'info');
  });
}

// App Bootstrap
document.addEventListener('DOMContentLoaded', () => {
  setupEventListeners();
  initAuth();
});
