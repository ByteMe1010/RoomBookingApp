package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class Div extends NComponet {


    public Div(String ID) {
        super(ID, "div");
    }
    public Div(String ID,String innerText) {
        super(ID, "div");
        setInnerText(innerText);
    }

    public Div(String ID, NWindow window) {
        super(ID, "div", window);
    }

    public static Div of(NWindow window, String id) {
        return new Div(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
