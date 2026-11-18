package com.codehack.nngf.js;

public class RegExp extends Prop {
    public RegExp(Context ctx) {
        super(ctx);
    }

    public RegExp(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValBool test(String str) {
        ctx.AddMethod("test", str);
        return new ValBool(ctx);
    }

    public Array exec(String str) {
        ctx.AddMethod("exec", str);
        return new Array(ctx);
    }

    public ValStr Source() {
        ctx.AddProperty("source");
        return new ValStr(ctx);
    }

    public ValBool Global() {
        ctx.AddProperty("global");
        return new ValBool(ctx);
    }

    public ValBool IgnoreCase() {
        ctx.AddProperty("ignoreCase");
        return new ValBool(ctx);
    }

    public ValBool Multiline() {
        ctx.AddProperty("multiline");
        return new ValBool(ctx);
    }

    public ValBool DotAll() {
        ctx.AddProperty("dotAll");
        return new ValBool(ctx);
    }

    public ValBool Unicode() {
        ctx.AddProperty("unicode");
        return new ValBool(ctx);
    }

    public ValBool Sticky() {
        ctx.AddProperty("sticky");
        return new ValBool(ctx);
    }

    public ValInt LastIndex() {
        ctx.AddProperty("lastIndex");
        return new ValInt(ctx);
    }
}
