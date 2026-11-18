package com.codehack.nngf.js;

public class Window extends Prop {
    public Window(Context ctx) {
        super(ctx);
    }

    public Window(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public Document Document() {
        ctx.AddProperty("document");
        return new Document(ctx);
    }

    public Navigator Navigator() {
        ctx.AddProperty("navigator");
        return new Navigator(ctx);
    }

    public Location Location() {
        ctx.AddProperty("location");
        return new Location(ctx);
    }

    public History History() {
        ctx.AddProperty("history");
        return new History(ctx);
    }

    public Screen Screen() {
        ctx.AddProperty("screen");
        return new Screen(ctx);
    }

    public Storage LocalStorage() {
        ctx.AddProperty("localStorage");
        return new Storage(ctx);
    }

    public Storage SessionStorage() {
        ctx.AddProperty("sessionStorage");
        return new Storage(ctx);
    }

    public Console Console() {
        ctx.AddProperty("console");
        return new Console(ctx);
    }

    public Window Self() {
        ctx.AddProperty("self");
        return this;
    }

    public Window Top() {
        ctx.AddProperty("top");
        return new Window(ctx);
    }

    public Window Parent() {
        ctx.AddProperty("parent");
        return new Window(ctx);
    }

    public Window Opener() {
        ctx.AddProperty("opener");
        return new Window(ctx);
    }

    public ValInt InnerWidth() {
        ctx.AddProperty("innerWidth");
        return new ValInt(ctx);
    }

    public ValInt InnerHeight() {
        ctx.AddProperty("innerHeight");
        return new ValInt(ctx);
    }

    public ValInt OuterWidth() {
        ctx.AddProperty("outerWidth");
        return new ValInt(ctx);
    }

    public ValInt OuterHeight() {
        ctx.AddProperty("outerHeight");
        return new ValInt(ctx);
    }

    public ValInt ScrollX() {
        ctx.AddProperty("scrollX");
        return new ValInt(ctx);
    }

    public ValInt ScrollY() {
        ctx.AddProperty("scrollY");
        return new ValInt(ctx);
    }

    public ValInt PageXOffset() {
        ctx.AddProperty("pageXOffset");
        return new ValInt(ctx);
    }

    public ValInt PageYOffset() {
        ctx.AddProperty("pageYOffset");
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

    public SEnd alert(String message) {
        ctx.AddMethod("alert", message);
        return new SEnd(ctx);
    }

    public ValBool confirm(String message) {
        ctx.AddMethod("confirm", message);
        return new ValBool(ctx);
    }

    public ValStr prompt(String message) {
        ctx.AddMethod("prompt", message);
        return new ValStr(ctx);
    }

    public ValStr prompt(String message, String defaultValue) {
        ctx.AddMethod("prompt", message, defaultValue);
        return new ValStr(ctx);
    }

    public ValInt setTimeout(Prop handler, int timeout) {
        ctx.AddMethod("setTimeout", handler, timeout);
        return new ValInt(ctx);
    }

    public ValInt setInterval(Prop handler, int interval) {
        ctx.AddMethod("setInterval", handler, interval);
        return new ValInt(ctx);
    }

    public SEnd clearTimeout(int id) {
        ctx.AddMethod("clearTimeout", id);
        return new SEnd(ctx);
    }

    public SEnd clearInterval(int id) {
        ctx.AddMethod("clearInterval", id);
        return new SEnd(ctx);
    }

    public Prop requestAnimationFrame(Prop callback) {
        ctx.AddMethod("requestAnimationFrame", callback);
        return new Prop(ctx);
    }

    public SEnd cancelAnimationFrame(int id) {
        ctx.AddMethod("cancelAnimationFrame", id);
        return new SEnd(ctx);
    }

    public SEnd scrollTo(int x, int y) {
        ctx.AddMethod("scrollTo", x, y);
        return new SEnd(ctx);
    }

    public SEnd scrollBy(int x, int y) {
        ctx.AddMethod("scrollBy", x, y);
        return new SEnd(ctx);
    }

    public Prop fetch(String url) {
        ctx.AddMethod("fetch", url);
        return new Prop(ctx);
    }

    public Prop fetch(String url, Prop options) {
        ctx.AddMethod("fetch", url, options);
        return new Prop(ctx);
    }

    public Prop btoa(String data) {
        ctx.AddMethod("btoa", data);
        return new Prop(ctx);
    }

    public Prop atob(String data) {
        ctx.AddMethod("atob", data);
        return new Prop(ctx);
    }

    public Prop Caches() {
        ctx.AddProperty("caches");
        return new Prop(ctx);
    }

    public Prop CookieStore() {
        ctx.AddProperty("cookieStore");
        return new Prop(ctx);
    }

    public Prop CrossOriginIsolated() {
        ctx.AddProperty("crossOriginIsolated");
        return new Prop(ctx);
    }

    public ValInt DevicePixelRatio() {
        ctx.AddProperty("devicePixelRatio");
        return new ValInt(ctx);
    }

    public Prop FrameElement() {
        ctx.AddProperty("frameElement");
        return new Prop(ctx);
    }

    public Window[] Frames() {
        ctx.AddProperty("frames");
        return new Window[]{this};
    }

    public Prop IndexedDB() {
        ctx.AddProperty("indexedDB");
        return new Prop(ctx);
    }

    public Performance Performance() {
        ctx.AddProperty("performance");
        return new Performance(ctx);
    }

    public Prop Scheduler() {
        ctx.AddProperty("scheduler");
        return new Prop(ctx);
    }

    public Storage Storage() {
        ctx.AddProperty("storage");
        return new Storage(ctx);
    }

    public Prop VisualViewport() {
        ctx.AddProperty("visualViewport");
        return new Prop(ctx);
    }

    public ValStr Name() {
        ctx.AddProperty("name");
        return new ValStr(ctx);
    }

    public SEnd postMessage(Object message) {
        ctx.AddMethod("postMessage", message);
        return new SEnd(ctx);
    }

    public SEnd postMessage(Object message, String targetOrigin) {
        ctx.AddMethod("postMessage", message, targetOrigin);
        return new SEnd(ctx);
    }

    public SEnd print() {
        ctx.AddMethod("print");
        return new SEnd(ctx);
    }

    public Prop structuredClone(Object value) {
        ctx.AddMethod("structuredClone", value);
        return new Prop(ctx);
    }
}
