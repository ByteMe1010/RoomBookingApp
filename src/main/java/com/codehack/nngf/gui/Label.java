package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLLabelElement;

public class Label extends NHTMLLabelElement {

    public Label(String id) {
        super(id);
        this.elementName = "Label";
        registerUserProperties();
    }

    public Label(String id, NWindow window) {
        super(id, window);
        this.elementName = "Label";
        registerUserProperties();
    }

    public Label(String id, String text) {
        super(id);
        this.elementName = "Label";
        setInnerText(text);
        registerUserProperties();
    }

    public Label(String id, String text, NWindow window) {
        super(id, window);
        this.elementName = "Label";
        setInnerText(text);
        registerUserProperties();
    }

    public static Label of(NWindow window, String id) {
        return new Label(id, window);
    }

    private void registerUserProperties() {
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::setInnerText, "Content", 2));
    }
}
