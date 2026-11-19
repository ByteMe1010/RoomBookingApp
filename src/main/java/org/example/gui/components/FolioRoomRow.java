package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Room;
import org.unbescape.html.HtmlEscape;

public class FolioRoomRow extends NComponet {
    private final Room room;
    private final double rate;
    private final long nights;
    private final double subtotal;

    public FolioRoomRow(String id, Room room, double rate, long nights, double subtotal) {
        super(id, "tr");
        this.room = room;
        this.rate = rate;
        this.nights = nights;
        this.subtotal = subtotal;
    }

    @Override
    public String buildHtml(String childHtml) {
        String roomNumber = (room == null) ? "Unknown" : escape(room.getRoomNumber());
        String roomType = (room == null) ? "" : " (" + escape(room.getRoomType()) + ")";
        return "<tr>"
                + "<td><strong>Room " + roomNumber + "</strong>" + roomType + "</td>"
                + "<td>$" + money(rate) + "/night</td>"
                + "<td>" + nights + " night(s)</td>"
                + "<td style=\"text-align:right;font-weight:700;\">$" + money(subtotal) + "</td>"
                + childHtml
                + "</tr>";
    }

    private static String money(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}