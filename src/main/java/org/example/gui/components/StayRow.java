package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Booking;
import org.example.Core.DatabaseModule.model.Customer;
import org.unbescape.html.HtmlEscape;

import java.util.Locale;

public class StayRow extends NComponet {
    private final Booking booking;
    private final Customer customer;
    private final String assignedRooms;
    private final long nights;
    private final double total;
    private final double paid;
    private final double balance;
    private final String statusClass;

    public StayRow(String id, Booking booking, Customer customer, String assignedRooms, long nights,
                   double total, double paid, double balance, String statusClass) {
        super(id, "tr");
        this.booking = booking;
        this.customer = customer;
        this.assignedRooms = assignedRooms;
        this.nights = nights;
        this.total = total;
        this.paid = paid;
        this.balance = balance;
        this.statusClass = statusClass;
    }

    @Override
    public String buildHtml(String childHtml) {
        String customerName = (customer == null) ? "Unknown Guest" : escape(customer.getFullName());
        String customerPhone = (customer == null || customer.getPhone() == null) ? "" : escape(customer.getPhone());
        String rooms = (assignedRooms == null || assignedRooms.isEmpty()) ? "None assigned" : escape(assignedRooms);
        String status = booking.getStatus() == null ? "PENDING" : booking.getStatus().toUpperCase(Locale.ROOT);
        return "<tr>"
                + "<td><strong style=\"color:var(--color-primary);\">#" + booking.getBookingId() + "</strong></td>"
                + "<td><div style=\"font-weight:600;\">" + customerName + "</div>"
                + "<small style=\"color:var(--color-text-muted);\">" + customerPhone + "</small></td>"
                + "<td>" + rooms + "</td>"
                + "<td><strong>" + booking.getCheckIn() + "</strong> → <strong>" + booking.getCheckOut() + "</strong><br>"
                + "<small style=\"color:var(--color-text-muted);\">" + nights + " night(s)</small></td>"
                + "<td><span class=\"badge " + statusClass + "\">" + escape(status) + "</span></td>"
                + "<td><div>Total: <strong>$" + money(total) + "</strong></div>"
                + "<div>Paid: $" + money(paid) + "</div>"
                + "<div>Balance: <strong>$" + money(balance) + "</strong></div></td>"
                + "<td><button id=\"folio-open-" + booking.getBookingId()
                + "\" class=\"btn btn-sm btn-outline btn-open-folio\" data-id=\"" + booking.getBookingId()
                + "\" style=\"font-weight:600;color:var(--color-primary);\">Folio & Ledger →</button></td>"
                + childHtml
                + "</tr>";
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}