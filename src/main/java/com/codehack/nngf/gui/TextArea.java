package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLTextAreaElement;

public class TextArea extends NHTMLTextAreaElement {

    public TextArea(String id) {
        super(id);
        this.elementName = "TextArea";
    }

    public TextArea(String id, String text) {
        super(id);
        this.elementName = "TextArea";
        value(text);
    }

    public TextArea(String id, NWindow window) {
        super(id, window);
        this.elementName = "TextArea";
    }

    public static TextArea of(NWindow window, String id) {
        return new TextArea(id, window);
    }

}
