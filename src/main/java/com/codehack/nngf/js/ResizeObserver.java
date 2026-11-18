package com.codehack.nngf.js;

public class ResizeObserver extends Prop {
    public ResizeObserver(Context ctx) {
        super(ctx);
    }

    public ResizeObserver(Context ctx, Metd metd) {
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
}
