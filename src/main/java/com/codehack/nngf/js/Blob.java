package com.codehack.nngf.js;

public class Blob extends Prop {
    public Blob(Context ctx) {
        super(ctx);
    }

    public Blob(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Size() {
        ctx.AddProperty("size");
        return new ValInt(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public Blob slice(int start) {
        ctx.AddMethod("slice", start);
        return new Blob(ctx);
    }

    public Blob slice(int start, int end) {
        ctx.AddMethod("slice", start, end);
        return new Blob(ctx);
    }

    public Blob slice(int start, int end, String contentType) {
        ctx.AddMethod("slice", start, end, contentType);
        return new Blob(ctx);
    }

    public Prop stream() {
        ctx.AddMethod("stream");
        return new Prop(ctx);
    }

    public Prop text() {
        ctx.AddMethod("text");
        return new Prop(ctx);
    }

    public Prop arrayBuffer() {
        ctx.AddMethod("arrayBuffer");
        return new Prop(ctx);
    }
}
