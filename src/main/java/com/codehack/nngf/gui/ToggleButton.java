package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

/**
 * A toggle switch rendered as an {@code <input type="checkbox">}.
 * <p>
 * Using a checkbox (rather than a button) ensures the native {@code :checked} state is
 * available, which all NNGF themes style to draw the switch. It also emits real
 * {@code change} events and exposes the {@code checked} property.
 */
public class ToggleButton extends NHTMLInputElement {

    public ToggleButton(String id) {
        super(id);
        this.elementName = "ToggleButton";
        registerToggleProperties();
    }

    public ToggleButton(String id, String text) {
        super(id);
        this.elementName = "ToggleButton";
        if (text != null && !text.isBlank()) {
            setAttribute("aria-label", text);
        }
        registerToggleProperties();
    }

    public ToggleButton(String id, NWindow window) {
        super(id, window);
        this.elementName = "ToggleButton";
        registerToggleProperties();
    }

    public ToggleButton(String id, String text, NWindow window) {
        super(id, window);
        this.elementName = "ToggleButton";
        if (text != null && !text.isBlank()) {
            setAttribute("aria-label", text);
        }
        registerToggleProperties();
    }

    public static ToggleButton of(NWindow window, String id) {
        return new ToggleButton(id, window);
    }

    private void registerToggleProperties() {
        registerProperty(PropertyDescriptor.bool("selected", "Selected", false, this::setSelected, "Toggle", 2));
    }

    public boolean isSelected() {
        return checked();
    }

    public void setSelected(boolean selected) {
        checked(selected);
    }

    public void toggle() {
        setSelected(!isSelected());
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"checkbox\">";
    }
}