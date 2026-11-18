package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLVideoElement extends NHTMLMediaElement {

    public NHTMLVideoElement(String id) {
        super(id, "video");
    }

    public NHTMLVideoElement(String id, NWindow window) {
        super(id, "video", window);
    }

    public void poster(String p)  { setAttribute("poster", p); }
    public String poster()  { return getAttributeValue("poster"); }

    public void playsInline(boolean p)  {
        if (p) setAttribute("playsinline", ""); else removeAttribute("playsinline");
    }
    public boolean playsInline()  { return getAttributeValue("playsinline") != null; }

    // Live-only
    public int videoWidth() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("videoWidth needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("videoWidth").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public int videoHeight() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("videoHeight needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("videoHeight").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<video>" + childHtml + "</video>";
    }
}
