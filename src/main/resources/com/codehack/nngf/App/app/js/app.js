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
  staysFilter: 'ALL',
  roomsFilter: 'ALL',
  inventoryFilter: 'ALL',
  customersSearch: '',
  messagesFilter: 'ALL',
  activeFolioBookingId: null,
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

  // Modals
  modalContainer: document.getElementById('modal-container'),
  modalTitle: document.getElementById('modal-title'),
  modalBody: document.getElementById('modal-body'),
  modalFooter: document.getElementById('modal-footer'),
  modalCloseBtn: document.getElementById('modal-close-btn'),

  // Slide-Over Guest Folio Drawer
  folioDrawerOverlay: document.getElementById('folio-drawer-overlay'),
  folioDrawer: document.getElementById('folio-drawer'),
  folioTitle: document.getElementById('folio-title'),
  folioStatusBadge: document.getElementById('folio-status-badge'),
  folioSubtitle: document.getElementById('folio-subtitle'),
  folioBody: document.getElementById('folio-body'),
  folioCloseBtn: document.getElementById('folio-close-btn')
};

// =============================================================================
// Toast Notification Utility
// =============================================================================
function showToast(message, level = 'info', visibleTime = 4000) {
  return window.showToast(message, level, visibleTime);
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
          (r.booking_ref && r.booking_ref.toLowerCase().includes(q)) ||
          (r.guest_phone && r.guest_phone.toLowerCase().includes(q))
      );
    }

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2.5rem; color: var(--color-text-muted);">No booking requests found matching criteria.</td></tr>`;
      return;
    }

    tbody.innerHTML = filtered
      .map((req) => {
        // Highlighted box for special_request
        const specialReqHtml = (req.special_request && req.special_request.trim())
          ? `<div class="special-request-box">
               <span class="special-req-tag">★ Special Request</span>
               <span>${escapeHtml(req.special_request)}</span>
             </div>`
          : `<span class="special-request-empty">None specified</span>`;

        // Meal plan badge (RO, BB, HB, FB, or "Unassigned" if NULL)
        let mealPlanBadgeHtml = '';
        if (req.meal_plan && req.meal_plan.code) {
          mealPlanBadgeHtml = `<span class="badge badge-active" title="${escapeHtml(req.meal_plan.name)}">${escapeHtml(req.meal_plan.code)}</span> <small style="display:block; color:var(--color-text-muted); font-size:0.75rem; margin-top:2px;">${escapeHtml(req.meal_plan.name)}</small>`;
        } else {
          mealPlanBadgeHtml = `<span class="badge badge-closed">Unassigned</span>`;
        }

        let actionsHtml = '';
        if (req.status === 'PENDING') {
          actionsHtml = `
            <div style="display:flex; gap:6px;">
              <button class="btn btn-sm btn-primary btn-accept-req" data-id="${req.request_id}">Accept</button>
              <button class="btn btn-sm btn-outline btn-deny-req" data-id="${req.request_id}" style="color:var(--status-failed-text);">Deny</button>
            </div>
          `;
        } else {
          actionsHtml = `<span style="color:var(--color-text-muted); font-size:0.78rem;">Decided (${renderStatusBadge(req.status)})</span>`;
        }

        return `
          <tr>
            <td>
              <strong style="color:var(--color-primary); font-size:0.95rem;">${escapeHtml(req.booking_ref)}</strong><br>
              ${renderStatusBadge(req.status)}
            </td>
            <td>
              <div style="font-weight:600;">${escapeHtml(req.guest_name)}</div>
              <small style="color:var(--color-text-muted);">${escapeHtml(req.guest_phone || 'No phone provided')}</small>
            </td>
            <td>${mealPlanBadgeHtml}</td>
            <td>
              <div><strong>${escapeHtml(req.check_in)}</strong> → <strong>${escapeHtml(req.check_out)}</strong></div>
              <small style="color:var(--color-text-muted); font-weight:600;">${req.rooms_wanted} Room(s) Wanted</small>
            </td>
            <td>${specialReqHtml}</td>
            <td>
              <strong style="color:var(--color-text-main); font-size:1rem;">$${Number(req.total_price || 0).toFixed(2)}</strong>
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
 * Modal Workflow: Accept Booking Request & Assign Exactly rooms_wanted Available Rooms
 */
async function promptAcceptRequest(requestId) {
  try {
    const req = await api.bookingRequests.getById(requestId);
    if (!req) return;

    // Get rooms available for requested stay dates (where status === 'AVAILABLE')
    const allAvailableRooms = await api.rooms.getAvailable(req.check_in, req.check_out);
    const availableRooms = (allAvailableRooms || []).filter((r) => r.status === 'AVAILABLE');
    const mealPlans = await api.mealPlans.getAll({ is_active: 1 });

    if (availableRooms.length < req.rooms_wanted) {
      showToast(`Insufficient available rooms: ${availableRooms.length} available, but ${req.rooms_wanted} required for ${req.check_in} to ${req.check_out}.`, 'error');
    }

    const roomCheckboxesHtml = availableRooms.length
      ? availableRooms
          .map(
            (r) => `
            <label class="room-checkbox-label" for="assign-room-${r.room_id}" style="display:flex; align-items:center; justify-content:space-between; padding:8px 12px; border:1px solid var(--color-border); border-radius:6px; margin-bottom:6px; cursor:pointer; background:#fff;">
              <div style="display:flex; align-items:center; gap:10px;">
                <input type="checkbox" id="assign-room-${r.room_id}" name="assigned_room" value="${r.room_id}" class="room-assign-cb">
                <div>
                  <strong>Room ${escapeHtml(r.room_number)}</strong> — ${escapeHtml(r.room_type)}
                </div>
              </div>
              <span style="font-weight:700; color:var(--color-primary); font-size:0.85rem;">$${Number(r.price_per_night).toFixed(2)} / nt</span>
            </label>
          `
          )
          .join('')
      : `<div style="color:var(--status-failed-text); padding:10px; text-align:center;">No rooms currently have status AVAILABLE for these stay dates.</div>`;

    const mealPlanOptionsHtml = mealPlans
      .map(
        (mp) => `<option value="${mp.meal_plan_id}" ${req.meal_plan_id === mp.meal_plan_id ? 'selected' : ''}>${escapeHtml(mp.code)} - ${escapeHtml(mp.name)}</option>`
      )
      .join('');

    const bodyHtml = `
      <div style="margin-bottom:1.25rem;">
        <div style="display:flex; justify-content:space-between; align-items:flex-start;">
          <div>
            <h4 style="font-size:1.1rem; font-weight:700; color:var(--color-text-main);">${escapeHtml(req.guest_name)}</h4>
            <div style="font-size:0.85rem; color:var(--color-text-muted); margin-top:2px;">
              Ref: <strong>${escapeHtml(req.booking_ref)}</strong> | Phone: ${escapeHtml(req.guest_phone || 'None provided')}
            </div>
          </div>
          <div style="text-align:right;">
            <div style="font-size:0.8rem; color:var(--color-text-muted);">${escapeHtml(req.check_in)} → ${escapeHtml(req.check_out)}</div>
            <strong style="color:var(--color-primary); font-size:0.95rem;">${req.rooms_wanted} Room(s) Required</strong>
          </div>
        </div>
      </div>

      ${req.special_request ? `
        <div class="special-request-box" style="max-width:100%; margin-bottom:1.25rem;">
          <span class="special-req-tag">★ Special Request From Email:</span>
          <span>${escapeHtml(req.special_request)}</span>
        </div>` : ''}

      <div class="form-group" style="margin-bottom:1.25rem;">
        <label class="form-label" for="accept-meal-plan-select">Meal Plan Confirmation:</label>
        <select id="accept-meal-plan-select" class="form-control">
          <option value="">None / Unassigned (Room Only)</option>
          ${mealPlanOptionsHtml}
        </select>
      </div>

      <div class="form-group">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:6px;">
          <span class="form-label" style="margin-bottom:0;">
            Select <strong>exactly ${req.rooms_wanted}</strong> room(s) (where status === AVAILABLE):
          </span>
          <span id="room-selection-counter" class="room-select-counter invalid">0 of ${req.rooms_wanted} selected</span>
        </div>
        <div style="max-height:220px; overflow-y:auto; border:1px solid var(--color-border); padding:6px; border-radius:6px; background:var(--color-surface-alt);">
          ${roomCheckboxesHtml}
        </div>
      </div>
    `;

    const footerHtml = `
      <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
      <button class="btn btn-primary btn-sm" id="btn-modal-confirm-accept">Confirm & Create Booking</button>
    `;

    openModal(`Accept Request #${req.booking_ref}`, bodyHtml, footerHtml);

    // Live room counter update
    const counterEl = document.getElementById('room-selection-counter');
    const checkboxes = document.querySelectorAll('.room-assign-cb');
    function updateCounter() {
      const checkedCount = document.querySelectorAll('.room-assign-cb:checked').length;
      if (checkedCount === req.rooms_wanted) {
        counterEl.className = 'room-select-counter valid';
        counterEl.innerText = `Exactly ${checkedCount} of ${req.rooms_wanted} selected ✓`;
      } else {
        counterEl.className = 'room-select-counter invalid';
        counterEl.innerText = `${checkedCount} of ${req.rooms_wanted} selected`;
      }
    }
    checkboxes.forEach((cb) => cb.addEventListener('change', updateCounter));

    document.getElementById('btn-modal-cancel').onclick = closeModal;
    document.getElementById('btn-modal-confirm-accept').onclick = async () => {
      const selected = Array.from(document.querySelectorAll('.room-assign-cb:checked')).map((cb) => Number(cb.value));

      // Enforce selecting exactly rooms_wanted rooms
      if (selected.length !== req.rooms_wanted) {
        showToast(`Validation Error: Please select exactly ${req.rooms_wanted} room(s). Currently selected: ${selected.length}`, 'error');
        return;
      }

      const mealPlanVal = document.getElementById('accept-meal-plan-select').value;
      const mealPlanId = mealPlanVal ? Number(mealPlanVal) : null;

      try {
        await api.bookingRequests.accept(requestId, {
          roomIds: selected,
          decidedBy: state.currentUser ? state.currentUser.owner_id : 1,
          mealPlanId
        });
        closeModal();
        showToast(`Request ${req.booking_ref} accepted! PENDING booking created.`, 'success');
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
    <p>Are you sure you want to deny reservation request <strong>${escapeHtml(req.booking_ref)}</strong> for <strong>${escapeHtml(req.guest_name)}</strong>?</p>
    <div class="form-group" style="margin-top:1rem;">
      <label class="form-label" for="deny-reason-select">Denial Reason (logged in customer dossier & message):</label>
      <select id="deny-reason-select" class="form-control" style="margin-bottom:8px;">
        <option value="No available rooms matching criteria">No available rooms matching criteria</option>
        <option value="Non-smoking policy conflict">Non-smoking policy conflict</option>
        <option value="Stay duration or pricing condition not met">Stay duration or pricing condition not met</option>
        <option value="Guest requested cancellation">Guest requested cancellation</option>
        <option value="Custom">Other reason (specify below)...</option>
      </select>
      <label class="form-label" for="deny-reason-custom" id="deny-reason-custom-label" style="display:none; margin-top:6px;">Custom Reason Notes:</label>
      <input type="text" id="deny-reason-custom" class="form-control" placeholder="Specify denial notes..." style="display:none;">
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-deny" style="background:#ef4444;">Confirm Denial</button>
  `;

  openModal(`Deny Request #${req.booking_ref}`, bodyHtml, footerHtml);

  const sel = document.getElementById('deny-reason-select');
  const customInput = document.getElementById('deny-reason-custom');
  const customLabel = document.getElementById('deny-reason-custom-label');
  sel.onchange = () => {
    const isCustom = sel.value === 'Custom';
    customInput.style.display = isCustom ? 'block' : 'none';
    if (customLabel) customLabel.style.display = isCustom ? 'block' : 'none';
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
      showToast(`Request ${req.booking_ref} denied. Customer record & denial counter updated.`, 'info');
      renderBookingRequestsView();
      updatePendingRequestsBadge();
    } catch (err) {
      showToast(`Failed to deny request: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 2: Active Stays & Guest Folio (#stays)
// =============================================================================
async function renderStaysView() {
  const tbody = document.getElementById('tbody-stays');
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading bookings...</td></tr>`;

  try {
    const allBookings = await api.bookings.getAll();
    if (!allBookings.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2.5rem; color:var(--color-text-muted);">No bookings recorded yet.</td></tr>`;
      return;
    }

    // Filter by status (ALL, PENDING, ACTIVE, FINISHED, CANCELED)
    let filtered = allBookings;
    if (state.staysFilter !== 'ALL') {
      filtered = filtered.filter((b) => b.status === state.staysFilter);
    }

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem; color:var(--color-text-muted);">No bookings found with status ${state.staysFilter}.</td></tr>`;
      return;
    }

    tbody.innerHTML = filtered
      .map((b) => {
        const guestName = b.customer ? b.customer.full_name : 'Guest';
        const roomNumbers = (b.rooms || []).map((r) => `Room ${r.room_number}`).join(', ') || 'Unassigned';

        // Calculate nights
        const checkInDate = new Date(b.check_in);
        const checkOutDate = new Date(b.check_out);
        const nights = Math.max(1, Math.ceil(Math.abs(checkOutDate - checkInDate) / (1000 * 60 * 60 * 24)));

        let actionButtonsHtml = '';
        if (b.status === 'PENDING') {
          actionButtonsHtml = `
            <button class="btn btn-sm btn-primary btn-checkin-stay" data-id="${b.booking_id}" title="Check In Guest (PENDING -> ACTIVE)">Check In</button>
            <button class="btn btn-sm btn-outline btn-noshow-stay" data-id="${b.booking_id}" style="color:var(--status-failed-text);" title="Cancel No-Show (PENDING -> CANCELED)">No-Show</button>
            <button class="btn btn-sm btn-outline btn-open-folio" data-id="${b.booking_id}" title="Open Guest Folio Drawer">Folio</button>
          `;
        } else if (b.status === 'ACTIVE') {
          actionButtonsHtml = `
            <button class="btn btn-sm btn-primary btn-checkout-stay" data-id="${b.booking_id}" title="Check Out Guest (ACTIVE -> FINISHED)">Check Out</button>
            <button class="btn btn-sm btn-outline btn-open-folio" data-id="${b.booking_id}" title="Open Guest Folio Drawer">Folio</button>
          `;
        } else {
          actionButtonsHtml = `
            <button class="btn btn-sm btn-outline btn-open-folio" data-id="${b.booking_id}" title="View Settled Folio">View Folio</button>
          `;
        }

        return `
          <tr>
            <td>
              <strong>Booking #${b.booking_id}</strong><br>
              <small style="color:var(--color-text-muted); font-size:0.75rem;">${b.booking_request ? escapeHtml(b.booking_request.booking_ref) : ''}</small>
            </td>
            <td>
              <div style="font-weight:600;">${escapeHtml(guestName)}</div>
              <small style="color:var(--color-text-muted);">${b.customer ? escapeHtml(b.customer.phone || 'No phone') : ''}</small>
            </td>
            <td><span class="badge badge-booked">${escapeHtml(roomNumbers)}</span></td>
            <td>
              <div>${escapeHtml(b.check_in)} → ${escapeHtml(b.check_out)}</div>
              <small style="color:var(--color-text-muted); font-weight:600;">${nights} night(s)</small>
            </td>
            <td>${renderStatusBadge(b.status)}</td>
            <td>
              <button class="btn btn-sm btn-outline btn-open-folio" data-id="${b.booking_id}" style="font-weight:600; color:var(--color-primary);">
                Folio & Ledger →
              </button>
            </td>
            <td>
              <div style="display:flex; gap:6px; flex-wrap:wrap;">
                ${actionButtonsHtml}
              </div>
            </td>
          </tr>
        `;
      })
      .join('');

    // Attach Stay actions
    tbody.querySelectorAll('.btn-open-folio').forEach((btn) => {
      btn.addEventListener('click', () => openFolioDrawer(btn.getAttribute('data-id')));
    });

    tbody.querySelectorAll('.btn-checkin-stay').forEach((btn) => {
      btn.addEventListener('click', () => handleCheckIn(btn.getAttribute('data-id')));
    });

    tbody.querySelectorAll('.btn-noshow-stay').forEach((btn) => {
      btn.addEventListener('click', () => promptNoShowCancel(btn.getAttribute('data-id')));
    });

    tbody.querySelectorAll('.btn-checkout-stay').forEach((btn) => {
      btn.addEventListener('click', () => handleCheckOut(btn.getAttribute('data-id')));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

/**
 * Check In: PENDING -> ACTIVE
 */
async function handleCheckIn(bookingId) {
  try {
    await api.bookings.checkIn(bookingId);
    showToast(`Guest checked in! Booking #${bookingId} is now ACTIVE.`, 'success');
    renderStaysView();
    if (state.activeFolioBookingId === Number(bookingId)) {
      renderFolioDrawer(bookingId);
    }
  } catch (err) {
    showToast(`Check-in failed: ${err.message}`, 'error');
  }
}

/**
 * Cancel No-Show: PENDING -> CANCELED
 * Sets BOOKING.status = 'CANCELED', resets rooms to 'AVAILABLE', offers optional CUSTOMER_REPORT note modal
 */
async function promptNoShowCancel(bookingId) {
  try {
    const booking = await api.bookings.getById(bookingId);
    if (!booking) return;

    const guestName = booking.customer ? booking.customer.full_name : 'Guest';

    const bodyHtml = `
      <p>Confirm marking Booking <strong>#${bookingId}</strong> (${escapeHtml(guestName)}) as <strong>No-Show</strong>?</p>
      <p style="font-size:0.85rem; color:var(--color-text-muted); margin-top:4px;">
        This will cancel the reservation and immediately release all assigned rooms back to <strong>AVAILABLE</strong>.
      </p>

      <div class="form-group" style="margin-top:1.25rem;">
        <label class="form-label" for="noshow-report-note">Optional Incident Note for Customer Dossier:</label>
        <textarea id="noshow-report-note" class="form-control" rows="3" placeholder="e.g. Guest failed to arrive by 22:00 cutoff; telephone contact went unanswered."></textarea>
      </div>
    `;

    const footerHtml = `
      <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Keep Reservation</button>
      <button class="btn btn-primary btn-sm" id="btn-modal-confirm-noshow" style="background:#ef4444;">Cancel Reservation</button>
    `;

    openModal(`No-Show Cancel - Booking #${bookingId}`, bodyHtml, footerHtml);

    document.getElementById('btn-modal-cancel').onclick = closeModal;
    document.getElementById('btn-modal-confirm-noshow').onclick = async () => {
      const note = document.getElementById('noshow-report-note').value.trim();

      try {
        if (note && booking.customer_id) {
          await api.customerReports.create({
            customerId: booking.customer_id,
            bookingId: booking.booking_id,
            note: `[No-Show Cancellation] ${note}`
          });
        }

        await api.bookings.cancel(bookingId);
        closeModal();
        showToast(`Booking #${bookingId} canceled as No-Show. Assigned rooms released to AVAILABLE.`, 'info');
        renderStaysView();
        if (state.activeFolioBookingId === Number(bookingId)) {
          renderFolioDrawer(bookingId);
        }
      } catch (err) {
        showToast(`Error: ${err.message}`, 'error');
      }
    };
  } catch (err) {
    showToast(`Error: ${err.message}`, 'error');
  }
}

/**
 * Check Out: ACTIVE -> FINISHED
 * Sets BOOKING.status = 'FINISHED' and resets rooms back to 'AVAILABLE'
 */
async function handleCheckOut(bookingId) {
  try {
    const booking = await api.bookings.getById(bookingId);
    if (!booking) return;

    const b = booking.billing;
    if (b && b.balance_due > 0) {
      if (!confirm(`Warning: Booking #${bookingId} has an outstanding balance of $${b.balance_due.toFixed(2)}!\n\nDo you want to proceed with Check-Out and release rooms anyway?`)) {
        openFolioDrawer(bookingId);
        return;
      }
    }

    await api.bookings.checkOut(bookingId);
    showToast(`Booking #${bookingId} checked out. Status is now FINISHED and rooms are AVAILABLE!`, 'success');
    renderStaysView();
    if (state.activeFolioBookingId === Number(bookingId)) {
      renderFolioDrawer(bookingId);
    }
  } catch (err) {
    showToast(`Check-out failed: ${err.message}`, 'error');
  }
}

// =============================================================================
// Slide-Over Guest Folio Drawer & Live Ledger
// =============================================================================
function openFolioDrawer(bookingId) {
  state.activeFolioBookingId = Number(bookingId);
  dom.folioDrawerOverlay.classList.remove('hidden');
  renderFolioDrawer(bookingId);
}

function closeFolioDrawer() {
  dom.folioDrawerOverlay.classList.add('hidden');
  dom.folioBody.innerHTML = '';
  state.activeFolioBookingId = null;
}

async function renderFolioDrawer(bookingId) {
  dom.folioBody.innerHTML = `<div style="text-align:center; padding:2rem; color:var(--color-text-muted);">Loading folio data...</div>`;

  try {
    const booking = await api.bookings.getById(bookingId);
    if (!booking) {
      dom.folioBody.innerHTML = `<div style="color:red;">Booking #${bookingId} not found.</div>`;
      return;
    }

    const activeItems = (await api.items.getAll()).filter((i) => i.is_active === 1);

    // Header updates
    const guestName = booking.customer ? booking.customer.full_name : 'Guest';
    dom.folioTitle.innerText = `Folio: ${guestName} (Booking #${booking.booking_id})`;
    dom.folioStatusBadge.innerHTML = renderStatusBadge(booking.status);
    dom.folioSubtitle.innerText = `Stay: ${booking.check_in} to ${booking.check_out} | Ref: ${booking.booking_request?.booking_ref || 'Direct'}`;

    // Calculate nights: check_out - check_in
    const checkInDate = new Date(booking.check_in);
    const checkOutDate = new Date(booking.check_out);
    const diffDays = Math.ceil(Math.abs(checkOutDate - checkInDate) / (1000 * 60 * 60 * 24));
    const nights = Math.max(1, diffDays);

    // 1. Room Charges Section (from BOOKING_ROOM)
    let roomChargesTotal = 0;
    const roomRowsHtml = (booking.rooms || []).map((r) => {
      const rate = Number(r.price_per_night) || 0;
      const subtotal = rate * nights;
      roomChargesTotal += subtotal;
      return `
        <tr>
          <td><strong>Room ${escapeHtml(r.room_number)}</strong> (${escapeHtml(r.room_type)})</td>
          <td>$${rate.toFixed(2)}/nt</td>
          <td>${nights} night(s)</td>
          <td style="text-align:right; font-weight:700;">$${subtotal.toFixed(2)}</td>
        </tr>
      `;
    }).join('');

    // 2. Item Orders Section
    let itemOrdersTotal = 0;
    const ordersRowsHtml = (booking.item_orders || []).length
      ? (booking.item_orders || []).map((io) => {
          const subtotal = Number(io.total_price || io.amount * io.unit_price);
          itemOrdersTotal += subtotal;
          const methodBadge = io.measuring_method === 'COUNT'
            ? `<span class="badge badge-count">COUNT</span>`
            : `<span class="badge badge-continuous">CONTINUOUS</span>`;

          return `
            <tr>
              <td><strong>${escapeHtml(io.item_name)}</strong> ${methodBadge}</td>
              <td>$${Number(io.unit_price).toFixed(2)}</td>
              <td>${io.amount}</td>
              <td style="text-align:right; font-weight:700;">$${subtotal.toFixed(2)}</td>
              <td style="text-align:right;">
                <button class="btn btn-sm btn-outline btn-delete-folio-order" data-order-id="${io.order_id}" style="padding:2px 6px; color:#ef4444;" title="Cancel Order & Restock">&times;</button>
              </td>
            </tr>
          `;
        }).join('')
      : `<tr><td colspan="5" style="text-align:center; color:var(--color-text-muted); padding:10px;">No supplies or room service ordered yet.</td></tr>`;

    // 3. Payments Section
    let paymentsTotal = 0;
    const paymentsRowsHtml = (booking.payments || []).length
      ? (booking.payments || []).map((p) => {
          paymentsTotal += Number(p.amount);
          return `
            <tr>
              <td>Payment #${p.payment_id}</td>
              <td><span class="badge badge-active">${escapeHtml(p.method)}</span></td>
              <td><small style="color:var(--color-text-muted);">${(p.paid_at || '').substring(0, 16).replace('T', ' ')}</small></td>
              <td style="text-align:right; font-weight:700; color:#059669;">-$${Number(p.amount).toFixed(2)}</td>
            </tr>
          `;
        }).join('')
      : `<tr><td colspan="4" style="text-align:center; color:var(--color-text-muted); padding:10px;">No payments recorded yet.</td></tr>`;

    // 4. Live Math: Room Total + Item Orders Total - Payments Total = Balance Due
    const totalCharges = parseFloat((roomChargesTotal + itemOrdersTotal).toFixed(2));
    const balanceDue = parseFloat((totalCharges - paymentsTotal).toFixed(2));

    // Active item dropdown options with data attributes for stock and measuring method
    const activeItemOptionsHtml = activeItems.map((itm) => {
      return `<option value="${itm.item_id}" data-method="${itm.measuring_method}" data-price="${itm.price_per_unit}" data-stock="${itm.stock_amount}">
        ${escapeHtml(itm.name)} ($${Number(itm.price_per_unit).toFixed(2)} / ${itm.measuring_method === 'COUNT' ? 'unit' : 'metric'}) [Stock: ${itm.stock_amount}]
      </option>`;
    }).join('');

    // Default first item details for form
    const defaultItem = activeItems[0] || null;

    dom.folioBody.innerHTML = `
      <!-- Live Math Card -->
      <div class="folio-math-card">
        <div class="folio-math-formula">Live Ledger Equation: Room Charges + Orders - Payments = Balance Due</div>
        <div class="folio-math-breakdown">
          <div class="folio-math-col">
            <div class="math-label">Room Total</div>
            <div class="math-val">$${roomChargesTotal.toFixed(2)}</div>
          </div>
          <div class="folio-math-op">+</div>
          <div class="folio-math-col">
            <div class="math-label">Item Orders</div>
            <div class="math-val">$${itemOrdersTotal.toFixed(2)}</div>
          </div>
          <div class="folio-math-op">-</div>
          <div class="folio-math-col">
            <div class="math-label">Payments Total</div>
            <div class="math-val">$${paymentsTotal.toFixed(2)}</div>
          </div>
        </div>
        <div class="folio-math-total">
          <div class="due-label">
            <span>Balance Due:</span>
            <div style="font-size:0.75rem; color:#94a3b8; font-weight:normal;">
              ${balanceDue === 0 ? '✓ Settled in Full (Ready for Check-Out)' : (balanceDue > 0 ? 'Outstanding balance pending' : 'Credit / Overpayment')}
            </div>
          </div>
          <div class="due-amount ${balanceDue > 0 ? 'due-positive' : 'due-settled'}">
            $${balanceDue.toFixed(2)}
          </div>
        </div>
      </div>

      <!-- Section 1: Assigned Rooms & Rates -->
      <div class="folio-section">
        <div class="folio-section-title">
          <span>Assigned Rooms (${booking.rooms?.length || 0})</span>
          <span style="font-weight:700; color:var(--color-primary);">$${roomChargesTotal.toFixed(2)}</span>
        </div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>Room</th>
              <th>Rate</th>
              <th>Duration</th>
              <th style="text-align:right;">Subtotal</th>
            </tr>
          </thead>
          <tbody>
            ${roomRowsHtml}
          </tbody>
        </table>
      </div>

      <!-- Section 2: Item Orders (Supplies & Room Service) -->
      <div class="folio-section">
        <div class="folio-section-title">
          <span>Item Orders & Supplies</span>
          <span style="font-weight:700; color:var(--color-primary);">$${itemOrdersTotal.toFixed(2)}</span>
        </div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Unit Price</th>
              <th>Qty/Amt</th>
              <th style="text-align:right;">Subtotal</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            ${ordersRowsHtml}
          </tbody>
        </table>

        <!-- Add Item Order Inline Form -->
        <form id="folio-add-order-form" class="mini-form-row">
          <div class="mini-form-group" style="flex:2;">
            <label for="folio-item-select">Add Active Item (is_active === 1):</label>
            <select id="folio-item-select" class="form-control" style="padding:0.45rem 0.65rem; font-size:0.825rem;" required>
              ${activeItemOptionsHtml}
            </select>
          </div>
          <div class="mini-form-group" style="flex:1;">
            <label for="folio-item-amount" id="folio-amount-label">Amount (Stock: ${defaultItem ? defaultItem.stock_amount : 0}):</label>
            <input type="number" id="folio-item-amount" class="form-control" style="padding:0.45rem 0.65rem; font-size:0.825rem;"
              min="${defaultItem && defaultItem.measuring_method === 'COUNT' ? '1' : '0.01'}"
              step="${defaultItem && defaultItem.measuring_method === 'COUNT' ? '1' : '0.01'}"
              max="${defaultItem ? defaultItem.stock_amount : '999'}"
              value="${defaultItem && defaultItem.measuring_method === 'COUNT' ? '1' : '1.00'}"
              required>
          </div>
          <button type="submit" class="btn btn-sm btn-primary" style="margin-bottom:1px;">+ Add Order</button>
        </form>
      </div>

      <!-- Section 3: Payments Ledger -->
      <div class="folio-section">
        <div class="folio-section-title">
          <span>Payments Ledger</span>
          <span style="font-weight:700; color:#059669;">-$${paymentsTotal.toFixed(2)}</span>
        </div>
        <table class="mini-table">
          <thead>
            <tr>
              <th>Payment ID</th>
              <th>Method</th>
              <th>Paid At</th>
              <th style="text-align:right;">Amount</th>
            </tr>
          </thead>
          <tbody>
            ${paymentsRowsHtml}
          </tbody>
        </table>

        <!-- Record Payment Inline Form -->
        <form id="folio-add-payment-form" class="mini-form-row">
          <div class="mini-form-group" style="flex:1;">
            <label for="folio-pay-method">Method:</label>
            <select id="folio-pay-method" class="form-control" style="padding:0.45rem 0.65rem; font-size:0.825rem;" required>
              <option value="CARD">CARD (Credit/Debit)</option>
              <option value="CASH">CASH</option>
              <option value="OTHER">OTHER (Transfer/Voucher)</option>
            </select>
          </div>
          <div class="mini-form-group" style="flex:1;">
            <label for="folio-pay-amount">Amount ($):</label>
            <input type="number" id="folio-pay-amount" class="form-control" style="padding:0.45rem 0.65rem; font-size:0.825rem;"
              min="0.01" step="0.01" value="${balanceDue > 0 ? balanceDue.toFixed(2) : ''}" placeholder="0.00" required>
          </div>
          <button type="submit" class="btn btn-sm btn-primary" style="margin-bottom:1px; background:#059669;">Record Pay</button>
        </form>
      </div>

      <!-- Drawer Actions Footer -->
      <div style="display:flex; justify-content:space-between; align-items:center; padding-top:1rem; border-top:1px solid var(--color-border); margin-top:auto;">
        <div>
          ${booking.status === 'PENDING' ? `
            <button class="btn btn-sm btn-primary btn-drawer-checkin">Check In Guest</button>
            <button class="btn btn-sm btn-outline btn-drawer-noshow" style="color:var(--status-failed-text);">No-Show Cancel</button>
          ` : ''}
          ${booking.status === 'ACTIVE' ? `
            <button class="btn btn-sm btn-primary btn-drawer-checkout">Complete Check Out</button>
          ` : ''}
        </div>
        <button class="btn btn-sm btn-outline" id="btn-drawer-close">Close Folio</button>
      </div>
    `;

    // -------------------------------------------------------------------------
    // Drawer Interactive Listeners
    // -------------------------------------------------------------------------
    const itemSelect = document.getElementById('folio-item-select');
    const amountInput = document.getElementById('folio-item-amount');
    const amountLabel = document.getElementById('folio-amount-label');

    // Dynamic Step & Max Stock sync on item change
    if (itemSelect && amountInput) {
      itemSelect.addEventListener('change', () => {
        const opt = itemSelect.selectedOptions[0];
        if (!opt) return;
        const method = opt.getAttribute('data-method');
        const stock = parseFloat(opt.getAttribute('data-stock')) || 0;

        amountLabel.innerText = `Amount (Stock: ${stock}):`;
        amountInput.max = stock;

        if (method === 'COUNT') {
          amountInput.step = '1';
          amountInput.min = '1';
          amountInput.value = Math.min(1, stock);
        } else {
          amountInput.step = '0.01';
          amountInput.min = '0.01';
          amountInput.value = Math.min(1.00, stock).toFixed(2);
        }
      });
    }

    // Submit Order Form
    const orderForm = document.getElementById('folio-add-order-form');
    if (orderForm) {
      orderForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const itemId = itemSelect.value;
        const amount = Number(amountInput.value);

        const opt = itemSelect.selectedOptions[0];
        const stock = parseFloat(opt.getAttribute('data-stock')) || 0;

        // Validation: amount > 0 and amount <= stock_amount
        if (amount <= 0) {
          showToast('Order amount must be greater than zero.', 'error');
          return;
        }
        if (amount > stock) {
          showToast(`Insufficient stock! Requested: ${amount}, Available: ${stock}`, 'error');
          return;
        }

        try {
          await api.itemOrders.create({ bookingId, itemId, amount });
          showToast(`Item order added and stock updated!`, 'success');
          renderFolioDrawer(bookingId);
          renderStaysView();
        } catch (err) {
          showToast(`Order failed: ${err.message}`, 'error');
        }
      });
    }

    // Delete Order buttons
    dom.folioBody.querySelectorAll('.btn-delete-folio-order').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const orderId = btn.getAttribute('data-order-id');
        if (confirm(`Remove this item order and return quantity back to stock?`)) {
          try {
            await api.itemOrders.delete(orderId);
            showToast('Order canceled and inventory returned to stock.', 'info');
            renderFolioDrawer(bookingId);
            renderStaysView();
          } catch (err) {
            showToast(`Error: ${err.message}`, 'error');
          }
        }
      });
    });

    // Submit Payment Form
    const paymentForm = document.getElementById('folio-add-payment-form');
    if (paymentForm) {
      paymentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = Number(document.getElementById('folio-pay-amount').value);
        const method = document.getElementById('folio-pay-method').value;

        // Validation: amount > 0 and method in 'CASH', 'CARD', 'OTHER'
        if (isNaN(amount) || amount <= 0) {
          showToast('Payment amount must be greater than zero.', 'error');
          return;
        }

        try {
          await api.payments.create({ bookingId, amount, method });
          showToast(`Payment of $${amount.toFixed(2)} recorded!`, 'success');
          renderFolioDrawer(bookingId);
          renderStaysView();
        } catch (err) {
          showToast(`Payment error: ${err.message}`, 'error');
        }
      });
    }

    // Drawer Bottom Buttons
    const btnDrawerClose = document.getElementById('btn-drawer-close');
    if (btnDrawerClose) btnDrawerClose.onclick = closeFolioDrawer;

    const btnDrawerCheckin = dom.folioBody.querySelector('.btn-drawer-checkin');
    if (btnDrawerCheckin) {
      btnDrawerCheckin.addEventListener('click', () => handleCheckIn(bookingId));
    }

    const btnDrawerNoshow = dom.folioBody.querySelector('.btn-drawer-noshow');
    if (btnDrawerNoshow) {
      btnDrawerNoshow.addEventListener('click', () => promptNoShowCancel(bookingId));
    }

    const btnDrawerCheckout = dom.folioBody.querySelector('.btn-drawer-checkout');
    if (btnDrawerCheckout) {
      btnDrawerCheckout.addEventListener('click', () => handleCheckOut(bookingId));
    }

  } catch (err) {
    dom.folioBody.innerHTML = `<div style="color:red; padding:1.5rem;">Error loading guest folio: ${escapeHtml(err.message)}</div>`;
  }
}


