package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;

public class Layouts {

    // ============================
    //  FLEX BOX
    // ============================

    public static StyleBlock HBox() {
        return StyleBlock.of().display("flex").flexDirection("row").alignItems("center");
    }

    public static StyleBlock HBox(int gap) {
        return HBox().gapPx(gap);
    }

    public static StyleBlock VBox() {
        return StyleBlock.of().display("flex").flexDirection("column");
    }

    public static StyleBlock VBox(int gap) {
        return VBox().gapPx(gap);
    }

    public static StyleBlock Center() {
        return StyleBlock.of().display("flex").justifyContent("center").alignItems("center");
    }

    public static StyleBlock Between() {
        return StyleBlock.of().display("flex").justifyContent("space-between").alignItems("center");
    }

    public static StyleBlock Around() {
        return StyleBlock.of().display("flex").justifyContent("space-around").alignItems("center");
    }

    public static StyleBlock Evenly() {
        return StyleBlock.of().display("flex").justifyContent("space-evenly").alignItems("center");
    }

    public static StyleBlock Wrap() {
        return StyleBlock.of().display("flex").style("flex-wrap", "wrap");
    }

    public static StyleBlock Wrap(int gap) {
        return Wrap().gapPx(gap);
    }

    // ============================
    //  GRID
    // ============================

    public static StyleBlock Grid(int cols) {
        return StyleBlock.of().display("grid")
                .style("grid-template-columns", "repeat(" + cols + ", 1fr)");
    }

    public static StyleBlock Grid(int cols, int gap) {
        return Grid(cols).gapPx(gap);
    }

    // ============================
    //  SURFACE / CARD
    // ============================

    public static StyleBlock Card() {
        return StyleBlock.of()
                .border("1px solid var(--nngf-border)")
                .borderRadius("var(--nngf-radius)")
                .backgroundColor("var(--nngf-bg)")
                .padding("16px");
    }

    public static StyleBlock Elevated() {
        return Card().boxShadow("var(--nngf-shadow-md)");
    }

    // ============================
    //  FLEX ITEM HELPERS
    // ============================

    public static StyleBlock Flex1() {
        return StyleBlock.of().flex("1");
    }

    public static StyleBlock FlexGrow(int grow) {
        return StyleBlock.of().style("flex-grow", String.valueOf(grow));
    }

    public static StyleBlock FlexShrink(int shrink) {
        return StyleBlock.of().style("flex-shrink", String.valueOf(shrink));
    }

    // ============================
    //  TEXT HELPERS
    // ============================

    public static StyleBlock Truncate() {
        return StyleBlock.of()
                .overflow("hidden")
                .style("text-overflow", "ellipsis")
                .style("white-space", "nowrap");
    }

    public static StyleBlock Muted() {
        return StyleBlock.of()
                .color("var(--nngf-text-secondary)")
                .fontSize("12px");
    }

    public static StyleBlock TextCenter() {
        return StyleBlock.of().textAlign("center");
    }

    // ============================
    //  SPACING
    // ============================

    public static StyleBlock Gap(int px) {
        return StyleBlock.of().gapPx(px);
    }

    public static StyleBlock Padding(int px) {
        return StyleBlock.of().paddingPx(px);
    }

    public static StyleBlock Margin(int px) {
        return StyleBlock.of().marginPx(px);
    }

    // ============================
    //  POSITION
    // ============================

    public static StyleBlock Relative() {
        return StyleBlock.of().position("relative");
    }

    public static StyleBlock Absolute() {
        return StyleBlock.of().position("absolute");
    }

    public static StyleBlock Fixed() {
        return StyleBlock.of().position("fixed");
    }

    // ============================
    //  SIZING / SCROLL
    // ============================

    public static StyleBlock FullWidth() {
        return StyleBlock.of().width("100%");
    }

    public static StyleBlock FullHeight() {
        return StyleBlock.of().height("100%");
    }

    public static StyleBlock Scrollable() {
        return StyleBlock.of().overflow("auto");
    }

    public static StyleBlock ScrollY() {
        return StyleBlock.of().style("overflow-y", "auto");
    }

    public static StyleBlock ScrollX() {
        return StyleBlock.of().style("overflow-x", "auto");
    }
}
