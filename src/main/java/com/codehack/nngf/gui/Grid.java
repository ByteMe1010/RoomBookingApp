package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NWindow;

/**
 * A CSS grid container with a fluent API.
 *
 * <pre>{@code
 * Grid board = new Grid("board", window)
 *         .columns(4)
 *         .gap(12);
 * Grid.of(window, "board", 4).gap(12);   // shortcut
 * }</pre>
 */
public class Grid extends Panel {

    public Grid(String id) {
        super(id);
        this.elementName = "Grid";
        style.display("grid");
    }

    public Grid(String id, NWindow window) {
        super(id, window);
        this.elementName = "Grid";
        style.display("grid");
    }

    public static Grid of(NWindow window, String id) {
        return new Grid(id, window);
    }

    /** Create a grid with {@code columns} equal columns. */
    public static Grid of(NWindow window, String id, int columns) {
        return new Grid(id, window).columns(columns);
    }

    //region columns / rows
    public Grid columns(int count) {
        return columns("repeat(" + count + ", 1fr)");
    }

    public Grid columns(String template) {
        add(StyleBlock.of().style("grid-template-columns", template));
        return this;
    }

    public Grid rows(int count) {
        return rows("repeat(" + count + ", 1fr)");
    }

    public Grid rows(String template) {
        add(StyleBlock.of().style("grid-template-rows", template));
        return this;
    }

    public Grid templateAreas(String areas) {
        add(StyleBlock.of().style("grid-template-areas", areas));
        return this;
    }

    public Grid autoColumns(String value) {
        add(StyleBlock.of().style("grid-auto-columns", value));
        return this;
    }

    public Grid autoRows(String value) {
        add(StyleBlock.of().style("grid-auto-rows", value));
        return this;
    }

    public Grid autoFlow(AutoFlow flow) {
        add(StyleBlock.of().style("grid-auto-flow", flow.css()));
        return this;
    }
    //endregion

    //region alignment
    public Grid justifyItems(String value) {
        add(StyleBlock.of().style("justify-items", value));
        return this;
    }

    public Grid alignItems(String value) {
        add(StyleBlock.of().alignItems(value));
        return this;
    }

    public Grid justifyContent(String value) {
        add(StyleBlock.of().justifyContent(value));
        return this;
    }

    public Grid alignContent(String value) {
        add(StyleBlock.of().style("align-content", value));
        return this;
    }
    //endregion

    //region spacing
    public Grid gap(int px) {
        add(StyleBlock.of().gapPx(px));
        return this;
    }

    public Grid gap(int rowPx, int columnPx) {
        add(StyleBlock.of().style("row-gap", rowPx + "px").style("column-gap", columnPx + "px"));
        return this;
    }

    public Grid rowGap(int px) {
        add(StyleBlock.of().style("row-gap", px + "px"));
        return this;
    }

    public Grid columnGap(int px) {
        add(StyleBlock.of().style("column-gap", px + "px"));
        return this;
    }
    //endregion

    private void add(StyleBlock block) {
        styles.addStyle(block);
    }

    //region enum
    public enum AutoFlow {
        ROW("row"),
        COLUMN("column"),
        ROW_DENSE("row dense"),
        COLUMN_DENSE("column dense");

        private final String css;

        AutoFlow(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }
    //endregion
}
