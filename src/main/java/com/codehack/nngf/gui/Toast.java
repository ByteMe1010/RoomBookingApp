package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import org.unbescape.javascript.JavaScriptEscape;

public class Toast extends NComponet {
    private long duration = 0;

    public Toast(String id) {
        super(id, "Toast");
    }

    public Toast(String id, NWindow window) {
        super(id, "Toast", window);
    }

    public Toast(String id, String text) {
        super(id, "Toast");
        setInnerText(text);
    }

    public void setDuration(long ms) {
        duration = ms;
    }

    public void show() {
        try {
            executeJs(Js.id(ID).prop("style.display", "inline-flex").end());
            if (duration > 0 && isAttached()) {
                String id = JavaScriptEscape.escapeJavaScript(ID);
                String js = "(function(){var e=document.getElementById('" + id +
                    "');if(!e)return;var m=window._nngfToasts||(window._nngfToasts={});" +
                    "if(m['" + id + "'])clearTimeout(m['" + id + "']);" +
                    "m['" + id + "']=setTimeout(function(){e.style.display='none';delete m['" + id + "']}," +
                    duration + ");})();";
                executeJs(js);
            }
        } catch (JsBridgeException e) {
            Logger.Error("Toast.show failed: " + e.getMessage(), 2);
        }
    }

    public void hide() {
        try {
            executeJs(Js.id(ID).prop("style.display", "none").end());
        } catch (JsBridgeException e) {
            Logger.Error("Toast.hide failed: " + e.getMessage(), 2);
        }
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
