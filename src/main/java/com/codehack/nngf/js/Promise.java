package com.codehack.nngf.js;

public class Promise extends Prop {
    public Promise(Context ctx) {
        super(ctx);
    }

    public Promise(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Promise then(Prop onFulfilled) {
        ctx.AddMethod("then", onFulfilled);
        return this;
    }

    public Promise then(Prop onFulfilled, Prop onRejected) {
        ctx.AddMethod("then", onFulfilled, onRejected);
        return this;
    }

    public Promise catch_(Prop onRejected) {
        ctx.AddMethod("catch", onRejected);
        return this;
    }

    public Promise finally_(Prop onFinally) {
        ctx.AddMethod("finally", onFinally);
        return this;
    }
}
