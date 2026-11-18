package com.codehack.nngf.Core.styleQuery;

import com.codehack.nngf.js.JS;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class JsManager {
    
    public static final Map<String, Consumer<DataPacket>> jsMap = new HashMap<>();

    static {
        // Register default macros
        register("toggleOnClick", x -> {
            if (x.component != null) {
                x.component.events.addJsEvent("click", JS.Document().getElementById(x.next()).ClassList().toggle(x.next()).E().toString());
            }
        });
    }

    public static void register(String code, Consumer<DataPacket> action) {
        jsMap.put(code, action);
    }

    public static void process(String code, DataPacket x) {
        Consumer<DataPacket> action = jsMap.get(code);
        if (action != null) {
            action.accept(x);
        } else {
            System.err.println("[JsManager] Unknown JS macro: " + code);
        }
    }
}
