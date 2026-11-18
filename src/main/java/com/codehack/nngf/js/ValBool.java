package com.codehack.nngf.js;

public class ValBool extends Prop {
    public ValBool(Context ctx) {
        super(ctx);
    }

    public ValBool(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValBool ValueOf() {
        ctx.AddProperty("valueOf");
        return this;
    }
}
