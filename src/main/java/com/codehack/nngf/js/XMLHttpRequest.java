package com.codehack.nngf.js;

public class XMLHttpRequest extends Prop {
    public XMLHttpRequest(Context ctx) {
        super(ctx);
    }

    public XMLHttpRequest(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt ReadyState() {
        ctx.AddProperty("readyState");
        return new ValInt(ctx);
    }

    public Prop Response() {
        ctx.AddProperty("response");
        return new Prop(ctx);
    }

    public ValStr ResponseText() {
        ctx.AddProperty("responseText");
        return new ValStr(ctx);
    }

    public ValStr ResponseType() {
        ctx.AddProperty("responseType");
        return new ValStr(ctx);
    }

    public Prop ResponseXML() {
        ctx.AddProperty("responseXML");
        return new Prop(ctx);
    }

    public ValInt Status() {
        ctx.AddProperty("status");
        return new ValInt(ctx);
    }

    public ValStr StatusText() {
        ctx.AddProperty("statusText");
        return new ValStr(ctx);
    }

    public ValInt Timeout() {
        ctx.AddProperty("timeout");
        return new ValInt(ctx);
    }

    public ValBool WithCredentials() {
        ctx.AddProperty("withCredentials");
        return new ValBool(ctx);
    }

    public ValInt Upload() {
        ctx.AddProperty("upload");
        return new ValInt(ctx);
    }

    public SEnd open(String method, String url) {
        ctx.AddMethod("open", method, url);
        return new SEnd(ctx);
    }

    public SEnd open(String method, String url, boolean async) {
        ctx.AddMethod("open", method, url, async);
        return new SEnd(ctx);
    }

    public SEnd open(String method, String url, boolean async, String user) {
        ctx.AddMethod("open", method, url, async, user);
        return new SEnd(ctx);
    }

    public SEnd open(String method, String url, boolean async, String user, String password) {
        ctx.AddMethod("open", method, url, async, user, password);
        return new SEnd(ctx);
    }

    public SEnd send() {
        ctx.AddMethod("send");
        return new SEnd(ctx);
    }

    public SEnd send(Object body) {
        ctx.AddMethod("send", body);
        return new SEnd(ctx);
    }

    public SEnd abort() {
        ctx.AddMethod("abort");
        return new SEnd(ctx);
    }

    public SEnd setRequestHeader(String header, String value) {
        ctx.AddMethod("setRequestHeader", header, value);
        return new SEnd(ctx);
    }

    public ValStr getResponseHeader(String header) {
        ctx.AddMethod("getResponseHeader", header);
        return new ValStr(ctx);
    }

    public ValStr getAllResponseHeaders() {
        ctx.AddMethod("getAllResponseHeaders");
        return new ValStr(ctx);
    }

    public SEnd overrideMimeType(String mime) {
        ctx.AddMethod("overrideMimeType", mime);
        return new SEnd(ctx);
    }
}
