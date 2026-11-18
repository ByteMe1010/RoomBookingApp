package com.codehack.nngf.js;

public class Screen extends Prop {
    public Screen(Context ctx) {
        super(ctx);
    }

    public Screen(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Width() {
        ctx.AddProperty("width");
        return new ValInt(ctx);
    }

    public ValInt Height() {
        ctx.AddProperty("height");
        return new ValInt(ctx);
    }

    public ValInt AvailWidth() {
        ctx.AddProperty("availWidth");
        return new ValInt(ctx);
    }

    public ValInt AvailHeight() {
        ctx.AddProperty("availHeight");
        return new ValInt(ctx);
    }

    public ValInt ColorDepth() {
        ctx.AddProperty("colorDepth");
        return new ValInt(ctx);
    }

    public ValInt PixelDepth() {
        ctx.AddProperty("pixelDepth");
        return new ValInt(ctx);
    }

    public ValInt AvailTop() {
        ctx.AddProperty("availTop");
        return new ValInt(ctx);
    }

    public ValInt AvailLeft() {
        ctx.AddProperty("availLeft");
        return new ValInt(ctx);
    }

    public ValInt Orientation() {
        ctx.AddProperty("orientation");
        return new ValInt(ctx);
    }
}
