package com.codehack.nngf.gui;

import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.dom.NHTMLDetailsElement;

public class TreeNode extends NHTMLDetailsElement {
    private String label;

    public TreeNode(String id, String label) {
        super(id);
        this.elementName = "TreeNode";
        this.label = label;
        open(true);
    }

    public TreeNode(String id, String label, NWindow window) {
        super(id, window);
        this.elementName = "TreeNode";
        this.label = label;
        open(true);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String buildHtml(String childHtml) {
        String s = label != null ? label : "";
//        return "<details open><summary>" + s + "</summary><div class=\"NNGF-TreeNode-children\">" + childHtml + "</div></details>";
        return "<details open style='border:none;'><summary style='cursor:pointer;font-size:12px;padding:2px 4px;font-family:monospace;'>" + s + "</summary><div style='margin-left:16px;'>" + childHtml + "</div></details>";
    }
}
