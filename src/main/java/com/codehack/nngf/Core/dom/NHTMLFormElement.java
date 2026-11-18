package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLFormElement extends NComponet {

    public NHTMLFormElement(String id) {
        super(id, "form");
    }

    public NHTMLFormElement(String id, NWindow window) {
        super(id, "form", window);
    }

    public void action(String a)  { setAttribute("action", a); }
    public String action()  { return getAttributeValue("action"); }

    public void method(String m)  { setAttribute("method", m); }
    public String method()  { return getAttributeValue("method"); }

    public void enctype(String e)  { setAttribute("enctype", e); }
    public String enctype()  { return getAttributeValue("enctype"); }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    public void target(String t)  { setAttribute("target", t); }
    public String target()  { return getAttributeValue("target"); }

    public void autocomplete(String a)  { setAttribute("autocomplete", a); }
    public String autocomplete()  { return getAttributeValue("autocomplete"); }

    public void noValidate(boolean n)  {
        if (n) setAttribute("novalidate", ""); else removeAttribute("novalidate");
    }
    public boolean noValidate()  { return getAttributeValue("novalidate") != null; }

    public void acceptCharset(String c)  { setAttribute("accept-charset", c); }
    public String acceptCharset()  { return getAttributeValue("accept-charset"); }

    // Live-only
    public void submit() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("submit() needs live DOM");
        executeChildJs(Js.id(ID).call("submit").end());
    }

    public void reset() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("reset() needs live DOM");
        executeChildJs(Js.id(ID).call("reset").end());
    }

    public boolean checkValidity() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("checkValidity() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("checkValidity").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public void reportValidity() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("reportValidity() needs live DOM");
        executeChildJs(Js.id(ID).call("reportValidity").end());
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<form>" + childHtml + "</form>";
    }
}
