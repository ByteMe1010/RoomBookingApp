package com.codehack.nngf.js;

public class Request extends Prop {
    public Request(Context ctx) {
        super(ctx);
    }

    public Request(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr Method() {
        ctx.AddProperty("method");
        return new ValStr(ctx);
    }

    public ValStr URL() {
        ctx.AddProperty("url");
        return new ValStr(ctx);
    }

    public Headers Headers() {
        ctx.AddProperty("headers");
        return new Headers(ctx);
    }

    public ValStr Referrer() {
        ctx.AddProperty("referrer");
        return new ValStr(ctx);
    }

    public ValStr ReferrerPolicy() {
        ctx.AddProperty("referrerPolicy");
        return new ValStr(ctx);
    }

    public ValStr Mode() {
        ctx.AddProperty("mode");
        return new ValStr(ctx);
    }

    public ValStr Credentials() {
        ctx.AddProperty("credentials");
        return new ValStr(ctx);
    }

    public ValStr Cache() {
        ctx.AddProperty("cache");
        return new ValStr(ctx);
    }

    public ValStr Redirect() {
        ctx.AddProperty("redirect");
        return new ValStr(ctx);
    }

    public ValStr Integrity() {
        ctx.AddProperty("integrity");
        return new ValStr(ctx);
    }

    public ValBool Keepalive() {
        ctx.AddProperty("keepalive");
        return new ValBool(ctx);
    }

    public ValBool IsHistoryNavigation() {
        ctx.AddProperty("isHistoryNavigation");
        return new ValBool(ctx);
    }

    public Prop Signal() {
        ctx.AddProperty("signal");
        return new Prop(ctx);
    }

    public Prop Body() {
        ctx.AddProperty("body");
        return new Prop(ctx);
    }

    public ValStr BodyUsed() {
        ctx.AddProperty("bodyUsed");
        return new ValStr(ctx);
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

    public Request clone() {
        ctx.AddMethod("clone");
        return new Request(ctx);
    }
}
