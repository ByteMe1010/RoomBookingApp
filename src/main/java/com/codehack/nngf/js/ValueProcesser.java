package com.codehack.nngf.js;

import org.unbescape.javascript.JavaScriptEscape;

import java.util.HashMap;
import java.util.Map;

public class ValueProcesser {

    private static final Map<String, ProccesserAdapter> converters = new HashMap<>();
    private static boolean firstRun = true;

    public static void add(String simpleName, ProccesserAdapter ad) {
        converters.put(simpleName, ad);
    }

    public static ProccesserAdapter get(String simpleName) {
        return converters.get(simpleName);
    }

    public static void remove(String simpleName) {
        converters.remove(simpleName);
    }

    public static void removeAll() {
        converters.clear();
    }

    private static void init() {
        if (!firstRun) return;
        firstRun = false;

        add(String.class.getSimpleName(), dta -> JSEscape((String) dta, true));
        add(Integer.class.getSimpleName(), dta -> ((Integer) dta).toString());
        add(Boolean.class.getSimpleName(), dta -> ((Boolean) dta) ? "true" : "false");
        add(Raw.class.getSimpleName(), dta -> ((Raw) dta).value());
    }

    public static String convert(Object dta) {
        init();
        String d = dta.getClass().getSimpleName();
        if (!converters.containsKey(d))
            throw new RuntimeException("Converter not found for Type : " + d);
        return converters.get(d).convert(dta);
    }

    private static String JSEscape(String data, boolean addBrackets) {
        String f = JavaScriptEscape.escapeJavaScript(data);
        if (!addBrackets) return f;
        return "\"" + f + "\"";
    }
}
