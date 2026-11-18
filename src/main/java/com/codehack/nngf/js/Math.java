package com.codehack.nngf.js;

public class Math extends Prop {
    public Math(Context ctx) {
        super(ctx);
    }

    public ValInt abs(int x) {
        ctx.AddMethod("abs", x);
        return new ValInt(ctx);
    }

    public ValInt floor(int x) {
        ctx.AddMethod("floor", x);
        return new ValInt(ctx);
    }

    public ValInt ceil(int x) {
        ctx.AddMethod("ceil", x);
        return new ValInt(ctx);
    }

    public ValInt round(int x) {
        ctx.AddMethod("round", x);
        return new ValInt(ctx);
    }

    public ValInt max(Object... values) {
        ctx.AddMethod("max", values);
        return new ValInt(ctx);
    }

    public ValInt min(Object... values) {
        ctx.AddMethod("min", values);
        return new ValInt(ctx);
    }

    public ValInt pow(int x, int y) {
        ctx.AddMethod("pow", x, y);
        return new ValInt(ctx);
    }

    public ValInt sqrt(int x) {
        ctx.AddMethod("sqrt", x);
        return new ValInt(ctx);
    }

    public ValInt random() {
        ctx.AddMethod("random");
        return new ValInt(ctx);
    }

    public ValInt sin(int x) {
        ctx.AddMethod("sin", x);
        return new ValInt(ctx);
    }

    public ValInt cos(int x) {
        ctx.AddMethod("cos", x);
        return new ValInt(ctx);
    }

    public ValInt tan(int x) {
        ctx.AddMethod("tan", x);
        return new ValInt(ctx);
    }

    public ValInt log(int x) {
        ctx.AddMethod("log", x);
        return new ValInt(ctx);
    }

    public ValInt exp(int x) {
        ctx.AddMethod("exp", x);
        return new ValInt(ctx);
    }

    public ValInt PI() {
        ctx.AddProperty("PI");
        return new ValInt(ctx);
    }
}
