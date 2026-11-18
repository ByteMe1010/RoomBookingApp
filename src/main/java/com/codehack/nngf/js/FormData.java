package com.codehack.nngf.js;

public class FormData extends Prop {
    public FormData(Context ctx) {
        super(ctx);
    }

    public FormData(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd append(String name, String value) {
        ctx.AddMethod("append", name, value);
        return new SEnd(ctx);
    }

    public SEnd append(String name, String value, String filename) {
        ctx.AddMethod("append", name, value, filename);
        return new SEnd(ctx);
    }

    public SEnd delete(String name) {
        ctx.AddMethod("delete", name);
        return new SEnd(ctx);
    }

    public ValStr get(String name) {
        ctx.AddMethod("get", name);
        return new ValStr(ctx);
    }

    public Array getAll(String name) {
        ctx.AddMethod("getAll", name);
        return new Array(ctx);
    }

    public ValBool has(String name) {
        ctx.AddMethod("has", name);
        return new ValBool(ctx);
    }

    public SEnd set(String name, String value) {
        ctx.AddMethod("set", name, value);
        return new SEnd(ctx);
    }

    public SEnd set(String name, String value, String filename) {
        ctx.AddMethod("set", name, value, filename);
        return new SEnd(ctx);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public SEnd forEach(Prop callback) {
        ctx.AddMethod("forEach", callback);
        return new SEnd(ctx);
    }

    public Prop entries() {
        ctx.AddMethod("entries");
        return new Prop(ctx);
    }

    public Prop keys() {
        ctx.AddMethod("keys");
        return new Prop(ctx);
    }

    public Prop values() {
        ctx.AddMethod("values");
        return new Prop(ctx);
    }
}