async function promptAddOrder(bookingId) {
  const items = await api.items.getAll({ is_active: 1 });
  const itemOptionsHtml = items
    .map((i) => `<option value="${i.item_id}" data-price="${i.price_per_unit}" data-method="${i.measuring_method}">${escapeHtml(i.name)} ($${Number(i.price_per_unit).toFixed(2)} / ${i.measuring_method === 'COUNT' ? 'unit' : 'metric'}) — Stock: ${i.stock_amount}</option>`)
    .join('');

  const bodyHtml = `
    <div class="form-group">
      <label class="form-label" for="order-item-id">Select Supply / Order Item:</label>
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
// View 3: Rooms & Kitchen (#rooms)
// =============================================================================
async function renderRoomsView() {
  await renderKitchenMealSummary();
  await renderRoomsGrid();
}

/**
 * Kitchen Meal Summary Panel:
 * Counts ACTIVE bookings grouped by MEAL_PLAN (RO, BB, HB, FB)
 */
async function renderKitchenMealSummary() {
  const panel = document.getElementById('kitchen-meal-summary');
  if (!panel) return;

  try {
    const [activeBookings, mealPlans] = await Promise.all([
      api.bookings.getAll({ status: 'ACTIVE' }),
      api.mealPlans.getAll()
    ]);

    // Meal counts map: code -> count
    const counts = {
      RO: 0,
      BB: 0,
      HB: 0,
      FB: 0
    };

    activeBookings.forEach((b) => {
      let code = 'RO';
      if (b.booking_request && b.booking_request.meal_plan_id) {
        const mp = mealPlans.find((m) => m.meal_plan_id === b.booking_request.meal_plan_id);
        if (mp && mp.code) {
          code = mp.code.toUpperCase();
        }
      }
      if (counts[code] !== undefined) {
        counts[code]++;
      } else {
        counts.RO++;
      }
    });

    panel.innerHTML = `
      <div class="kitchen-summary-header">
        <div style="display:flex; align-items:center; gap:8px;">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8h1a4 4 0 0 1 0 8h-1M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8zM6 1v3M10 1v3M14 1v3"/>
          </svg>
          <h3 style="font-size:1.05rem; font-weight:700; margin:0;">Kitchen Meal Summary (Active Checked-In Stays)</h3>
        </div>
        <span class="badge badge-active">${activeBookings.length} Active Stays</span>
      </div>
      <div class="kitchen-grid">
        <div class="kitchen-stat-box box-ro">
          <div class="meal-top">
            <span class="badge badge-closed">RO</span>
            <small style="color:var(--color-text-muted);">Room Only</small>
          </div>
          <div class="meal-name">Room Only</div>
          <div class="meal-count">${counts.RO}</div>
          <small>No catering / room-only reservation</small>
        </div>

        <div class="kitchen-stat-box box-bb">
          <div class="meal-top">
            <span class="badge badge-active">BB</span>
            <small style="color:var(--color-text-muted);">Breakfast Buffet</small>
          </div>
          <div class="meal-name">Breakfast Included</div>
          <div class="meal-count">${counts.BB}</div>
          <small>Morning dining buffet covers</small>
        </div>

        <div class="kitchen-stat-box box-hb">
          <div class="meal-top">
            <span class="badge badge-booked">HB</span>
            <small style="color:var(--color-text-muted);">Half Board</small>
          </div>
          <div class="meal-name">Half Board</div>
          <div class="meal-count">${counts.HB}</div>
          <small>Breakfast buffet + Dinner service</small>
        </div>

        <div class="kitchen-stat-box box-fb">
          <div class="meal-top">
            <span class="badge badge-warning">FB</span>
            <small style="color:var(--color-text-muted);">Full Board</small>
          </div>
          <div class="meal-name">Full Board</div>
          <div class="meal-count">${counts.FB}</div>
          <small>All 3 daily meals (Breakfast, Lunch, Dinner)</small>
        </div>
      </div>
    `;
  } catch (err) {
    panel.innerHTML = `<div style="color:red; font-size:0.85rem; padding:1rem;">Failed to load kitchen summary: ${escapeHtml(err.message)}</div>`;
  }
}

/**
 * Color-coded Room Grid (AVAILABLE green, BOOKED blue, CLOSED red)
 * Toggle button switches AVAILABLE rooms to CLOSED (maintenance) and back
 */
async function renderRoomsGrid() {
  const grid = document.getElementById('rooms-grid-container');
  if (!grid) return;
  grid.innerHTML = `<div style="grid-column: 1 / -1; text-align:center; padding: 2rem;">Loading rooms...</div>`;

  try {
    const rooms = await api.rooms.getAll();

    let filtered = rooms;
    if (state.roomsFilter !== 'ALL') {
      filtered = filtered.filter((r) => r.status === state.roomsFilter);
    }

    if (!filtered.length) {
      grid.innerHTML = `<div style="grid-column: 1 / -1; text-align:center; padding: 2.5rem; color:var(--color-text-muted);">No rooms found with status ${state.roomsFilter}.</div>`;
      return;
    }

    grid.innerHTML = filtered
      .map((r) => {
        // Color-coded border class: AVAILABLE green, BOOKED blue, CLOSED red
        let statusClass = 'status-closed';
        let actionBtnHtml = '';

        if (r.status === 'AVAILABLE') {
          statusClass = 'status-available';
          actionBtnHtml = `<button class="btn btn-sm btn-outline btn-toggle-room-status" data-id="${r.room_id}" data-status="${r.status}" title="Switch room to CLOSED (maintenance)">Set Closed</button>`;
        } else if (r.status === 'CLOSED') {
          statusClass = 'status-closed';
          actionBtnHtml = `<button class="btn btn-sm btn-primary btn-toggle-room-status" data-id="${r.room_id}" data-status="${r.status}" style="background:#10b981;" title="Switch room back to AVAILABLE">Reopen</button>`;
        } else if (r.status === 'BOOKED') {
          statusClass = 'status-booked';
          actionBtnHtml = `<button class="btn btn-sm btn-outline btn-toggle-room-status" data-id="${r.room_id}" data-status="${r.status}" title="Occupied by an active or pending stay">Occupied</button>`;
        }

        return `
          <div class="room-card ${statusClass}">
            <div>
              <div class="room-card-header">
                <span class="room-number-lg">Room ${escapeHtml(r.room_number)}</span>
                ${renderStatusBadge(r.status)}
              </div>
              <div class="room-type-text">${escapeHtml(r.room_type)}</div>
            </div>
            <div class="room-card-footer">
              <div class="room-price-val">$${Number(r.price_per_night).toFixed(2)} <span>/ night</span></div>
              ${actionBtnHtml}
            </div>
          </div>
        `;
      })
      .join('');

    // Status toggle listeners (AVAILABLE <-> CLOSED)
    grid.querySelectorAll('.btn-toggle-room-status').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const id = btn.getAttribute('data-id');
        const currentStatus = btn.getAttribute('data-status');

        if (currentStatus === 'BOOKED') {
          showToast(`Room is currently occupied by an active guest stay and cannot be placed on maintenance.`, 'warning');
          return;
        }

        const newStatus = currentStatus === 'AVAILABLE' ? 'CLOSED' : 'AVAILABLE';
        try {
          await api.rooms.updateStatus(id, newStatus);
          showToast(`Room status updated to ${newStatus}.`, 'success');
          renderRoomsView();
        } catch (err) {
          showToast(`Failed to update room: ${err.message}`, 'error');
        }
      });
    });

  } catch (err) {
    grid.innerHTML = `<div style="grid-column: 1 / -1; color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</div>`;
  }
}

