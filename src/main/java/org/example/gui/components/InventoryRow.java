package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Item;
import org.unbescape.html.HtmlEscape;

import java.util.Locale;

public class InventoryRow extends NComponet {
    private final Item item;
    private final String method;
    private final boolean lowStock;

    public InventoryRow(String id, Item item, String method, boolean lowStock) {
        super(id, "tr");
        this.item = item;
        this.method = method;
        this.lowStock = lowStock;
    }

    @Override
    public String buildHtml(String childHtml) {
        boolean active = item.isActive();
        String rowStyle = active ? "" : "style=\"opacity:0.7;background-color:#fafafa;\"";
        String methodBadge = "COUNT".equals(method)
                ? "<span class=\"badge badge-count\">COUNT (Units)</span>"
                : "<span class=\"badge badge-continuous\">CONTINUOUS (kg / L)</span>";
        String stockDisplay = lowStock
                ? "<strong style=\"color:var(--status-failed-text);\">" + money(item.getStockAmount()) + " ⚠ Low Stock</strong>"
                : "<strong>" + money(item.getStockAmount()) + "</strong>";
        String activeToggle = active
                ? "<button class=\"toggle-switch-btn active btn-toggle-item-active\" data-id=\"" + item.getItemId()
                + "\" data-active=\"1\" title=\"Archive item\"><span class=\"toggle-dot active\"></span> Active</button>"
                : "<button class=\"toggle-switch-btn inactive btn-toggle-item-active\" data-id=\"" + item.getItemId()
                + "\" data-active=\"0\" title=\"Reactivate item\"><span class=\"toggle-dot inactive\"></span> Inactive</button>";
        String archivedBadge = active ? "" : " <span class=\"badge badge-closed\" style=\"margin-left:6px;font-size:0.7rem;\">Archived</span>";
        return "<tr " + rowStyle + ">"
                + "<td>#" + item.getItemId() + "</td>"
                + "<td><strong>" + escape(item.getName()) + "</strong>" + archivedBadge + "</td>"
                + "<td>" + methodBadge + "</td>"
                + "<td>$" + money(item.getPricePerUnit()) + "</td>"
                + "<td>" + stockDisplay + "</td>"
                + "<td>" + activeToggle + "</td>"
                + "<td><button class=\"btn btn-sm btn-primary btn-restock-item\" "
                + "data-id=\"" + item.getItemId() + "\" "
                + "data-name=\"" + escape(item.getName()) + "\" "
                + "data-stock=\"" + money(item.getStockAmount()) + "\" "
                + "data-method=\"" + method + "\" "
                + "data-price=\"" + money(item.getPricePerUnit()) + "\">Restock</button></td>"
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