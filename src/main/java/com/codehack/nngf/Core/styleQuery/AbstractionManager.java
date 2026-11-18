package com.codehack.nngf.Core.styleQuery;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.gui.Layouts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AbstractionManager {
    
    public static final Map<String, Consumer<DataPacket>> layoutMap = new HashMap<>();

    static {
        register("vbox", x -> x.styleBuffer.styles.putAll(Layouts.VBox().styles));
        register("col", x -> x.styleBuffer.styles.putAll(Layouts.VBox().styles));
        register("hbox", x -> x.styleBuffer.styles.putAll(Layouts.HBox().styles));
        register("row", x -> x.styleBuffer.styles.putAll(Layouts.HBox().styles));
        register("center", x -> x.styleBuffer.styles.putAll(Layouts.Center().styles));
        register("card", x -> x.styleBuffer.styles.putAll(Layouts.Card().styles));
    }

    public static void register(String name, Consumer<DataPacket> action) {
        layoutMap.put(name, action);
    }

    public static void process(String name, DataPacket x) {
        Consumer<DataPacket> action = layoutMap.get(name);
        if (action != null) {
            action.accept(x);
        } else {
            System.err.println("[AbstractionManager] Unknown layout: " + name);
        }
    }
}
