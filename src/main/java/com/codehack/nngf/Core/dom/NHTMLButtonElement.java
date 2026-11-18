package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

public abstract class NHTMLButtonElement extends NComponet {

    public NHTMLButtonElement(String id) {
        super(id, "button");
        registerButtonProperties();
    }

    public NHTMLButtonElement(String id, NWindow window) {
        super(id, "button", window);
        registerButtonProperties();
    }

    private void registerButtonProperties() {
        registerProperty(PropertyDescriptor.text("type", "Type", "button", this::type, "Button", 1));
        registerProperty(PropertyDescriptor.text("value", "Value", "", this::value, "Button", 1));
        registerProperty(PropertyDescriptor.text("name", "Name", "", this::name, "Button", 1));
        registerProperty(PropertyDescriptor.bool("disabled", "Disabled", false, this::disabled, "Button", 1));
    }

    public void disabled(boolean d)  {
        if (d) setAttribute("disabled", ""); else removeAttribute("disabled");
    }
    public boolean disabled()  { return getAttributeValue("disabled") != null; }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    public void type(String t)  { setAttribute("type", t); }
    public String type()  { return getAttributeValue("type"); }

    public void value(String v)  { setAttribute("value", v); }
    public String value()  { return getAttributeValue("value"); }

    public void formAction(String a)  { setAttribute("formaction", a); }
    public String formAction()  { return getAttributeValue("formaction"); }

    public void formEnctype(String e)  { setAttribute("formenctype", e); }
    public String formEnctype()  { return getAttributeValue("formenctype"); }

    public void formMethod(String m)  { setAttribute("formmethod", m); }
    public String formMethod()  { return getAttributeValue("formmethod"); }

    public void formNoValidate(boolean n)  {
        if (n) setAttribute("formnovalidate", ""); else removeAttribute("formnovalidate");
    }
    public boolean formNoValidate()  { return getAttributeValue("formnovalidate") != null; }

    public void formTarget(String t)  { setAttribute("formtarget", t); }
    public String formTarget()  { return getAttributeValue("formtarget"); }

    public void popoverTarget(String t)  { setAttribute("popovertarget", t); }
    public String popoverTarget()  { return getAttributeValue("popovertarget"); }

    public void popoverTargetAction(String a)  { setAttribute("popovertargetaction", a); }
    public String popoverTargetAction()  { return getAttributeValue("popovertargetaction"); }

    public void commandForElement(String id)  { setAttribute("commandfor", id); }
    public String commandForElement()  { return getAttributeValue("commandfor"); }

    public void command(String c)  { setAttribute("command", c); }
    public String command()  { return getAttributeValue("command"); }

    // Live-only
    public void showPopover() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("showPopover() needs live DOM");
        executeChildJs(Js.id(ID).call("showPopover").end());
    }

    public void hidePopover() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("hidePopover() needs live DOM");
        executeChildJs(Js.id(ID).call("hidePopover").end());
    }

    public void togglePopover() throws LifecycleViolationException {
        if (!isAttached()) throw new LifecycleViolationException("togglePopover() needs live DOM");
        executeChildJs(Js.id(ID).call("togglePopover").end());
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<button>" + childHtml + "</button>";
    }
}
