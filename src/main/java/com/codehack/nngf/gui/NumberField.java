package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

public class NumberField extends NHTMLInputElement {

    public NumberField(String id) {
        super(id);
        this.elementName = "NumberField";
        registerProperty(PropertyDescriptor.number("min", "Min", "", this::min, "Number", 2));
        registerProperty(PropertyDescriptor.number("max", "Max", "", this::max, "Number", 2));
        registerProperty(PropertyDescriptor.number("step", "Step", "1", this::step, "Number", 2));
    }

    public NumberField(String id, Number value) {
        super(id);
        this.elementName = "NumberField";
        value(String.valueOf(value));
        registerProperty(PropertyDescriptor.number("min", "Min", "", this::min, "Number", 2));
        registerProperty(PropertyDescriptor.number("max", "Max", "", this::max, "Number", 2));
        registerProperty(PropertyDescriptor.number("step", "Step", "1", this::step, "Number", 2));
    }

    public NumberField(String id, NWindow window) {
        super(id, window);
        this.elementName = "NumberField";
        registerProperty(PropertyDescriptor.number("min", "Min", "", this::min, "Number", 2));
        registerProperty(PropertyDescriptor.number("max", "Max", "", this::max, "Number", 2));
        registerProperty(PropertyDescriptor.number("step", "Step", "1", this::step, "Number", 2));
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"number\">";
    }
}
