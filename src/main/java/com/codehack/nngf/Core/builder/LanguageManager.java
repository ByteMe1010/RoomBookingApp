package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NNGF;
import com.codehack.nngf.Core.NWindow;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Core class of the System. mainly assist NWindow to build HTML,
 * Store event UUID's routes events when called, stores window level JS/CSS,
 *
 */
public class LanguageManager {
    private final Map<String, NEvent> events = new ConcurrentHashMap<>();
    private final List<String> StaticJs = new ArrayList<>();
    private final Map<String, Map<String, String>> styles = new HashMap<>();
    private final List<String> staticCss = new ArrayList<>();
    private final Map<String,String> jsFileUrls = new LinkedHashMap<>();
    private final Map<String,String> cssFileUrls = new LinkedHashMap<>();
    NWindow window;
    NComponet mainHtmlNode;
    private String initCss;
    private String initJs;

    public LanguageManager(NWindow w) {
        window = w;
    }

    public void setRoot(NComponet rootnode) {
        mainHtmlNode = rootnode;
        events.clear();
    }

    //region EVT : Event Api
    public void clearEvents() {
        Logger.Log("LM Events Cleared", 0);
        events.clear();
    }
    public void registerEvent(String UUID, NEvent event) {
        events.put(UUID, event);
    }
    public void removeEvent(String UUID) {
        events.remove(UUID);
    }
    public void deattach() {
        events.clear();
    }
    //endregion

    //region CSS JS : Static Css/Js and css/js files
    public void addStaticJs(String js) {
        StaticJs.add(js);
    }

    public void addCssStyle(String target, Map<String, String> style) {
        StyleBlock.mergeInto(styles, target, style);
    }

    public void addStaticCss(String css) {
        staticCss.add(css);
    }

    public void addJsFile(String id,String url) {
        jsFileUrls.put(id,url);
    }

    public void addCssFile(String id,String url) {
        cssFileUrls.put(id,url);
    }
    //endregion

    //region BUILDER : Builder Methods
    public void collectEventBindings(NComponet root) {
        root.getEventBindings(events);
    }

    public String buildCssHead() {
        if (mainHtmlNode == null) throw new NullPointerException("No root component set");
        Map<String, Map<String, String>> allStyles = new HashMap<>();
        StyleBlock.mergeAllInto(allStyles, NNGF.getCss());
        StyleBlock.mergeAllInto(allStyles, styles);
        StyleBlock.mergeAllInto(allStyles, mainHtmlNode.collectCss());

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String,String> url : cssFileUrls.entrySet()) {

            sb.append("<link id=\""+url.getKey()+"\" rel=\"stylesheet\" href=\"").append(url.getValue()).append("\">\n");
        }
        sb.append("<style id=\"nngf-document-root-css\">");
        if (initCss != null) {
            sb.append(initCss).append("\n");
        }
        for (Map.Entry<String, Map<String, String>> mp : allStyles.entrySet()) {
            sb.append(mp.getKey()).append("{");
            for (Map.Entry<String, String> sp : mp.getValue().entrySet()) {
                sb.append(sp.getKey()).append(":").append(sp.getValue()).append(";");
            }
            sb.append("}\n");
        }
        for (String raw : staticCss) {
            sb.append(raw).append("\n");
        }
        for (String raw : NNGF.getCssFiles()) {
            sb.append(raw).append("\n");
        }
        sb.append("</style>");
        return sb.toString();
    }

    public String buildJsHead() {
        if (mainHtmlNode == null) throw new NullPointerException("No root component set");
        List<String> allJs = new ArrayList<>();
        allJs.addAll(StaticJs);
        allJs.addAll(mainHtmlNode.collectJs());
        allJs.addAll(NNGF.getJs());

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> url : jsFileUrls.entrySet()) {
            sb.append("<script id=\""+url.getKey()+"\" src=\"").append(url.getValue()).append("\"></script>\n");
        }
        if (initJs != null || !allJs.isEmpty()) {
            sb.append("<script id=\"nngf-document-root-js\">");
            if (initJs != null) {
                sb.append(initJs).append("\n");
            }
            for (String j : allJs) {
                sb.append(j).append("\n");
            }
            sb.append("</script>\n");
        }
        return sb.toString();
    }

    public String build(boolean addBase) {
        Logger.Log("LanguageManager: Building HTML document", 1);
        if (mainHtmlNode == null) throw new NullPointerException();
        String Html = mainHtmlNode.buildHtmlDoc(window, new ArrayList<String>());
        mainHtmlNode.getEventBindings(events);
        java.net.URL x = getClass().getResource("/nngf/server");
        if (x == null && addBase)
            throw new RuntimeException("nngf/server folder didn't found. create nngf/server folder in resources for resources and import framework base files to it.");
        String baseHref = "";
        if (addBase && x != null) {
            String b = x.toExternalForm();
            if (!b.endsWith("/")) b += "/";
            baseHref = " <base href=\"" + b + "\">";
        }
        return "<html id=\"nngf-document-html\"><head id=\"nngf-document-head\">" + baseHref + buildCssHead() + "</head><body id=\"nngf-document-body\">" + Html + buildJsHead() + "</body></html>";
    }
    //endregion

    public void routeEvent(String UUID, Object event) {
        Logger.Log("LanguageManager: Routing event. UUID: " + UUID, 1);
        NEvent evt = events.get(UUID);
        if (evt != null) {
            evt.executeEvent(event, UUID);
        } else {
            Logger.Error("LanguageManager: No registered handler found for UUID: " + UUID, 2);
        }
    }
}
