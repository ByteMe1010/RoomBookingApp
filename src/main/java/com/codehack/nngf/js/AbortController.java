package com.codehack.nngf.js;

public class AbortController extends Prop {
    public AbortController(Context ctx) {
        super(ctx);
    }

    public AbortController(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Prop Signal() {
        ctx.AddProperty("signal");
        return new Prop(ctx);
    }

    public SEnd abort() {
        ctx.AddMethod("abort");
        return new SEnd(ctx);
    }

    public SEnd abort(Object reason) {
        ctx.AddMethod("abort", reason);
        return new SEnd(ctx);
    }
}
