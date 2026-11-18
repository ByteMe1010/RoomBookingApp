package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

public class Slider extends NHTMLInputElement {

    public Slider(String id) {
        super(id);
        this.elementName = "Slider";
        registerSliderProperties();
    }

    public Slider(String id, int min, int max, int value) {
        super(id);
        this.elementName = "Slider";
        min(String.valueOf(min));
        max(String.valueOf(max));
        value(String.valueOf(value));
        registerSliderProperties();
    }

    public Slider(String id, NWindow window) {
        super(id, window);
        this.elementName = "Slider";
        registerSliderProperties();
    }

    private void registerSliderProperties() {
        registerProperty(PropertyDescriptor.number("min", "Min", "0", this::min, "Slider", 2));
        registerProperty(PropertyDescriptor.number("max", "Max", "100", this::max, "Slider", 2));
        registerProperty(PropertyDescriptor.number("step", "Step", "1", this::step, "Slider", 2));
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"range\">";
    }
}
