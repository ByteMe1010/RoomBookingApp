package com.codehack.nngf.Core.dom;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;

public abstract class NHTMLDetailsElement extends NComponet {

    public NHTMLDetailsElement(String id) {
        super(id, "details");
    }

    public NHTMLDetailsElement(String id, NWindow window) {
        super(id, "details", window);
    }

    public void open(boolean o)  {
        if (o) setAttribute("open", ""); else removeAttribute("open");
    }
    public boolean open()  { return getAttributeValue("open") != null; }

    public void name(String n)  { setAttribute("name", n); }
    public String name()  { return getAttributeValue("name"); }

    @Override
    public String buildHtml(String childHtml) {
        return "<details>" + childHtml + "</details>";
    }
}
