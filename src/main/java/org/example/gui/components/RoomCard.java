package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Room;
import org.unbescape.html.HtmlEscape;

import java.util.Locale;

public class RoomCard extends NComponet {
    private final Room room;
    private final String status;

    public RoomCard(String id, Room room, String status) {
        super(id, "div");
        this.room = room;
        this.status = status == null ? "AVAILABLE" : status.toUpperCase(Locale.ROOT);
    }

    @Override
    public String buildHtml(String childHtml) {
        String statusClass, badgeClass, action;
        String safeStatus = status;
        switch (safeStatus) {
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
                safeStatus = "AVAILABLE";
                statusClass = "status-available";
                badgeClass = "badge-available";
                action = "<button class=\"btn btn-sm btn-outline btn-toggle-room-status\" data-id=\""
                        + room.getRoomId() + "\" data-status=\"AVAILABLE\">Set Closed</button>";
                break;
        }
        return "<div class=\"room-card " + statusClass + "\">"
                + "<div><div class=\"room-card-header\"><span class=\"room-number-lg\">Room " + escape(room.getRoomNumber()) + "</span>"
                + "<span class=\"badge " + badgeClass + "\">" + safeStatus + "</span></div>"
                + "<div class=\"room-type-text\">" + escape(room.getRoomType()) + "</div></div>"
                + "<div class=\"room-card-footer\"><div class=\"room-price-val\">$"
                + money(room.getPricePerNight()) + " <span>/ night</span></div>" + action + "</div></div>";
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}