package org.example.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.NEvent;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.TextArea;
import com.codehack.nngf.gui.TextField;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.BookingRequest;
import org.example.Core.DatabaseModule.model.BookingRoom;
import org.example.Core.DatabaseModule.model.Customer;
import org.example.Core.DatabaseModule.model.MealPlan;
import org.example.Core.DatabaseModule.model.Room;
import org.example.Core.GoogleModule.GmailIntegrationService;
import org.example.gui.components.BookingRequestRow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingRequestHandler {
    private static String currentStatus = "ALL";
    private static String currentSearch = "";

    public static void handle(NWindow window) {
        bindStatusFilter(window, "requests-filter-all", "ALL");
        bindStatusFilter(window, "requests-filter-pending", "PENDING");
        bindStatusFilter(window, "requests-filter-accepted", "ACCEPTED");
        bindStatusFilter(window, "requests-filter-denied", "DENIED");
        bindStatusFilter(window, "requests-filter-canceled", "CANCELED");

        TextField searchField = TextField.of(window, "requests-search-input");
        searchField.events.addEvent(NEvent.INPUT, event -> {
            currentSearch = event.GetString("searchVal");
            if (currentSearch == null) {
                currentSearch = "";
            }
            updateTable(window);
        },
                searchField.q("searchVal").value());

        Button.of(window, "btn-refresh-requests").onClick(event -> {
            updateCards(window);
            updateTable(window);
        });

        Button.of(window, "btn-gmail-check").onClick(event -> {
            GmailIntegrationService svc = HotelApp.gmailService;
            if (svc == null || !svc.isConfigured()) {
                HotelApp.showToast(window, svc == null ? "Gmail is not configured." : svc.missingConfigMessage(), "warn");
                return;
            }
            HotelApp.showToast(window, "Checking Gmail...", "info");
            svc.checkGmail(
                    () -> {
                        updateCards(window);
                        updateTable(window);
                        HotelApp.showToast(window, "Gmail check finished. Requests refreshed.", "success");
                    },
                    t -> HotelApp.showToast(window, "Gmail check failed: " + t.getClass().getSimpleName(), "error")
            );
        });

        Button.of(window, "btn-gmail-simulate").onClick(event -> openGmailSimulateModal(window));

        Div tbody = Div.of(window, "tbody-booking-requests");
        tbody.events.addEvent(
                NEvent.CLICK,
                "ex = e.target.closest('.btn-accept-req, .btn-deny-req'); ex = ex ? ex.getAttribute('data-id') : null;",
                event -> {
                    if (event == null) {
                        return;
                    }

                    String dataId = event.toString();
                    if (dataId.length() < 2) {
                        return;
                    }

                    char action = dataId.charAt(0);
                    int requestId;
                    try {
                        requestId = Integer.parseInt(dataId.substring(1));
                    } catch (NumberFormatException exception) {
                        HotelApp.showToast(window, "Invalid booking request action.", "error");
                        return;
                    }

                    if (action == 'A') {
                        acceptRequest(window, requestId);
                    } else if (action == 'D') {
                        denyRequest(window, requestId);
                    }
                }
        );

        updateCards(window);
        updateTable(window);
    }

    private static void bindStatusFilter(NWindow window, String buttonId, String status) {
        Button.of(window, buttonId).onClick(event -> {
            currentStatus = status;
            setActiveStatusButton(window, buttonId);
            updateTable(window);
        });
    }

    private static void setActiveStatusButton(NWindow window, String activeId) {
        String[] buttonIds = {
                "requests-filter-all",
                "requests-filter-pending",
                "requests-filter-accepted",
                "requests-filter-denied",
                "requests-filter-canceled"
        };

        for (String buttonId : buttonIds) {
            Button button = Button.of(window, buttonId);
            if (buttonId.equals(activeId)) {
                button.classList.add("active");
            } else {
                button.classList.remove("active");
            }
        }
    }

    public static void updateCards(NWindow window) {
        Div pendingRequests = Div.of(window, "stat-requests-pending");
        Div acceptedRequests = Div.of(window, "stat-requests-accepted");
        Div totalRequests = Div.of(window, "stat-requests-total");

        try {
            List<BookingRequest> allRequests = HotelApp.databaseModule.getAllBookingRequests();
            int pending = 0;
            int accepted = 0;

            for (BookingRequest request : allRequests) {
                String status = request.getStatus() == null
                        ? ""
                        : request.getStatus().toUpperCase(Locale.ROOT);
                if ("PENDING".equals(status)) {
                    pending++;
                } else if ("ACCEPTED".equals(status)) {
                    accepted++;
                }
            }

            pendingRequests.setInnerText(String.valueOf(pending));
            acceptedRequests.setInnerText(String.valueOf(accepted));
            totalRequests.setInnerText(String.valueOf(allRequests.size()));
        } catch (SQLException exception) {
            System.err.println("Error while fetching booking request cards: " + exception.getMessage());
        }
    }

    public static void updateTable(NWindow window) {
        Div tbody = Div.of(window, "tbody-booking-requests");

        try {
            List<BookingRequest> allRequests = HotelApp.databaseModule.getAllBookingRequests();
            List<BookingRequest> filteredRequests = new ArrayList<>();
            String search = currentSearch == null ? "" : currentSearch.trim().toLowerCase(Locale.ROOT);

            for (BookingRequest request : allRequests) {
                String requestStatus = request.getStatus() == null
                        ? ""
                        : request.getStatus().toUpperCase(Locale.ROOT);

                boolean statusMatches = "ALL".equals(currentStatus)
                        || requestStatus.equals(currentStatus);

                boolean searchMatches = search.isEmpty()
                        || (request.getGuestName() != null
                        && request.getGuestName().toLowerCase(Locale.ROOT).contains(search))
                        || (request.getBookingRef() != null
                        && request.getBookingRef().toLowerCase(Locale.ROOT).contains(search))
                        || (request.getGuestPhone() != null
                        && request.getGuestPhone().toLowerCase(Locale.ROOT).contains(search));

                if (statusMatches && searchMatches) {
                    filteredRequests.add(request);
                }
            }

            if (filteredRequests.isEmpty()) {
                tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center; padding:2rem;\">No booking requests match the selected filters.</td></tr>");
                return;
            }

            StringBuilder rows = new StringBuilder();
            for (BookingRequest request : filteredRequests) {
                MealPlan mealPlan = null;
                if (request.getMealPlanId() != null) {
                    mealPlan = HotelApp.databaseModule.getMealPlanById(request.getMealPlanId());
                }

                BookingRequestRow row = new BookingRequestRow(
                        "booking-request-" + request.getRequestId(),
                        window,
                        request,
                        mealPlan
                );
                rows.append(row.buildHtml(""));
            }

            tbody.setInnerHTML(rows.toString());
        } catch (SQLException exception) {
            tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center; padding:2rem; color:var(--status-failed-text);\">Unable to load booking requests.</td></tr>");
            System.err.println("Error while fetching booking requests: " + exception.getMessage());
        }
    }

    private static void acceptRequest(NWindow window, int requestId) {
        if (HotelApp.shopOwner == null || HotelApp.shopOwner.getOwnerId() <= 0) {
            HotelApp.showToast(window, "A valid signed-in shop owner is required to accept requests.", "error");
            return;
        }

        try {
            BookingRequest request = HotelApp.databaseModule.getBookingRequestById(requestId);
            if (request == null) {
                HotelApp.showToast(window, "Booking request was not found.", "error");
                return;
            }

            if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
                HotelApp.showToast(window, "This booking request has already been decided.", "warn");
                updateCards(window);
                updateTable(window);
                return;
            }

            int roomsWanted = Math.max(1, request.getRoomsWanted());
            List<Room> availableRooms = HotelApp.databaseModule.getAvailableRooms();
            if (availableRooms.size() < roomsWanted) {
                HotelApp.showToast(
                        window,
                        "Insufficient available rooms: " + availableRooms.size()
                                + " available, but " + roomsWanted + " required.",
                        "error"
                );
                return;
            }

            Integer customerId = request.getCustomerId();
            if (customerId == null) {
                Customer existingCustomer = null;
                if (request.getGuestPhone() != null && !request.getGuestPhone().trim().isEmpty()) {
                    existingCustomer = HotelApp.databaseModule.getCustomerByPhone(request.getGuestPhone().trim());
                }

                if (existingCustomer != null) {
                    customerId = existingCustomer.getCustomerId();
                } else {
                    Customer newCustomer = new Customer(request.getGuestName(), request.getGuestPhone());
                    customerId = HotelApp.databaseModule.saveCustomer(newCustomer);
                    if (customerId <= 0) {
                        HotelApp.showToast(window, "Could not create or find the guest customer record.", "error");
                        return;
                    }
                }

                request.setCustomerId(customerId);
                HotelApp.databaseModule.updateBookingRequest(request);
            }

            Booking booking = new Booking(
                    request.getRequestId(),
                    customerId,
                    request.getCheckIn(),
                    request.getCheckOut()
            );
            int bookingId = HotelApp.databaseModule.createBooking(booking);
            if (bookingId <= 0) {
                HotelApp.showToast(window, "Could not create the booking.", "error");
                return;
            }

            for (int i = 0; i < roomsWanted; i++) {
                Room room = availableRooms.get(i);
                HotelApp.databaseModule.linkRoomToBooking(
                        new BookingRoom(bookingId, room.getRoomId(), room.getPricePerNight())
                );
                HotelApp.databaseModule.updateRoomStatus(room.getRoomId(), "BOOKED");
            }

            HotelApp.databaseModule.updateBookingRequestStatus(
                    requestId,
                    "ACCEPTED",
                    HotelApp.shopOwner.getOwnerId()
            );

            HotelApp.showToast(window,
                    "Request " + value(request.getBookingRef(), "#" + requestId)
                            + " accepted and " + roomsWanted + " room(s) assigned.",
                    "success");
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to accept request: " + exception.getMessage(), "error");
        } finally {
            updateCards(window);
            updateTable(window);
        }
    }

    private static void denyRequest(NWindow window, int requestId) {
        if (HotelApp.shopOwner == null || HotelApp.shopOwner.getOwnerId() <= 0) {
            HotelApp.showToast(window, "A valid signed-in shop owner is required to deny requests.", "error");
            return;
        }

        try {
            BookingRequest request = HotelApp.databaseModule.getBookingRequestById(requestId);
            if (request == null) {
                HotelApp.showToast(window, "Booking request was not found.", "error");
                return;
            }

            if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
                HotelApp.showToast(window, "This booking request has already been decided.", "warn");
                updateCards(window);
                updateTable(window);
                return;
            }

            HotelApp.databaseModule.updateBookingRequestStatus(
                    requestId,
                    "DENIED",
                    HotelApp.shopOwner.getOwnerId()
            );

            if (request.getCustomerId() != null) {
                HotelApp.databaseModule.incrementCustomerDeniedCount(request.getCustomerId());
            }

            HotelApp.showToast(window,
                    "Request " + value(request.getBookingRef(), "#" + requestId) + " denied.",
                    "info");
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to deny request: " + exception.getMessage(), "error");
        } finally {
            updateCards(window);
            updateTable(window);
        }
    }

    private static String value(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static void openGmailSimulateModal(NWindow window) {
        GmailIntegrationService svc0 = HotelApp.gmailService;
        if (svc0 == null || !svc0.canSimulate()) {
            if (HotelApp.databaseModule == null) {
                HotelApp.showToast(window, "Database is not available.", "warn");
                return;
            }
            svc0 = GmailIntegrationService.forSimulate(HotelApp.databaseModule);
        }
        final GmailIntegrationService svc = svc0;

        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        String saveButtonId = "btn-save-test-" + System.nanoTime();
        title.setInnerText("Simulate Gmail Booking");
        body.setInnerHTML("<div class=\"form-group\">"
                + "<label class=\"form-label\" for=\"gmail-sim-blob\">Paste an example Gmail message (subject + body):</label>"
                + "<textarea id=\"gmail-sim-blob\" class=\"form-control\" rows=\"12\">Subject: New reservation\nReservation ID: VIVA-SYN-002\nGuest name: Sample Guest\nCheck-in: 2027-01-01\nCheck-out: 2027-01-03\nRoom: Double\nRooms: 2\nPhone: 12345</textarea>"
                + "<p>Saving is available only for HOTEL_DB_TEST and reference VIVA-SYN-002.</p>"
                + "</div>");
        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"" + saveButtonId + "\" class=\"btn btn-outline btn-sm\" disabled>Save to Test Database</button>"
                + "<button id=\"btn-modal-confirm-simulate\" class=\"btn btn-primary btn-sm\">Parse Only</button>");

        modal.classList.remove("hidden");

        Button.of(window, "btn-modal-cancel").onClick(e -> closeGmailSimulateModal(window));
        Button.of(window, saveButtonId).onClick(e -> {
            String blob = TextArea.of(window, "gmail-sim-blob").value();
            title.setInnerText("Confirm Test Database Insert");
            body.setInnerText("Insert the validated VIVA-SYN-002 synthetic reservation into HOTEL_DB_TEST? Existing requests will not be changed.");
            footer.setInnerHTML("<button id=\"btn-save-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                    + "<button id=\"btn-save-confirm\" class=\"btn btn-primary btn-sm\">Confirm Insert</button>");
            Button.of(window, "btn-save-cancel").onClick(cancel -> closeGmailSimulateModal(window));
            Button.of(window, "btn-save-confirm").onClick(confirm -> {
                closeGmailSimulateModal(window);
                svc.saveSyntheticToTestDatabase(blob, true, result -> {
                    switch (result) {
                        case INSERTED -> { updateCards(window); updateTable(window); HotelApp.showToast(window, "Synthetic request saved to HOTEL_DB_TEST.", "success"); }
                        case DUPLICATE -> HotelApp.showToast(window, "Duplicate request: nothing was inserted.", "warn");
                        case REJECTED -> HotelApp.showToast(window, "Reservation rejected: check the synthetic message and reference.", "warn");
                        case FAILED -> HotelApp.showToast(window, "Save failed; verify the test database and connection.", "error");
                    }
                });
            });
        });
        svc.checkTestDatabase(available -> {
            if (available) window.executeJS("{const b=document.getElementById('" + saveButtonId + "'); if(b) b.disabled=false;}");
        });
        Button.of(window, "btn-modal-confirm-simulate").onClick(e -> {
            TextArea area = TextArea.of(window, "gmail-sim-blob");
            String blob = area.value();
            closeGmailSimulateModal(window);
            HotelApp.showToast(window, "Running simulation...", "info");
            svc.simulate(blob,
                    result -> HotelApp.showToast(window, result.message(), result.succeeded() ? "success" : "warn"),
                    t -> HotelApp.showToast(window, "Simulation failed: " + t.getClass().getSimpleName(), "error")
            );
        });
    }

    private static void closeGmailSimulateModal(NWindow window) {
        Div.of(window, "modal-container").classList.add("hidden");
        Div.of(window, "modal-body").setInnerHTML("");
        Div.of(window, "modal-footer").setInnerHTML("");
    }

    public static void resetFilters(NWindow window) {
        currentStatus = "ALL";
        currentSearch = "";
        TextField.of(window, "requests-search-input").value.set("");
        setActiveStatusButton(window, "requests-filter-all");
        updateTable(window);
    }

    public BookingRequestRow getBookingRequestRow(BookingRequest bookingRequest) {
        return new BookingRequestRow(String.valueOf(bookingRequest.getRequestId()), bookingRequest);
    }
}
