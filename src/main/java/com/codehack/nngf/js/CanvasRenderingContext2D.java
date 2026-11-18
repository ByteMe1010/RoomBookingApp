package com.codehack.nngf.js;

public class CanvasRenderingContext2D extends Prop {
    public CanvasRenderingContext2D(Context ctx) {
        super(ctx);
    }

    public CanvasRenderingContext2D(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Prop Canvas() {
        ctx.AddProperty("canvas");
        return new Prop(ctx);
    }

    public ValStr FillStyle() {
        ctx.AddProperty("fillStyle");
        return new ValStr(ctx);
    }

    public ValStr StrokeStyle() {
        ctx.AddProperty("strokeStyle");
        return new ValStr(ctx);
    }

    public ValInt LineWidth() {
        ctx.AddProperty("lineWidth");
        return new ValInt(ctx);
    }

    public ValStr Font() {
        ctx.AddProperty("font");
        return new ValStr(ctx);
    }

    public ValStr TextAlign() {
        ctx.AddProperty("textAlign");
        return new ValStr(ctx);
    }

    public ValStr TextBaseline() {
        ctx.AddProperty("textBaseline");
        return new ValStr(ctx);
    }

    public ValInt GlobalAlpha() {
        ctx.AddProperty("globalAlpha");
        return new ValInt(ctx);
    }

    public SEnd fillRect(int x, int y, int w, int h) {
        ctx.AddMethod("fillRect", x, y, w, h);
        return new SEnd(ctx);
    }

    public SEnd strokeRect(int x, int y, int w, int h) {
        ctx.AddMethod("strokeRect", x, y, w, h);
        return new SEnd(ctx);
    }

    public SEnd clearRect(int x, int y, int w, int h) {
        ctx.AddMethod("clearRect", x, y, w, h);
        return new SEnd(ctx);
    }

    public SEnd fillText(String text, int x, int y) {
        ctx.AddMethod("fillText", text, x, y);
        return new SEnd(ctx);
    }

    public SEnd strokeText(String text, int x, int y) {
        ctx.AddMethod("strokeText", text, x, y);
        return new SEnd(ctx);
    }

    public ValInt measureText(String text) {
        ctx.AddMethod("measureText", text);
        return new ValInt(ctx);
    }

    public SEnd beginPath() {
        ctx.AddMethod("beginPath");
        return new SEnd(ctx);
    }

    public SEnd closePath() {
        ctx.AddMethod("closePath");
        return new SEnd(ctx);
    }

    public SEnd moveTo(int x, int y) {
        ctx.AddMethod("moveTo", x, y);
        return new SEnd(ctx);
    }

    public SEnd lineTo(int x, int y) {
        ctx.AddMethod("lineTo", x, y);
        return new SEnd(ctx);
    }

    public SEnd arc(int x, int y, int r, int startAngle, int endAngle) {
        ctx.AddMethod("arc", x, y, r, startAngle, endAngle);
        return new SEnd(ctx);
    }

    public SEnd arc(int x, int y, int r, int startAngle, int endAngle, boolean counterclockwise) {
        ctx.AddMethod("arc", x, y, r, startAngle, endAngle, counterclockwise);
        return new SEnd(ctx);
    }

    public SEnd fill() {
        ctx.AddMethod("fill");
        return new SEnd(ctx);
    }

    public SEnd stroke() {
        ctx.AddMethod("stroke");
        return new SEnd(ctx);
    }

    public SEnd clip() {
        ctx.AddMethod("clip");
        return new SEnd(ctx);
    }

    public SEnd save() {
        ctx.AddMethod("save");
        return new SEnd(ctx);
    }

    public SEnd restore() {
        ctx.AddMethod("restore");
        return new SEnd(ctx);
    }

    public SEnd scale(int x, int y) {
        ctx.AddMethod("scale", x, y);
        return new SEnd(ctx);
    }

    public SEnd rotate(int angle) {
        ctx.AddMethod("rotate", angle);
        return new SEnd(ctx);
    }

    public SEnd translate(int x, int y) {
        ctx.AddMethod("translate", x, y);
        return new SEnd(ctx);
    }

    public SEnd drawImage(Prop image, int dx, int dy) {
        ctx.AddMethod("drawImage", image, dx, dy);
        return new SEnd(ctx);
    }

    public SEnd drawImage(Prop image, int dx, int dy, int dw, int dh) {
        ctx.AddMethod("drawImage", image, dx, dy, dw, dh);
        return new SEnd(ctx);
    }
}
