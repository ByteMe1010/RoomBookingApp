package com.codehack.nngf.js;

public class HTMLElement extends Element {
    public HTMLElement(Context ctx) {
        super(ctx);
    }

    public HTMLElement(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public SEnd click() {
        ctx.AddMethod("click");
        return new SEnd(ctx);
    }

    public SEnd focus() {
        ctx.AddMethod("focus");
        return new SEnd(ctx);
    }

    public SEnd blur() {
        ctx.AddMethod("blur");
        return new SEnd(ctx);
    }

    public ValInt OffsetTop() {
        ctx.AddProperty("offsetTop");
        return new ValInt(ctx);
    }

    public ValInt OffsetLeft() {
        ctx.AddProperty("offsetLeft");
        return new ValInt(ctx);
    }

    public ValInt OffsetWidth() {
        ctx.AddProperty("offsetWidth");
        return new ValInt(ctx);
    }

    public ValInt OffsetHeight() {
        ctx.AddProperty("offsetHeight");
        return new ValInt(ctx);
    }

    public ValInt ScrollTop() {
        ctx.AddProperty("scrollTop");
        return new ValInt(ctx);
    }

    public ValInt ScrollLeft() {
        ctx.AddProperty("scrollLeft");
        return new ValInt(ctx);
    }

    public ValInt ClientWidth() {
        ctx.AddProperty("clientWidth");
        return new ValInt(ctx);
    }

    public ValInt ClientHeight() {
        ctx.AddProperty("clientHeight");
        return new ValInt(ctx);
    }

    public ValInt ScrollWidth() {
        ctx.AddProperty("scrollWidth");
        return new ValInt(ctx);
    }

    public ValInt ScrollHeight() {
        ctx.AddProperty("scrollHeight");
        return new ValInt(ctx);
    }

    public SEnd scrollIntoView(boolean alignToTop) {
        ctx.AddMethod("scrollIntoView", alignToTop);
        return new SEnd(ctx);
    }

    public ValStr InnerText() {
        ctx.AddProperty("innerText");
        return new ValStr(ctx);
    }

    public ValStr Title() {
        ctx.AddProperty("title");
        return new ValStr(ctx);
    }

    public ValStr Lang() {
        ctx.AddProperty("lang");
        return new ValStr(ctx);
    }

    public ValBool Draggable() {
        ctx.AddProperty("draggable");
        return new ValBool(ctx);
    }

    public ValStr AccessKey() {
        ctx.AddProperty("accessKey");
        return new ValStr(ctx);
    }

    public SEnd showPopover() {
        ctx.AddMethod("showPopover");
        return new SEnd(ctx);
    }

    public SEnd hidePopover() {
        ctx.AddMethod("hidePopover");
        return new SEnd(ctx);
    }

    public SEnd togglePopover() {
        ctx.AddMethod("togglePopover");
        return new SEnd(ctx);
    }
}
