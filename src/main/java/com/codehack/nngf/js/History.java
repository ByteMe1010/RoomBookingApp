package com.codehack.nngf.js;

public class History extends Prop {
    public History(Context ctx) {
        super(ctx);
    }

    public History(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public ValInt ScrollRestoration() {
        ctx.AddProperty("scrollRestoration");
        return new ValInt(ctx);
    }

    public ValStr State() {
        ctx.AddProperty("state");
        return new ValStr(ctx);
    }

    public SEnd back() {
        ctx.AddMethod("back");
        return new SEnd(ctx);
    }

    public SEnd forward() {
        ctx.AddMethod("forward");
        return new SEnd(ctx);
    }

    public SEnd go(int delta) {
        ctx.AddMethod("go", delta);
        return new SEnd(ctx);
    }

    public SEnd pushState(Object state, String title) {
        ctx.AddMethod("pushState", state, title);
        return new SEnd(ctx);
    }

    public SEnd pushState(Object state, String title, String url) {
        ctx.AddMethod("pushState", state, title, url);
        return new SEnd(ctx);
    }

    public SEnd replaceState(Object state, String title) {
        ctx.AddMethod("replaceState", state, title);
        return new SEnd(ctx);
    }

    public SEnd replaceState(Object state, String title, String url) {
        ctx.AddMethod("replaceState", state, title, url);
        return new SEnd(ctx);
    }
}
