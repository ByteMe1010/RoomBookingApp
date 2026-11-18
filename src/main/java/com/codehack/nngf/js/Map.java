package com.codehack.nngf.js;

public class Map extends Prop {
    public Map(Context ctx) {
        super(ctx);
    }

    public Map(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Prop get(Object key) {
        ctx.AddMethod("get", key);
        return new Prop(ctx);
    }

    public Map set(Object key, Object value) {
        ctx.AddMethod("set", key, value);
        return this;
    }

    public ValBool has(Object key) {
        ctx.AddMethod("has", key);
        return new ValBool(ctx);
    }

    public ValBool delete(Object key) {
        ctx.AddMethod("delete", key);
        return new ValBool(ctx);
    }

    public SEnd clear() {
        ctx.AddMethod("clear");
        return new SEnd(ctx);
    }

    public ValInt Size() {
        ctx.AddProperty("size");
        return new ValInt(ctx);
    }

    public SEnd forEach(Prop callback) {
        ctx.AddMethod("forEach", callback);
        return new SEnd(ctx);
    }

    public Map entries() {
        ctx.AddMethod("entries");
        return new Map(ctx);
    }

    public Map keys() {
        ctx.AddMethod("keys");
        return new Map(ctx);
    }

    public Map values() {
        ctx.AddMethod("values");
        return new Map(ctx);
    }
}
