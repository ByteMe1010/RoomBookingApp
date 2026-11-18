package com.codehack.nngf.Core;

import com.codehack.nngf.Core.DataModel.StyleBlock;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class NNGF {
    private static final Map<String, Map<String, String>> css = new ConcurrentHashMap<>();
    private static final List<String> jss = new CopyOnWriteArrayList<>();
    private static final List<String> cssFiles = new CopyOnWriteArrayList<>();

    public static void addStyle(StyleBlock... styles) {
        for (StyleBlock i : styles) {
            StyleBlock.mergeInto(css, i.target, i.styles);
        }
    }

    public static void addStyle(String style) {
        cssFiles.add(style);
    }

    public static void addJs(String js) {
        jss.add(js);
    }

    public static Map<String, Map<String, String>> getCss() {
        return Collections.unmodifiableMap(css);
    }

    public static List<String> getCssFiles() {
        return Collections.unmodifiableList(cssFiles);
    }

    public static List<String> getJs() {
        return Collections.unmodifiableList(jss);
    }
}
