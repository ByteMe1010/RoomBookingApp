package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NWindow;

/**
 * Horizontal flex container (row direction, vertically centered by default).
 */
public class HBox extends Panel {

    public HBox(String id) {
        super(id);
        this.elementName = "HBox";
        style.display("flex").flexDirection("row").alignItems("center");
    }

    public HBox(String id, NWindow window) {
        super(id, window);
        this.elementName = "HBox";
        style.display("flex").flexDirection("row").alignItems("center");
    }

    public HBox(String id, int gap) {
        this(id);
        gap(gap);
    }

    public HBox(String id, int gap, NWindow window) {
        this(id, window);
        gap(gap);
    }

    public static HBox of(NWindow window, String id) {
        return new HBox(id, window);
    }

    public HBox gap(int px) {
        styles.addStyle(StyleBlock.of().gapPx(px));
        return this;
    }

    public HBox alignItems(String value) {
        styles.addStyle(StyleBlock.of().alignItems(value));
        return this;
    }

    public HBox justifyContent(String value) {
        styles.addStyle(StyleBlock.of().justifyContent(value));
        return this;
    }
}
