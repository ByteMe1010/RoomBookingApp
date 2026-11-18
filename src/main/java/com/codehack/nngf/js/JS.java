package com.codehack.nngf.js;

import org.unbescape.javascript.JavaScriptEscape;

public class JS {
    public static Document Document() {
        Context ctx = new Context();
        ctx.AddProperty("document");
        return new Document(ctx);
    }

    public static Window Window() {
        Context ctx = new Context();
        ctx.AddProperty("window");
        return new Window(ctx);
    }

    public static Element Element() {
        Context ctx = new Context();
        ctx.AddProperty("element");
        return new Element(ctx);
    }

    public static Event Event() {
        Context ctx = new Context();
        ctx.AddProperty("event");
        return new Event(ctx);
    }

    public static MouseEvent MouseEvent() {
        Context ctx = new Context();
        ctx.AddProperty("mouseEvent");
        return new MouseEvent(ctx);
    }

    public static Node Node() {
        Context ctx = new Context();
        ctx.AddProperty("node");
        return new Node(ctx);
    }

    public static Prop Var(String name) {
        Context ctx = new Context();
        ctx.AddProperty(name);
        return new Prop(ctx);
    }

    public static <T extends Prop> T Var(String name, Class<T> type) {
        Context ctx = new Context();
        ctx.AddProperty(name);
        try {
            return type.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Prop Prop(String name) {
        Context ctx = new Context();
        ctx.AddProperty(name);
        return new Prop(ctx);
    }

    public static <T extends Prop> T Prop(String name, Class<T> type) {
        Context ctx = new Context();
        ctx.AddProperty(name);
        try {
            return type.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Console Console() {
        Context ctx = new Context();
        ctx.AddProperty("console");
        return new Console(ctx);
    }

    public static JSON JSON() {
        Context ctx = new Context();
        ctx.AddProperty("JSON");
        return new JSON(ctx);
    }

    public static Math Math() {
        Context ctx = new Context();
        ctx.AddProperty("Math");
        return new Math(ctx);
    }

    public static Storage LocalStorage() {
        Context ctx = new Context();
        ctx.AddProperty("localStorage");
        return new Storage(ctx);
    }

    public static Storage SessionStorage() {
        Context ctx = new Context();
        ctx.AddProperty("sessionStorage");
        return new Storage(ctx);
    }

    public static Navigator Navigator() {
        Context ctx = new Context();
        ctx.AddProperty("navigator");
        return new Navigator(ctx);
    }

    public static Location Location() {
        Context ctx = new Context();
        ctx.AddProperty("location");
        return new Location(ctx);
    }

    public static History History() {
        Context ctx = new Context();
        ctx.AddProperty("history");
        return new History(ctx);
    }

    public static Screen Screen() {
        Context ctx = new Context();
        ctx.AddProperty("screen");
        return new Screen(ctx);
    }

    public static URL URL() {
        Context ctx = new Context();
        ctx.AddProperty("URL");
        return new URL(ctx);
    }

    public static Array Array() {
        Context ctx = new Context();
        ctx.AddProperty("Array");
        return new Array(ctx);
    }

    public static RegExp RegExp() {
        Context ctx = new Context();
        ctx.AddProperty("RegExp");
        return new RegExp(ctx);
    }

    public static Date Date() {
        Context ctx = new Context();
        ctx.AddProperty("Date");
        return new Date(ctx);
    }

    public static Promise Promise() {
        Context ctx = new Context();
        ctx.AddProperty("Promise");
        return new Promise(ctx);
    }

    public static FormData FormData() {
        Context ctx = new Context();
        ctx.AddProperty("formData");
        return new FormData(ctx);
    }

    public static Headers Headers() {
        Context ctx = new Context();
        ctx.AddProperty("Headers");
        return new Headers(ctx);
    }

    public static AbortController AbortController() {
        Context ctx = new Context();
        ctx.AddProperty("AbortController");
        return new AbortController(ctx);
    }

    public static WebSocket WebSocket() {
        Context ctx = new Context();
        ctx.AddProperty("WebSocket");
        return new WebSocket(ctx);
    }

    public static Worker Worker() {
        Context ctx = new Context();
        ctx.AddProperty("Worker");
        return new Worker(ctx);
    }

    public static Blob Blob() {
        Context ctx = new Context();
        ctx.AddProperty("Blob");
        return new Blob(ctx);
    }

    // --- Bridge factory methods (compatible with Core.Js) ---

    public static Element el() {
        Context ctx = new Context();
        ctx.AddProperty("element");
        return new Element(ctx);
    }

    public static Event evt() {
        Context ctx = new Context();
        ctx.AddProperty("e");
        return new Event(ctx);
    }

    public static Element id(String id) {
        Context ctx = new Context();
        ctx.AddProperty("document.getElementById('" + JavaScriptEscape.escapeJavaScript(id) + "')");
        return new Element(ctx);
    }

    public static Prop expr() {
        Context ctx = new Context();
        return new Prop(ctx);
    }

    public static Prop global(String name) {
        Context ctx = new Context();
        ctx.AddProperty(name);
        return new Prop(ctx);
    }

    // --- End bridge factory methods ---

    public static Raw Expr(String rawJs) {
        return new Raw(rawJs);
    }

    public static String Assign(String name, Object value) {
        return name + " = " + resolveValue(value) + ";";
    }

    public static String If(Object condition, Object thenBlock) {
        return "if (" + resolveValue(condition) + ") { " + resolveBlock(thenBlock) + " }";
    }

    public static String If(Object condition, Object thenBlock, Object elseBlock) {
        return "if (" + resolveValue(condition) + ") { " + resolveBlock(thenBlock) + " } else { " + resolveBlock(elseBlock) + " }";
    }

    public static String Try(Object tryBlock, String catchVar, Object catchBlock) {
        return "try { " + resolveBlock(tryBlock) + " } catch (" + catchVar + ") { " + resolveBlock(catchBlock) + " }";
    }

    public static String Block(Object... statements) {
        StringBuilder sb = new StringBuilder();
        for (Object stmt : statements) {
            sb.append(resolveBlock(stmt));
        }
        return sb.toString();
    }

    public static String Eq(Object a, Object b) {
        return resolveValue(a) + " === " + resolveValue(b);
    }

    public static String Neq(Object a, Object b) {
        return resolveValue(a) + " !== " + resolveValue(b);
    }

    public static String Gt(Object a, Object b) {
        return resolveValue(a) + " > " + resolveValue(b);
    }

    public static String Lt(Object a, Object b) {
        return resolveValue(a) + " < " + resolveValue(b);
    }

    public static String Gte(Object a, Object b) {
        return resolveValue(a) + " >= " + resolveValue(b);
    }

    public static String Lte(Object a, Object b) {
        return resolveValue(a) + " <= " + resolveValue(b);
    }

    public static String And(Object a, Object b) {
        return resolveValue(a) + " && " + resolveValue(b);
    }

    public static String Or(Object a, Object b) {
        return resolveValue(a) + " || " + resolveValue(b);
    }

    public static String Not(Object a) {
        return "!" + resolveValue(a);
    }

    private static String resolveValue(Object o) {
        if (o instanceof Prop) return ((Prop) o).getExpr();
        return ValueProcesser.convert(o);
    }

    private static String resolveBlock(Object o) {
        if (o instanceof String s) {
            return s.endsWith(";") ? s : s + ";";
        }
        if (o instanceof SEnd) return o.toString();
        return resolveValue(o) + ";";
    }
}
