package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLImageElement extends NComponet {

    public NHTMLImageElement(String id) {
        super(id, "img");
        registerImageProperties();
    }

    public NHTMLImageElement(String id, NWindow window) {
        super(id, "img", window);
        registerImageProperties();
    }

    private void registerImageProperties() {
        registerProperty(PropertyDescriptor.text("src", "Source", "", this::src, "Media", 1));
        registerProperty(PropertyDescriptor.text("alt", "Alt Text", "", this::alt, "Media", 1));
        registerProperty(PropertyDescriptor.text("width", "Width", "", v -> width(Integer.parseInt(v)), "Media", 1));
        registerProperty(PropertyDescriptor.text("height", "Height", "", v -> height(Integer.parseInt(v)), "Media", 1));
        registerProperty(PropertyDescriptor.text("loading", "Loading", "lazy", this::loading, "Media", 1));
    }

    public void src(String s)  { setAttribute("src", s); }
    public String src()  { return getAttributeValue("src"); }

    public void alt(String a)  { setAttribute("alt", a); }
    public String alt()  { return getAttributeValue("alt"); }

    public void width(int w)  { setAttribute("width", String.valueOf(w)); }
    public int width()  {
        String v = getAttributeValue("width");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void height(int h)  { setAttribute("height", String.valueOf(h)); }
    public int height()  {
        String v = getAttributeValue("height");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void loading(String l)  { setAttribute("loading", l); }
    public String loading()  { return getAttributeValue("loading"); }

    public void decoding(String d)  { setAttribute("decoding", d); }
    public String decoding()  { return getAttributeValue("decoding"); }

    public void srcset(String s)  { setAttribute("srcset", s); }
    public String srcset()  { return getAttributeValue("srcset"); }

    public void sizes(String s)  { setAttribute("sizes", s); }
    public String sizes()  { return getAttributeValue("sizes"); }

    public void crossOrigin(String c)  { setAttribute("crossorigin", c); }
    public String crossOrigin()  { return getAttributeValue("crossorigin"); }

    public void referrerPolicy(String p)  { setAttribute("referrerpolicy", p); }
    public String referrerPolicy()  { return getAttributeValue("referrerpolicy"); }

    // Live-only
    public int naturalWidth() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("naturalWidth needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("naturalWidth").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public int naturalHeight() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("naturalHeight needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("naturalHeight").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public boolean complete() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("complete needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("complete").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public String currentSrc() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("currentSrc needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("currentSrc").ret().end());
        return v != null ? v.toString() : "";
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<img>";
    }
}
