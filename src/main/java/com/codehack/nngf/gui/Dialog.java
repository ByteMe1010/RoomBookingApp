package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLDialogElement;

public class Dialog extends NHTMLDialogElement {

    public Dialog(String id) {
        super(id);
        this.elementName = "Dialog";
    }

    public Dialog(String id, NWindow window) {
        super(id, window);
        this.elementName = "Dialog";
    }

    public static Dialog of(NWindow window, String id) {
        return new Dialog(id, window);
    }

}
