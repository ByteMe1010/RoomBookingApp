package com.codehack.nngf.js;

public class Selection extends Prop {
    public Selection(Context ctx) {
        super(ctx);
    }

    public Selection(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Prop AnchorNode() {
        ctx.AddProperty("anchorNode");
        return new Prop(ctx);
    }

    public ValInt AnchorOffset() {
        ctx.AddProperty("anchorOffset");
        return new ValInt(ctx);
    }

    public Prop FocusNode() {
        ctx.AddProperty("focusNode");
        return new Prop(ctx);
    }

    public ValInt FocusOffset() {
        ctx.AddProperty("focusOffset");
        return new ValInt(ctx);
    }

    public ValBool IsCollapsed() {
        ctx.AddProperty("isCollapsed");
        return new ValBool(ctx);
    }

    public ValInt RangeCount() {
        ctx.AddProperty("rangeCount");
        return new ValInt(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public SEnd addRange(Range range) {
        ctx.AddMethod("addRange", range);
        return new SEnd(ctx);
    }

    public SEnd removeAllRanges() {
        ctx.AddMethod("removeAllRanges");
        return new SEnd(ctx);
    }

    public Range getRangeAt(int index) {
        ctx.AddMethod("getRangeAt", index);
        return new Range(ctx);
    }

    public SEnd collapse(Node node, int offset) {
        ctx.AddMethod("collapse", node, offset);
        return new SEnd(ctx);
    }

    public SEnd collapseToStart() {
        ctx.AddMethod("collapseToStart");
        return new SEnd(ctx);
    }

    public SEnd collapseToEnd() {
        ctx.AddMethod("collapseToEnd");
        return new SEnd(ctx);
    }

    public SEnd selectAllChildren(Node node) {
        ctx.AddMethod("selectAllChildren", node);
        return new SEnd(ctx);
    }

    public SEnd deleteFromDocument() {
        ctx.AddMethod("deleteFromDocument");
        return new SEnd(ctx);
    }

    public SEnd setPosition(Node node, int offset) {
        ctx.AddMethod("setPosition", node, offset);
        return new SEnd(ctx);
    }

    public SEnd setBaseAndExtent(Node anchorNode, int anchorOffset, Node focusNode, int focusOffset) {
        ctx.AddMethod("setBaseAndExtent", anchorNode, anchorOffset, focusNode, focusOffset);
        return new SEnd(ctx);
    }
}
