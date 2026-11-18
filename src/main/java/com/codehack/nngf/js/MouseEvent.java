package com.codehack.nngf.js;

public class MouseEvent extends Event {
    public MouseEvent(Context ctx) {
        super(ctx);
    }

    public MouseEvent(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt ClientX() {
        ctx.AddProperty("clientX");
        return new ValInt(ctx);
    }

    public ValInt ClientY() {
        ctx.AddProperty("clientY");
        return new ValInt(ctx);
    }

    public ValInt ScreenX() {
        ctx.AddProperty("screenX");
        return new ValInt(ctx);
    }

    public ValInt ScreenY() {
        ctx.AddProperty("screenY");
        return new ValInt(ctx);
    }

    public ValInt PageX() {
        ctx.AddProperty("pageX");
        return new ValInt(ctx);
    }

    public ValInt PageY() {
        ctx.AddProperty("pageY");
        return new ValInt(ctx);
    }

    public ValInt OffsetX() {
        ctx.AddProperty("offsetX");
        return new ValInt(ctx);
    }

    public ValInt OffsetY() {
        ctx.AddProperty("offsetY");
        return new ValInt(ctx);
    }

    public ValInt Button() {
        ctx.AddProperty("button");
        return new ValInt(ctx);
    }

    public ValInt Buttons() {
        ctx.AddProperty("buttons");
        return new ValInt(ctx);
    }

    public ValBool AltKey() {
        ctx.AddProperty("altKey");
        return new ValBool(ctx);
    }

    public ValBool CtrlKey() {
        ctx.AddProperty("ctrlKey");
        return new ValBool(ctx);
    }

    public ValBool ShiftKey() {
        ctx.AddProperty("shiftKey");
        return new ValBool(ctx);
    }

    public ValBool MetaKey() {
        ctx.AddProperty("metaKey");
        return new ValBool(ctx);
    }

    public Element RelatedTarget() {
        ctx.AddProperty("relatedTarget");
        return new Element(ctx);
    }

    public ValStr Region() {
        ctx.AddProperty("region");
        return new ValStr(ctx);
    }
}
