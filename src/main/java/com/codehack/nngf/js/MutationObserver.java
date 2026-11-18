package com.codehack.nngf.js;

public class MutationObserver extends Prop {
    public MutationObserver(Context ctx) {
        super(ctx);
    }

    public MutationObserver(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd observe(Prop target, Prop options) {
        ctx.AddMethod("observe", target, options);
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
}
