package com.codehack.nngf.js;

public class Location extends Prop {
    public Location(Context ctx) {
        super(ctx);
    }

    public Location(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr Href() {
        ctx.AddProperty("href");
        return new ValStr(ctx);
    }

    public ValStr Protocol() {
        ctx.AddProperty("protocol");
        return new ValStr(ctx);
    }

    public ValStr Host() {
        ctx.AddProperty("host");
        return new ValStr(ctx);
    }

    public ValStr Hostname() {
        ctx.AddProperty("hostname");
        return new ValStr(ctx);
    }

    public ValStr Port() {
        ctx.AddProperty("port");
        return new ValStr(ctx);
    }

    public ValStr Pathname() {
        ctx.AddProperty("pathname");
        return new ValStr(ctx);
    }

    public ValStr Search() {
        ctx.AddProperty("search");
        return new ValStr(ctx);
    }

    public ValStr Hash() {
        ctx.AddProperty("hash");
        return new ValStr(ctx);
    }

    public ValStr Origin() {
        ctx.AddProperty("origin");
        return new ValStr(ctx);
    }

    public SEnd assign(String url) {
        ctx.AddMethod("assign", url);
        return new SEnd(ctx);
    }

    public SEnd replace(String url) {
        ctx.AddMethod("replace", url);
        return new SEnd(ctx);
    }

    public SEnd reload() {
        ctx.AddMethod("reload");
        return new SEnd(ctx);
    }
}
