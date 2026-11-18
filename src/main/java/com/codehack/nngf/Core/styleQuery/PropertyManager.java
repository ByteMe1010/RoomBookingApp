package com.codehack.nngf.Core.styleQuery;

public class PropertyManager {

    public static void process(String token, DataPacket x) {
        if (x.component == null) return;
        
        // Check if it's an attribute bracket `[`
        if (token.equals("[")) {
            String content = "";
            while (x.hasNext()) {
                String next = x.next();
                if (next.equals("]")) break;
                content += next;
            }
            
            // content might be "disabled" or "id,123"
            if (content.contains(",")) {
                String[] parts = content.split(",", 2);
                x.component.setAttribute(parts[0].trim(), parts[1].trim());
            } else {
                // Special property handler
                if (content.equals("disabled")) {
                    x.component.setDisabled(true);
                } else {
                    x.component.setAttribute(content.trim(), "true");
                }
            }
        } else {
            // Normal string means a CSS class
            x.component.classList.add(token);
        }
    }
}
