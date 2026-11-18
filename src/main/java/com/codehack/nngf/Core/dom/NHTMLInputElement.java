package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLInputElement extends NComponet {

    public NHTMLInputElement(String id) {
        super(id, "input");
        registerInputProperties();
    }

    public NHTMLInputElement(String id, NWindow window) {
        super(id, "input", window);
        registerInputProperties();
    }

    private void registerInputProperties() {
        registerProperty(PropertyDescriptor.text("type", "Type", "text", this::type, "Input", 1));
        registerProperty(PropertyDescriptor.text("value", "Value", "", this::value, "Input", 1));
        registerProperty(PropertyDescriptor.text("placeholder", "Placeholder", "", this::placeholder, "Input", 1));
        registerProperty(PropertyDescriptor.text("name", "Name", "", this::name, "Input", 1));
        registerProperty(PropertyDescriptor.bool("disabled", "Disabled", false, this::disabled, "Input", 1));
        registerProperty(PropertyDescriptor.bool("required", "Required", false, this::required, "Input", 1));
        registerProperty(PropertyDescriptor.bool("readOnly", "Read Only", false, this::readOnly, "Input", 1));
    }

    // IDL properties that diverge from the HTML attribute
    public String value()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("value").ret().end());
            return v != null ? v.toString() : "";
        }
        return getAttributeValue("value");
    }

    public void value(String v)  {
        if (isAttached()) {
            executeChildJs(Js.id(ID).prop("value", v).end());
        } else {
            setAttribute("value", v);
        }
    }

    public boolean checked()  {
        if (isAttached()) {
            Object v = executeChildJsRead(Js.id(ID).get("checked").ret().end());
            return v instanceof Boolean && (Boolean) v;
        }
        return getAttributeValue("checked") != null;
    }

    public void checked(boolean c)  {
        if (isAttached()) {
            executeChildJs(Js.id(ID).prop("checked", c).end());
        } else if (c) {
            setAttribute("checked", "");
        } else {
            removeAttribute("checked");
        }
    }

    public String defaultValue()  { return getAttributeValue("value"); }
    public boolean defaultChecked()  { return getAttributeValue("checked") != null; }

    // HTML attribute-backed properties (delegate to setAttribute/getAttributeValue)
    public void placeholder(String p)  { setAttribute("placeholder", p); }
    public String placeholder()  { return getAttributeValue("placeholder"); }

    public void type(String t)  { setAttribute("type", t); }
    public String type()  { return getAttributeValue("type"); }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    public void disabled(boolean d)  {
        if (d) setAttribute("disabled", ""); else removeAttribute("disabled");
    }
    public boolean disabled()  { return getAttributeValue("disabled") != null; }

    public void readOnly(boolean r)  {
        if (r) setAttribute("readonly", ""); else removeAttribute("readonly");
    }
    public boolean readOnly()  { return getAttributeValue("readonly") != null; }

    public void required(boolean r)  {
        if (r) setAttribute("required", ""); else removeAttribute("required");
    }
    public boolean required()  { return getAttributeValue("required") != null; }

    public void maxLength(int n)  { setAttribute("maxlength", String.valueOf(n)); }
    public int maxLength()  {
        String v = getAttributeValue("maxlength");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void minLength(int n)  { setAttribute("minlength", String.valueOf(n)); }
    public int minLength()  {
        String v = getAttributeValue("minlength");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void pattern(String p)  { setAttribute("pattern", p); }
    public String pattern()  { return getAttributeValue("pattern"); }

    public void autocomplete(String a)  { setAttribute("autocomplete", a); }
    public String autocomplete()  { return getAttributeValue("autocomplete"); }

    public void accept(String a)  { setAttribute("accept", a); }
    public String accept()  { return getAttributeValue("accept"); }

    public void multiple(boolean m)  {
        if (m) setAttribute("multiple", ""); else removeAttribute("multiple");
    }
    public boolean multiple()  { return getAttributeValue("multiple") != null; }

    public void min(String m)  { setAttribute("min", m); }
    public String min()  { return getAttributeValue("min"); }

    public void max(String m)  { setAttribute("max", m); }
    public String max()  { return getAttributeValue("max"); }

    public void step(String s)  { setAttribute("step", s); }
    public String step()  { return getAttributeValue("step"); }

    public void size(int s)  { setAttribute("size", String.valueOf(s)); }
    public int size()  {
        String v = getAttributeValue("size");
        return v != null ? Integer.parseInt(v) : -1;
    }

    // Live-only (no HTML attribute)
    public void indeterminate(boolean v) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("indeterminate needs live DOM");
        executeChildJs(Js.id(ID).prop("indeterminate", v).end());
    }

    public void select() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("select() needs live DOM");
        executeChildJs(Js.id(ID).call("select").end());
    }

    public void stepUp(int n) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("stepUp() needs live DOM");
        executeChildJs(Js.id(ID).call("stepUp", n).end());
    }

    public void stepDown(int n) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("stepDown() needs live DOM");
        executeChildJs(Js.id(ID).call("stepDown", n).end());
    }

    public boolean checkValidity() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("checkValidity() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("checkValidity").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    public String validationMessage()  {
        if (!isAttached()) return "";
        Object v = executeChildJsRead(Js.id(ID).get("validationMessage").ret().end());
        return v != null ? v.toString() : "";
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input>";
    }
}
