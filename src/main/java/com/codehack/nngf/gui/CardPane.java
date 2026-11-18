package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.JsBridgeException;

public class CardPane extends NComponet {
    private int selectedIndex = 0;

    public CardPane(String id) {
        super(id, "CardPane");
    }

    public CardPane(String id, NWindow window) {
        super(id, "CardPane", window);
    }

    public void showPage(int index) {
        selectedIndex = index;
        if (isAttached()) {
            try {
                executeJs(Js.id(ID).call("setAttribute", "selected", String.valueOf(index)).end());
            } catch (JsBridgeException e) {
                Logger.Error("CardPane.showPage failed: " + e.getMessage(), 2);
            }
        }
    }

    public void showPageById(String id) {
        if (isAttached()) {
            try {
                executeJs(Js.id(ID).call("showPageById", id).end());
            } catch (JsBridgeException e) {
                Logger.Error("CardPane.showPageById failed: " + e.getMessage(), 2);
            }
        }
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<nngf-cardpane selected=\"" + selectedIndex + "\">" + childHtml + "</nngf-cardpane>";
    }
}
