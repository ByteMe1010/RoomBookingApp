package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

import java.util.Arrays;
import java.util.List;

public class NullGroup extends NComponet {
    public NullGroup(String ID, String elementName) {
        super(ID, elementName);
    }

    public NullGroup(String ID, String elementName, NWindow window) {
        super(ID, elementName, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return childHtml;
    }

    public void childs(NComponet... Childs) {
        this.childs.addAll(Arrays.stream(Childs).toList());
    }

    public void childs(List<NComponet> Childs) {
        this.childs.addAll(Childs);
    }

    @Override
    public void addChild(NComponet child) {
        this.childs.add(child);
    }

    public String getChildIDs() {
        StringBuilder sb = new StringBuilder();
        if (childs.isEmpty())
            return "";
        sb.append("\"");
        for (NComponet c : childs) {
            sb.append(", ").append("#").append(c.ID);
        }
        sb.append("\"");
        if (!sb.isEmpty())
            return sb.toString().replaceFirst(",", "").strip();
        return "";
    }
}
