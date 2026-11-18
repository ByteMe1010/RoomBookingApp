package com.codehack.nngf.js;

public class DocumentFragment extends Node {
    public DocumentFragment(Context ctx) {
        super(ctx);
    }

    public DocumentFragment(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Element querySelector(String selector) {
        ctx.AddMethod("querySelector", selector);
        return new Element(ctx);
    }

    public NodeList querySelectorAll(String selector) {
        ctx.AddMethod("querySelectorAll", selector);
        return new NodeList(ctx);
    }

    public Element getElementById(String id) {
        ctx.AddMethod("getElementById", id);
        return new Element(ctx);
    }

    public HTMLCollection Children() {
        ctx.AddProperty("children");
        return new HTMLCollection(ctx);
    }

    public Element FirstElementChild() {
        ctx.AddProperty("firstElementChild");
        return new Element(ctx);
    }

    public Element LastElementChild() {
        ctx.AddProperty("lastElementChild");
        return new Element(ctx);
    }

    public ValInt ChildElementCount() {
        ctx.AddProperty("childElementCount");
        return new ValInt(ctx);
    }
}
