package com.codehack.nngf.js;

public class Worker extends Prop {
    public Worker(Context ctx) {
        super(ctx);
    }

    public Worker(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd postMessage(Object message) {
        ctx.AddMethod("postMessage", message);
        return new SEnd(ctx);
    }

    public SEnd postMessage(Object message, Prop transfer) {
        ctx.AddMethod("postMessage", message, transfer);
        return new SEnd(ctx);
    }

    public SEnd terminate() {
        ctx.AddMethod("terminate");
        return new SEnd(ctx);
    }
}
