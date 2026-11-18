package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Sidebar extends NComponet {

    public Sidebar(String id) {
        super(id, "Sidebar");
    }

    public Sidebar(String id, NWindow window) {
        super(id, "Sidebar", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<nav>" + childHtml + "</nav>";
    }
}
