package com.codehack.nngf.js;

public class File extends Blob {
    public File(Context ctx) {
        super(ctx);
    }

    public File(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr Name() {
        ctx.AddProperty("name");
        return new ValStr(ctx);
    }

    public ValInt LastModified() {
        ctx.AddProperty("lastModified");
        return new ValInt(ctx);
    }

    public ValStr WebkitRelativePath() {
        ctx.AddProperty("webkitRelativePath");
        return new ValStr(ctx);
    }
}
