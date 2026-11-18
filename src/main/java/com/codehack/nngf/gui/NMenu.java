package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class NMenu extends NComponet {

    public NMenu(String id) {
        super(id, "NMenu");
    }

    public NMenu(String id, NWindow window) {
        super(id, "NMenu", window);
    }

    public static NMenu of(String id, NWindow window) {
        return new NMenu(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
