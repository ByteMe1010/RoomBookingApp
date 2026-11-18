package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Badge extends NComponet {
    private String text = "";

    public Badge(String id) {
        super(id, "Badge");
    }

    public Badge(String id, String text) {
        super(id, "Badge");
        this.text = text;
    }

    public Badge(String id, NWindow window) {
        super(id, "Badge", window);
    }

    public Badge(String id, String text, NWindow window) {
        super(id, "Badge", window);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<span>" + text + childHtml + "</span>";
    }
}
