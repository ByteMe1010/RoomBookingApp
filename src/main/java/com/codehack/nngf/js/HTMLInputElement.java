package com.codehack.nngf.js;

public class HTMLInputElement extends HTMLElement {
    public HTMLInputElement(Context ctx) {
        super(ctx);
    }

    public HTMLInputElement(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr Value() {
        ctx.AddProperty("value");
        return new ValStr(ctx);
    }

    public ValBool Checked() {
        ctx.AddProperty("checked");
        return new ValBool(ctx);
    }

    public ValStr Name() {
        ctx.AddProperty("name");
        return new ValStr(ctx);
    }

    public ValStr Placeholder() {
        ctx.AddProperty("placeholder");
        return new ValStr(ctx);
    }

    public ValBool Disabled() {
        ctx.AddProperty("disabled");
        return new ValBool(ctx);
    }

    public ValStr Type() {
        ctx.AddProperty("type");
        return new ValStr(ctx);
    }

    public SEnd select() {
        ctx.AddMethod("select");
        return new SEnd(ctx);
    }

    public SEnd focus() {
        ctx.AddMethod("focus");
        return new SEnd(ctx);
    }

    public ValStr DefaultValue() {
        ctx.AddProperty("defaultValue");
        return new ValStr(ctx);
    }

    public ValBool DefaultChecked() {
        ctx.AddProperty("defaultChecked");
        return new ValBool(ctx);
    }

    public ValBool Required() {
        ctx.AddProperty("required");
        return new ValBool(ctx);
    }

    public ValBool ReadOnly() {
        ctx.AddProperty("readOnly");
        return new ValBool(ctx);
    }

    public ValInt MaxLength() {
        ctx.AddProperty("maxLength");
        return new ValInt(ctx);
    }

    public ValInt MinLength() {
        ctx.AddProperty("minLength");
        return new ValInt(ctx);
    }

    public ValStr Pattern() {
        ctx.AddProperty("pattern");
        return new ValStr(ctx);
    }

    public ValStr Autocomplete() {
        ctx.AddProperty("autocomplete");
        return new ValStr(ctx);
    }

    public SEnd stepUp(int n) {
        ctx.AddMethod("stepUp", n);
        return new SEnd(ctx);
    }

    public SEnd stepDown(int n) {
        ctx.AddMethod("stepDown", n);
        return new SEnd(ctx);
    }

    public ValBool checkValidity() {
        ctx.AddMethod("checkValidity");
        return new ValBool(ctx);
    }

    public ValStr ValidationMessage() {
        ctx.AddProperty("validationMessage");
        return new ValStr(ctx);
    }
}
