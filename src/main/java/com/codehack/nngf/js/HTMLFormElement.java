package com.codehack.nngf.js;

public class HTMLFormElement extends HTMLElement {
    public HTMLFormElement(Context ctx) {
        super(ctx);
    }

    public HTMLFormElement(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public HTMLCollection Elements() {
        ctx.AddProperty("elements");
        return new HTMLCollection(ctx);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public ValStr Name() {
        ctx.AddProperty("name");
        return new ValStr(ctx);
    }

    public ValStr Action() {
        ctx.AddProperty("action");
        return new ValStr(ctx);
    }

    public ValStr Method() {
        ctx.AddProperty("method");
        return new ValStr(ctx);
    }

    public ValStr Enctype() {
        ctx.AddProperty("enctype");
        return new ValStr(ctx);
    }

    public ValStr Encoding() {
        ctx.AddProperty("encoding");
        return new ValStr(ctx);
    }

    public ValStr Target() {
        ctx.AddProperty("target");
        return new ValStr(ctx);
    }

    public ValBool NoValidate() {
        ctx.AddProperty("noValidate");
        return new ValBool(ctx);
    }

    public ValStr AcceptCharset() {
        ctx.AddProperty("acceptCharset");
        return new ValStr(ctx);
    }

    public SEnd submit() {
        ctx.AddMethod("submit");
        return new SEnd(ctx);
    }

    public SEnd reset() {
        ctx.AddMethod("reset");
        return new SEnd(ctx);
    }

    public ValBool checkValidity() {
        ctx.AddMethod("checkValidity");
        return new ValBool(ctx);
    }

    public SEnd reportValidity() {
        ctx.AddMethod("reportValidity");
        return new SEnd(ctx);
    }

    public Element item(int index) {
        ctx.AddMethod("item", index);
        return new Element(ctx);
    }

    public Element namedItem(String name) {
        ctx.AddMethod("namedItem", name);
        return new Element(ctx);
    }
}
