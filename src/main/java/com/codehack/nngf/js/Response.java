package com.codehack.nngf.js;

public class Response extends Prop {
    public Response(Context ctx) {
        super(ctx);
    }

    public Response(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Headers Headers() {
        ctx.AddProperty("headers");
        return new Headers(ctx);
    }

    public ValBool Ok() {
        ctx.AddProperty("ok");
        return new ValBool(ctx);
    }

    public ValBool Redirected() {
        ctx.AddProperty("redirected");
        return new ValBool(ctx);
    }

    public ValInt Status() {
        ctx.AddProperty("status");
        return new ValInt(ctx);
    }

    public ValStr StatusText() {
        ctx.AddProperty("statusText");
        return new ValStr(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public ValStr URL() {
        ctx.AddProperty("url");
        return new ValStr(ctx);
    }

    public ValBool BodyUsed() {
        ctx.AddProperty("bodyUsed");
        return new ValBool(ctx);
    }

    public Prop arrayBuffer() {
        ctx.AddMethod("arrayBuffer");
        return new Prop(ctx);
    }

    public Prop blob() {
        ctx.AddMethod("blob");
        return new Prop(ctx);
    }

    public Prop formData() {
        ctx.AddMethod("formData");
        return new Prop(ctx);
    }

    public Prop json() {
        ctx.AddMethod("json");
        return new Prop(ctx);
    }

    public Prop text() {
        ctx.AddMethod("text");
        return new Prop(ctx);
    }

    public Response clone() {
        ctx.AddMethod("clone");
        return new Response(ctx);
    }
}
