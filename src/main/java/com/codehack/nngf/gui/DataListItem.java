package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.Observer;

/**
 * A single row inside a {@link DataList}.
 */
public class DataListItem extends NComponet {

    public final Observer<String> text = new Observer<String>(this) {
        @Override
        public void set(String value) {
            parent.setInnerText(value);
        }

        @Override
        public String get() {
            return parent.getInnerText();
        }
    };

    public DataListItem(String id) {
        super(id, "DataListItem");
    }

    public DataListItem(String id, String text) {
        super(id, "DataListItem");
        this.text.set(text);
    }

    public DataListItem(String id, NWindow window) {
        super(id, "DataListItem", window);
    }

    public DataListItem(String id, String text, NWindow window) {
        super(id, "DataListItem", window);
        this.text.set(text);
    }

    public static DataListItem of(NWindow window, String id, String text) {
        return new DataListItem(id, text, window);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
