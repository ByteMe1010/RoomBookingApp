package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Breadcrumbs extends NComponet {

    public Breadcrumbs(String id) {
        super(id, "Breadcrumbs");
    }

    public Breadcrumbs(String id, NWindow window) {
        super(id, "Breadcrumbs", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<nav>" + childHtml + "</nav>";
    }
}
