package com.codehack.nngf.js;

public class HTMLCollection extends Prop {
    public HTMLCollection(Context ctx) {
        super(ctx);
    }

    public HTMLCollection(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
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
