package com.codehack.nngf.js;

public class StyleSheet extends Prop {
    public StyleSheet(Context ctx) {
        super(ctx);
    }

    public StyleSheet(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValBool Disabled() {
        ctx.AddProperty("disabled");
        return new ValBool(ctx);
    }

    public ValStr Href() {
        ctx.AddProperty("href");
        return new ValStr(ctx);
    }

    public ValStr Title() {
        ctx.AddProperty("title");
        return new ValStr(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public ValInt CssRules() {
        ctx.AddProperty("cssRules");
        return new ValInt(ctx);
    }

    public ValInt insertRule(String rule, int index) {
        ctx.AddMethod("insertRule", rule, index);
        return new ValInt(ctx);
    }

    public SEnd deleteRule(int index) {
        ctx.AddMethod("deleteRule", index);
        return new SEnd(ctx);
    }
}