/**
 * Add Room Form:
 * room_number, room_type, price_per_night >= 0
 */
async function promptAddRoomModal() {
  const bodyHtml = `
    <form id="form-add-room">
      <div class="form-group">
        <label class="form-label" for="add-room-number">Room Number / Identifier (Unique):</label>
        <input type="text" id="add-room-number" class="form-control" placeholder="e.g. 105, 303, PH-02" maxlength="10" required>
      </div>

      <div class="form-group">
        <label class="form-label" for="add-room-type">Room Type / Category:</label>
        <select id="add-room-type" class="form-control" required>
          <option value="Single Standard">Single Standard</option>
          <option value="Double Standard" selected>Double Standard</option>
          <option value="Double Superior Ocean View">Double Superior Ocean View</option>
          <option value="Family Suite">Family Suite</option>
          <option value="Executive Penthouse Suite">Executive Penthouse Suite</option>
          <option value="Custom">Other (Custom category)...</option>
        </select>
        <label class="form-label" for="add-room-type-custom" id="add-room-type-custom-label" style="display:none; margin-top:6px;">Custom Category Name:</label>
        <input type="text" id="add-room-type-custom" class="form-control" placeholder="Enter custom room type..." style="display:none; margin-top:6px;">
      </div>

      <div class="form-group">
        <label class="form-label" for="add-room-price">Price Per Night ($ USD, >= 0):</label>
        <input type="number" id="add-room-price" class="form-control" step="0.01" min="0" value="95.00" required>
      </div>
    </form>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-add-room">Create Room</button>
  `;

  openModal('Add New Hotel Room', bodyHtml, footerHtml);

  const typeSelect = document.getElementById('add-room-type');
  const typeCustom = document.getElementById('add-room-type-custom');
  const typeCustomLabel = document.getElementById('add-room-type-custom-label');
  typeSelect.addEventListener('change', () => {
    const isCustom = typeSelect.value === 'Custom';
    typeCustom.style.display = isCustom ? 'block' : 'none';
    if (typeCustomLabel) typeCustomLabel.style.display = isCustom ? 'block' : 'none';
  });

  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-add-room').onclick = async () => {
    const roomNumber = document.getElementById('add-room-number').value.trim();
    let roomType = typeSelect.value;
    if (roomType === 'Custom') {
      roomType = typeCustom.value.trim() || 'Custom Room';
    }
    const price = parseFloat(document.getElementById('add-room-price').value);

    // Enforce constraints
    if (!roomNumber) {
      showToast('Room number is required.', 'error');
      return;
    }
    if (isNaN(price) || price < 0) {
      showToast('Price per night must be greater than or equal to 0 (price_per_night >= 0).', 'error');
      return;
    }

    try {
      // Validate uniqueness
      const existingRooms = await api.rooms.getAll();
      if (existingRooms.some((r) => r.room_number.toLowerCase() === roomNumber.toLowerCase())) {
        showToast(`A room with number "${roomNumber}" already exists!`, 'error');
        return;
      }

      await api.rooms.create({
        room_number: roomNumber,
        room_type: roomType,
        price_per_night: price,
        status: 'AVAILABLE'
      });

      closeModal();
      showToast(`Room ${roomNumber} created successfully!`, 'success');
      renderRoomsView();
    } catch (err) {
      showToast(`Failed to add room: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 4: Stock & Items (#inventory)
// =============================================================================
async function renderInventoryView() {
  const tbody = document.getElementById('tbody-inventory');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading inventory...</td></tr>`;

  try {
    const items = await api.items.getAll();

    let filtered = items;
    if (state.inventoryFilter !== 'ALL') {
      filtered = filtered.filter((i) => i.measuring_method === state.inventoryFilter);
    }

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:2.5rem; color:var(--color-text-muted);">No items found matching filter.</td></tr>`;
      return;
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

        const isActive = Number(item.is_active) === 1;
        // is_active toggle switch instead of delete button so historical ITEM_ORDER bills remain intact
        const activeToggleHtml = isActive
          ? `<button class="toggle-switch-btn active btn-toggle-item-active" data-id="${item.item_id}" data-active="1" title="Click to deactivate (archive) item"><span class="toggle-dot active"></span> Active</button>`
          : `<button class="toggle-switch-btn inactive btn-toggle-item-active" data-id="${item.item_id}" data-active="0" title="Click to reactivate item"><span class="toggle-dot inactive"></span> Inactive</button>`;

        const rowStyle = !isActive ? 'opacity: 0.7; background-color: #fafafa;' : '';

        return `
          <tr style="${rowStyle}">
            <td>#${item.item_id}</td>
            <td>
              <strong>${escapeHtml(item.name)}</strong>
              ${!isActive ? `<span class="badge badge-closed" style="margin-left:6px; font-size:0.7rem;">Archived</span>` : ''}
            </td>
            <td>${methodBadge}</td>
            <td>$${Number(item.price_per_unit).toFixed(2)}</td>
            <td>${stockDisplay}</td>
            <td>${activeToggleHtml}</td>
            <td>
              <div style="display:flex; gap:6px;">
                <button class="btn btn-sm btn-primary btn-restock-item" data-id="${item.item_id}" data-name="${escapeHtml(item.name)}" data-stock="${item.stock_amount}" data-method="${item.measuring_method}" data-price="${item.price_per_unit}">
                  Restock
                </button>
              </div>
            </td>
          </tr>
        `;
      })
      .join('');

    // Toggle is_active switch listeners (instead of delete button!)
    tbody.querySelectorAll('.btn-toggle-item-active').forEach((btn) => {
      btn.addEventListener('click', async () => {
        const id = btn.getAttribute('data-id');
        const currentActive = btn.getAttribute('data-active') === '1';
        const newActive = currentActive ? 0 : 1;

        try {
          await api.items.update(id, { is_active: newActive });
          showToast(`Item updated to ${newActive ? 'Active' : 'Inactive (Archived)'}. Historical bills preserved.`, 'info');
          renderInventoryView();
        } catch (err) {
          showToast(`Failed to update item status: ${err.message}`, 'error');
        }
      });
    });

    // Restock listeners
    tbody.querySelectorAll('.btn-restock-item').forEach((btn) => {
      btn.addEventListener('click', () => {
        const id = btn.getAttribute('data-id');
        const name = btn.getAttribute('data-name');
        const stock = btn.getAttribute('data-stock');
        const method = btn.getAttribute('data-method');
        const price = btn.getAttribute('data-price');
        promptRestockItem(id, name, stock, method, price);
      });
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

/**
 * Add Item Form:
 * Enforces measuring_method ('COUNT' or 'CONTINUOUS') and price_per_unit >= 0, stock_amount >= 0
 */
async function promptAddItemModal() {
  const bodyHtml = `
    <form id="form-add-item">
      <div class="form-group">
        <label class="form-label" for="add-item-name">Item Name / Description:</label>
        <input type="text" id="add-item-name" class="form-control" placeholder="e.g. Bottled Artesian Water 750ml, Ground Espresso (kg)" maxlength="100" required>
      </div>

      <div class="form-group">
        <label class="form-label" for="add-item-method">Measuring Method:</label>
        <select id="add-item-method" class="form-control" required>
          <option value="COUNT" selected>COUNT (Discrete Units / Bottles / Pieces)</option>
          <option value="CONTINUOUS">CONTINUOUS (Decimal Weight kg / Volume Liters)</option>
        </select>
        <small style="color:var(--color-text-muted); font-size:0.75rem;">
          COUNT items use integer steps (1, 2, 3), while CONTINUOUS items support decimal portions (0.50 L, 1.25 kg).
        </small>
      </div>

      <div class="form-group">
        <label class="form-label" for="add-item-price">Price Per Unit ($ USD, >= 0):</label>
        <input type="number" id="add-item-price" class="form-control" step="0.01" min="0" value="5.00" required>
      </div>

      <div class="form-group">
        <label class="form-label" for="add-item-stock">Initial Stock Amount (>= 0):</label>
        <input type="number" id="add-item-stock" class="form-control" step="1" min="0" value="50" required>
      </div>
    </form>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-add-item">Add Supply Item</button>
  `;

  openModal('Add New Supply / Order Item', bodyHtml, footerHtml);

  const methodSelect = document.getElementById('add-item-method');
  const stockInput = document.getElementById('add-item-stock');
  methodSelect.addEventListener('change', () => {
    if (methodSelect.value === 'COUNT') {
      stockInput.step = '1';
      stockInput.value = Math.round(Number(stockInput.value) || 0);
    } else {
      stockInput.step = '0.01';
    }
  });

  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-add-item').onclick = async () => {
    const name = document.getElementById('add-item-name').value.trim();
    const method = methodSelect.value;
    const price = parseFloat(document.getElementById('add-item-price').value);
    const stock = parseFloat(stockInput.value);

    // Enforce constraints
    if (!name) {
      showToast('Item name is required.', 'error');
      return;
    }
    if (isNaN(price) || price < 0) {
      showToast('Price per unit must be greater than or equal to 0 (price_per_unit >= 0).', 'error');
      return;
    }
    if (isNaN(stock) || stock < 0) {
      showToast('Stock amount must be greater than or equal to 0 (stock_amount >= 0).', 'error');
      return;
    }
    if (!['COUNT', 'CONTINUOUS'].includes(method)) {
      showToast('Measuring method must be COUNT or CONTINUOUS.', 'error');
      return;
    }

    try {
      await api.items.create({
        name,
        measuring_method: method,
        price_per_unit: price,
        stock_amount: stock,
        is_active: 1
      });

      closeModal();
      showToast(`Item "${name}" created successfully!`, 'success');
      renderInventoryView();
    } catch (err) {
      showToast(`Failed to add item: ${err.message}`, 'error');
    }
  };
}

/**
 * Restock Item Form:
 * Enforces measuring_method ('COUNT' step 1 vs 'CONTINUOUS' step 0.01) and stock_amount >= 0
 */
function promptRestockItem(itemId, itemName, currentStock, method, currentPrice) {
  const isCount = method === 'COUNT';
  const step = isCount ? '1' : '0.01';

  const bodyHtml = `
    <p>Restocking inventory for <strong>${escapeHtml(itemName)}</strong>:</p>
    <div style="background:var(--color-surface-alt); padding:10px 14px; border-radius:6px; margin:0.75rem 0; font-size:0.85rem; border:1px solid var(--color-border);">
      <div>Current Stock: <strong>${Number(currentStock).toFixed(2)}</strong> (${method})</div>
      <div>Current Unit Price: <strong>$${Number(currentPrice).toFixed(2)}</strong></div>
    </div>

    <div class="form-group" style="margin-top:1rem;">
      <label class="form-label" for="restock-delta">Quantity to Add to Stock (>= 0):</label>
      <input type="number" id="restock-delta" class="form-control" step="${step}" min="${step}" value="${isCount ? '10' : '5.00'}" required>
      <small style="color:var(--color-text-muted); font-size:0.75rem;">
        Stepping enforces ${method}: step=${step}
      </small>
    </div>

    <div class="form-group">
      <label class="form-label" for="restock-price">Update Price Per Unit ($ USD, >= 0, optional):</label>
      <input type="number" id="restock-price" class="form-control" step="0.01" min="0" value="${Number(currentPrice).toFixed(2)}">
    </div>
  `;

  const footerHtml = `
    <button class="btn btn-outline btn-sm" id="btn-modal-cancel">Cancel</button>
    <button class="btn btn-primary btn-sm" id="btn-modal-confirm-restock">Apply Restock</button>
  `;

  openModal(`Restock Supply: ${itemName}`, bodyHtml, footerHtml);

  document.getElementById('btn-modal-cancel').onclick = closeModal;
  document.getElementById('btn-modal-confirm-restock').onclick = async () => {
    const delta = parseFloat(document.getElementById('restock-delta').value);
    const newPrice = parseFloat(document.getElementById('restock-price').value);

    if (isNaN(delta) || delta <= 0) {
      showToast('Please enter a positive restock quantity to add.', 'error');
      return;
    }
    if (isNaN(newPrice) || newPrice < 0) {
      showToast('Price per unit cannot be negative (>= 0).', 'error');
      return;
    }

    try {
      // Adjust stock
      await api.items.adjustStock(itemId, delta);

      // If price was modified, update unit price
      if (Math.abs(newPrice - Number(currentPrice)) > 0.001) {
        await api.items.update(itemId, { price_per_unit: newPrice });
      }

      closeModal();
      showToast(`Restocked ${delta} units of "${itemName}" successfully!`, 'success');
      renderInventoryView();
    } catch (err) {
      showToast(`Restock failed: ${err.message}`, 'error');
    }
  };
}

// =============================================================================
// View 5: Customer Directory & Telegram (#customers)
// =============================================================================
async function renderCustomersView() {
  await renderCustomersTable();
  await renderMessagesTable();
}

/**
 * Customers Table:
 * Search CUSTOMER by full_name and phone.
 * Display times_denied, last_denied_date, Telegram status badge (Linked vs Not Linked),
 * one-click copy button for t.me/YourBot?start=<booking_ref> using their latest booking reference.
 */
async function renderCustomersTable() {
  const tbody = document.getElementById('tbody-customers');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2rem;">Loading customers...</td></tr>`;

  try {
    const [customers, allRequests] = await Promise.all([
      api.customers.getAll(),
      api.bookingRequests.getAll()
    ]);

    // Map each customer to their latest booking reference
    const customerLatestRefMap = new Map();
    const sortedRequests = [...allRequests].sort((a, b) => b.request_id - a.request_id);
    sortedRequests.forEach((req) => {
      if (req.customer_id && !customerLatestRefMap.has(req.customer_id) && req.booking_ref) {
        customerLatestRefMap.set(req.customer_id, req.booking_ref);
      }
    });

    // Search by full_name and phone
    let filtered = customers;
    if (state.customersSearch) {
      const q = state.customersSearch.toLowerCase();
      filtered = filtered.filter((c) => {
        const nameMatch = c.full_name && c.full_name.toLowerCase().includes(q);
        const phoneMatch = c.phone && c.phone.toLowerCase().includes(q);
        return nameMatch || phoneMatch;
      });
    }

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding: 2.5rem; color:var(--color-text-muted);">No customers found matching "${escapeHtml(state.customersSearch)}".</td></tr>`;
      return;
    }

    tbody.innerHTML = filtered
      .map((c) => {
        // Telegram status badge (Linked when telegram_chat_id is non-null vs Not Linked)
        const telegramBadge = c.telegram_chat_id
          ? `<span class="badge badge-active" title="Telegram Chat ID: ${c.telegram_chat_id}">✓ Linked (${c.telegram_chat_id})</span>`
          : `<span class="badge badge-closed">Not Linked</span>`;

        // Denial history
        const deniedHtml = c.times_denied > 0
          ? `<span class="badge badge-failed">${c.times_denied} Denied</span> <small style="display:block; color:var(--color-text-muted); font-size:0.75rem; margin-top:2px;">Last: ${c.last_denied_date || 'N/A'}</small>`
          : `<span class="badge badge-available">0 (Clean)</span>`;

        // One-click copy button for t.me/YourBot?start=<booking_ref> using latest booking reference
        const latestRef = customerLatestRefMap.get(c.customer_id);
        let telegramLinkHtml = '';
        if (latestRef) {
          const fullLink = `t.me/YourBot?start=${latestRef}`;
          telegramLinkHtml = `
            <button class="btn-copy-telegram btn-copy-link" data-link="${fullLink}" title="Click to copy Telegram start link: ${fullLink}">
              <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="9" y="9" width="13" height="13" rx="2" ry="2"/>
                <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>
              </svg>
              <span>${fullLink}</span>
            </button>
          `;
        } else {
          telegramLinkHtml = `<span style="color:var(--color-text-muted); font-size:0.78rem;">No active ref</span>`;
        }

        return `
          <tr>
            <td>#${c.customer_id}</td>
            <td>
              <div style="font-weight:600;">${escapeHtml(c.full_name)}</div>
              <small style="color:var(--color-text-muted);">${escapeHtml(c.phone || 'No phone')}</small>
            </td>
            <td>${telegramBadge}</td>
            <td>${telegramLinkHtml}</td>
            <td>${deniedHtml}</td>
            <td><small style="color:var(--color-text-muted);">${(c.created_at || '').substring(0, 10)}</small></td>
            <td>
              <button class="btn btn-sm btn-outline btn-customer-dossier" data-id="${c.customer_id}">
                Dossier & Notes
              </button>
            </td>
          </tr>
        `;
      })
      .join('');

    // One-click copy link listeners
    tbody.querySelectorAll('.btn-copy-link').forEach((btn) => {
      btn.addEventListener('click', (e) => {
        e.stopPropagation();
        const link = btn.getAttribute('data-link');
        copyToClipboard(link);
      });
    });

    // Customer Dossier & Notes listeners
    tbody.querySelectorAll('.btn-customer-dossier').forEach((btn) => {
      btn.addEventListener('click', () => showCustomerDossierModal(btn.getAttribute('data-id')));
    });

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" style="color:red; padding:2rem;">Error: ${escapeHtml(err.message)}</td></tr>`;
  }
}

// Clipboard copy utility
function copyToClipboard(text) {
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(text).then(
      () => showToast(`Copied to clipboard: ${text}`, 'success'),
      () => fallbackCopyText(text)
    );
  } else {
    fallbackCopyText(text);
  }
}

function fallbackCopyText(text) {
  const ta = document.createElement('textarea');
  ta.value = text;
  document.body.appendChild(ta);
  ta.select();
  document.execCommand('copy');
  ta.remove();
  showToast(`Copied to clipboard: ${text}`, 'success');
}

/**
 * Customer Dossier Modal:
 * Displays Telegram status, one-click copy button, table of MESSAGE delivery logs (SENT / FAILED),
 * and a form to add CUSTOMER_REPORT notes.
 */
async function showCustomerDossierModal(customerId) {
  try {
    const history = await api.customers.getHistory(customerId);
    if (!history) return;

    const c = history.customer;

    // Find their latest booking ref
    const requests = history.requests || [];
    const latestReq = requests.length ? requests[requests.length - 1] : null;
    const latestRef = latestReq ? latestReq.booking_ref : null;
    const telegramLink = latestRef ? `t.me/YourBot?start=${latestRef}` : null;

    // Table of MESSAGE delivery logs (SENT / FAILED)
    const messages = history.messages || [];
    const messagesRowsHtml = messages.length
      ? messages
          .map((m) => {
            const statusBadge = m.status === 'SENT'
              ? `<span class="badge badge-active">SENT</span>`
              : `<span class="badge badge-failed">FAILED</span>`;
            return `
              <tr>
                <td>#${m.message_id}</td>
                <td>${statusBadge}</td>
                <td style="font-size:0.825rem;">${escapeHtml(m.content)}</td>
                <td style="font-size:0.75rem; color:var(--color-text-muted);">${(m.sent_at || '').replace('T', ' ')}</td>
              </tr>
            `;
          })
          .join('')
      : `<tr><td colspan="4" style="text-align:center; padding:1rem; color:var(--color-text-muted);">No messages logged for this guest yet.</td></tr>`;

    // Existing CUSTOMER_REPORT notes
    const reports = history.reports || [];
    const reportsRowsHtml = reports.length
      ? reports
          .map((r) => `
            <div style="background:var(--color-surface-alt); border:1px solid var(--color-border); border-left:3px solid var(--color-primary); padding:8px 12px; border-radius:6px; margin-bottom:6px; font-size:0.835rem;">
              <div style="display:flex; justify-content:space-between; margin-bottom:3px; color:var(--color-text-muted); font-size:0.75rem;">
                <strong>Logged on ${(r.created_at || '').replace('T', ' ')}</strong>
                <span>${r.booking_id ? `Booking #${r.booking_id}` : 'General Note'}</span>
              </div>
              <div>${escapeHtml(r.note)}</div>
            </div>
          `)
          .join('')
      : `<p style="color:var(--color-text-muted); font-size:0.825rem;">No incident notes on file.</p>`;

    // Bookings select options for the report form
    const bookings = history.bookings || [];
    const bookingOptionsHtml = bookings
      .map((b) => `<option value="${b.booking_id}">Booking #${b.booking_id} (${b.check_in} to ${b.check_out}) - [${b.status}]</option>`)
      .join('');

    const bodyHtml = `
      <div style="font-size:0.9rem;">
        <!-- Guest Profile Header -->
        <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom:1rem; padding-bottom:0.75rem; border-bottom:1px solid var(--color-border);">
          <div>
            <h4 style="margin:0 0 4px 0; font-size:1.1rem; color:var(--color-text-main);">${escapeHtml(c.full_name)}</h4>
            <p style="color:var(--color-text-muted); font-size:0.85rem; margin:0;">
              Phone: <strong>${escapeHtml(c.phone || 'None')}</strong> | 
              Telegram: <strong>${c.telegram_chat_id ? `Linked (Chat ID: ${c.telegram_chat_id})` : 'Not Linked'}</strong>
            </p>
          </div>
          <div>
            ${c.times_denied > 0 
              ? `<span class="badge badge-failed">${c.times_denied} Denied</span>` 
              : `<span class="badge badge-available">Clean Record</span>`}
          </div>
        </div>

        <!-- Telegram Onboarding Link Box -->
        <div style="background:#f0fdf4; border:1px solid #bbf7d0; padding:10px 14px; border-radius:6px; margin-bottom:1.25rem;">
          <div style="font-size:0.825rem; font-weight:700; color:#166534; margin-bottom:4px;">Telegram Concierge Link:</div>
          ${telegramLink ? `
            <div style="display:flex; align-items:center; gap:8px;">
              <code style="background:#ffffff; padding:4px 8px; border-radius:4px; border:1px solid #dcfce7; font-size:0.8rem; color:#15803d; flex:1;">${telegramLink}</code>
              <button class="btn-copy-telegram" id="btn-modal-copy-tg" data-link="${telegramLink}">📋 Copy Link</button>
            </div>
            <small style="color:#166534; font-size:0.75rem; margin-top:4px; display:block;">
              Send this start link to the guest to link their Telegram account with Booking Ref <strong>${latestRef}</strong>.
            </small>
          ` : `
            <p style="margin:0; font-size:0.8rem; color:var(--color-text-muted);">No reservation reference recorded for this guest yet.</p>
          `}
        </div>

        <!-- Form to Add CUSTOMER_REPORT Notes -->
        <div style="background:var(--color-surface); border:1px solid var(--color-border); padding:1rem; border-radius:8px; margin-bottom:1.25rem;">
          <h5 style="margin:0 0 8px 0; color:var(--color-primary); font-size:0.95rem;">Add Incident Note / Customer Report:</h5>
          <form id="form-add-customer-report">
            ${bookings.length ? `
              <div class="form-group" style="margin-bottom:8px;">
                <label class="form-label" for="report-booking-id">Link to Specific Booking (Optional):</label>
                <select id="report-booking-id" class="form-control" style="font-size:0.85rem; padding:0.45rem 0.65rem;">
                  <option value="">General Incident (No specific booking)</option>
                  ${bookingOptionsHtml}
                </select>
              </div>
            ` : ''}
            <div class="form-group" style="margin-bottom:8px;">
              <label class="form-label" for="report-note">Incident Report / Staff Note:</label>
              <textarea id="report-note" class="form-control" rows="2" placeholder="Record guest incident, damage note, VIP preference, or special observation..." required style="font-size:0.85rem;"></textarea>
            </div>
            <button type="submit" class="btn btn-sm btn-primary" id="btn-submit-report">Save Report Note</button>
          </form>
        </div>

        <!-- Incident Reports History -->
        <h5 style="margin:1rem 0 0.5rem 0; color:var(--color-text-main); font-size:0.95rem;">Historical Incident Reports & Notes:</h5>
        <div style="max-height:160px; overflow-y:auto; margin-bottom:1.25rem;">
          ${reportsRowsHtml}
        </div>

        <!-- Table of MESSAGE Delivery Logs -->
        <h5 style="margin:1rem 0 0.5rem 0; color:var(--color-text-main); font-size:0.95rem;">Telegram Message Dispatch Logs (SENT / FAILED):</h5>
        <div style="max-height:160px; overflow-y:auto; border:1px solid var(--color-border); border-radius:6px;">
          <table class="mini-table" style="margin-bottom:0;">
            <thead>
              <tr>
                <th>Msg ID</th>
                <th>Status</th>
                <th>Content</th>
                <th>Sent At</th>
              </tr>
            </thead>
            <tbody>
              ${messagesRowsHtml}
            </tbody>
          </table>
        </div>
      </div>
    `;

    const footerHtml = `<button class="btn btn-outline btn-sm" id="btn-modal-close">Close</button>`;

    openModal(`Guest Dossier - ${c.full_name}`, bodyHtml, footerHtml);
    document.getElementById('btn-modal-close').onclick = closeModal;

    // Modal Copy button
    const modalCopyBtn = document.getElementById('btn-modal-copy-tg');
    if (modalCopyBtn) {
      modalCopyBtn.addEventListener('click', () => {
        copyToClipboard(modalCopyBtn.getAttribute('data-link'));
      });
    }

    // Submit report form
    const reportForm = document.getElementById('form-add-customer-report');
    if (reportForm) {
      reportForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const note = document.getElementById('report-note').value.trim();
        const bookingSelect = document.getElementById('report-booking-id');
        const bookingId = bookingSelect && bookingSelect.value ? Number(bookingSelect.value) : null;

        if (!note) {
          showToast('Report note cannot be empty.', 'error');
          return;
        }

        try {
          await api.customerReports.create({
            customerId: c.customer_id,
            bookingId,
            note
          });
          showToast('Customer report note logged successfully!', 'success');
          // Re-render modal and main view
          showCustomerDossierModal(customerId);
          renderCustomersTable();
        } catch (err) {
          showToast(`Failed to add note: ${err.message}`, 'error');
        }
      });
    }

  } catch (err) {
    showToast(`Error: ${err.message}`, 'error');
  }
}

