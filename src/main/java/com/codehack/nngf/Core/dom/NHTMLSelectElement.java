package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLSelectElement extends NComponet {

    public NHTMLSelectElement(String id) {
        super(id, "select");
        registerSelectProperties();
    }

    public NHTMLSelectElement(String id, NWindow window) {
        super(id, "select", window);
        registerSelectProperties();
    }

    private void registerSelectProperties() {
        registerProperty(PropertyDescriptor.text("name", "Name", "", this::name, "Select", 1));
        registerProperty(PropertyDescriptor.bool("disabled", "Disabled", false, this::disabled, "Select", 1));
        registerProperty(PropertyDescriptor.bool("required", "Required", false, this::required, "Select", 1));
        registerProperty(PropertyDescriptor.bool("multiple", "Multiple", false, this::multiple, "Select", 1));
    }

    public String value()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("value").ret().end());
            return v != null ? v.toString() : "";
        }
        return "";
    }

    public void value(String v)  {
        if (isAttached()) {
            executeChildJs(Js.id(ID).prop("value", v).end());
        }
    }

    public int selectedIndex() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectedIndex needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("selectedIndex").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : -1;
    }

    public void selectedIndex(int i) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectedIndex needs live DOM");
        executeChildJs(Js.id(ID).prop("selectedIndex", i).end());
    }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    public void disabled(boolean d)  {
        if (d) setAttribute("disabled", ""); else removeAttribute("disabled");
    }
    public boolean disabled()  { return getAttributeValue("disabled") != null; }

    public void required(boolean r)  {
        if (r) setAttribute("required", ""); else removeAttribute("required");
    }
    public boolean required()  { return getAttributeValue("required") != null; }

    public void multiple(boolean m)  {
        if (m) setAttribute("multiple", ""); else removeAttribute("multiple");
    }
    public boolean multiple()  { return getAttributeValue("multiple") != null; }

    public void size(int s)  { setAttribute("size", String.valueOf(s)); }
    public int size()  {
        String v = getAttributeValue("size");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void autocomplete(String a)  { setAttribute("autocomplete", a); }
    public String autocomplete()  { return getAttributeValue("autocomplete"); }

    public boolean checkValidity() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("checkValidity() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("checkValidity").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<select>" + childHtml + "</select>";
    }
}
