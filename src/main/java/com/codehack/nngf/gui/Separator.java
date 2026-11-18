package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Separator extends NComponet {

    public Separator(String id) {
        super(id, "Separator");
    }

    public Separator(String id, NWindow window) {
        super(id, "Separator", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<hr>";
    }
}
