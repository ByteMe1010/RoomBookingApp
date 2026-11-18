package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.Observer;
import com.codehack.nngf.Core.dom.NHTMLInputElement;

public class PasswordField extends NHTMLInputElement {

    public Observer<String> value = new Observer<String>(this) {
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

    public PasswordField(String id) {
        super(id);
        this.elementName = "PasswordField";
        setAttribute("type", "password");
    }

    public PasswordField(String id, NWindow window) {
        super(id, window);
        this.elementName = "PasswordField";
        setAttribute("type", "password");
    }

    public PasswordField(String id, String placeholder) {
        super(id);
        this.elementName = "PasswordField";
        setAttribute("type", "password");
        placeholder(placeholder);
    }

    public PasswordField(String id, String placeholder, NWindow window) {
        super(id, window);
        this.elementName = "PasswordField";
        setAttribute("type", "password");
        placeholder(placeholder);
    }

    public static PasswordField of(String id, NWindow window) {
        return new PasswordField(id, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<input type=\"password\">";
    }
}
