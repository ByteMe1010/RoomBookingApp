package com.codehack.nngf.js;

public class Navigator extends Prop {
    public Navigator(Context ctx) {
        super(ctx);
    }

    public Navigator(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValStr UserAgent() {
        ctx.AddProperty("userAgent");
        return new ValStr(ctx);
    }

    public ValStr AppName() {
        ctx.AddProperty("appName");
        return new ValStr(ctx);
    }

    public ValStr AppVersion() {
        ctx.AddProperty("appVersion");
        return new ValStr(ctx);
    }

    public ValStr Platform() {
        ctx.AddProperty("platform");
        return new ValStr(ctx);
    }

    public ValStr Language() {
        ctx.AddProperty("language");
        return new ValStr(ctx);
    }

    public Array Languages() {
        ctx.AddProperty("languages");
        return new Array(ctx);
    }

    public ValBool CookieEnabled() {
        ctx.AddProperty("cookieEnabled");
        return new ValBool(ctx);
    }

    public ValBool Online() {
        ctx.AddProperty("onLine");
        return new ValBool(ctx);
    }

    public Prop MediaDevices() {
        ctx.AddProperty("mediaDevices");
        return new Prop(ctx);
    }

    public Prop Geolocation() {
        ctx.AddProperty("geolocation");
        return new Prop(ctx);
    }

    public Prop ServiceWorker() {
        ctx.AddProperty("serviceWorker");
        return new Prop(ctx);
    }

    public Prop Clipboard() {
        ctx.AddProperty("clipboard");
        return new Prop(ctx);
    }

    public Prop Permissions() {
        ctx.AddProperty("permissions");
        return new Prop(ctx);
    }

    public Prop Storage() {
        ctx.AddProperty("storage");
        return new Prop(ctx);
    }

    public Prop Connection() {
        ctx.AddProperty("connection");
        return new Prop(ctx);
    }

    public Prop Battery() {
        ctx.AddProperty("battery");
        return new Prop(ctx);
    }

    public Prop Credentials() {
        ctx.AddProperty("credentials");
        return new Prop(ctx);
    }

    public Prop Usb() {
        ctx.AddProperty("usb");
        return new Prop(ctx);
    }

    public Prop Bluetooth() {
        ctx.AddProperty("bluetooth");
        return new Prop(ctx);
    }

    public Prop Serial() {
        ctx.AddProperty("serial");
        return new Prop(ctx);
    }

    public Prop Hid() {
        ctx.AddProperty("hid");
        return new Prop(ctx);
    }

    public Prop Wifi() {
        ctx.AddProperty("wifi");
        return new Prop(ctx);
    }

    public Prop Locks() {
        ctx.AddProperty("locks");
        return new Prop(ctx);
    }

    public Prop Scheduling() {
        ctx.AddProperty("scheduling");
        return new Prop(ctx);
    }

    public ValStr Vendor() {
        ctx.AddProperty("vendor");
        return new ValStr(ctx);
    }

    public ValStr VendorSub() {
        ctx.AddProperty("vendorSub");
        return new ValStr(ctx);
    }

    public ValStr Product() {
        ctx.AddProperty("product");
        return new ValStr(ctx);
    }

    public ValStr ProductSub() {
        ctx.AddProperty("productSub");
        return new ValStr(ctx);
    }

    public ValBool javaEnabled() {
        ctx.AddMethod("javaEnabled");
        return new ValBool(ctx);
    }

    public Prop registerProtocolHandler(String scheme, String url, String title) {
        ctx.AddMethod("registerProtocolHandler", scheme, url, title);
        return new Prop(ctx);
    }

    public Prop unregisterProtocolHandler(String scheme, String url) {
        ctx.AddMethod("unregisterProtocolHandler", scheme, url);
        return new Prop(ctx);
    }

    public Prop sendBeacon(String url, Object data) {
        ctx.AddMethod("sendBeacon", url, data);
        return new Prop(ctx);
    }

    public ValBool vibrate(Object pattern) {
        ctx.AddMethod("vibrate", pattern);
        return new ValBool(ctx);
    }

    public Array getGamepads() {
        ctx.AddMethod("getGamepads");
        return new Array(ctx);
    }
}
