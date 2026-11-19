package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.unbescape.html.HtmlEscape;

public class TelegramCustomerRow extends NComponet {
    private final int customerId;
    private final String name;
    private final String phone;
    private final boolean linked;
    private final long chatId;
    private final int timesDenied;
    private final String registered;

    public TelegramCustomerRow(String id, int customerId, String name, String phone, boolean linked, long chatId,
                              int timesDenied, String registered) {
        super(id, "tr");
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.linked = linked;
        this.chatId = chatId;
        this.timesDenied = timesDenied;
        this.registered = registered;
    }

    @Override
    public String buildHtml(String childHtml) {
        String statusBadge = linked
                ? "<span class=\"badge badge-success\">Linked</span>"
                : "<span class=\"badge badge-muted\">Not registered</span>";
        String chatCell = linked ? String.valueOf(chatId) : "-";
        String actions = linked
                ? "<button class=\"btn btn-outline btn-sm btn-send-tg\" data-id=\"" + customerId + "\" data-chat=\"" + chatId + "\">Send Message</button>"
                : "<span style=\"color:var(--color-text-muted);font-size:0.8rem;\">-</span>";
        return "<tr>"
                + "<td>" + customerId + "</td>"
                + "<td>" + escape(name) + "<br><small>" + escape(phone) + "</small></td>"
                + "<td>" + statusBadge + "</td>"
                + "<td>" + chatCell + "</td>"
                + "<td>" + timesDenied + "</td>"
                + "<td>" + escape(registered) + "</td>"
                + "<td>" + actions + "</td>"
                + childHtml
                + "</tr>";
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}