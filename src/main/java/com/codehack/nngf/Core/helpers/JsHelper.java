package com.codehack.nngf.Core.helpers;

import org.unbescape.javascript.JavaScriptEscape;

public class JsHelper {
    public static String getValueOfIDtoEx(String ID) {
        return "ex = document.getElementById('" + JavaScriptEscape.escapeJavaScript(ID) + "').value;";
    }
}
