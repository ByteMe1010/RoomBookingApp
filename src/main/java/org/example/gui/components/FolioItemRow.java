package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Item;
import org.example.Core.DatabaseModule.model.ItemOrder;
import org.unbescape.html.HtmlEscape;

import java.util.Locale;

public class FolioItemRow extends NComponet {
    private final ItemOrder order;
    private final Item item;
    private final String method;
    private final double subtotal;
    private final boolean showCancel;

    public FolioItemRow(String id, ItemOrder order, Item item, String method, double subtotal, boolean showCancel) {
        super(id, "tr");
        this.order = order;
        this.item = item;
        this.method = method;
        this.subtotal = subtotal;
        this.showCancel = showCancel;
    }

    @Override
    public String buildHtml(String childHtml) {
        String itemName = (item == null) ? "Unknown Item" : escape(item.getName());
        String badgeClass = "COUNT".equals(method) ? "badge-count" : "badge-continuous";
        String cancelBtn = "";
        if (showCancel) {
            cancelBtn = "<button id=\"folio-cancel-order-" + order.getOrderId()
                    + "\" class=\"btn btn-sm btn-outline btn-delete-folio-order\" data-order-id=\""
                    + order.getOrderId() + "\" style=\"padding:2px 6px;color:#ef4444;\" title=\"Cancel Order & Restock\">&times;</button>";
        }
        return "<tr>"
                + "<td><strong>" + itemName + "</strong> <span class=\"badge " + badgeClass + "\">" + escape(method) + "</span></td>"
                + "<td>$" + money(order.getUnitPrice()) + "</td>"
                + "<td>" + String.format(Locale.US, "%.2f", order.getAmount()) + "</td>"
                + "<td style=\"text-align:right;font-weight:700;\">$" + money(subtotal) + "</td>"
                + "<td style=\"text-align:right;\">" + cancelBtn + "</td>"
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