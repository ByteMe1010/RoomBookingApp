package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class CompPaletteItem extends NComponet {

    public CompPaletteItem(String id, String label) {
        super(id, "CompPaletteItem");
        classList.add("comp-item");
        setAttribute("data-id", id);
        if (label != null) setInnerText(label);
    }

    public CompPaletteItem(String id, String label, NWindow window) {
        super(id, "CompPaletteItem", window);
        classList.add("comp-item");
        setAttribute("data-id", id);
        if (label != null) setInnerText(label);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
