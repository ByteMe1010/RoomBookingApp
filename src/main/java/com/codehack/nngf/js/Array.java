package com.codehack.nngf.js;

public class Array extends Prop {
    public Array(Context ctx) {
        super(ctx);
    }

    public Array(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt Length() {
        ctx.AddProperty("length");
        return new ValInt(ctx);
    }

    public ValStr join() {
        ctx.AddMethod("join");
        return new ValStr(ctx);
    }

    public ValStr join(String separator) {
        ctx.AddMethod("join", separator);
        return new ValStr(ctx);
    }

    public Prop pop() {
        ctx.AddMethod("pop");
        return new Prop(ctx);
    }

    public ValInt push(Object... items) {
        ctx.AddMethod("push", items);
        return new ValInt(ctx);
    }

    public Prop shift() {
        ctx.AddMethod("shift");
        return new Prop(ctx);
    }

    public ValInt unshift(Object... items) {
        ctx.AddMethod("unshift", items);
        return new ValInt(ctx);
    }

    public Array map(Prop callback) {
        ctx.AddMethod("map", callback);
        return new Array(ctx);
    }

    public Array filter(Prop callback) {
        ctx.AddMethod("filter", callback);
        return new Array(ctx);
    }

    public Prop find(Prop callback) {
        ctx.AddMethod("find", callback);
        return new Prop(ctx);
    }

    public ValInt findIndex(Prop callback) {
        ctx.AddMethod("findIndex", callback);
        return new ValInt(ctx);
    }

    public ValBool includes(Object searchElement) {
        ctx.AddMethod("includes", searchElement);
        return new ValBool(ctx);
    }

    public ValInt indexOf(Object searchElement) {
        ctx.AddMethod("indexOf", searchElement);
        return new ValInt(ctx);
    }

    public ValInt lastIndexOf(Object searchElement) {
        ctx.AddMethod("lastIndexOf", searchElement);
        return new ValInt(ctx);
    }

    public SEnd forEach(Prop callback) {
        ctx.AddMethod("forEach", callback);
        return new SEnd(ctx);
    }

    public Array slice(int start) {
        ctx.AddMethod("slice", start);
        return new Array(ctx);
    }

    public Array slice(int start, int end) {
        ctx.AddMethod("slice", start, end);
        return new Array(ctx);
    }

    public Array concat(Object... arrays) {
        ctx.AddMethod("concat", arrays);
        return new Array(ctx);
    }

    public Array flat(int depth) {
        ctx.AddMethod("flat", depth);
        return new Array(ctx);
    }

    public Array flatMap(Prop callback) {
        ctx.AddMethod("flatMap", callback);
        return new Array(ctx);
    }

    public Array sort() {
        ctx.AddMethod("sort");
        return new Array(ctx);
    }

    public Array sort(Prop compareFn) {
        ctx.AddMethod("sort", compareFn);
        return new Array(ctx);
    }

    public Array reverse() {
        ctx.AddMethod("reverse");
        return new Array(ctx);
    }

    public Prop reduce(Prop callback) {
        ctx.AddMethod("reduce", callback);
        return new Prop(ctx);
    }

    public Prop reduceRight(Prop callback) {
        ctx.AddMethod("reduceRight", callback);
        return new Prop(ctx);
    }

    public ValBool every(Prop callback) {
        ctx.AddMethod("every", callback);
        return new ValBool(ctx);
    }

    public ValBool some(Prop callback) {
        ctx.AddMethod("some", callback);
        return new ValBool(ctx);
    }

    public Array entries() {
        ctx.AddMethod("entries");
        return new Array(ctx);
    }

    public Array keys() {
        ctx.AddMethod("keys");
        return new Array(ctx);
    }

    public Array values() {
        ctx.AddMethod("values");
        return new Array(ctx);
    }

    public Prop fill(Object value) {
        ctx.AddMethod("fill", value);
        return this;
    }

    public Prop fill(Object value, int start) {
        ctx.AddMethod("fill", value, start);
        return this;
    }

    public Prop fill(Object value, int start, int end) {
        ctx.AddMethod("fill", value, start, end);
        return this;
    }
}
