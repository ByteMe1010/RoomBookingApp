package com.codehack.nngf.js;

public class ValInt extends Prop {
    public ValInt(Context ctx) {
        super(ctx);
    }

    public ValInt(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr toFixed(int digits) {
        ctx.AddMethod("toFixed", digits);
        return new ValStr(ctx);
    }

    public ValStr toPrecision(int precision) {
        ctx.AddMethod("toPrecision", precision);
        return new ValStr(ctx);
    }

    public ValStr toExponential(int digits) {
        ctx.AddMethod("toExponential", digits);
        return new ValStr(ctx);
    }

    public ValInt ValueOf() {
        ctx.AddProperty("valueOf");
        return this;
    }
}
