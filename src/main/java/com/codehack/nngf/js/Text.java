package com.codehack.nngf.js;

public class Text extends Node {
    public Text(Context ctx) {
        super(ctx);
    }

    public Text(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr WholeText() {
        ctx.AddProperty("wholeText");
        return new ValStr(ctx);
    }

    public SEnd splitText(int offset) {
        ctx.AddMethod("splitText", offset);
        return new SEnd(ctx);
    }

    public ValStr Data() {
        ctx.AddProperty("data");
        return new ValStr(ctx);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }
}
