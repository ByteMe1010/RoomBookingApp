package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;

public class ProgressBar extends NComponet {
    private int value = 0;

    public ProgressBar(String id) {
        super(id, "ProgressBar");
    }

    public ProgressBar(String id, int value) {
        super(id, "ProgressBar");
        this.value = Math.max(0, Math.min(100, value));
    }

    public ProgressBar(String id, NWindow window) {
        super(id, "ProgressBar", window);
    }

    public void setValue(int percent) {
        value = Math.max(0, Math.min(100, percent));
        if (isAttached()) {
            try {
                executeChildJs(Js.el().call("querySelector", ".NNGF-ProgressBar-fill").prop("style.width", value + "%").end());
            } catch (JsBridgeException e) {
                Logger.Error("ProgressBar.setValue failed: " + e.getMessage(), 2);
            }
        }
    }

    public int getValue() {
        return value;
    }

    public void setIndeterminate(boolean indeterminate) {
        if (isAttached()) {
            try {
                executeChildJs(Js.el().call("querySelector", ".NNGF-ProgressBar-fill")
                    .call("classList.toggle", "NNGF-ProgressBar-fill-indeterminate", indeterminate).end());
            } catch (JsBridgeException e) {
                Logger.Error("ProgressBar.setIndeterminate failed: " + e.getMessage(), 2);
            }
        }
    }

    public void toggleIndeterminate() {
        if (isAttached()) {
            try {
                executeChildJs(Js.el().call("querySelector", ".NNGF-ProgressBar-fill")
                    .call("classList.toggle", "NNGF-ProgressBar-fill-indeterminate").end());
            } catch (JsBridgeException e) {
                Logger.Error("ProgressBar.toggleIndeterminate failed: " + e.getMessage(), 2);
            }
        }
    }

    @Override
    public String buildHtml(String childHtml) {
        String fillStyle = value > 0 ? " style=\"width:" + value + "%\"" : "";
        return "<div><div class=\"NNGF-ProgressBar-track\"><div class=\"NNGF-ProgressBar-fill\"" + fillStyle + "></div></div>" + childHtml + "</div>";
    }
}
