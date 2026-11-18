package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLTextAreaElement extends NComponet {

    public NHTMLTextAreaElement(String id) {
        super(id, "textarea");
        registerTextAreaProperties();
    }

    public NHTMLTextAreaElement(String id, NWindow window) {
        super(id, "textarea", window);
        registerTextAreaProperties();
    }

    private void registerTextAreaProperties() {
        registerProperty(PropertyDescriptor.text("value", "Value", "", this::value, "TextArea", 1));
        registerProperty(PropertyDescriptor.text("placeholder", "Placeholder", "", this::placeholder, "TextArea", 1));
        registerProperty(PropertyDescriptor.text("name", "Name", "", this::name, "TextArea", 1));
        registerProperty(PropertyDescriptor.bool("disabled", "Disabled", false, this::disabled, "TextArea", 1));
        registerProperty(PropertyDescriptor.bool("required", "Required", false, this::required, "TextArea", 1));
        registerProperty(PropertyDescriptor.bool("readOnly", "Read Only", false, this::readOnly, "TextArea", 1));
        registerProperty(PropertyDescriptor.number("rows", "Rows", "4", v -> rows(Integer.parseInt(v)), "TextArea", 1));
        registerProperty(PropertyDescriptor.number("cols", "Cols", "20", v -> cols(Integer.parseInt(v)), "TextArea", 1));
    }

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

    public void placeholder(String p)  { setAttribute("placeholder", p); }
    public String placeholder()  { return getAttributeValue("placeholder"); }

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

    public void autocomplete(String a)  { setAttribute("autocomplete", a); }
    public String autocomplete()  { return getAttributeValue("autocomplete"); }

    public void rows(int r)  { setAttribute("rows", String.valueOf(r)); }
    public int rows()  {
        String v = getAttributeValue("rows");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void cols(int c)  { setAttribute("cols", String.valueOf(c)); }
    public int cols()  {
        String v = getAttributeValue("cols");
        return v != null ? Integer.parseInt(v) : -1;
    }

    public void wrap(String w)  { setAttribute("wrap", w); }
    public String wrap()  { return getAttributeValue("wrap"); }

    // Live-only
    public void select() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("select() needs live DOM");
        executeChildJs(Js.id(ID).call("select").end());
    }

    public void setSelectionRange(int start, int end) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("setSelectionRange() needs live DOM");
        executeChildJs(Js.id(ID).call("setSelectionRange", start, end).end());
    }

    public int selectionStart() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectionStart needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("selectionStart").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public void selectionStart(int s) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectionStart needs live DOM");
        executeChildJs(Js.id(ID).prop("selectionStart", s).end());
    }

    public int selectionEnd() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectionEnd needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("selectionEnd").ret().end());
        return v instanceof Number ? ((Number) v).intValue() : 0;
    }

    public void selectionEnd(int s) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("selectionEnd needs live DOM");
        executeChildJs(Js.id(ID).prop("selectionEnd", s).end());
    }

    public boolean checkValidity() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("checkValidity() needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).call("checkValidity").ret().end());
        return v instanceof Boolean && (Boolean) v;
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<textarea>" + childHtml + "</textarea>";
    }
}
