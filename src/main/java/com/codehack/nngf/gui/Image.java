package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLImageElement;

public class Image extends NHTMLImageElement {

    public Image(String id) {
        super(id);
        this.elementName = "Image";
    }

    public Image(String id, String src, String alt) {
        super(id);
        this.elementName = "Image";
        src(src);
        alt(alt);
    }

    public Image(String id, NWindow window) {
        super(id, window);
        this.elementName = "Image";
    }

    public static Image of(NWindow window, String id) {
        return new Image(id, window);
    }

}
