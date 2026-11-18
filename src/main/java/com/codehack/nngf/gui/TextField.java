package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.Observer;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

public class TextField extends NHTMLInputElement {

    public Observer<String> value = new Observer<>(this) {
        @Override
        public void set(String value) {
            executeChildJs(Js.el().prop("value", value).end());
        }

        @Override
        public String get() {
            Object result = executeChildJsRead(Js.el().ret().get("value").end());
            return result != null ? result.toString() : null;
        }
    };

    public TextField(String id) {
        super(id);
        this.elementName = "TextField";
    }

    public TextField(String id, String placeholder) {
        super(id);
        this.elementName = "TextField";
        placeholder(placeholder);
    }

    public TextField(String id, NWindow window) {
        super(id, window);
        this.elementName = "TextField";
    }

    public static TextField of(NWindow window, String id) {
        return new TextField(id, window);
    }

}
