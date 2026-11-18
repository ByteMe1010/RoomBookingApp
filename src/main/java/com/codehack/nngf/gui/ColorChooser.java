package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;

public class ColorChooser extends TextField {

    public ColorChooser(String id) {
        super(id);
        this.elementName = "ColorChooser";
        setAttribute("type", "color");
        setAttribute("value", "#000000");
    }

    public ColorChooser(String id, String color) {
        super(id);
        this.elementName = "ColorChooser";
        setAttribute("type", "color");
        setAttribute("value", color);
    }

    public ColorChooser(String id, NWindow window) {
        super(id, window);
        this.elementName = "ColorChooser";
        setAttribute("type", "color");
        setAttribute("value", "#000000");
    }

    public ColorChooser(String id, String color, NWindow window) {
        super(id, window);
        this.elementName = "ColorChooser";
        setAttribute("type", "color");
        setAttribute("value", color);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"color\">";
    }
}
