package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Panel extends NComponet {

    public Panel(String id) {
        super(id, "Panel");
    }

    public Panel(String id, NWindow window) {
        super(id, "Panel", window);
    }

    public static Panel of(NWindow window, String id) {
        return new Panel(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
