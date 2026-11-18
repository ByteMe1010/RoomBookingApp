package com.codehack.nngf.js;

public class DOMRect extends Prop {
    public DOMRect(Context ctx) {
        super(ctx);
    }

    public DOMRect(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt X() {
        ctx.AddProperty("x");
        return new ValInt(ctx);
    }

    public ValInt Y() {
        ctx.AddProperty("y");
        return new ValInt(ctx);
    }

    public ValInt Width() {
        ctx.AddProperty("width");
        return new ValInt(ctx);
    }

    public ValInt Height() {
        ctx.AddProperty("height");
        return new ValInt(ctx);
    }

    public ValInt Top() {
        ctx.AddProperty("top");
        return new ValInt(ctx);
    }

    public ValInt Right() {
        ctx.AddProperty("right");
        return new ValInt(ctx);
    }

    public ValInt Bottom() {
        ctx.AddProperty("bottom");
        return new ValInt(ctx);
    }

    public ValInt Left() {
        ctx.AddProperty("left");
        return new ValInt(ctx);
    }
}
