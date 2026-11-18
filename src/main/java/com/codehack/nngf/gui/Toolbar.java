package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Toolbar extends NComponet {

    public Toolbar(String id) {
        super(id, "Toolbar");
    }

    public Toolbar(String id, NWindow window) {
        super(id, "Toolbar", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
