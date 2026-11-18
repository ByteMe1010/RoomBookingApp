package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class EditableLabel extends NComponet {
    private String text = "";

    public EditableLabel(String id) {
        super(id, "EditableLabel");
        setAttribute("contenteditable", "true");
        registerUserProperties();
    }

    public EditableLabel(String id, String text) {
        super(id, "EditableLabel");
        this.text = text;
        setAttribute("contenteditable", "true");
        registerUserProperties();
    }

    public EditableLabel(String id, NWindow window) {
        super(id, "EditableLabel", window);
        setAttribute("contenteditable", "true");
        registerUserProperties();
    }

    public EditableLabel(String id, String text, NWindow window) {
        super(id, "EditableLabel", window);
        this.text = text;
        setAttribute("contenteditable", "true");
        registerUserProperties();
    }

    private void registerUserProperties() {
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::value, "Content", 2));
    }

    public String value() {
        return text;
    }

    public void value(String v) {
        this.text = v;
        if (isAttached()) {
            executeChildJs(Js.id(ID).call("replaceChildren", v).end());
        }
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<span>" + text + childHtml + "</span>";
    }
}
