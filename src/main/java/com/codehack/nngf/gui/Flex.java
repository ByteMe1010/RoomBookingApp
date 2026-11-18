package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NWindow;

/**
 * A CSS flexbox container with a fluent API.
 *
 * <pre>{@code
 * Flex row = new Flex("toolbar", window)
 *         .direction(Flex.Direction.ROW)
 *         .justifyContent(Flex.Justify.SPACE_BETWEEN)
 *         .alignItems(Flex.Align.CENTER)
 *         .gap(8);
 * }</pre>
 *
 * Every setter works both before and after the component is attached: before attach the
 * style is merged into the document stylesheet, after attach it is applied live.
 */
public class Flex extends Panel {

    public Flex(String id) {
        super(id);
        this.elementName = "Flex";
        style.display("flex");
    }

    public Flex(String id, NWindow window) {
        super(id, window);
        this.elementName = "Flex";
        style.display("flex");
    }

    public static Flex of(NWindow window, String id) {
        return new Flex(id, window);
    }

    //region direction
    public Flex direction(Direction direction) {
        return direction(direction.css());
    }

    public Flex direction(String value) {
        add(StyleBlock.of().flexDirection(value));
        return this;
    }
    //endregion

    //region wrap
    public Flex wrap(Wrap wrap) {
        add(StyleBlock.of().style("flex-wrap", wrap.css()));
        return this;
    }

    public Flex wrap() {
        return wrap(Wrap.WRAP);
    }

    public Flex noWrap() {
        return wrap(Wrap.NOWRAP);
    }

    public Flex wrapReverse() {
        return wrap(Wrap.WRAP_REVERSE);
    }
    //endregion

    //region alignment
    public Flex justifyContent(Justify value) {
        return justifyContent(value.css());
    }

    public Flex justifyContent(String value) {
        add(StyleBlock.of().justifyContent(value));
        return this;
    }

    public Flex alignItems(Align value) {
        return alignItems(value.css());
    }

    public Flex alignItems(String value) {
        add(StyleBlock.of().alignItems(value));
        return this;
    }

    public Flex alignContent(AlignContent value) {
        return alignContent(value.css());
    }

    public Flex alignContent(String value) {
        add(StyleBlock.of().style("align-content", value));
        return this;
    }

    /** Center children on both axes. */
    public Flex center() {
        return justifyContent(Justify.CENTER).alignItems(Align.CENTER);
    }
    //endregion

    //region spacing
    public Flex gap(int px) {
        add(StyleBlock.of().gapPx(px));
        return this;
    }

    public Flex gap(int rowPx, int columnPx) {
        add(StyleBlock.of().style("row-gap", rowPx + "px").style("column-gap", columnPx + "px"));
        return this;
    }

    public Flex rowGap(int px) {
        add(StyleBlock.of().style("row-gap", px + "px"));
        return this;
    }

    public Flex columnGap(int px) {
        add(StyleBlock.of().style("column-gap", px + "px"));
        return this;
    }
    //endregion

    /** Make this container fill its parent's height (common for full-page shells). */
    public Flex fullHeight() {
        add(StyleBlock.of().height("100%"));
        return this;
    }

    private void add(StyleBlock block) {
        styles.addStyle(block);
    }

    //region enums
    public enum Direction {
        ROW("row"),
        ROW_REVERSE("row-reverse"),
        COLUMN("column"),
        COLUMN_REVERSE("column-reverse");

        private final String css;

        Direction(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }

    public enum Wrap {
        NOWRAP("nowrap"),
        WRAP("wrap"),
        WRAP_REVERSE("wrap-reverse");

        private final String css;

        Wrap(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }

    public enum Justify {
        START("flex-start"),
        END("flex-end"),
        CENTER("center"),
        SPACE_BETWEEN("space-between"),
        SPACE_AROUND("space-around"),
        SPACE_EVENLY("space-evenly");

        private final String css;

        Justify(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }

    public enum Align {
        START("flex-start"),
        END("flex-end"),
        CENTER("center"),
        BASELINE("baseline"),
        STRETCH("stretch");

        private final String css;

        Align(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }

    public enum AlignContent {
        START("flex-start"),
        END("flex-end"),
        CENTER("center"),
        SPACE_BETWEEN("space-between"),
        SPACE_AROUND("space-around"),
        STRETCH("stretch");

        private final String css;

        AlignContent(String css) {
            this.css = css;
        }

        public String css() {
            return css;
        }
    }
    //endregion
}
