package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NWindow;

/**
 * Vertical flex container (column direction).
 * Use {@code gap(...)} to space children and the inherited style helpers to align them.
 */
public class VBox extends Panel {

    public VBox(String id) {
        super(id);
        this.elementName = "VBox";
        style.display("flex").flexDirection("column");
    }

    public VBox(String id, NWindow window) {
        super(id, window);
        this.elementName = "VBox";
        style.display("flex").flexDirection("column");
    }

    public VBox(String id, int gap) {
        this(id);
        gap(gap);
    }

    public VBox(String id, int gap, NWindow window) {
        this(id, window);
        gap(gap);
    }

    public static VBox of(NWindow window, String id) {
        return new VBox(id, window);
    }

    public VBox gap(int px) {
        styles.addStyle(StyleBlock.of().gapPx(px));
        return this;
    }

    public VBox alignItems(String value) {
        styles.addStyle(StyleBlock.of().alignItems(value));
        return this;
    }

    public VBox justifyContent(String value) {
        styles.addStyle(StyleBlock.of().justifyContent(value));
        return this;
    }

    /** Stretch children across the full width of the column. */
    public VBox stretch() {
        return alignItems("stretch");
    }
}
