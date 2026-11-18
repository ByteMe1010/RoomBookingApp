package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLDetailsElement;
import com.codehack.nngf.Core.exceptions.JsBridgeException;

public class Accordion extends NHTMLDetailsElement {
    private String title;

    public Accordion(String id) {
        super(id);
        this.elementName = "Accordion";
    }

    public Accordion(String id, String title) {
        super(id);
        this.elementName = "Accordion";
        this.title = title;
    }

    public Accordion(String id, NWindow window) {
        super(id, window);
        this.elementName = "Accordion";
    }

    public Accordion(String id, String title, NWindow window) {
        super(id, window);
        this.elementName = "Accordion";
        this.title = title;
    }

    public void setTitle(String t) {
        title = t;
        if (isAttached()) {
            try {
                executeJs(Js.id(ID).call("querySelector", "summary").prop("innerHTML", t).end());
            } catch (JsBridgeException e) {
                Logger.Error("Accordion.setTitle failed: " + e.getMessage(), 2);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String buildHtml(String childHtml) {
        String s = title != null ? title : "";
        return "<details><summary>" + s + "</summary>" + childHtml + "</details>";
    }
}
