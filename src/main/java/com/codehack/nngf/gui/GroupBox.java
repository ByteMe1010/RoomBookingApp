package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class GroupBox extends NComponet {

    public GroupBox(String id) {
        super(id, "GroupBox");
    }

    public GroupBox(String id, NWindow window) {
        super(id, "GroupBox", window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<fieldset>" + childHtml + "</fieldset>";
    }
}
