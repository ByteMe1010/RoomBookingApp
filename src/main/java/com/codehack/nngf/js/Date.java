package com.codehack.nngf.js;

public class Date extends Prop {
    public Date(Context ctx) {
        super(ctx);
    }

    public Date(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt getFullYear() {
        ctx.AddMethod("getFullYear");
        return new ValInt(ctx);
    }

    public ValInt getMonth() {
        ctx.AddMethod("getMonth");
        return new ValInt(ctx);
    }

    public ValInt getDate() {
        ctx.AddMethod("getDate");
        return new ValInt(ctx);
    }

    public ValInt getDay() {
        ctx.AddMethod("getDay");
        return new ValInt(ctx);
    }

    public ValInt getHours() {
        ctx.AddMethod("getHours");
        return new ValInt(ctx);
    }

    public ValInt getMinutes() {
        ctx.AddMethod("getMinutes");
        return new ValInt(ctx);
    }

    public ValInt getSeconds() {
        ctx.AddMethod("getSeconds");
        return new ValInt(ctx);
    }

    public ValInt getMilliseconds() {
        ctx.AddMethod("getMilliseconds");
        return new ValInt(ctx);
    }

    public ValInt getTime() {
        ctx.AddMethod("getTime");
        return new ValInt(ctx);
    }

    public ValStr toISOString() {
        ctx.AddMethod("toISOString");
        return new ValStr(ctx);
    }

    public ValStr toDateString() {
        ctx.AddMethod("toDateString");
        return new ValStr(ctx);
    }

    public ValStr toTimeString() {
        ctx.AddMethod("toTimeString");
        return new ValStr(ctx);
    }

    public ValInt Now() {
        ctx.AddProperty("now");
        return new ValInt(ctx);
    }

    public ValInt Parse() {
        ctx.AddProperty("parse");
        return new ValInt(ctx);
    }

    public ValInt UTC() {
        ctx.AddProperty("UTC");
        return new ValInt(ctx);
    }
}
