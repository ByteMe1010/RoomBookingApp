package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLAnchorElement;

public class Link extends NHTMLAnchorElement {

    public Link(String id) {
        super(id);
        this.elementName = "Link";
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::text, "Content", 2));
    }

    public Link(String id, String href) {
        super(id);
        this.elementName = "Link";
        href(href);
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::text, "Content", 2));
    }

    public Link(String id, String href, String text) {
        super(id);
        this.elementName = "Link";
        href(href);
        setInnerText(text);
        registerProperty(PropertyDescriptor.text("text", "Text", text, this::text, "Content", 2));
    }

    public Link(String id, NWindow window) {
        super(id, window);
        this.elementName = "Link";
        registerProperty(PropertyDescriptor.text("text", "Text", "", this::text, "Content", 2));
    }

    public static Link of(NWindow window, String id) {
        return new Link(id, window);
    }
}
