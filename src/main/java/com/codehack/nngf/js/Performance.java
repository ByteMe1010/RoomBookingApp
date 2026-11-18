package com.codehack.nngf.js;

public class Performance extends Prop {
    public Performance(Context ctx) {
        super(ctx);
    }

    public Performance(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt now() {
        ctx.AddMethod("now");
        return new ValInt(ctx);
    }

    public ValInt TimeOrigin() {
        ctx.AddProperty("timeOrigin");
        return new ValInt(ctx);
    }

    public Array getEntries() {
        ctx.AddMethod("getEntries");
        return new Array(ctx);
    }

    public Array getEntriesByType(String type) {
        ctx.AddMethod("getEntriesByType", type);
        return new Array(ctx);
    }

    public Array getEntriesByName(String name) {
        ctx.AddMethod("getEntriesByName", name);
        return new Array(ctx);
    }

    public SEnd mark(String name) {
        ctx.AddMethod("mark", name);
        return new SEnd(ctx);
    }

    public SEnd clearMarks(String name) {
        ctx.AddMethod("clearMarks", name);
        return new SEnd(ctx);
    }

    public SEnd measure(String name) {
        ctx.AddMethod("measure", name);
        return new SEnd(ctx);
    }

    public SEnd measure(String name, String startMark) {
        ctx.AddMethod("measure", name, startMark);
        return new SEnd(ctx);
    }

    public SEnd measure(String name, String startMark, String endMark) {
        ctx.AddMethod("measure", name, startMark, endMark);
        return new SEnd(ctx);
    }

    public SEnd clearMeasures(String name) {
        ctx.AddMethod("clearMeasures", name);
        return new SEnd(ctx);
    }
}
