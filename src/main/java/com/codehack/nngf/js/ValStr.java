package com.codehack.nngf.js;

public class ValStr extends Prop {
    public ValStr(Context ctx) {
        super(ctx);
    }

    public ValStr(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public ValStr trim() {
        ctx.AddMethod("trim");
        return this;
    }

    public ValStr trimStart() {
        ctx.AddMethod("trimStart");
        return this;
    }

    public ValStr trimEnd() {
        ctx.AddMethod("trimEnd");
        return this;
    }

    public ValBool includes(String search) {
        ctx.AddMethod("includes", search);
        return new ValBool(ctx);
    }

    public ValBool startsWith(String search) {
        ctx.AddMethod("startsWith", search);
        return new ValBool(ctx);
    }

    public ValBool endsWith(String search) {
        ctx.AddMethod("endsWith", search);
        return new ValBool(ctx);
    }

    public ValStr match(RegExp regexp) {
        ctx.AddMethod("match", regexp);
        return this;
    }

    public ValStr matchAll(RegExp regexp) {
        ctx.AddMethod("matchAll", regexp);
        return this;
    }

    public ValStr match(String regexp) {
        ctx.AddMethod("match", regexp);
        return this;
    }

    public ValStr replace(String search, String replacement) {
        ctx.AddMethod("replace", search, replacement);
        return this;
    }

    public ValStr replaceAll(String search, String replacement) {
        ctx.AddMethod("replaceAll", search, replacement);
        return this;
    }

    public Array split(String separator) {
        ctx.AddMethod("split", separator);
        return new Array(ctx);
    }

    public Array split(String separator, int limit) {
        ctx.AddMethod("split", separator, limit);
        return new Array(ctx);
    }

    public ValStr slice(int start) {
        ctx.AddMethod("slice", start);
        return this;
    }

    public ValStr slice(int start, int end) {
        ctx.AddMethod("slice", start, end);
        return this;
    }

    public ValStr toLowerCase() {
        ctx.AddMethod("toLowerCase");
        return this;
    }

    public ValStr toUpperCase() {
        ctx.AddMethod("toUpperCase");
        return this;
    }

    public ValInt indexOf(String search) {
        ctx.AddMethod("indexOf", search);
        return new ValInt(ctx);
    }

    public ValInt lastIndexOf(String search) {
        ctx.AddMethod("lastIndexOf", search);
        return new ValInt(ctx);
    }

    public ValStr charAt(int index) {
        ctx.AddMethod("charAt", index);
        return this;
    }

    public ValInt charCodeAt(int index) {
        ctx.AddMethod("charCodeAt", index);
        return new ValInt(ctx);
    }

    public ValStr concat(String... strings) {
        ctx.AddMethod("concat", (Object[]) strings);
        return this;
    }

    public ValStr substring(int start) {
        ctx.AddMethod("substring", start);
        return this;
    }

    public ValStr substring(int start, int end) {
        ctx.AddMethod("substring", start, end);
        return this;
    }

    public ValStr substr(int start) {
        ctx.AddMethod("substr", start);
        return this;
    }

    public ValStr substr(int start, int length) {
        ctx.AddMethod("substr", start, length);
        return this;
    }

    public ValInt search(RegExp regexp) {
        ctx.AddMethod("search", regexp);
        return new ValInt(ctx);
    }

    public ValStr padStart(int targetLength) {
        ctx.AddMethod("padStart", targetLength);
        return this;
    }

    public ValStr padStart(int targetLength, String padString) {
        ctx.AddMethod("padStart", targetLength, padString);
        return this;
    }

    public ValStr padEnd(int targetLength) {
        ctx.AddMethod("padEnd", targetLength);
        return this;
    }

    public ValStr padEnd(int targetLength, String padString) {
        ctx.AddMethod("padEnd", targetLength, padString);
        return this;
    }

    public ValStr repeat(int count) {
        ctx.AddMethod("repeat", count);
        return this;
    }

    public ValStr ValueOf() {
        ctx.AddProperty("valueOf");
        return this;
    }
}
