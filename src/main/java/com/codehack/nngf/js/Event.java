package com.codehack.nngf.js;

public class Event extends Prop {
    public Event(Context ctx) {
        super(ctx);
    }

    public Event(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Element Target() {
        ctx.AddProperty("target");
        return new Element(ctx);
    }

    public Element CurrentTarget() {
        ctx.AddProperty("currentTarget");
        return new Element(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public SEnd preventDefault() {
        ctx.AddMethod("preventDefault");
        return new SEnd(ctx);
    }

    public SEnd stopPropagation() {
        ctx.AddMethod("stopPropagation");
        return new SEnd(ctx);
    }

    public SEnd stopImmediatePropagation() {
        ctx.AddMethod("stopImmediatePropagation");
        return new SEnd(ctx);
    }

    public ValBool Bubbles() {
        ctx.AddProperty("bubbles");
        return new ValBool(ctx);
    }

    public ValBool Cancelable() {
        ctx.AddProperty("cancelable");
        return new ValBool(ctx);
    }

    public ValBool DefaultPrevented() {
        ctx.AddProperty("defaultPrevented");
        return new ValBool(ctx);
    }

    public ValInt EventPhase() {
        ctx.AddProperty("eventPhase");
        return new ValInt(ctx);
    }

    public Prop Detail() {
        ctx.AddProperty("detail");
        return new Prop(ctx);
    }

    public Date TimeStamp() {
        ctx.AddProperty("timeStamp");
        return new Date(ctx);
    }

    public ValBool IsTrusted() {
        ctx.AddProperty("isTrusted");
        return new ValBool(ctx);
    }

    public Prop composedPath() {
        ctx.AddMethod("composedPath");
        return new Prop(ctx);
    }
}
