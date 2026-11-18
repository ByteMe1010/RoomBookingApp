package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Tooltip extends NComponet {

    public Tooltip(String id) {
        super(id, "Tooltip");
    }

    public Tooltip(String id, NWindow window) {
        super(id, "Tooltip", window);
    }

    public void setContent(NComponet c) {
        c.classList.add("NNGF-Tooltip-content");
        addChild(c);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<span>" + childHtml + "</span>";
    }
}
