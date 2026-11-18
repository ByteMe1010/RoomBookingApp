package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLLabelElement extends NComponet {

    public NHTMLLabelElement(String id) {
        super(id, "label");
        registerLabelProperties();
    }

    public NHTMLLabelElement(String id, NWindow window) {
        super(id, "label", window);
        registerLabelProperties();
    }

    private void registerLabelProperties() {
        registerProperty(PropertyDescriptor.text("htmlFor", "For", "", this::htmlFor, "Label", 1));
    }

    public void htmlFor(String f)  { setAttribute("for", f); }
    public String htmlFor()  { return getAttributeValue("for"); }

    // Live-only
    public String control() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("control needs live DOM");
        Object v = executeChildJsRead(Js.id(ID).get("control").ret().end());
        return v != null ? v.toString() : "";
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<label>" + childHtml + "</label>";
    }
}
