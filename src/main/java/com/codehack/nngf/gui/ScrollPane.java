package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class ScrollPane extends NComponet {

    public ScrollPane(String id) {
        super(id, "ScrollPane");
    }

    public ScrollPane(String id, NWindow window) {
        super(id, "ScrollPane", window);
    }

    public static ScrollPane of(NWindow window, String id) {
        return new ScrollPane(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
