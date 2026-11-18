package com.codehack.nngf.js;

public class DOMTokenList extends Prop {
    public DOMTokenList(Context ctx) {
        super(ctx);
    }

    public DOMTokenList(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public SEnd add(Object... tokens) {
        ctx.AddMethod("add", tokens);
        return new SEnd(ctx);
    }

    public SEnd remove(Object... tokens) {
        ctx.AddMethod("remove", tokens);
        return new SEnd(ctx);
    }

    public ValBool contains(String token) {
        ctx.AddMethod("contains", token);
        return new ValBool(ctx);
    }

    public ValBool toggle(String token) {
        ctx.AddMethod("toggle", token);
        return new ValBool(ctx);
    }

    public SEnd replace(String oldToken, String newToken) {
        ctx.AddMethod("replace", oldToken, newToken);
        return new SEnd(ctx);
    }

    public ValStr item(int index) {
        ctx.AddMethod("item", index);
        return new ValStr(ctx);
    }

    public ValStr Value() {
        ctx.AddProperty("value");
        return new ValStr(ctx);
    }
}
