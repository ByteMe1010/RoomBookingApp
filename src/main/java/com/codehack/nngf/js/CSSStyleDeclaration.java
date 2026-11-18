package com.codehack.nngf.js;

public class CSSStyleDeclaration extends Prop {
    public CSSStyleDeclaration(Context ctx) {
        super(ctx);
    }

    public CSSStyleDeclaration(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr CssText() {
        ctx.AddProperty("cssText");
        return new ValStr(ctx);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public ValStr getPropertyValue(String property) {
        ctx.AddMethod("getPropertyValue", property);
        return new ValStr(ctx);
    }

    public SEnd setProperty(String property, String value) {
        ctx.AddMethod("setProperty", property, value);
        return new SEnd(ctx);
    }

    public ValStr removeProperty(String property) {
        ctx.AddMethod("removeProperty", property);
        return new ValStr(ctx);
    }

    public ValStr item(int index) {
        ctx.AddMethod("item", index);
        return new ValStr(ctx);
    }

    public ValStr getPropertyPriority(String property) {
        ctx.AddMethod("getPropertyPriority", property);
        return new ValStr(ctx);
    }
}
