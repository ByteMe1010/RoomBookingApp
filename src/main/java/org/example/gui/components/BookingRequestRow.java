package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import org.example.Core.DatabaseModule.model.BookingRequest;
import org.example.Core.DatabaseModule.model.MealPlan;
import org.unbescape.html.HtmlEscape;

import java.time.LocalDate;
import java.util.Locale;

public class BookingRequestRow extends NComponet {
    private final BookingRequest request;
    private final MealPlan mealPlan;

    public BookingRequestRow(String ID, BookingRequest request) {
        this(ID, request, null);
    }

    public BookingRequestRow(String ID, BookingRequest request, MealPlan mealPlan) {
        super(ID, "tr");
        this.request = request;
        this.mealPlan = mealPlan;
    }

    public BookingRequestRow(String ID, NWindow window, BookingRequest request) {
        this(ID, window, request, null);
    }

    public BookingRequestRow(String ID, NWindow window, BookingRequest request, MealPlan mealPlan) {
        super(ID, "tr", window);
        this.request = request;
        this.mealPlan = mealPlan;
    }

    public BookingRequestRow(String ID) {
        super(ID, "tr");
        this.request = null;
        this.mealPlan = null;
    }

    public BookingRequestRow(String ID, NWindow window) {
        super(ID, "tr", window);
        this.request = null;
        this.mealPlan = null;
    }

    @Override
    public String buildHtml(String childHtml) {
        if (request == null) {
            return "<tr>" + childHtml + "</tr>";
        }

        String bookingRef = value(request.getBookingRef());
        String guestName = value(request.getGuestName());
        String guestPhone = value(request.getGuestPhone(), "No phone provided");
        String status = value(request.getStatus(), "PENDING").toUpperCase(Locale.ROOT);
        String statusBadgeClass = statusBadgeClass(status);
        String checkIn = formatDate(request.getCheckIn());
        String checkOut = formatDate(request.getCheckOut());

        String mealPlanHtml;
        if (mealPlan != null) {
            mealPlanHtml = "<span class=\"badge badge-active\" title=\"" + escape(mealPlan.getName())
                    + "\">" + escape(mealPlan.getCode()) + "</span>"
                    + " <small style=\"display:block; color:var(--color-text-muted); font-size:0.75rem; margin-top:2px;\">"
                    + escape(mealPlan.getName()) + "</small>";
        } else {
            mealPlanHtml = "<span class=\"badge badge-closed\">Unassigned</span>";
        }

        String specialRequestHtml;
        if (request.getSpecialRequest() != null && !request.getSpecialRequest().trim().isEmpty()) {
            specialRequestHtml = "<div class=\"special-request-box\">"
                    + "<span class=\"special-req-tag\">★ Special Request</span>"
                    + "<span>" + escape(request.getSpecialRequest()) + "</span>"
                    + "</div>";
        } else {
            specialRequestHtml = "<span class=\"special-request-empty\">None specified</span>";
        }

        String actionsHtml;
        if ("PENDING".equals(status)) {
            actionsHtml = "<div style=\"display:flex; gap:6px;\">"
                    + "<button class=\"btn btn-sm btn-primary btn-accept-req\" data-id=\"A" + request.getRequestId() + "\">Accept</button>"
                    + "<button class=\"btn btn-sm btn-outline btn-deny-req\" data-id=\"D" + request.getRequestId()
                    + "\" style=\"color:var(--status-failed-text);\">Deny</button>"
                    + "</div>";
        } else {
            actionsHtml = "<span style=\"color:var(--color-text-muted); font-size:0.78rem;\">Decided "
                    + "<span class=\"badge " + statusBadgeClass + "\">" + escape(status) + "</span>"
                    + "</span>";
        }

        double totalPrice = request.getTotalPrice() == null ? 0.0 : request.getTotalPrice();

        return "<tr>"
                + "<td>"
                + "<strong style=\"color:var(--color-primary); font-size:0.95rem;\">" + escape(bookingRef) + "</strong><br>"
                + "<span class=\"badge " + statusBadgeClass + "\">" + escape(status) + "</span>"
                + "</td>"
                + "<td>"
                + "<div style=\"font-weight:600;\">" + escape(guestName) + "</div>"
                + "<small style=\"color:var(--color-text-muted);\">" + escape(guestPhone) + "</small>"
                + "</td>"
                + "<td>" + mealPlanHtml + "</td>"
                + "<td>"
                + "<div><strong>" + escape(checkIn) + "</strong> → <strong>" + escape(checkOut) + "</strong></div>"
                + "<small style=\"color:var(--color-text-muted); font-weight:600;\">" + request.getRoomsWanted() + " Room(s) Wanted</small>"
                + "</td>"
                + "<td>" + specialRequestHtml + "</td>"
                + "<td><strong style=\"color:var(--color-text-main); font-size:1rem;\">$"
                + String.format(Locale.US, "%.2f", totalPrice) + "</strong></td>"
                + "<td>" + actionsHtml + "</td>"
                + childHtml
                + "</tr>";
    }

    private static String formatDate(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private static String statusBadgeClass(String status) {
        return switch (status) {
            case "AVAILABLE" -> "badge-available";
            case "PENDING" -> "badge-pending";
            case "ACTIVE" -> "badge-active";
            case "BOOKED" -> "badge-booked";
            case "FAILED" -> "badge-failed";
            case "DENIED" -> "badge-denied";
            case "ACCEPTED" -> "badge-accepted";
            case "FINISHED" -> "badge-finished";
            case "CANCELED" -> "badge-canceled";
            default -> "badge-closed";
        };
    }

    private static String value(String value) {
        return value == null ? "" : value;
    }

    private static String value(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}
