package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLAnchorElement extends NComponet {

    public NHTMLAnchorElement(String id) {
        super(id, "a");
        registerAnchorProperties();
    }

    public NHTMLAnchorElement(String id, NWindow window) {
        super(id, "a", window);
        registerAnchorProperties();
    }

    private void registerAnchorProperties() {
        registerProperty(PropertyDescriptor.text("href", "Href", "", this::href, "Link", 1));
        registerProperty(PropertyDescriptor.text("target", "Target", "", this::target, "Link", 1));
        registerProperty(PropertyDescriptor.text("rel", "Rel", "", this::rel, "Link", 1));
        registerProperty(PropertyDescriptor.text("download", "Download", "", this::download, "Link", 1));
    }

    public void href(String h)  { setAttribute("href", h); }
    public String href()  { return getAttributeValue("href"); }

    public void target(String t)  { setAttribute("target", t); }
    public String target()  { return getAttributeValue("target"); }

    public void rel(String r)  { setAttribute("rel", r); }
    public String rel()  { return getAttributeValue("rel"); }

    public void download(String d)  { setAttribute("download", d); }
    public String download()  { return getAttributeValue("download"); }

    public void hreflang(String h)  { setAttribute("hreflang", h); }
    public String hreflang()  { return getAttributeValue("hreflang"); }

    public void type(String t)  { setAttribute("type", t); }
    public String type()  { return getAttributeValue("type"); }

    public void ping(String p)  { setAttribute("ping", p); }
    public String ping()  { return getAttributeValue("ping"); }

    public void referrerPolicy(String p)  { setAttribute("referrerpolicy", p); }
    public String referrerPolicy()  { return getAttributeValue("referrerpolicy"); }

    public void text(String t)  {
        if (isAttached()) {
            executeChildJs(Js.id(ID).prop("text", t).end());
        }
    }
    public String text()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("text").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String protocol()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("protocol").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String host()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("host").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String hostname()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("hostname").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String port()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("port").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String pathname()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("pathname").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String search()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("search").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String hash()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("hash").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public String origin()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("origin").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<a>" + childHtml + "</a>";
    }
}
