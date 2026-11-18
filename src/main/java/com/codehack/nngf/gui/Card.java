package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Card extends NComponet {

    public Card(String id) {
        super(id, "Card");
    }

    public Card(String id, NWindow window) {
        super(id, "Card", window);
    }

    public static Card of(NWindow window, String id) {
        return new Card(id, window);
    }

    public void setHeader(NComponet c) {
        c.classList.add("NNGF-Card-header");
        addChild(c);
    }

    public void setHeader(String text) {
        Label lbl = new Label(ID + "-hdr", text);
        lbl.classList.add("NNGF-Card-header");
        addChild(lbl);
    }

    public void setFooter(NComponet c) {
        c.classList.add("NNGF-Card-footer");
        addChild(c);
    }

    public void setFooter(String text) {
        Label lbl = new Label(ID + "-ftr", text);
        lbl.classList.add("NNGF-Card-footer");
        addChild(lbl);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
