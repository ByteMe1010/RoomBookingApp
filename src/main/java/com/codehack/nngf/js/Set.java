package com.codehack.nngf.js;

public class Set extends Prop {
    public Set(Context ctx) {
        super(ctx);
    }

    public Set(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Set add(Object value) {
        ctx.AddMethod("add", value);
        return this;
    }

    public ValBool has(Object value) {
        ctx.AddMethod("has", value);
        return new ValBool(ctx);
    }

    public ValBool delete(Object value) {
        ctx.AddMethod("delete", value);
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

    public Set entries() {
        ctx.AddMethod("entries");
        return new Set(ctx);
    }

    public Set keys() {
        ctx.AddMethod("keys");
        return new Set(ctx);
    }

    public Set values() {
        ctx.AddMethod("values");
        return new Set(ctx);
    }
}
