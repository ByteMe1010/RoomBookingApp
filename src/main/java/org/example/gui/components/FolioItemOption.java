package org.example.gui.components;

import com.codehack.nngf.Core.NComponet;
import org.example.Core.DatabaseModule.model.Item;
import org.unbescape.html.HtmlEscape;

import java.util.Locale;

public class FolioItemOption extends NComponet {
    private final Item item;

    public FolioItemOption(String id, Item item) {
        super(id, "option");
        this.item = item;
    }

    @Override
    public String buildHtml(String childHtml) {
        String method = item.getMeasuringMethod() == null ? "COUNT" : item.getMeasuringMethod().toUpperCase(Locale.ROOT);
        String unit = "COUNT".equalsIgnoreCase(method) ? "unit" : "metric";
        return "<option value=\"" + item.getItemId() + "\""
                + " data-method=\"" + normalizeMethod(method) + "\""
                + " data-stock=\"" + String.format(Locale.US, "%.2f", item.getStockAmount()) + "\">"
                + escape(item.getName()) + " ($" + money(item.getPricePerUnit()) + " / " + unit
                + ") [Stock: " + String.format(Locale.US, "%.2f", item.getStockAmount()) + "]</option>";
    }

    private static String normalizeMethod(String m) {
        if (m == null) return "COUNT";
        return m.trim().toUpperCase(Locale.ROOT);
    }

    private static String money(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static String escape(String value) {
        return HtmlEscape.escapeHtml5(value == null ? "" : value);
    }
}