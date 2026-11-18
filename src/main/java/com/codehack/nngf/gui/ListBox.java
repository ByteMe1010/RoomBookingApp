package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLSelectElement;

public class ListBox extends NHTMLSelectElement {
    private int itemCounter = 0; //TODO:Himansa review- variable never used, either logic is broken or variable can be removed

    public ListBox(String id) {
        super(id);
        this.elementName = "ListBox";
    }

    public ListBox(String id, NWindow window) {
        super(id, window);
        this.elementName = "ListBox";
    }

    public void addItem(String id, String text) {
        ListItem item = new ListItem(id,id,text);
        addChild(item);
    }
    private class ListItem extends NComponet
    {
        private final String value;
        private final String displayText;

        //TODO:new element
        public ListItem(String ID, String value,String displayText) {
            super(ID, "ListBox-item");
            this.value = value;
            this.displayText = displayText;
        }

        @Override
        public String buildHtml(String childHtml) {
            return "<option value=\""+value+"\">"+displayText+"</option>";
        }
    }

    public ListBox AddItem(String valueAid,String text) {
        addItem(valueAid, text);
        return this;
    }
}
