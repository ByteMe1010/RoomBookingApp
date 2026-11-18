package com.codehack.nngf.js;

public class Element extends Node {
    public Element(Context ctx) {
        super(ctx);
    }

    public Element(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr TagName() {
        ctx.AddProperty("tagName");
        return new ValStr(ctx);
    }
    public Element property(String prop) {
        ctx.AddProperty(prop);
        return new Element(ctx);
    }

    public ValStr Id() {
        ctx.AddProperty("id");
        return new ValStr(ctx);
    }

    public ValStr ClassName() {
        ctx.AddProperty("className");
        return new ValStr(ctx);
    }

    public DOMTokenList ClassList() {
        ctx.AddProperty("classList");
        return new DOMTokenList(ctx);
    }

    public CSSStyleDeclaration Style() {
        ctx.AddProperty("style");
        return new CSSStyleDeclaration(ctx);
    }

    public ValStr InnerHTML() {
        ctx.AddProperty("innerHTML");
        return new ValStr(ctx);
    }

    public ValStr OuterHTML() {
        ctx.AddProperty("outerHTML");
        return new ValStr(ctx);
    }

    public ValStr TextContent() {
        ctx.AddProperty("textContent");
        return new ValStr(ctx);
    }

    public ValBool Hidden() {
        ctx.AddProperty("hidden");
        return new ValBool(ctx);
    }

    public Element closest(String selector) {
        ctx.AddMethod("closest", selector);
        return new Element(ctx);
    }

    public Element querySelector(String selector) {
        ctx.AddMethod("querySelector", selector);
        return new Element(ctx);
    }

    public NodeList querySelectorAll(String selector) {
        ctx.AddMethod("querySelectorAll", selector);
        return new NodeList(ctx);
    }

    public ValStr getAttribute(String name) {
        ctx.AddMethod("getAttribute", name);
        return new ValStr(ctx);
    }

    public SEnd setAttribute(String name, Object value) {
        ctx.AddMethod("setAttribute", name, value);
        return new SEnd(ctx);
    }

    public ValBool matches(String selector) {
        ctx.AddMethod("matches", selector);
        return new ValBool(ctx);
    }

    public Element ParentElement() {
        ctx.AddProperty("parentElement");
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

    public Element PreviousElementSibling() {
        ctx.AddProperty("previousElementSibling");
        return new Element(ctx);
    }

    public Element NextElementSibling() {
        ctx.AddProperty("nextElementSibling");
        return new Element(ctx);
    }

    public ValInt ChildElementCount() {
        ctx.AddProperty("childElementCount");
        return new ValInt(ctx);
    }

    public SEnd remove() {
        ctx.AddMethod("remove");
        return new SEnd(ctx);
    }

    public SEnd before(String... nodes) {
        ctx.AddMethod("before", (Object[]) nodes);
        return new SEnd(ctx);
    }

    public SEnd after(String... nodes) {
        ctx.AddMethod("after", (Object[]) nodes);
        return new SEnd(ctx);
    }

    public SEnd prepend(String... nodes) {
        ctx.AddMethod("prepend", (Object[]) nodes);
        return new SEnd(ctx);
    }

    public SEnd append(String... nodes) {
        ctx.AddMethod("append", (Object[]) nodes);
        return new SEnd(ctx);
    }

    public ValBool hasAttribute(String name) {
        ctx.AddMethod("hasAttribute", name);
        return new ValBool(ctx);
    }

    public SEnd removeAttribute(String name) {
        ctx.AddMethod("removeAttribute", name);
        return new SEnd(ctx);
    }

    public ValStr getHTML() {
        ctx.AddMethod("getHTML");
        return new ValStr(ctx);
    }
}
