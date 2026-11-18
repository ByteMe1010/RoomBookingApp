package com.codehack.nngf.js;

public class IntersectionObserver extends Prop {
    public IntersectionObserver(Context ctx) {
        super(ctx);
    }

    public IntersectionObserver(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd observe(Prop target) {
        ctx.AddMethod("observe", target);
        return new SEnd(ctx);
    }

    public SEnd unobserve(Prop target) {
        ctx.AddMethod("unobserve", target);
        return new SEnd(ctx);
    }

    public SEnd disconnect() {
        ctx.AddMethod("disconnect");
        return new SEnd(ctx);
    }

    public Array takeRecords() {
        ctx.AddMethod("takeRecords");
        return new Array(ctx);
    }

    public Prop Root() {
        ctx.AddProperty("root");
        return new Prop(ctx);
    }

    public ValStr RootMargin() {
        ctx.AddProperty("rootMargin");
        return new ValStr(ctx);
    }

    public ValInt Thresholds() {
        ctx.AddProperty("thresholds");
        return new ValInt(ctx);
    }
}
