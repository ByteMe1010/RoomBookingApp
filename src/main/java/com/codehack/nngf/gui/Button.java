package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLButtonElement;

public class Button extends NHTMLButtonElement {

    public Button(String id) {
        super(id);
        this.elementName = "Button";
        registerUserProperties();
    }

    public Button(String id, NWindow window) {
        super(id, window);
        this.elementName = "Button";
        registerUserProperties();
    }

    public Button(String id, String text) {
        super(id);
        this.elementName = "Button";
        setInnerText(text);
        registerUserProperties();
    }

    public Button(String id, String text, NWindow window) {
        super(id, window);
        this.elementName = "Button";
        setInnerText(text);
        registerUserProperties();
    }

    public static Button of(NWindow window, String id) {
        return new Button(id, window);
    }

    private void registerUserProperties() {
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::setInnerText, "Content", 2));
    }
}
