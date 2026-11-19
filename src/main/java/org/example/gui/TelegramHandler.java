package org.example.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.gui.Button;
import com.codehack.nngf.gui.Div;
import com.codehack.nngf.gui.TextArea;
import com.codehack.nngf.gui.TextField;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.Customer;
import org.example.Core.TelegramModule.telegram.TelegramBotService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class TelegramHandler {
    private static String currentSearch = "";

    public static void handle(NWindow window) {
        Button.of(window, "btn-refresh-customers").onClick(e -> { updateCustomersTable(window); updateMessageLog(window); });

        Button.of(window, "btn-add-telegram-user").onClick(e -> openAddTelegramUserModal(window));

        TextField searchField = TextField.of(window, "customers-search-input");
        searchField.events.addEvent(com.codehack.nngf.Core.builder.NEvent.INPUT, event -> {
            currentSearch = event.GetString("searchVal");
            if (currentSearch == null) currentSearch = "";
            updateCustomersTable(window);
        }, searchField.q("searchVal").value());

        updateCustomersTable(window);
        updateMessageLog(window);
    }

    public static void updateMessageLog(NWindow window) {
        Div tbody = Div.of(window, "tbody-messages");
        if (tbody == null) return;
        StringBuilder rows = new StringBuilder();
        try {
            List<Customer> customers = HotelApp.databaseModule.getAllCustomers();
            for (Customer c : customers) {
                List<org.example.Core.DatabaseModule.model.Message> msgs =
                        HotelApp.databaseModule.getMessagesForCustomer(c.getCustomerId());
                for (org.example.Core.DatabaseModule.model.Message m : msgs) {
                    rows.append(new org.example.gui.components.TelegramMessageRow(
                            "tg-msg-" + m.getMessageId(), m, c.getFullName()).buildHtml(""));
                }
            }
            if (rows.length() == 0) {
                rows.append("<tr><td colspan=\"5\" style=\"text-align:center;padding:2rem;\">No messages logged.</td></tr>");
            }
            tbody.setInnerHTML(rows.toString());
        } catch (SQLException e) {
            tbody.setInnerHTML("<tr><td colspan=\"5\" style=\"text-align:center;padding:2rem;color:var(--status-failed-text);\">Unable to load message log.</td></tr>");
        }
    }

    public static void updateCustomersTable(NWindow window) {
        Div tbody = Div.of(window, "tbody-customers");
        StringBuilder rows = new StringBuilder();
        String search = currentSearch == null ? "" : currentSearch.trim().toLowerCase();

        try {
            List<Customer> customers = HotelApp.databaseModule.getAllCustomers();
            for (Customer c : customers) {
                String name = c.getFullName() == null ? "" : c.getFullName();
                String phone = c.getPhone() == null ? "" : c.getPhone();
                if (!search.isEmpty() && !name.toLowerCase().contains(search) && !phone.toLowerCase().contains(search)) {
                    continue;
                }
                Long chatId = c.getTelegramChatId();
                boolean linked = chatId != null;
                String registered = c.getCreatedAt() == null ? "-" : c.getCreatedAt().toString();
                rows.append(new org.example.gui.components.TelegramCustomerRow(
                        "tg-cust-" + c.getCustomerId(), c.getCustomerId(), name, phone, linked,
                        linked ? chatId : 0L, c.getTimesDenied(), registered).buildHtml(""));
            }
            if (rows.length() == 0) {
                rows.append("<tr><td colspan=\"7\" style=\"text-align:center;padding:2rem;\">No customers found.</td></tr>");
            }
            tbody.setInnerHTML(rows.toString());

            // Wire send-message buttons via delegated click
            Div tbl = Div.of(window, "tbody-customers");
            tbl.events.addEvent(com.codehack.nngf.Core.builder.NEvent.CLICK,
                    "ex = e.target.closest('.btn-send-tg'); ex = ex ? ex.getAttribute('data-id') + '|' + ex.getAttribute('data-chat') : null;",
                    event -> {
                        if (event == null) return;
                        String[] parts = event.toString().split("\\|", -1);
                        if (parts.length != 2) return;
                        try {
                            int cid = Integer.parseInt(parts[0]);
                            long chat = Long.parseLong(parts[1]);
                            openSendMessageModal(window, cid, chat);
                        } catch (NumberFormatException ignored) { }
                    });
        } catch (SQLException e) {
            tbody.setInnerHTML("<tr><td colspan=\"7\" style=\"text-align:center;padding:2rem;color:var(--status-failed-text);\">Unable to load customers.</td></tr>");
        }
    }

    private static void openAddTelegramUserModal(NWindow window) {
        // Build dropdown of active bookings that have a customer
        StringBuilder options = new StringBuilder();
        options.append("<option value=\"\">-- Select an active booking --</option>");
        try {
            List<Booking> bookings = HotelApp.databaseModule.getAllBookings();
            for (Booking b : bookings) {
                String status = b.getStatus() == null ? "" : b.getStatus().toUpperCase();
                if (!"ACTIVE".equals(status) && !"PENDING".equals(status)) continue;
                if (b.getCustomerId() <= 0) continue;
                Customer c = HotelApp.databaseModule.getCustomerById(b.getCustomerId());
                String label = "Booking #" + b.getBookingId() + " - " + (c == null ? "Unknown" : c.getFullName());
                options.append("<option value=\"").append(b.getBookingId()).append("\">").append(escape(label)).append("</option>");
            }
        } catch (SQLException e) {
            HotelApp.showToast(window, "Failed to load bookings: " + e.getMessage(), "error");
            return;
        }

        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        title.setInnerText("Add Telegram User");
        body.setInnerHTML("<div class=\"form-group\">"
                + "<label class=\"form-label\" for=\"tg-booking-select\">Active Booking</label>"
                + "<select id=\"tg-booking-select\" class=\"form-control\">" + options + "</select>"
                + "</div>"
                + "<div id=\"tg-code-area\" style=\"display:none;margin-top:1rem;text-align:center;\">"
                + "<div style=\"font-size:0.85rem;color:var(--color-text-muted);\">Code (valid 5 minutes):</div>"
                + "<div id=\"tg-code-value\" style=\"font-size:2rem;font-weight:800;letter-spacing:4px;margin:0.5rem 0;\">----</div>"
                + "<div style=\"font-size:0.75rem;color:var(--color-text-muted);\">Customer sends <code>/register CODE</code> to the bot, or scans the QR deep link.</div>"
                + "</div>");
        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"btn-modal-generate-code\" class=\"btn btn-primary btn-sm\">Generate Code</button>");

        modal.classList.remove("hidden");

        Button.of(window, "btn-modal-cancel").onClick(e -> closeTelegramModal(window));

        Button.of(window, "btn-modal-generate-code").onClick(e -> {
            // read via JS value
            Object v = null;
            try { v = Div.of(window, "tg-booking-select").executeChildJsRead(
                    com.codehack.nngf.Core.Js.id("tg-booking-select").get("value").ret().end()); } catch (Exception ignored) { }
            String selected = (v == null) ? "" : v.toString();
            if (selected.isEmpty()) {
                HotelApp.showToast(window, "Please select a booking.", "warn");
                return;
            }
            int bookingId;
            try { bookingId = Integer.parseInt(selected); }
            catch (NumberFormatException ex) { HotelApp.showToast(window, "Invalid booking.", "error"); return; }

            // Generate 6-digit code
            String code = String.format("%06d", new Random().nextInt(1000000));
            LocalDateTime expires = LocalDateTime.now().plusMinutes(5);
            try {
                HotelApp.databaseModule.setTelegramLinkCode(bookingId, code, expires);
            } catch (SQLException ex) {
                HotelApp.showToast(window, "Failed to generate code: " + ex.getMessage(), "error");
                return;
            }

            // Show code
            Div.of(window, "tg-code-area").setAttribute("style", "display:block;margin-top:1rem;text-align:center;");
            try {
                Div.of(window, "tg-code-value").setInnerText(code);
            } catch (Exception ignored) { }
            HotelApp.showToast(window, "Code generated: " + code + " (valid 5 min)", "success");
        });
    }

    private static void openSendMessageModal(NWindow window, int customerId, long chatId) {
        Div modal = Div.of(window, "modal-container");
        Div title = Div.of(window, "modal-title");
        Div body = Div.of(window, "modal-body");
        Div footer = Div.of(window, "modal-footer");

        title.setInnerText("Send Telegram Message");
        body.setInnerHTML("<div class=\"form-group\">"
                + "<label class=\"form-label\" for=\"tg-msg-text\">Message</label>"
                + "<textarea id=\"tg-msg-text\" class=\"form-control\" rows=\"4\" placeholder=\"Type your message...\"></textarea>"
                + "</div>");
        footer.setInnerHTML("<button id=\"btn-modal-cancel\" class=\"btn btn-outline btn-sm\">Cancel</button>"
                + "<button id=\"btn-modal-send-tg\" class=\"btn btn-primary btn-sm\">Send</button>");

        modal.classList.remove("hidden");

        Button.of(window, "btn-modal-cancel").onClick(e -> closeTelegramModal(window));

        Button.of(window, "btn-modal-send-tg").onClick(e -> {
            TextArea area = TextArea.of(window, "tg-msg-text");
            String text = area.value();
            if (text == null || text.trim().isEmpty()) {
                HotelApp.showToast(window, "Message cannot be empty.", "warn");
                return;
            }
            closeTelegramModal(window);
            try {
                TelegramBotService svc = new TelegramBotService();
                svc.sendMessage(String.valueOf(chatId), text.trim());
                org.example.Core.DatabaseModule.model.Message msg =
                        new org.example.Core.DatabaseModule.model.Message(customerId, text.trim(), "SENT");
                HotelApp.databaseModule.logMessage(msg);
                HotelApp.showToast(window, "Message sent.", "success");
                updateMessageLog(window);
            } catch (Exception ex) {
                try {
                    org.example.Core.DatabaseModule.model.Message msg =
                            new org.example.Core.DatabaseModule.model.Message(customerId, text.trim(), "FAILED");
                    HotelApp.databaseModule.logMessage(msg);
                } catch (SQLException ignored) { }
                HotelApp.showToast(window, "Failed to send: " + ex.getClass().getSimpleName(), "error");
                updateMessageLog(window);
            }
        });
    }

    private static void closeTelegramModal(NWindow window) {
        Div.of(window, "modal-container").classList.add("hidden");
        Div.of(window, "modal-body").setInnerHTML("");
        Div.of(window, "modal-footer").setInnerHTML("");
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}