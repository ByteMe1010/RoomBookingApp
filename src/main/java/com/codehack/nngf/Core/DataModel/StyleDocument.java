package com.codehack.nngf.Core.DataModel;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.Observer;

import java.util.HashMap;
import java.util.Map;

public class StyleDocument extends NComponet {
    public final Observer<Boolean> disabled = new Observer<Boolean>(this) {
        @Override
        public void set(Boolean value) {
            executeChildJs(Js.el().prop("disabled", value).end());
        }

        @Override
        public Boolean get() {
            Object d = executeChildJsRead(Js.el().ret().get("disabled").end());
            if (d == null)
                return false;
            return (Boolean) d;
        }
    };
    private final Map<String, StyleBlock> styles = new HashMap<>();

    public StyleDocument(String ID) {
        super(ID, "style-document");
    }

    public StyleDocument(String ID, NWindow window) {
        super(ID, "style-document", window);
    }

    public void AddStyle(String Target, StyleBlock style) {
        StyleBlock.mergeBlock(styles, Target, style);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<style>" + cssBuild() + "</style>";
    }

    private String cssBuild() {
        StringBuilder css = new StringBuilder();
        for (Map.Entry<String, StyleBlock> mp : styles.entrySet()) {
            css.append(mp.getKey()).append("{\n");
            for (Map.Entry<String, String> sp : mp.getValue().styles.entrySet()) {
                css.append(sp.getKey()).append(" : ").append(sp.getValue()).append(";");
            }
            css.append("}").append("\n");
        }
        return css.toString();
    }
}
