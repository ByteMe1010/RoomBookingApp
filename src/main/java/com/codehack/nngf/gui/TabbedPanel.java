package com.codehack.nngf.gui;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.Observer;

public class TabbedPanel extends NComponet {

    public Observer<Integer> activeTabIndex = new Observer<Integer>(this) {
        @Override
        public void set(Integer value) {
            parent.executeChildJs("Array.from(element.children).forEach(child => child.classList.add('tabHidden'));" +
                    "element.children[" + value + "].classList.remove('tabHidden');");
        }

        @Override
        public Integer get() {
            return (Integer) parent.executeChildJsRead("return Array.from(element.children).findIndex(\n" +
                    "    child => !child.classList.contains('tabHidden')\n" +
                    "  );");
        }
    };

    public Observer<String> activeTabId = new Observer<String>(this) {
        @Override
        public void set(String value) {
            parent.executeChildJs("Array.from(element.children).forEach(child => child.classList.add('tabHidden'));" +
                    Js.el().call("querySelector", "#" + value).call("classList.remove", "tabHidden").end());
        }

        @Override
        public String get() {
            Object opt = parent.executeChildJsRead("const child = Array.from(element.children).find(\n" +
                    "    el => !el.classList.contains('tabHidden')\n" +
                    "  );\n" +
                    "  return child?.id || null;");
            return opt == null ? null : opt.toString();
        }
    };

    public TabbedPanel(String id) {
        super(id, "TabbedPanel");
    }

    public TabbedPanel(String id, NWindow window) {
        super(id, "TabbedPanel", window);
    }

    public static TabbedPanel of(String id, NWindow window) {
        return new TabbedPanel(id, window);
    }

    public void addTab(NComponet tab) {
        tab.classList.add("tabHidden");
        addChild(tab);
    }

    public void addTabs(NComponet... tabs) {
        for (NComponet tab : tabs) {
            tab.classList.add("tabHidden");
            addChild(tab);
        }
    }

    public void removeTab(int tabIndex) {
        removeChild(tabIndex);
    }

    public void setTabActive(NComponet tab) {
        activeTabId.set(tab.ID);
    }

    @Override
    public String buildHtml(String childHtml) {
        return "<div>" + childHtml + "</div>";
    }
}
