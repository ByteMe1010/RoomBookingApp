package org.example.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.builder.NEvent;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.ComboBox;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.NumberField;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.BookingRoom;
import org.example.Core.DatabaseModule.model.Customer;
import org.example.Core.DatabaseModule.model.Item;
import org.example.Core.DatabaseModule.model.ItemOrder;
import org.example.Core.DatabaseModule.model.Payment;
import org.example.Core.DatabaseModule.model.Room;
import org.unbescape.html.HtmlEscape;

import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

public class FolioHandler {
    private static int activeBookingId = -1;

    public static void handle(NWindow window) {
        Button.of(window, "folio-close-btn").onClick(event -> close(window));
    }

    public static void open(NWindow window, int bookingId) {
        activeBookingId = bookingId;
        Div.of(window, "folio-drawer-overlay").classList.remove("hidden");
        update(window, bookingId);
    }

    public static void close(NWindow window) {
        activeBookingId = -1;
        Div.of(window, "folio-drawer-overlay").classList.add("hidden");
        Div.of(window, "folio-body").setInnerHTML("");
    }

    public static void update(NWindow window, int bookingId) {
        Div body = Div.of(window, "folio-body");
        body.setInnerHTML("<div style=\"text-align:center;padding:2rem;color:var(--color-text-muted);\">Loading guest folio...</div>");

        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null) {
                body.setInnerHTML("<div style=\"color:red;padding:1.5rem;\">Booking #" + bookingId + " was not found.</div>");
                return;
            }

            String status = booking.getStatus() == null
                    ? "PENDING"
                    : booking.getStatus().toUpperCase(Locale.ROOT);

            Customer customer = HotelApp.databaseModule.getCustomerById(booking.getCustomerId());
            String customerName = customer == null ? "Unknown Guest" : customer.getFullName();
            String customerPhone = customer == null || customer.getPhone() == null ? "" : customer.getPhone();

            Div.of(window, "folio-title").setInnerText("Folio: " + customerName + " (Booking #" + bookingId + ")");
            Div.of(window, "folio-subtitle").setInnerText("Stay: " + booking.getCheckIn() + " to " + booking.getCheckOut()
                    + (customerPhone.isEmpty() ? "" : " | " + customerPhone));
            Div.of(window, "folio-status-badge").setInnerHTML("<span class=\"badge " + statusBadgeClass(status) + "\">" + escape(status) + "</span>");

