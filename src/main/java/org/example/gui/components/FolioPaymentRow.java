package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Payment;
import org.unbescape.html.HtmlEscape;

public class FolioPaymentRow extends NComponet {
    private final Payment payment;
    private final String paidAt;

    public FolioPaymentRow(String id, Payment payment, String paidAt) {
        super(id, "tr");
        this.payment = payment;
        this.paidAt = paidAt;
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<tr>"
                + "<td>Payment #" + payment.getPaymentId() + "</td>"
                + "<td><span class=\"badge badge-active\">" + escape(payment.getMethod()) + "</span></td>"
                + "<td><small style=\"color:var(--color-text-muted);\">" + escape(paidAt) + "</small></td>"
                + "<td style=\"text-align:right;font-weight:700;color:#059669;\">-$" + money(payment.getAmount()) + "</td>"
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