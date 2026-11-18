package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.NEventDef;

import java.util.ArrayList;
import java.util.List;

public class ListView extends NComponet {
    private final List<String> items = new ArrayList<>();

    public ListView(String id) {
        super(id, "ListView");
    }

    public ListView(String id, NWindow window) {
        super(id, "ListView", window);
    }

    public static ListView of(String id, NWindow window) {
        return new ListView(id, window);
    }

    public void addItem(String text) {
        items.add(text);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }

    public List<String> getItems() {
        return items;
    }

    public void onSelect(NEventDef handler) {
        events.addEvent("click", handler);
    }

    @Override
    public String buildHtml(String childHtml) {
        StringBuilder html = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            html.append("<div class=\"NNGF-ListView-item\" data-index=\"").append(i).append("\">")
                    .append(items.get(i)).append("</div>");
        }
        return html.toString();
    }
}
