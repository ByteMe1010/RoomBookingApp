package com.codehack.nngf.js;

public class NodeList extends Prop {
    public NodeList(Context ctx) {
        super(ctx);
    }

    public NodeList(Context ctx, Metd metd) {
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

    public NodeList entries() {
        ctx.AddMethod("entries");
        return new NodeList(ctx);
    }

    public NodeList values() {
        ctx.AddMethod("values");
        return new NodeList(ctx);
    }

    public SEnd forEach(Prop callback) {
        ctx.AddMethod("forEach", callback);
        return new SEnd(ctx);
    }

    public Prop keys() {
        ctx.AddMethod("keys");
        return new Prop(ctx);
    }
}
