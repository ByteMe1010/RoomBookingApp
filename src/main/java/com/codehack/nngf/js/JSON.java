package com.codehack.nngf.js;

public class JSON extends Prop {
    public JSON(Context ctx) {
        super(ctx);
    }

    public JSON(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr stringify(Object value) {
        ctx.AddMethod("stringify", value);
        return new ValStr(ctx);
    }

    public ValStr stringify(Object value, Object replacer) {
        ctx.AddMethod("stringify", value, replacer);
        return new ValStr(ctx);
    }

    public ValStr stringify(Object value, Object replacer, int space) {
        ctx.AddMethod("stringify", value, replacer, space);
        return new ValStr(ctx);
    }

    public Prop parse(String text) {
        ctx.AddMethod("parse", text);
        return new Prop(ctx);
    }

    public Prop parse(String text, Prop reviver) {
        ctx.AddMethod("parse", text, reviver);
        return new Prop(ctx);
    }
}
