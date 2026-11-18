package com.codehack.nngf.js;

public class Console extends Prop {
    public Console(Context ctx) {
        super(ctx);
    }

    public Console(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd log(Object... args) {
        ctx.AddMethod("log", args);
        return new SEnd(ctx);
    }

    public SEnd warn(Object... args) {
        ctx.AddMethod("warn", args);
        return new SEnd(ctx);
    }

    public SEnd error(Object... args) {
        ctx.AddMethod("error", args);
        return new SEnd(ctx);
    }

    public SEnd info(Object... args) {
        ctx.AddMethod("info", args);
        return new SEnd(ctx);
    }

    public SEnd debug(Object... args) {
        ctx.AddMethod("debug", args);
        return new SEnd(ctx);
    }

    public SEnd trace(Object... args) {
        ctx.AddMethod("trace", args);
        return new SEnd(ctx);
    }

    public SEnd dir(Object obj) {
        ctx.AddMethod("dir", obj);
        return new SEnd(ctx);
    }

    public SEnd table(Object data) {
        ctx.AddMethod("table", data);
        return new SEnd(ctx);
    }

    public SEnd table(Object data, Object columns) {
        ctx.AddMethod("table", data, columns);
        return new SEnd(ctx);
    }

    public SEnd group(Object... args) {
        ctx.AddMethod("group", args);
        return new SEnd(ctx);
    }

    public SEnd groupCollapsed(Object... args) {
        ctx.AddMethod("groupCollapsed", args);
        return new SEnd(ctx);
    }

    public SEnd groupEnd() {
        ctx.AddMethod("groupEnd");
        return new SEnd(ctx);
    }

    public SEnd time(String label) {
        ctx.AddMethod("time", label);
        return new SEnd(ctx);
    }

    public SEnd timeLog(String label) {
        ctx.AddMethod("timeLog", label);
        return new SEnd(ctx);
    }

    public SEnd timeEnd(String label) {
        ctx.AddMethod("timeEnd", label);
        return new SEnd(ctx);
    }

    public SEnd count(String label) {
        ctx.AddMethod("count", label);
        return new SEnd(ctx);
    }

    public SEnd countReset(String label) {
        ctx.AddMethod("countReset", label);
        return new SEnd(ctx);
    }

    public SEnd clear() {
        ctx.AddMethod("clear");
        return new SEnd(ctx);
    }

    public SEnd assert_(boolean condition, Object... args) {
        ctx.AddMethod("assert", condition, args);
        return new SEnd(ctx);
    }
}
