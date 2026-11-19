package org.example.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.NEvent;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.BookingRoom;
import org.example.Core.DatabaseModule.model.Customer;
import org.example.Core.DatabaseModule.model.ItemOrder;
import org.example.Core.DatabaseModule.model.Room;
import org.unbescape.html.HtmlEscape;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class StayHandler {
    private static String currentStatus = "ALL";

    public static void handle(NWindow window) {
        bindStatusFilter(window, "stays-filter-all", "ALL");
        bindStatusFilter(window, "stays-filter-pending", "PENDING");
        bindStatusFilter(window, "stays-filter-active", "ACTIVE");
        bindStatusFilter(window, "stays-filter-finished", "FINISHED");
        bindStatusFilter(window, "stays-filter-canceled", "CANCELED");

        Button.of(window, "btn-refresh-stays").onClick(event -> updateTable(window));

        Div tbody = Div.of(window, "tbody-stays");
        tbody.events.addEvent(
                NEvent.CLICK,
                event -> {
                    String bookingId = event.GetString("bookingId");
                    if (bookingId == null) return;
                    try {
                        FolioHandler.open(window, Integer.parseInt(bookingId));
                    } catch (NumberFormatException exception) {
                        HotelApp.showToast(window, "Invalid booking.", "error");
                    }
                },
                tbody.q("bookingId").target().attr("data-id")
        );

        FolioHandler.handle(window);

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
                "stays-filter-all",
                "stays-filter-pending",
                "stays-filter-active",
                "stays-filter-finished",
                "stays-filter-canceled"
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

    public static void updateTable(NWindow window) {
        Div tbody = Div.of(window, "tbody-stays");

        try {
            List<Booking> bookings = HotelApp.databaseModule.getAllBookings();
            StringBuilder rows = new StringBuilder();
            int visibleCount = 0;

            for (Booking booking : bookings) {
                String status = booking.getStatus() == null
                        ? "PENDING"
                        : booking.getStatus().toUpperCase(Locale.ROOT);

                if (!"ALL".equals(currentStatus) && !currentStatus.equals(status)) {
                    continue;
                }

                visibleCount++;

                Customer customer = HotelApp.databaseModule.getCustomerById(booking.getCustomerId());
                String customerName = customer == null ? "Unknown Guest" : customer.getFullName();
                String customerPhone = customer == null || customer.getPhone() == null
                        ? ""
                        : customer.getPhone();

                List<BookingRoom> bookingRooms = HotelApp.databaseModule.getRoomsForBooking(booking.getBookingId());
                StringBuilder roomNumbers = new StringBuilder();
                double roomTotal = 0.0;

                for (int i = 0; i < bookingRooms.size(); i++) {
                    BookingRoom bookingRoom = bookingRooms.get(i);
                    Room room = HotelApp.databaseModule.getRoomById(bookingRoom.getRoomId());
                    if (roomNumbers.length() > 0) {
                        roomNumbers.append(", ");
                    }
                    roomNumbers.append(room == null ? "Unknown" : room.getRoomNumber());
                    roomTotal += bookingRoom.getPricePerNight();
                }

                long nights = Math.max(1, ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()));
                double total = roomTotal * nights;
                double paid = HotelApp.databaseModule.getTotalPaidForBooking(booking.getBookingId());
                double balance = total - paid;

                // Include item order charges in the billing summary
                List<ItemOrder> itemOrders = HotelApp.databaseModule.getOrdersForBooking(booking.getBookingId());
                double itemTotal = 0.0;
                for (ItemOrder itemOrder : itemOrders) {
                    itemTotal += itemOrder.getAmount() * itemOrder.getUnitPrice();
                }
                total += itemTotal;
                balance = total - paid;

                String statusClass = switch (status) {
                    case "ACTIVE" -> "badge-active";
                    case "FINISHED" -> "badge-finished";
                    case "CANCELED" -> "badge-canceled";
                    default -> "badge-pending";
                };

                String assignedRooms = roomNumbers.length() == 0 ? "None assigned" : roomNumbers.toString();

                rows.append(new org.example.gui.components.StayRow(
                        "stay-" + booking.getBookingId(), booking, customer, assignedRooms, nights, total, paid, balance, statusClass).buildHtml(""));
            }

            if (visibleCount == 0) {
                tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center; padding:2rem;\">No bookings match the selected status.</td></tr>");
            } else {
                tbody.setInnerHTML(rows.toString());
            }
        } catch (SQLException exception) {
            tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center; padding:2rem; color:var(--status-failed-text);\">Unable to load bookings.</td></tr>");
            System.err.println("Error while fetching stays: " + exception.getMessage());
        }
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlEscape.escapeHtml5(value);
    }
}
