package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLDialogElement extends NComponet {

    public NHTMLDialogElement(String id) {
        super(id, "dialog");
        registerDialogProperties();
    }

    public NHTMLDialogElement(String id, NWindow window) {
        super(id, "dialog", window);
        registerDialogProperties();
    }

    private void registerDialogProperties() {
        registerProperty(PropertyDescriptor.bool("open", "Open", false, this::open, "Dialog", 1));
    }

    public void open(boolean o)  {
        if (o) setAttribute("open", ""); else removeAttribute("open");
    }
    public boolean open()  { return getAttributeValue("open") != null; }

    public String returnValue() throws LifecycleViolationException {
        if (!isAttached()) return "";
        Object v = executeChildJsRead(Js.id(ID).get("returnValue").ret().end());
        return v != null ? v.toString() : "";
    }

    public void returnValue(String r) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("returnValue setter needs live DOM");
        executeChildJs(Js.id(ID).prop("returnValue", r).end());
    }

    // Live-only
    public void show() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("show() needs live DOM");
        executeChildJs(Js.id(ID).call("show").end());
    }

    public void showModal() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("showModal() needs live DOM");
        executeChildJs(Js.id(ID).call("showModal").end());
    }

    public void close(String returnValue) throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("close() needs live DOM");
        executeChildJs(Js.id(ID).call("close", returnValue).end());
    }

    public void close() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("close() needs live DOM");
        executeChildJs(Js.id(ID).call("close").end());
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<dialog>" + childHtml + "</dialog>";
    }
}
