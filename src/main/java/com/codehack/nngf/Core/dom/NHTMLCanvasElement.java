package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLCanvasElement extends NComponet {

    public NHTMLCanvasElement(String id) {
        super(id, "canvas");
    }

    public NHTMLCanvasElement(String id, NWindow window) {
        super(id, "canvas", window);
    }

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

    // Live-only
    public String toDataURL(String type, double quality) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("toDataURL() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("toDataURL", type, quality).ret().end());
        return v != null ? v.toString() : "";
    }

    public String toDataURL(String type) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("toDataURL() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("toDataURL", type).ret().end());
        return v != null ? v.toString() : "";
    }

    public String toDataURL() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("toDataURL() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("toDataURL").ret().end());
        return v != null ? v.toString() : "";
    }

    public void toBlob(String callback, String type, double quality) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("toBlob() needs live DOM");
        executeChildJs(Js.id(ID).call("toBlob", callback, type, quality).end());
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<canvas>" + childHtml + "</canvas>";
    }
}
