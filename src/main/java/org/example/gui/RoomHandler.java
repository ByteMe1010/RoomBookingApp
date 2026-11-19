package org.example.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.NEvent;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.BookingRequest;
import org.example.Core.DatabaseModule.model.MealPlan;
import org.example.Core.DatabaseModule.model.Room;
import org.unbescape.html.HtmlEscape;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomHandler {
    private static String currentStatus = "ALL";

    public static void handle(NWindow window) {
        bindStatusFilter(window, "rooms-filter-all", "ALL");
        bindStatusFilter(window, "rooms-filter-available", "AVAILABLE");
        bindStatusFilter(window, "rooms-filter-booked", "BOOKED");
        bindStatusFilter(window, "rooms-filter-closed", "CLOSED");

        Button.of(window, "btn-refresh-rooms").onClick(event -> updateView(window));
        Button.of(window, "btn-add-room-modal").onClick(event -> openAddRoomModal(window));
        Button.of(window, "modal-close-btn").onClick(event -> closeAddRoomModal(window));

        Div grid = Div.of(window, "rooms-grid-container");
        grid.events.addEvent(
                NEvent.CLICK,
                "ex = e.target.closest('.btn-toggle-room-status'); ex = ex ? ex.getAttribute('data-id') + '|' + ex.getAttribute('data-status') : null;",
                event -> {
                    if (event == null) return;
                    String[] parts = event.toString().split("\\|", -1);
                    if (parts.length != 2) return;

                    int roomId;
                    try {
                        roomId = Integer.parseInt(parts[0]);
                    } catch (NumberFormatException exception) {
                        HotelApp.showToast(window, "Invalid room action.", "error");
                        return;
                    }

                    toggleRoomStatus(window, roomId, parts[1]);
                }
        );

        updateView(window);
    }

    private static void bindStatusFilter(NWindow window, String buttonId, String status) {
        Button.of(window, buttonId).onClick(event -> {
            currentStatus = status;
            setActiveStatusButton(window, buttonId);
            updateView(window);
        });
    }

    private static void setActiveStatusButton(NWindow window, String activeId) {
        String[] ids = {
                "rooms-filter-all", "rooms-filter-available",
                "rooms-filter-booked", "rooms-filter-closed"
        };

        for (String id : ids) {
            Button button = Button.of(window, id);
            if (id.equals(activeId)) button.classList.add("active");
            else button.classList.remove("active");
        }
    }

    public static void updateView(NWindow window) {
        updateKitchenSummary(window);
        updateRoomGrid(window);
    }

    private static void updateKitchenSummary(NWindow window) {
        Div panel = Div.of(window, "kitchen-meal-summary");

        try {
            Map<String, Integer> counts = new LinkedHashMap<>();
            counts.put("RO", 0);
            counts.put("BB", 0);
            counts.put("HB", 0);
            counts.put("FB", 0);

            int activeStayCount = 0;
            for (Booking booking : HotelApp.databaseModule.getAllBookings()) {
                if (!"ACTIVE".equalsIgnoreCase(booking.getStatus())) continue;

                activeStayCount++;
                String code = "RO";
                BookingRequest request = HotelApp.databaseModule.getBookingRequestById(booking.getRequestId());
                if (request != null && request.getMealPlanId() != null) {
                    MealPlan mealPlan = HotelApp.databaseModule.getMealPlanById(request.getMealPlanId());
                    if (mealPlan != null && mealPlan.getCode() != null) {
                        code = mealPlan.getCode().toUpperCase(Locale.ROOT);
                    }
                }
                if (!counts.containsKey(code)) code = "RO";
                counts.put(code, counts.get(code) + 1);
            }

            panel.setInnerHTML("<div class=\"kitchen-summary-header\">"
                    + "<div style=\"display:flex; align-items:center; gap:8px;\">"
                    + "<h3 style=\"font-size:1.05rem; font-weight:700; margin:0;\">Kitchen Meal Summary (Active Checked-In Stays)</h3></div>"
                    + "<span class=\"badge badge-active\">" + activeStayCount + " Active Stays</span></div>"
                    + "<div class=\"kitchen-grid\">"
                    + kitchenBox("box-ro", "badge-closed", "RO", "Room Only", counts.get("RO"), "No catering / room-only reservation")
                    + kitchenBox("box-bb", "badge-active", "BB", "Breakfast Included", counts.get("BB"), "Morning dining buffet covers")
                    + kitchenBox("box-hb", "badge-booked", "HB", "Half Board", counts.get("HB"), "Breakfast buffet + Dinner service")
                    + kitchenBox("box-fb", "badge-finished", "FB", "Full Board", counts.get("FB"), "All 3 daily meals")
                    + "</div>");
        } catch (SQLException exception) {
            panel.setInnerHTML("<div style=\"color:red; padding:1rem;\">Failed to load kitchen summary: "
                    + escape(exception.getMessage()) + "</div>");
        }
    }

    private static String kitchenBox(String boxClass, String badgeClass, String code,
                                     String name, int count, String description) {
        return "<div class=\"kitchen-stat-box " + boxClass + "\">"
                + "<div class=\"meal-top\"><span class=\"badge " + badgeClass + "\">" + code + "</span></div>"
                + "<div class=\"meal-name\">" + escape(name) + "</div>"
                + "<div class=\"meal-count\">" + count + "</div>"
                + "<small>" + escape(description) + "</small></div>";
    }

    public static void updateRoomGrid(NWindow window) {
        Div grid = Div.of(window, "rooms-grid-container");

        try {
            List<Room> rooms = HotelApp.databaseModule.getAllRooms();
            StringBuilder cards = new StringBuilder();
            int visibleCount = 0;

            for (Room room : rooms) {
                String status = room.getStatus() == null
                        ? "AVAILABLE"
                        : room.getStatus().toUpperCase(Locale.ROOT);

                if (!"ALL".equals(currentStatus) && !currentStatus.equals(status)) continue;
                visibleCount++;

                String statusClass;
                String badgeClass;
                String action;

                switch (status) {
                    case "CLOSED":
                        statusClass = "status-closed";
                        badgeClass = "badge-closed";
                        action = "<button class=\"btn btn-sm btn-primary btn-toggle-room-status\" data-id=\""
                                + room.getRoomId() + "\" data-status=\"CLOSED\" style=\"background:#10b981;\">Reopen</button>";
                        break;
                    case "BOOKED":
                        statusClass = "status-booked";
                        badgeClass = "badge-booked";
                        action = "<button class=\"btn btn-sm btn-outline btn-toggle-room-status\" data-id=\""
                                + room.getRoomId() + "\" data-status=\"BOOKED\">Occupied</button>";
                        break;
                    default:
                        status = "AVAILABLE";
                        statusClass = "status-available";
                        badgeClass = "badge-available";
                        action = "<button class=\"btn btn-sm btn-outline btn-toggle-room-status\" data-id=\""
                                + room.getRoomId() + "\" data-status=\"AVAILABLE\">Set Closed</button>";
                        break;
                }

                cards.append(new org.example.gui.components.RoomCard(
                        "room-card-" + room.getRoomId(), room, status).buildHtml(""));
            }

            if (visibleCount == 0) {
                grid.setInnerHTML("<div style=\"grid-column:1/-1;text-align:center;padding:2.5rem;color:var(--color-text-muted);\">"
                        + "No rooms found with status " + escape(currentStatus) + ".</div>");
            } else {
                grid.setInnerHTML(cards.toString());
            }
        } catch (SQLException exception) {
            grid.setInnerHTML("<div style=\"grid-column:1/-1;color:red;padding:2rem;\">Unable to load rooms: "
                    + escape(exception.getMessage()) + "</div>");
        }
    }

    private static void toggleRoomStatus(NWindow window, int roomId, String currentStatusValue) {
        if ("BOOKED".equalsIgnoreCase(currentStatusValue)) {
            HotelApp.showToast(window,
                    "Room is currently occupied by an active or pending stay and cannot be placed on maintenance.",
                    "warn");
            return;
        }

        String newStatus = "AVAILABLE".equalsIgnoreCase(currentStatusValue) ? "CLOSED" : "AVAILABLE";
        try {
            HotelApp.databaseModule.updateRoomStatus(roomId, newStatus);
            HotelApp.showToast(window, "Room status updated to " + newStatus + ".", "success");
            updateView(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to update room: " + exception.getMessage(), "error");
        }
    }

    private static void openAddRoomModal(NWindow window) {
        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        title.setInnerText("Add New Hotel Room");
        body.setInnerHTML("<div class=\"form-group\"><label class=\"form-label\" for=\"add-room-number\">Room Number / Identifier (Unique):</label>"
                + "<input type=\"text\" id=\"add-room-number\" class=\"form-control\" maxlength=\"10\" placeholder=\"e.g. 105, 303, PH-02\" required></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"add-room-type\">Room Type / Category:</label>"
                + "<select id=\"add-room-type\" class=\"form-control\"><option value=\"Single Standard\">Single Standard</option>"
                + "<option value=\"Double Standard\" selected>Double Standard</option><option value=\"Double Superior Ocean View\">Double Superior Ocean View</option>"
                + "<option value=\"Family Suite\">Family Suite</option><option value=\"Executive Penthouse Suite\">Executive Penthouse Suite</option>"
                + "<option value=\"Custom\">Other (Custom category)...</option></select>"
                + "<label class=\"form-label\" for=\"add-room-type-custom\" id=\"add-room-type-custom-label\" style=\"display:none; margin-top:6px;\">Custom Category Name:</label>"
                + "<input type=\"text\" id=\"add-room-type-custom\" class=\"form-control\" placeholder=\"Enter custom room type...\" style=\"display:none; margin-top:6px;\"></div>"
                + "<div class=\"form-group\"><label class=\"form-label\" for=\"add-room-price\">Price Per Night ($ USD, >= 0):</label>"
                + "<input type=\"number\" id=\"add-room-price\" class=\"form-control\" step=\"0.01\" min=\"0\" value=\"95.00\" required></div>");

        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"btn-modal-confirm-add-room\" class=\"btn btn-primary btn-sm\">Create Room</button>");

        modal.classList.remove("hidden");

        Button.of(window, "btn-modal-cancel").onClick(event -> closeAddRoomModal(window));
        Button.of(window, "btn-modal-confirm-add-room").events.addEvent(
                NEvent.CLICK,
                "ex = document.getElementById('add-room-number').value + '|' + "
                        + "(document.getElementById('add-room-type').value === 'Custom' ? document.getElementById('add-room-type-custom').value : document.getElementById('add-room-type').value) + '|' + "
                        + "document.getElementById('add-room-price').value;",
                event -> createRoomFromModal(window, event)
        );

        // Show custom room type field when Custom is selected.
        Div.of(window, "add-room-type").events.addJsEvent(NEvent.CHANGE,
                "document.getElementById('add-room-type-custom').style.display = this.value === 'Custom' ? 'block' : 'none';"
                        + "document.getElementById('add-room-type-custom-label').style.display = this.value === 'Custom' ? 'block' : 'none';");
    }

    private static void createRoomFromModal(NWindow window, Object event) {
        if (event == null) return;
        String[] values = event.toString().split("\\|", -1);
        if (values.length != 3) return;

        String roomNumber = values[0].trim();
        String roomType = values[1].trim();
        double price;

        try {
            price = Double.parseDouble(values[2].trim());
        } catch (NumberFormatException exception) {
            HotelApp.showToast(window, "Price per night must be a valid number.", "error");
            return;
        }

        if (roomNumber.isEmpty()) {
            HotelApp.showToast(window, "Room number is required.", "error");
            return;
        }
        if (roomType.isEmpty()) roomType = "Custom Room";
        if (price < 0) {
            HotelApp.showToast(window, "Price per night must be greater than or equal to 0.", "error");
            return;
        }

        try {
            for (Room room : HotelApp.databaseModule.getAllRooms()) {
                if (room.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                    HotelApp.showToast(window, "A room with number \"" + roomNumber + "\" already exists!", "error");
                    return;
                }
            }

            Room room = new Room(roomNumber, roomType, price);
            int roomId = HotelApp.databaseModule.createRoom(room);
            if (roomId <= 0) {
                HotelApp.showToast(window, "Failed to create room.", "error");
                return;
            }

            closeAddRoomModal(window);
            HotelApp.showToast(window, "Room " + roomNumber + " created successfully!", "success");
            updateView(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to add room: " + exception.getMessage(), "error");
        }
    }

    private static void closeAddRoomModal(NWindow window) {
        Div.of(window, "modal-container").classList.add("hidden");
        Div.of(window, "modal-body").setInnerHTML("");
        Div.of(window, "modal-footer").setInnerHTML("");
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlEscape.escapeHtml5(value);
    }
}
