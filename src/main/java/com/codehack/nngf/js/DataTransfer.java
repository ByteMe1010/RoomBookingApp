package com.codehack.nngf.js;

public class DataTransfer extends Prop {
    public DataTransfer(Context ctx) {
        super(ctx);
    }

    public DataTransfer(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr DropEffect() {
        ctx.AddProperty("dropEffect");
        return new ValStr(ctx);
    }

    public ValStr EffectAllowed() {
        ctx.AddProperty("effectAllowed");
        return new ValStr(ctx);
    }

    public Array Files() {
        ctx.AddProperty("files");
        return new Array(ctx);
    }

    public Array Items() {
        ctx.AddProperty("items");
        return new Array(ctx);
    }

    public Array Types() {
        ctx.AddProperty("types");
        return new Array(ctx);
    }

    public SEnd setData(String format, String data) {
        ctx.AddMethod("setData", format, data);
        return new SEnd(ctx);
    }

    public ValStr getData(String format) {
        ctx.AddMethod("getData", format);
        return new ValStr(ctx);
    }

    public SEnd clearData() {
        ctx.AddMethod("clearData");
        return new SEnd(ctx);
    }

    public SEnd clearData(String format) {
        ctx.AddMethod("clearData", format);
        return new SEnd(ctx);
    }

    public Prop setDragImage(Prop image, int xOffset, int yOffset) {
        ctx.AddMethod("setDragImage", image, xOffset, yOffset);
        return new Prop(ctx);
    }
}