            long nights = Math.max(1, ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()));

            List<BookingRoom> bookingRooms = HotelApp.databaseModule.getRoomsForBooking(bookingId);
            double roomCharges = 0.0;
            StringBuilder roomRows = new StringBuilder();

            for (BookingRoom bookingRoom : bookingRooms) {
                Room room = HotelApp.databaseModule.getRoomById(bookingRoom.getRoomId());
                double rate = bookingRoom.getPricePerNight();
                double subtotal = rate * nights;
                roomCharges += subtotal;

                roomRows.append(new org.example.gui.components.FolioRoomRow(
                        "folio-room-" + bookingRoom.getRoomId(), room, rate, nights, subtotal).buildHtml(""));
            }

            if (roomRows.length() == 0) {
                roomRows.append("<tr><td colspan=\"4\" style=\"text-align:center;color:var(--color-text-muted);padding:10px;\">No rooms assigned.</td></tr>");
            }

            List<ItemOrder> itemOrders = HotelApp.databaseModule.getOrdersForBooking(bookingId);
            double itemCharges = 0.0;
            StringBuilder itemRows = new StringBuilder();

            for (ItemOrder order : itemOrders) {
                Item item = HotelApp.databaseModule.getItemById(order.getItemId());
                double subtotal = order.getAmount() * order.getUnitPrice();
                itemCharges += subtotal;
                String method = item == null ? "COUNT" : (item.getMeasuringMethod() == null ? "COUNT" : item.getMeasuringMethod().toUpperCase(Locale.ROOT));
                boolean showCancel = "ACTIVE".equals(status) || "PENDING".equals(status);

                itemRows.append(new org.example.gui.components.FolioItemRow(
                        "folio-item-" + order.getOrderId(), order, item, method, subtotal, showCancel).buildHtml(""));
            }

            if (itemOrders.isEmpty()) {
                itemRows.append("<tr><td colspan=\"5\" style=\"text-align:center;color:var(--color-text-muted);padding:10px;\">No supplies or room service ordered yet.</td></tr>");
            }

            List<Payment> payments = HotelApp.databaseModule.getPaymentsForBooking(bookingId);
            double paymentsTotal = 0.0;
            StringBuilder paymentRows = new StringBuilder();

            for (Payment payment : payments) {
                paymentsTotal += payment.getAmount();
                String paidAt = payment.getPaidAt() == null
                        ? ""
                        : payment.getPaidAt().toString().replace('T', ' ');

                paymentRows.append(new org.example.gui.components.FolioPaymentRow(
                        "folio-payment-" + payment.getPaymentId(), payment, paidAt).buildHtml(""));
            }

            if (payments.isEmpty()) {
                paymentRows.append("<tr><td colspan=\"4\" style=\"text-align:center;color:var(--color-text-muted);padding:10px;\">No payments recorded yet.</td></tr>");
            }

            double totalCharges = roomCharges + itemCharges;
            double balanceDue = totalCharges - paymentsTotal;

            StringBuilder itemOptions = new StringBuilder();
            Item defaultItem = null;
            if ("ACTIVE".equals(status)) {
                List<Item> activeItems = HotelApp.databaseModule.getAllActiveItems();
                for (Item item : activeItems) {
                    if (defaultItem == null) defaultItem = item;
                    itemOptions.append(new org.example.gui.components.FolioItemOption(
                            "folio-item-opt-" + item.getItemId(), item).buildHtml(""));
                }
            }

            StringBuilder html = new StringBuilder();
            html.append("<div class=\"folio-math-card\"><div class=\"folio-math-formula\">Live Ledger: Room Charges + Item Charges - Payments = Balance Due</div>")
                    .append("<div class=\"folio-math-breakdown\">")
                    .append("<div class=\"folio-math-col\"><div class=\"math-label\">Room Total</div><div class=\"math-val\">$").append(money(roomCharges)).append("</div></div>")
                    .append("<div class=\"folio-math-op\">+</div>")
                    .append("<div class=\"folio-math-col\"><div class=\"math-label\">Item Orders</div><div class=\"math-val\">$").append(money(itemCharges)).append("</div></div>")
                    .append("<div class=\"folio-math-op\">-</div>")
                    .append("<div class=\"folio-math-col\"><div class=\"math-label\">Payments</div><div class=\"math-val\">$").append(money(paymentsTotal)).append("</div></div>")
                    .append("</div><div class=\"folio-math-total\"><div class=\"due-label\">Balance Due:<div style=\"font-size:0.75rem;color:#94a3b8;\">")
                    .append(balanceDue <= 0 ? "Settled in Full" : "Outstanding balance")
                    .append("</div></div><div class=\"due-amount ").append(balanceDue > 0 ? "due-positive" : "due-settled")
                    .append("\">$").append(money(balanceDue)).append("</div></div></div>");

            html.append("<div class=\"folio-section\"><div class=\"folio-section-title\"><span>Assigned Rooms</span><span>$")
                    .append(money(roomCharges)).append("</span></div><table class=\"mini-table\"><thead><tr><th>Room</th><th>Rate</th><th>Duration</th><th style=\"text-align:right;\">Subtotal</th></tr></thead><tbody>")
                    .append(roomRows).append("</tbody></table></div>");

            html.append("<div class=\"folio-section\"><div class=\"folio-section-title\"><span>Item Orders & Supplies</span><span>$")
                    .append(money(itemCharges)).append("</span></div><table class=\"mini-table\"><thead><tr><th>Item</th><th>Unit Price</th><th>Qty/Amt</th><th style=\"text-align:right;\">Subtotal</th><th></th></tr></thead><tbody>")
                    .append(itemRows).append("</tbody></table>");

            if ("ACTIVE".equals(status)) {
                String defaultMethod = defaultItem == null ? "COUNT" : normalizeMethod(defaultItem.getMeasuringMethod());
                String defaultStock = defaultItem == null ? "0.00" : String.format(Locale.US, "%.2f", defaultItem.getStockAmount());
                String defaultAmount = "COUNT".equals(defaultMethod) ? "1" : "1.00";
                String step = "COUNT".equals(defaultMethod) ? "1" : "0.01";

                html.append("<form id=\"folio-add-order-form\" class=\"mini-form-row\"><div class=\"mini-form-group\" style=\"flex:2;\">")
                        .append("<label for=\"folio-item-select\">Add Active Item:</label><select id=\"folio-item-select\" class=\"form-control\" required>")
                        .append(itemOptions.length() == 0 ? "<option value=\"\">No active items</option>" : itemOptions)
                        .append("</select></div><div class=\"mini-form-group\" style=\"flex:1;\"><label for=\"folio-item-amount\" id=\"folio-amount-label\">Amount (Stock: ")
                        .append(defaultStock).append("):</label><input type=\"number\" id=\"folio-item-amount\" class=\"form-control\" min=\"")
                        .append("COUNT".equals(defaultMethod) ? "1" : "0.01").append("\" step=\"").append(step).append("\" value=\"").append(defaultAmount).append("\" required>")
                        .append("</div><button type=\"button\" id=\"btn-folio-add-order\" class=\"btn btn-sm btn-primary\">+ Add Order</button></form>");
            }
            html.append("</div>");

            html.append("<div class=\"folio-section\"><div class=\"folio-section-title\"><span>Payments Ledger</span><span style=\"color:#059669;\">-$")
                    .append(money(paymentsTotal)).append("</span></div><table class=\"mini-table\"><thead><tr><th>Payment ID</th><th>Method</th><th>Paid At</th><th style=\"text-align:right;\">Amount</th></tr></thead><tbody>")
                    .append(paymentRows).append("</tbody></table>");

            if (!"CANCELED".equals(status)) {
                html.append("<form id=\"folio-add-payment-form\" class=\"mini-form-row\"><div class=\"mini-form-group\" style=\"flex:1;\">")
                        .append("<label for=\"folio-pay-method\">Method:</label><select id=\"folio-pay-method\" class=\"form-control\" required>")
                        .append("<option value=\"CARD\">CARD</option><option value=\"CASH\">CASH</option><option value=\"OTHER\">OTHER</option>")
                        .append("</select></div><div class=\"mini-form-group\" style=\"flex:1;\"><label for=\"folio-pay-amount\">Amount ($):</label>")
                        .append("<input type=\"number\" id=\"folio-pay-amount\" class=\"form-control\" min=\"0.01\" step=\"0.01\" value=\"")
                        .append(balanceDue > 0 ? money(balanceDue) : "")
                        .append("\" placeholder=\"0.00\" required></div><button type=\"button\" id=\"btn-folio-record-payment\" class=\"btn btn-sm btn-primary\" style=\"background:#059669;\">Record Pay</button></form>");
            }
            html.append("</div>");

            html.append("<div style=\"display:flex;justify-content:space-between;align-items:center;padding-top:1rem;border-top:1px solid var(--color-border);margin-top:auto;\"><div>");
            if ("PENDING".equals(status)) {
                html.append("<button id=\"btn-folio-checkin\" class=\"btn btn-sm btn-primary\">Check In Guest</button>")
                        .append(" <button id=\"btn-folio-noshow\" class=\"btn btn-sm btn-outline\" style=\"color:var(--status-failed-text);\">No-Show Cancel</button>");
            } else if ("ACTIVE".equals(status)) {
                html.append("<button id=\"btn-folio-checkout\" class=\"btn btn-sm btn-primary\">Complete Check Out</button>");
            }
            html.append("</div><button id=\"folio-drawer-close\" class=\"btn btn-sm btn-outline\">Close Folio</button></div>");

            body.setInnerHTML(html.toString());
            bindDynamicControls(window, bookingId, status);
        } catch (SQLException exception) {
            body.setInnerHTML("<div style=\"color:red;padding:1.5rem;\">Unable to load folio: " + escape(exception.getMessage()) + "</div>");
        }
    }

    private static void bindDynamicControls(NWindow window, int bookingId, String status) {
        Button.of(window, "folio-drawer-close").onClick(event -> close(window));

        Div body = Div.of(window, "folio-body");
        try {
            for (ItemOrder order : HotelApp.databaseModule.getOrdersForBooking(bookingId)) {
                if ("ACTIVE".equals(status) || "PENDING".equals(status)) {
                    Button.of(window, "folio-cancel-order-" + order.getOrderId())
                            .onClick(event -> cancelItemOrder(window, bookingId, order.getOrderId()));
                }
            }
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Unable to bind folio order actions: " + exception.getMessage(), "error");
        }

        if ("ACTIVE".equals(status)) {
            ComboBox itemSelect = ComboBox.of(window, "folio-item-select");
            itemSelect.events.addJsEvent(NEvent.CHANGE,
                    "let option = this.options[this.selectedIndex]; let amount = document.getElementById('folio-item-amount'); "
                    + "let label = document.getElementById('folio-amount-label'); let method = option ? option.getAttribute('data-method') : 'COUNT'; "
                    + "let stock = option ? option.getAttribute('data-stock') : '0.00'; "
                    + "amount.step = method === 'COUNT' ? '1' : '0.01'; amount.min = method === 'COUNT' ? '1' : '0.01'; "
                    + "amount.max = stock; amount.value = method === 'COUNT' ? '1' : '1.00'; "
                    + "label.innerText = 'Amount (Stock: ' + stock + '):';");
            NumberField amountField = new NumberField("folio-item-amount", window);

            Button.of(window, "btn-folio-add-order").events.addEvent(
                    NEvent.CLICK,
                    event -> {
                        String itemIdText = event.GetString("itemId");
                        String amountText = event.GetString("amount");
                        int itemId;
                        double amount;
                        try {
                            itemId = Integer.parseInt(itemIdText == null ? "" : itemIdText);
                            amount = Double.parseDouble(amountText == null ? "" : amountText);
                        } catch (NumberFormatException exception) {
                            HotelApp.showToast(window, "Enter a valid item and quantity.", "error");
                            return;
                        }
                        addItemOrder(window, bookingId, itemId, amount);
                    },
                    itemSelect.q("itemId").value(),
                    amountField.q("amount").value()
            );
        }

        if (!"CANCELED".equals(status)) {
            ComboBox methodSelect = ComboBox.of(window, "folio-pay-method");
            NumberField amountField = new NumberField("folio-pay-amount", window);

            Button.of(window, "btn-folio-record-payment").events.addEvent(
                    NEvent.CLICK,
                    event -> {
                        String method = event.GetString("method");
                        String amountText = event.GetString("amount");
                        double amount;
                        try {
                            amount = Double.parseDouble(amountText == null ? "" : amountText);
                        } catch (NumberFormatException exception) {
                            HotelApp.showToast(window, "Enter a valid payment amount.", "error");
                            return;
                        }
                        recordPayment(window, bookingId, amount, method);
                    },
                    methodSelect.q("method").value(),
                    amountField.q("amount").value()
            );
        }

        if ("PENDING".equals(status)) {
            Button.of(window, "btn-folio-checkin").onClick(event -> checkIn(window, bookingId));
            Button.of(window, "btn-folio-noshow").onClick(event -> cancelNoShow(window, bookingId));
        } else if ("ACTIVE".equals(status)) {
            Button.of(window, "btn-folio-checkout").onClick(event -> checkOut(window, bookingId));
        }
    }

    private static void addItemOrder(NWindow window, int bookingId, int itemId, double amount) {
        if (itemId <= 0) {
            HotelApp.showToast(window, "Select an active item.", "error");
            return;
        }
        if (amount <= 0) {
            HotelApp.showToast(window, "Order amount must be greater than zero.", "error");
            return;
        }

        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null || !"ACTIVE".equalsIgnoreCase(booking.getStatus())) {
                HotelApp.showToast(window, "Items can only be charged to an active stay.", "error");
                return;
            }

            Item item = HotelApp.databaseModule.getItemById(itemId);
            if (item == null) {
                HotelApp.showToast(window, "Item was not found.", "error");
                return;
            }
            if (!item.isActive()) {
                HotelApp.showToast(window, "Archived items cannot be ordered.", "error");
                return;
            }
            if (amount > item.getStockAmount()) {
                HotelApp.showToast(window, "Insufficient stock. Available: " + String.format(Locale.US, "%.2f", item.getStockAmount()), "error");
                return;
            }
            if ("COUNT".equalsIgnoreCase(normalizeMethod(item.getMeasuringMethod()))
                    && Math.abs(amount - Math.rint(amount)) > 0.000001) {
                HotelApp.showToast(window, "COUNT items require whole-number quantities.", "error");
                return;
            }

            ItemOrder order = new ItemOrder(bookingId, itemId, amount, item.getPricePerUnit());
            int orderId = HotelApp.databaseModule.purchaseItemOrder(order);
            if (orderId <= 0) {
                HotelApp.showToast(window, "Failed to record item order.", "error");
                return;
            }

            HotelApp.showToast(window, "Item charged to the guest folio and stock updated.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to add item order: " + exception.getMessage(), "error");
        }
    }

    private static void cancelItemOrder(NWindow window, int bookingId, int orderId) {
        try {
            ItemOrder order = HotelApp.databaseModule.getOrderById(orderId);
            if (order == null) {
                HotelApp.showToast(window, "Item order was not found.", "error");
                return;
            }

            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking != null && ("FINISHED".equalsIgnoreCase(booking.getStatus()) || "CANCELED".equalsIgnoreCase(booking.getStatus()))) {
                HotelApp.showToast(window, "A finished or canceled folio cannot be modified.", "error");
                return;
            }

            Item item = HotelApp.databaseModule.getItemById(order.getItemId());
            boolean canceled = HotelApp.databaseModule.cancelItemOrderAndRestock(orderId);
            if (!canceled) {
                HotelApp.showToast(window, "Item order was not found.", "error");
                return;
            }

            HotelApp.showToast(window, "Item order canceled and stock restored.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to cancel item order: " + exception.getMessage(), "error");
        }
    }

    private static void recordPayment(NWindow window, int bookingId, double amount, String method) {
        if (amount <= 0) {
            HotelApp.showToast(window, "Payment amount must be greater than zero.", "error");
            return;
        }

        String normalizedMethod = method == null ? "" : method.toUpperCase(Locale.ROOT);
        if (!normalizedMethod.matches("CASH|CARD|OTHER")) {
            HotelApp.showToast(window, "Payment method must be CASH, CARD, or OTHER.", "error");
            return;
        }

        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null) {
                HotelApp.showToast(window, "Booking was not found.", "error");
                return;
            }
            if ("CANCELED".equalsIgnoreCase(booking.getStatus())) {
                HotelApp.showToast(window, "Payments cannot be added to a canceled booking.", "error");
                return;
            }

            int paymentId = HotelApp.databaseModule.recordPayment(new Payment(bookingId, amount, normalizedMethod));
            if (paymentId <= 0) {
                HotelApp.showToast(window, "Failed to record payment.", "error");
                return;
            }

            HotelApp.showToast(window, "Payment of $" + money(amount) + " recorded.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Failed to record payment: " + exception.getMessage(), "error");
        }
    }

    private static void checkIn(NWindow window, int bookingId) {
        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null || !"PENDING".equalsIgnoreCase(booking.getStatus())) {
                HotelApp.showToast(window, "Only pending stays can be checked in.", "error");
                return;
            }

            HotelApp.databaseModule.updateBookingStatus(bookingId, "ACTIVE");
            for (BookingRoom bookingRoom : HotelApp.databaseModule.getRoomsForBooking(bookingId)) {
                HotelApp.databaseModule.updateRoomStatus(bookingRoom.getRoomId(), "BOOKED");
            }

            HotelApp.showToast(window, "Guest checked in and rooms marked as booked.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Check-in failed: " + exception.getMessage(), "error");
        }
    }

    private static void cancelNoShow(NWindow window, int bookingId) {
        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null || !"PENDING".equalsIgnoreCase(booking.getStatus())) {
                HotelApp.showToast(window, "Only pending stays can be canceled as no-show.", "error");
                return;
            }

            HotelApp.databaseModule.updateBookingStatus(bookingId, "CANCELED");
            for (BookingRoom bookingRoom : HotelApp.databaseModule.getRoomsForBooking(bookingId)) {
                HotelApp.databaseModule.updateRoomStatus(bookingRoom.getRoomId(), "AVAILABLE");
            }

            HotelApp.showToast(window, "No-show canceled and rooms released.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "No-show cancellation failed: " + exception.getMessage(), "error");
        }
    }

    private static void checkOut(NWindow window, int bookingId) {
        try {
            Booking booking = HotelApp.databaseModule.getBookingById(bookingId);
            if (booking == null || !"ACTIVE".equalsIgnoreCase(booking.getStatus())) {
                HotelApp.showToast(window, "Only active stays can be checked out.", "error");
                return;
            }

            double roomCharges = calculateRoomCharges(booking);
            double itemCharges = calculateItemCharges(bookingId);
            double paid = HotelApp.databaseModule.getTotalPaidForBooking(bookingId);
            double balance = roomCharges + itemCharges - paid;

            if (balance > 0.004) {
                HotelApp.showToast(window, "Cannot check out. Outstanding balance: $" + money(balance) + ".", "error");
                return;
            }

            HotelApp.databaseModule.updateBookingStatus(bookingId, "FINISHED");
            for (BookingRoom bookingRoom : HotelApp.databaseModule.getRoomsForBooking(bookingId)) {
                HotelApp.databaseModule.updateRoomStatus(bookingRoom.getRoomId(), "AVAILABLE");
            }

            HotelApp.showToast(window, "Guest checked out and rooms released.", "success");
            update(window, bookingId);
            StayHandler.updateTable(window);
        } catch (SQLException exception) {
            HotelApp.showToast(window, "Check-out failed: " + exception.getMessage(), "error");
        }
    }

    private static double calculateRoomCharges(Booking booking) throws SQLException {
        long nights = Math.max(1, ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut()));
        double total = 0.0;
        for (BookingRoom bookingRoom : HotelApp.databaseModule.getRoomsForBooking(booking.getBookingId())) {
            total += bookingRoom.getPricePerNight() * nights;
        }
        return total;
    }

    private static double calculateItemCharges(int bookingId) throws SQLException {
        double total = 0.0;
        for (ItemOrder order : HotelApp.databaseModule.getOrdersForBooking(bookingId)) {
            total += order.getAmount() * order.getUnitPrice();
        }
        return total;
    }

    private static String normalizeMethod(String method) {
        return "CONTINUOUS".equalsIgnoreCase(method) ? "CONTINUOUS" : "COUNT";
    }

    private static String statusBadgeClass(String status) {
        return switch (status) {
            case "ACTIVE" -> "badge-active";
            case "FINISHED" -> "badge-finished";
            case "CANCELED" -> "badge-canceled";
            default -> "badge-pending";
        };
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlEscape.escapeHtml5(value);
    }
}
