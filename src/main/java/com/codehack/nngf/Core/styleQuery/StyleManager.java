package com.codehack.nngf.Core.styleQuery;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class StyleManager {
    // Maps a shortcode (alias) to a unique property ID
    public static final Map<String, String> aliasMap = new HashMap<>();
    
    // Maps a unique property ID to its actual CSS name and the extractor function
    public static final Map<String, Map.Entry<String, Extractor>> styleMap = new HashMap<>();

    static {
        // Universal Extractors
        Extractor boxExtractor = (cssName, x) -> {
            StringBuilder val = new StringBuilder();
            while (x.hasNext()) {
                String token = x.peek();
                // If it's a new letter property, stop.
                if (token.matches("[a-zA-Z]+") && !isKeyword(token)) {
                    break;
                }
                // Stop at QueryResolver special prefixes — these are not CSS values
                if (token.equals("&") || token.equals(".") || token.equals("$")) {
                    break;
                }
                // Handle commas for multi-value by replacing with spaces
                if (token.equals(",")) {
                    val.append(" ");
                } else if (token.equals("!") || token.equals("none")) {
                    val.append("none");
                } else if (token.equals("%")) {
                    val.append("100%");
                } else if (token.matches("-?\\d+(?:\\.\\d+)?")) {
                    val.append(token).append("px");
                } else {
                    val.append(token);
                }
                x.next(); // consume
            }
            if (val.length() > 0) {
                x.styleBuffer.style(cssName, val.toString().trim());
            }
        };

        Extractor flexDirExtractor = (cssName, x) -> {
            String token = x.next();
            if (token.equals("-")) {
                String next = x.peek();
                if (next.equals("r") || next.equals("row")) { x.next(); x.styleBuffer.style(cssName, "row-reverse"); return; }
                if (next.equals("c") || next.equals("col")) { x.next(); x.styleBuffer.style(cssName, "column-reverse"); return; }
            }
            if (token.equals("r") || token.equals("row")) x.styleBuffer.style(cssName, "row");
            else if (token.equals("c") || token.equals("col")) x.styleBuffer.style(cssName, "column");
        };

        Extractor stringExtractor = (cssName, x) -> {
            String token = x.next();
            // Handle universal value dictionary
            if (token.equals("c")) token = "center";
            else if (token.equals("fs")) token = "flex-start";
            else if (token.equals("fe")) token = "flex-end";
            else if (token.equals("sb")) token = "space-between";
            
            x.styleBuffer.style(cssName, token);
        };

        // Register Style Extractors
        registerStyle("margin", "margin", boxExtractor, "m", "mar");
        registerStyle("margin_top", "margin-top", boxExtractor, "mt", "mart");
        registerStyle("margin_right", "margin-right", boxExtractor, "mr", "mari");
        registerStyle("margin_bottom", "margin-bottom", boxExtractor, "mb", "marb");
        registerStyle("margin_left", "margin-left", boxExtractor, "ml", "marl");
        registerStyle("padding", "padding", boxExtractor, "p", "pad");
        registerStyle("padding_top", "padding-top", boxExtractor, "pt", "padt");
        registerStyle("padding_right", "padding-right", boxExtractor, "pr", "padr");
        registerStyle("padding_bottom", "padding-bottom", boxExtractor, "pb", "padb");
        registerStyle("padding_left", "padding-left", boxExtractor, "pl", "padl");
        registerStyle("width", "width", boxExtractor, "w", "wid");
        registerStyle("height", "height", boxExtractor, "h", "hei");
        registerStyle("display", "display", stringExtractor, "d", "dis");
        registerStyle("flex_direction", "flex-direction", flexDirExtractor, "fd", "fled");
        registerStyle("justify_content", "justify-content", stringExtractor, "jc", "jusc");
        registerStyle("align_items", "align-items", stringExtractor, "ai", "alia");
        registerStyle("background_color", "background-color", stringExtractor, "bg", "bac", "bacc");
        registerStyle("color", "color", stringExtractor, "fg", "col", "c");
    }

    public static void registerStyle(String id, String cssName, Extractor extractor, String... aliases) {
        styleMap.put(id, new AbstractMap.SimpleEntry<>(cssName, extractor));
        for (String alias : aliases) {
            aliasMap.put(alias, id);
        }
    }

    public static void process(String code, DataPacket x) {
        String id = aliasMap.get(code);
        if (id != null) {
            Map.Entry<String, Extractor> entry = styleMap.get(id);
            if (entry != null) {
                entry.getValue().extract(entry.getKey(), x);
            }
        } else {
            System.err.println("[StyleManager] Unknown style code: " + code);
        }
    }
    
    private static boolean isKeyword(String token) {
        return token.equals("c") || token.equals("fs") || token.equals("fe") || token.equals("none") || token.equals("auto") || token.equals("inherit");
    }
}
