package com.codehack.nngf.js;

public class Document extends Node {
    public Document(Context ctx) {
        super(ctx);
    }

    public Document(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Element DocumentElement() {
        ctx.AddProperty("documentElement");
        return new Element(ctx);
    }

    public HTMLElement Body() {
        ctx.AddProperty("body");
        return new HTMLElement(ctx);
    }

    public Element Head() {
        ctx.AddProperty("head");
        return new Element(ctx);
    }

    public Element getElementById(String id) {
        ctx.AddMethod("getElementById", id);
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

    public Element createElement(String tag) {
        ctx.AddMethod("createElement", tag);
        return new Element(ctx);
    }

    public Text createTextNode(String data) {
        ctx.AddMethod("createTextNode", data);
        return new Text(ctx);
    }

    public DocumentFragment createDocumentFragment() {
        ctx.AddMethod("createDocumentFragment");
        return new DocumentFragment(ctx);
    }

    public ValStr Title() {
        ctx.AddProperty("title");
        return new ValStr(ctx);
    }

    public ValStr URL() {
        ctx.AddProperty("URL");
        return new ValStr(ctx);
    }

    public ValStr DocumentURI() {
        ctx.AddProperty("documentURI");
        return new ValStr(ctx);
    }

    public ValStr Domain() {
        ctx.AddProperty("domain");
        return new ValStr(ctx);
    }

    public ValStr Cookie() {
        ctx.AddProperty("cookie");
        return new ValStr(ctx);
    }

    public ValStr Referrer() {
        ctx.AddProperty("referrer");
        return new ValStr(ctx);
    }

    public Element getElementsByClassName(String name) {
        ctx.AddMethod("getElementsByClassName", name);
        return new Element(ctx);
    }

    public NodeList getElementsByTagName(String name) {
        ctx.AddMethod("getElementsByTagName", name);
        return new NodeList(ctx);
    }

    public ValBool hasFocus() {
        ctx.AddMethod("hasFocus");
        return new ValBool(ctx);
    }

    public SEnd write(String... text) {
        ctx.AddMethod("write", (Object[]) text);
        return new SEnd(ctx);
    }

    public SEnd writeln(String... text) {
        ctx.AddMethod("writeln", (Object[]) text);
        return new SEnd(ctx);
    }

    public Element ActiveElement() {
        ctx.AddProperty("activeElement");
        return new Element(ctx);
    }

    public HTMLCollection Forms() {
        ctx.AddProperty("forms");
        return new HTMLCollection(ctx);
    }

    public HTMLCollection Images() {
        ctx.AddProperty("images");
        return new HTMLCollection(ctx);
    }

    public HTMLCollection Links() {
        ctx.AddProperty("links");
        return new HTMLCollection(ctx);
    }

    public HTMLCollection Scripts() {
        ctx.AddProperty("scripts");
        return new HTMLCollection(ctx);
    }

    public StyleSheet StyleSheets() {
        ctx.AddProperty("styleSheets");
        return new StyleSheet(ctx);
    }

    public SEnd adoptNode(Node node) {
        ctx.AddMethod("adoptNode", node);
        return new SEnd(ctx);
    }

    public SEnd importNode(Node node, boolean deep) {
        ctx.AddMethod("importNode", node, deep);
        return new SEnd(ctx);
    }
}
