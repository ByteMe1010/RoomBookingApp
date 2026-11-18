package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLSelectElement;

public class ComboBox extends NHTMLSelectElement {

    public ComboBox(String id) {
        super(id);
        this.elementName = "ComboBox";
    }

    public ComboBox(String id, NWindow window) {
        super(id, window);
        this.elementName = "ComboBox";
    }

    public static ComboBox of(NWindow window, String id) {
        return new ComboBox(id, window);
    }

    public ComboBox addOption(String text) {
        return addOption(text, text, false);
    }

    public ComboBox addOption(String text, boolean selected) {
        return addOption(text, text, selected);
    }

    public ComboBox addOption(String value, String text) {
        return addOption(value, text, false);
    }

    public ComboBox addOption(String value, String text, boolean selected) {
        StringBuilder sb = new StringBuilder();
        sb.append("<option");
        sb.append(" value=\"").append(escapeHtml(value)).append("\"");
        if (selected) sb.append(" selected");
        sb.append(">").append(escapeHtml(text)).append("</option>");
        appendInnerHTML(sb.toString());
        return this;
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
