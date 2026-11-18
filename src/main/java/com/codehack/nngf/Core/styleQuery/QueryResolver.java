package com.codehack.nngf.Core.styleQuery;

import com.codehack.nngf.Core.NComponet;

public class QueryResolver {

    public static void resolve(String query, NComponet component) {
        DataPacket packet = new DataPacket(query, component);
        
        while (packet.hasNext()) {
            String token = packet.next();
            
            if (token.equals("&")) {
                String layoutName = packet.next();
                AbstractionManager.process(layoutName, packet);
            } else if (token.equals(".")) {
                String next = packet.next();
                PropertyManager.process(next, packet);
            } else if (token.equals("$")) {
                String macro = packet.next();
                JsManager.process(macro, packet);
            } else if (token.matches("[a-zA-Z]+")) {
                StyleManager.process(token, packet);
            }
        }
        
        packet.apply();
    }
}
