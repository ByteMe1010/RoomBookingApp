package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

public class CheckBox extends NHTMLInputElement {

    public CheckBox(String id) {
        super(id);
        this.elementName = "CheckBox";
        registerProperty(PropertyDescriptor.bool("checked", "Checked", false, this::checked, "CheckBox", 2));
    }

    public CheckBox(String id, boolean checked) {
        super(id);
        this.elementName = "CheckBox";
        checked(checked);
        registerProperty(PropertyDescriptor.bool("checked", "Checked", checked, this::checked, "CheckBox", 2));
    }

    public CheckBox(String id, NWindow window) {
        super(id, window);
        this.elementName = "CheckBox";
        registerProperty(PropertyDescriptor.bool("checked", "Checked", false, this::checked, "CheckBox", 2));
    }

    public static CheckBox of(NWindow window, String id) {
        return new CheckBox(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"checkbox\">";
    }
}
