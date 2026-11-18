package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Spinner extends NComponet {

    public Spinner(String id) {
        super(id, "Spinner");
    }

    public Spinner(String id, NWindow window) {
        super(id, "Spinner", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div class=\"NNGF-Spinner\"><div class=\"NNGF-Spinner-ring\"></div></div>";
    }
}
