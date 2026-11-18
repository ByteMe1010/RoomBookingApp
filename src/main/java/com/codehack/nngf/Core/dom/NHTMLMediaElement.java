package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLMediaElement extends NComponet {

    protected NHTMLMediaElement(String id, String elementName) {
        super(id, elementName);
    }

    protected NHTMLMediaElement(String id, String elementName, NWindow window) {
        super(id, elementName, window);
    }

    public void src(String s)  { setAttribute("src", s); }
    public String src()  { return getAttributeValue("src"); }

    public void crossOrigin(String c)  { setAttribute("crossorigin", c); }
    public String crossOrigin()  { return getAttributeValue("crossorigin"); }

    public void preload(String p)  { setAttribute("preload", p); }
    public String preload()  { return getAttributeValue("preload"); }

    public void autoplay(boolean a)  {
        if (a) setAttribute("autoplay", ""); else removeAttribute("autoplay");
    }
    public boolean autoplay()  { return getAttributeValue("autoplay") != null; }

    public void loop(boolean l)  {
        if (l) setAttribute("loop", ""); else removeAttribute("loop");
    }
    public boolean loop()  { return getAttributeValue("loop") != null; }

    public void muted(boolean m)  {
        if (m) setAttribute("muted", ""); else removeAttribute("muted");
    }
    public boolean muted()  { return getAttributeValue("muted") != null; }

    public void controls(boolean c)  {
        if (c) setAttribute("controls", ""); else removeAttribute("controls");
    }
    public boolean controls()  { return getAttributeValue("controls") != null; }

    // Live-only IDL properties
    public double currentTime() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("currentTime needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("currentTime").ret().end());
        return v instanceof Number ? ((Number) v).doubleValue() : 0;
    }

    public void currentTime(double t) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("currentTime needs live DOM");
        executeChildJs(Js.id(ID).prop("currentTime", t).end());
    }

    public double duration() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("duration needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("duration").ret().end());
        return v instanceof Number ? ((Number) v).doubleValue() : 0;
    }

    public boolean paused() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("paused needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("paused").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public boolean ended() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("ended needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("ended").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public boolean seeking() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("seeking needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("seeking").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public double volume() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("volume needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("volume").ret().end());
        return v instanceof Number ? ((Number) v).doubleValue() : 1;
    }

    public void volume(double v) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("volume needs live DOM");
        executeChildJs(Js.id(ID).prop("volume", v).end());
    }

    public double playbackRate() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("playbackRate needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("playbackRate").ret().end());
        return v instanceof Number ? ((Number) v).doubleValue() : 1;
    }

    public void playbackRate(double r) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("playbackRate needs live DOM");
        executeChildJs(Js.id(ID).prop("playbackRate", r).end());
    }

    public String currentSrc() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("currentSrc needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("currentSrc").ret().end());
        return v != null ? v.toString() : "";
    }

    public int networkState() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("networkState needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("networkState").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public int readyState() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("readyState needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("readyState").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public void play() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("play() needs live DOM");
        executeChildJs(Js.id(ID).call("play").end());
    }

    public void pause() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("pause() needs live DOM");
        executeChildJs(Js.id(ID).call("pause").end());
    }

    public void load() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("load() needs live DOM");
        executeChildJs(Js.id(ID).call("load").end());
    }

    public String canPlayType(String type) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("canPlayType() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("canPlayType", type).ret().end());
        return v != null ? v.toString() : "";
    }
}
