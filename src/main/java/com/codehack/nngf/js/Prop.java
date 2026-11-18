package com.codehack.nngf.js;

public class Prop {
    protected Context ctx;

    public Prop(Context ctx) {
        this.ctx = ctx;
    }

    public Prop(Context ctx, Metd metd) {
        this.ctx = ctx;
        ctx.AddMethod(metd.name(), metd.arg());
    }

    public Prop method(String name, Object... args) {
        ctx.AddMethod(name, args);
        return new Prop(ctx);
    }

    public <T extends Prop> T method(Class<T> type, String name, Object... args) {
        ctx.AddMethod(name, args);
        try {
            return type.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public SEnd E() {
        return new SEnd(ctx);
    }

    public String getExpr() {
        return ctx.getString();
    }
}
