package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLIFrameElement extends NComponet {

    public NHTMLIFrameElement(String id) {
        super(id, "iframe");
    }

    public NHTMLIFrameElement(String id, NWindow window) {
        super(id, "iframe", window);
    }

    public void src(String s)  { setAttribute("src", s); }
    public String src()  { return getAttributeValue("src"); }

    public void srcdoc(String s)  { setAttribute("srcdoc", s); }
    public String srcdoc()  { return getAttributeValue("srcdoc"); }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    public void sandbox(String s)  { setAttribute("sandbox", s); }
    public String sandbox()  { return getAttributeValue("sandbox"); }

    public void allow(String a)  { setAttribute("allow", a); }
    public String allow()  { return getAttributeValue("allow"); }

    public void allowFullscreen(boolean a)  {
        if (a) setAttribute("allowfullscreen", ""); else removeAttribute("allowfullscreen");
    }
    public boolean allowFullscreen()  { return getAttributeValue("allowfullscreen") != null; }

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

    public void referrerPolicy(String p)  { setAttribute("referrerpolicy", p); }
    public String referrerPolicy()  { return getAttributeValue("referrerpolicy"); }

    // Live-only
    public String contentWindow() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("contentWindow needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("contentWindow").ret().end());
        return v != null ? v.toString() : "";
    }

    public String contentDocument() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("contentDocument needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("contentDocument").ret().end());
        return v != null ? v.toString() : "";
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<iframe>" + childHtml + "</iframe>";
    }
}
