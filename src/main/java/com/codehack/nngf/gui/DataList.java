package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

/**
 * A vertical list of {@link DataListItem}s.
 *
 * <pre>{@code
 * DataList list = new DataList("people", window);
 * list.addListItem("Ada");
 * list.addListItem("Grace");
 * }</pre>
 */
public class DataList extends NComponet {

    private int autoId = 0;

    public DataList(String id) {
        super(id, "DataList");
    }

    public DataList(String id, NWindow window) {
        super(id, "DataList", window);
    }

    public static DataList of(NWindow window, String id) {
        return new DataList(id, window);
    }

    /** Add an item using an auto-generated id ({@code <listId>-item-<n>}). */
    public DataListItem addListItem(String text) {
        DataListItem item = new DataListItem(ID + "-item-" + (autoId++), text);
        addChild(item);
        return item;
    }

    /** Add an item with an explicit id. */
    public DataListItem addListItem(String id, String text) {
        DataListItem item = new DataListItem(id, text);
        addChild(item);
        return item;
    }

    public void removeListItem(int index) {
        removeChild(index);
    }

    public void clearList() {
        clearChilds();
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
