package com.codehack.nngf.Core.styleQuery;

import java.util.Map;

public class SyntaxDocGenerator {
    
    public static String generateMarkdown() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("# NNGF Component Configuration DSL V2\n\n");
        
        sb.append("## Layouts (`&`)\n");
        sb.append("| Macro | Action |\n");
        sb.append("|-------|--------|\n");
        for (String name : AbstractionManager.layoutMap.keySet()) {
            sb.append(String.format("| `& %s` | Applies layout/abstraction |\n", name));
        }
        
        sb.append("\n## Style Properties (CSS)\n");
        sb.append("| Shorthand (Aliases) | CSS Property |\n");
        sb.append("|---------------------|--------------|\n");
        for (Map.Entry<String, Map.Entry<String, Extractor>> entry : StyleManager.styleMap.entrySet()) {
            String id = entry.getKey();
            String cssName = entry.getValue().getKey();
            
            // Collect aliases for this ID
            StringBuilder aliases = new StringBuilder();
            for (Map.Entry<String, String> aliasEntry : StyleManager.aliasMap.entrySet()) {
                if (aliasEntry.getValue().equals(id)) {
                    if (aliases.length() > 0) aliases.append(", ");
                    aliases.append("`").append(aliasEntry.getKey()).append("`");
                }
            }
            sb.append(String.format("| %s | `%s` |\n", aliases.toString(), cssName));
        }
        
        sb.append("\n## Javascript Macros (`$`)\n");
        sb.append("| Macro | Action |\n");
        sb.append("|-------|--------|\n");
        for (String name : JsManager.jsMap.keySet()) {
            sb.append(String.format("| `$ %s` | Attaches JS events/behavior |\n", name));
        }
        
        return sb.toString();
    }
    
    public static void printDocs() {
        System.out.println(generateMarkdown());
    }
}
