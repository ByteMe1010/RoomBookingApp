package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Message;
import org.unbescape.html.HtmlEscape;

public class TelegramMessageRow extends NComponet {
    private final Message message;
    private final String customerName;

    public TelegramMessageRow(String id, Message message, String customerName) {
        super(id, "tr");
        this.message = message;
        this.customerName = customerName;
    }

    @Override
    public String buildHtml(String childHtml) {
        String status = message.getStatus() == null ? "" : message.getStatus().toUpperCase();
        String badge = "SENT".equals(status)
                ? "<span class=\"badge badge-success\">SENT</span>"
                : "<span class=\"badge badge-failed\">FAILED</span>";
        String sentAt = message.getSentAt() == null ? "-" : message.getSentAt().toString();
        return "<tr>"
                + "<td>" + message.getMessageId() + "</td>"
                + "<td>" + escape(customerName) + "</td>"
                + "<td>" + badge + "</td>"
                + "<td>" + escape(message.getContent()) + "</td>"
                + "<td>" + escape(sentAt) + "</td>"
                + childHtml
                + "</tr>";
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}