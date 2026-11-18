package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import org.unbescape.javascript.JavaScriptEscape;

public class Alert extends NComponet {
    private String title;
    private long duration = 0;

    public Alert(String id) {
        super(id, "Alert");
    }

    public Alert(String id, String title) {
        super(id, "Alert");
        this.title = title;
    }

    public Alert(String id, NWindow window) {
        super(id, "Alert", window);
    }

    public void setDuration(long ms) {
        duration = ms;
    }

    public void setTitle(String t) {
        title = t;
        if (isAttached()) {
            try {
                executeJs(Js.id(ID).call("querySelector", ".NNGF-Alert-title").prop("innerHTML", t).end());
            } catch (JsBridgeException e) {
                Logger.Error("Alert.setTitle failed: " + e.getMessage(), 2);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void show() {
        try {
            executeJs(Js.id(ID).prop("style.display", "flex").end());
            if (duration > 0 && isAttached()) {
                String id = JavaScriptEscape.escapeJavaScript(ID);
                String js = "(function(){var e=document.getElementById('" + id +
                    "');if(!e)return;var m=window._nngfAlerts||(window._nngfAlerts={});" +
                    "if(m['" + id + "'])clearTimeout(m['" + id + "']);" +
                    "m['" + id + "']=setTimeout(function(){e.style.display='none';delete m['" + id + "']}," +
                    duration + ");})();";
                executeJs(js);
            }
        } catch (JsBridgeException e) {
            Logger.Error("Alert.show failed: " + e.getMessage(), 2);
        }
    }

    public void hide() {
        try {
            executeJs(Js.id(ID).prop("style.display", "none").end());
        } catch (JsBridgeException e) {
            Logger.Error("Alert.hide failed: " + e.getMessage(), 2);
        }
    }

    @Override
    public String buildHtml(String childHtml) {
        String t = title != null ? title : "";
        return "<div role=\"alert\"><div class=\"NNGF-Alert-title\">" + t +
               "<button class=\"NNGF-Alert-close\" onclick=\"this.closest('[role=alert]').style.display='none'\">&times;</button>" +
               "</div><div class=\"NNGF-Alert-body\">" + childHtml + "</div></div>";
    }
}
