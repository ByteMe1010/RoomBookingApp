package com.codehack.nngf.js;

public class Range extends Prop {
    public Range(Context ctx) {
        super(ctx);
    }

    public Range(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Node StartContainer() {
        ctx.AddProperty("startContainer");
        return new Node(ctx);
    }

    public ValInt StartOffset() {
        ctx.AddProperty("startOffset");
        return new ValInt(ctx);
    }

    public Node EndContainer() {
        ctx.AddProperty("endContainer");
        return new Node(ctx);
    }

    public ValInt EndOffset() {
        ctx.AddProperty("endOffset");
        return new ValInt(ctx);
    }

    public ValBool Collapsed() {
        ctx.AddProperty("collapsed");
        return new ValBool(ctx);
    }

    public Node CommonAncestorContainer() {
        ctx.AddProperty("commonAncestorContainer");
        return new Node(ctx);
    }

    public SEnd setStart(Node node, int offset) {
        ctx.AddMethod("setStart", node, offset);
        return new SEnd(ctx);
    }

    public SEnd setEnd(Node node, int offset) {
        ctx.AddMethod("setEnd", node, offset);
        return new SEnd(ctx);
    }

    public SEnd setStartBefore(Node node) {
        ctx.AddMethod("setStartBefore", node);
        return new SEnd(ctx);
    }

    public SEnd setStartAfter(Node node) {
        ctx.AddMethod("setStartAfter", node);
        return new SEnd(ctx);
    }

    public SEnd setEndBefore(Node node) {
        ctx.AddMethod("setEndBefore", node);
        return new SEnd(ctx);
    }

    public SEnd setEndAfter(Node node) {
        ctx.AddMethod("setEndAfter", node);
        return new SEnd(ctx);
    }

    public SEnd collapse(boolean toStart) {
        ctx.AddMethod("collapse", toStart);
        return new SEnd(ctx);
    }

    public SEnd selectNode(Node node) {
        ctx.AddMethod("selectNode", node);
        return new SEnd(ctx);
    }

    public SEnd selectNodeContents(Node node) {
        ctx.AddMethod("selectNodeContents", node);
        return new SEnd(ctx);
    }

    public DocumentFragment cloneContents() {
        ctx.AddMethod("cloneContents");
        return new DocumentFragment(ctx);
    }

    public DocumentFragment extractContents() {
        ctx.AddMethod("extractContents");
        return new DocumentFragment(ctx);
    }

    public DocumentFragment deleteContents() {
        ctx.AddMethod("deleteContents");
        return new DocumentFragment(ctx);
    }

    public SEnd insertNode(Node node) {
        ctx.AddMethod("insertNode", node);
        return new SEnd(ctx);
    }

    public SEnd surroundContents(Node newParent) {
        ctx.AddMethod("surroundContents", newParent);
        return new SEnd(ctx);
    }

    public Range cloneRange() {
        ctx.AddMethod("cloneRange");
        return new Range(ctx);
    }

    public SEnd detach() {
        ctx.AddMethod("detach");
        return new SEnd(ctx);
    }

    public ValBool isPointInRange(Node node, int offset) {
        ctx.AddMethod("isPointInRange", node, offset);
        return new ValBool(ctx);
    }

    public ValInt comparePoint(Node node, int offset) {
        ctx.AddMethod("comparePoint", node, offset);
        return new ValInt(ctx);
    }

    public ValBool intersectsNode(Node node) {
        ctx.AddMethod("intersectsNode", node);
        return new ValBool(ctx);
    }
}