/**
 * Global Table of MESSAGE Delivery Logs (SENT / FAILED)
 */
async function renderMessagesTable() {
  const tbody = document.getElementById('tbody-messages');
  if (!tbody) return;
  tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:1.5rem;">Loading delivery logs...</td></tr>`;

  try {
    const allMessages = await api.messages.getAll();

    let filtered = allMessages;
    if (state.messagesFilter !== 'ALL') {
      filtered = filtered.filter((m) => m.status === state.messagesFilter);
    }

    // Sort descending by message_id
    filtered.sort((a, b) => b.message_id - a.message_id);

    if (!filtered.length) {
      tbody.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:2rem; color:var(--color-text-muted);">No messages found matching status ${state.messagesFilter}.</td></tr>`;
      return;
    }

    tbody.innerHTML = filtered
      .map((m) => {
        const statusBadge = m.status === 'SENT'
          ? `<span class="badge badge-active">SENT</span>`
          : `<span class="badge badge-failed">FAILED</span>`;

        return `
          <tr>
            <td><strong>#${m.message_id}</strong></td>
            <td>
              <strong>${escapeHtml(m.customer_name || 'Customer')}</strong>
              <small style="color:var(--color-text-muted); display:block;">ID: #${m.customer_id}</small>
            </td>
            <td>${statusBadge}</td>
            <td style="max-width:360px;">${escapeHtml(m.content)}</td>
            <td><small style="color:var(--color-text-muted);">${(m.sent_at || '').replace('T', ' ')}</small></td>
          </tr>
        `;
      })
      .join('');

  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" style="color:red; padding:1.5rem;">Error loading delivery logs: ${escapeHtml(err.message)}</td></tr>`;
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

  // Slide-Over Guest Folio Drawer close handlers
  if (dom.folioCloseBtn) {
    dom.folioCloseBtn.addEventListener('click', closeFolioDrawer);
  }
  if (dom.folioDrawerOverlay) {
    dom.folioDrawerOverlay.addEventListener('click', (e) => {
      if (e.target === dom.folioDrawerOverlay) closeFolioDrawer();
    });
  }

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

  // Filter Buttons for Stays View
  const staysFilterContainer = document.getElementById('stays-status-filters');
  if (staysFilterContainer) {
    staysFilterContainer.addEventListener('click', (e) => {
      if (e.target.classList.contains('filter-pill')) {
        staysFilterContainer.querySelectorAll('.filter-pill').forEach((p) => p.classList.remove('active'));
        e.target.classList.add('active');
        state.staysFilter = e.target.getAttribute('data-status');
        renderStaysView();
      }
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

  // Add Room & Add Item Modals
  document.getElementById('btn-add-room-modal')?.addEventListener('click', promptAddRoomModal);
  document.getElementById('btn-add-item-modal')?.addEventListener('click', promptAddItemModal);

  // Search input for Customers
  const custSearchInput = document.getElementById('customers-search-input');
  if (custSearchInput) {
    custSearchInput.addEventListener('input', (e) => {
      state.customersSearch = e.target.value.trim();
      renderCustomersTable();
    });
  }

  // Filter Buttons for Telegram Dispatch Messages
  const msgFilterContainer = document.getElementById('messages-status-filters');
  if (msgFilterContainer) {
    msgFilterContainer.addEventListener('click', (e) => {
      if (e.target.classList.contains('filter-pill')) {
        msgFilterContainer.querySelectorAll('.filter-pill').forEach((p) => p.classList.remove('active'));
        e.target.classList.add('active');
        state.messagesFilter = e.target.getAttribute('data-status');
        renderMessagesTable();
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
