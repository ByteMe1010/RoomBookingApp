package com.codehack.nngf.js;

public class Storage extends Prop {
    public Storage(Context ctx) {
        super(ctx);
    }

    public Storage(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public Prop getItem(String key) {
        ctx.AddMethod("getItem", key);
        return new Prop(ctx);
    }

    public SEnd setItem(String key, String value) {
        ctx.AddMethod("setItem", key, value);
        return new SEnd(ctx);
    }

    public SEnd removeItem(String key) {
        ctx.AddMethod("removeItem", key);
        return new SEnd(ctx);
    }

    public SEnd clear() {
        ctx.AddMethod("clear");
        return new SEnd(ctx);
    }

    public Prop key(int index) {
        ctx.AddMethod("key", index);
        return new Prop(ctx);
    }
}
