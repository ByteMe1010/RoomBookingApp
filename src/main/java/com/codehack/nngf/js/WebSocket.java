package com.codehack.nngf.js;

public class WebSocket extends Prop {
    public WebSocket(Context ctx) {
        super(ctx);
    }

    public WebSocket(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr URL() {
        ctx.AddProperty("url");
        return new ValStr(ctx);
    }

    public ValInt ReadyState() {
        ctx.AddProperty("readyState");
        return new ValInt(ctx);
    }

    public ValInt BufferedAmount() {
        ctx.AddProperty("bufferedAmount");
        return new ValInt(ctx);
    }

    public ValStr Extensions() {
        ctx.AddProperty("extensions");
        return new ValStr(ctx);
    }

    public ValStr Protocol() {
        ctx.AddProperty("protocol");
        return new ValStr(ctx);
    }

    public SEnd send(String data) {
        ctx.AddMethod("send", data);
        return new SEnd(ctx);
    }

    public SEnd close() {
        ctx.AddMethod("close");
        return new SEnd(ctx);
    }

    public SEnd close(int code) {
        ctx.AddMethod("close", code);
        return new SEnd(ctx);
    }

    public SEnd close(int code, String reason) {
        ctx.AddMethod("close", code, reason);
        return new SEnd(ctx);
    }
}
