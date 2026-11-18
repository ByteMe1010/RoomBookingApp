package com.codehack.nngf.js;

public class FileReader extends Prop {
    public FileReader(Context ctx) {
        super(ctx);
    }

    public FileReader(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd readAsArrayBuffer(Blob blob) {
        ctx.AddMethod("readAsArrayBuffer", blob);
        return new SEnd(ctx);
    }

    public SEnd readAsBinaryString(Blob blob) {
        ctx.AddMethod("readAsBinaryString", blob);
        return new SEnd(ctx);
    }

    public SEnd readAsDataURL(Blob blob) {
        ctx.AddMethod("readAsDataURL", blob);
        return new SEnd(ctx);
    }

    public SEnd readAsText(Blob blob) {
        ctx.AddMethod("readAsText", blob);
        return new SEnd(ctx);
    }

    public SEnd readAsText(Blob blob, String encoding) {
        ctx.AddMethod("readAsText", blob, encoding);
        return new SEnd(ctx);
    }

    public SEnd abort() {
        ctx.AddMethod("abort");
        return new SEnd(ctx);
    }

    public ValInt ReadyState() {
        ctx.AddProperty("readyState");
        return new ValInt(ctx);
    }

    public Prop Result() {
        ctx.AddProperty("result");
        return new Prop(ctx);
    }

    public Prop Error() {
        ctx.AddProperty("error");
        return new Prop(ctx);
    }
}
