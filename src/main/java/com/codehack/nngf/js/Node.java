package com.codehack.nngf.js;

public class Node extends Prop {
    public Node(Context ctx) {
        super(ctx);
    }

    public Node(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Node ParentNode() {
        ctx.AddProperty("parentNode");
        return new Node(ctx);
    }

    public NodeList ChildNodes() {
        ctx.AddProperty("childNodes");
        return new NodeList(ctx);
    }

    public Node FirstChild() {
        ctx.AddProperty("firstChild");
        return new Node(ctx);
    }

    public Node LastChild() {
        ctx.AddProperty("lastChild");
        return new Node(ctx);
    }

    public ValStr TextContent() {
        ctx.AddProperty("textContent");
        return new ValStr(ctx);
    }

    public Node PreviousSibling() {
        ctx.AddProperty("previousSibling");
        return new Node(ctx);
    }

    public Node NextSibling() {
        ctx.AddProperty("nextSibling");
        return new Node(ctx);
    }

    public Node appendChild(Node child) {
        ctx.AddMethod("appendChild", child);
        return this;
    }

    public Node removeChild(Node child) {
        ctx.AddMethod("removeChild", child);
        return this;
    }

    public Node cloneNode(boolean deep) {
        ctx.AddMethod("cloneNode", deep);
        return this;
    }

    public ValBool hasChildNodes() {
        ctx.AddMethod("hasChildNodes");
        return new ValBool(ctx);
    }

    public Prop insertBefore(Node newChild, Node refChild) {
        ctx.AddMethod("insertBefore", newChild, refChild);
        return this;
    }

    public Node replaceChild(Node newChild, Node oldChild) {
        ctx.AddMethod("replaceChild", newChild, oldChild);
        return this;
    }

    public ValBool contains(Node other) {
        ctx.AddMethod("contains", other);
        return new ValBool(ctx);
    }

    public ValStr NodeName() {
        ctx.AddProperty("nodeName");
        return new ValStr(ctx);
    }

    public ValInt NodeType() {
        ctx.AddProperty("nodeType");
        return new ValInt(ctx);
    }
}
