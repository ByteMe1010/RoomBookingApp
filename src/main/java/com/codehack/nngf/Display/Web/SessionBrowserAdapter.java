package com.codehack.nngf.Display.Web;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.bridge.BrowserAdapter;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SessionBrowserAdapter implements BrowserAdapter {

    private final List<Runnable> pageLoadCallbacks = new ArrayList<>();
    private final Queue<String> pendingJs = new ConcurrentLinkedQueue<>();
    private final int wsPort;
    WebSocket connection;
    private NWindow window;
    private String htmlContent;
    private String resourceBase;
    private boolean bridgeReady = false;
    private Runnable pageLoadCallback;

    public SessionBrowserAdapter(int wsPort) {
        this.wsPort = wsPort;
    }

    void setConnection(WebSocket conn) {
        if (this.connection != null && this.connection != conn && pageLoadCallback != null) {
            synchronized (pageLoadCallbacks) {
                pageLoadCallbacks.add(pageLoadCallback);
            }
            bridgeReady = false;
            pendingJs.clear();
        }
        this.connection = conn;
    }

    boolean hasConnection(WebSocket conn) {
        return this.connection == conn;
    }

    void setWindow(NWindow window) {
        this.window = window;
        if (!window.isBatchingOverridden()) window.enableRequestMode();
        synchronized (pageLoadCallbacks) {
            pageLoadCallbacks.clear();
        }
    }

    String getContent() {
        return htmlContent;
    }

    String getResourceBase() {
        return resourceBase;
    }

    void injectSessionBridge(String sessionId) {
        String bridgeJs = "(function(){var h=window.location.hostname;var p=" + wsPort
                + ";var ws=new WebSocket('ws://'+h+':'+p+'/?session=" + sessionId + "');window.java={handleClick:function(ex,uuid)"
                + "{var t=typeof ex;var m=uuid+'|';if(t==='string'){ws.send(m+'1|'+ex)}else if(t==='number')"
                + "{ws.send(m+'2|'+ex)}else if(t==='boolean'){ws.send(m+'3|'+ex)}else if(ex===null)"
                + "{ws.send(m+'4|')}else{ws.send(m+'0|'+JSON.stringify(ex))}}};window.jlog={log:function(m)"
                + "{console.log(m)},error:function(m){console.error(m)}};ws.onopen=function(){"
                + "ws.send('{\\\"type\\\":\\\"ready\\\"}')};ws.onmessage=function(e){var msg=JSON.parse(e.data);"
                + "if(msg.type==='eval'){eval(msg.js)}};})();";

        // Remove any external /nngf-bridge.js reference (replaced by inline bridge JS)
        htmlContent = htmlContent.replace("<script src=\"/nngf-bridge.js\"></script>", "");
        htmlContent = htmlContent.replace("<script src='/nngf-bridge.js'></script>", "");

        String scriptTag = "<script>" + bridgeJs + "</script>";
        if (htmlContent.contains("</head>")) {
            htmlContent = htmlContent.replace("</head>", scriptTag + "</head>");
        } else {
            htmlContent = scriptTag + htmlContent;
        }
    }

    @Override
    public Object executeJS(String js) {
        if (bridgeReady && connection != null && connection.isOpen()) {
            sendEval(js);
        } else {
            pendingJs.add(js);
        }
        return null;
    }

    private void sendEval(String js) {
        String escaped = js.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
        connection.send("{\"type\":\"eval\",\"js\":\"" + escaped + "\"}");
    }

    @Override
    public void setJsMember(String name, Object value) {
    }

    @Override
    public void onPageLoad(Runnable callback) {
        synchronized (pageLoadCallbacks) {
            pageLoadCallbacks.add(callback);
        }
        pageLoadCallback = callback;
    }

    @Override
    public void loadContent(String html) {
        this.htmlContent = html;
    }

    @Override
    public void loadUrl(String url) {
        try {
            URL u = new URL(url);
            int s = url.lastIndexOf('/');
            resourceBase = s >= 0 ? url.substring(0, s + 1) : url;
            try (InputStream is = u.openStream()) {
                this.htmlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            Logger.Error("SessionBrowserAdapter: Failed to load URL: " + e.getMessage(), 2);
        }
    }

    @Override
    public DataMap extract(Object event, List<String> keys) {
        if (event instanceof Map<?, ?> map) {
            Map<String, Object> result = new HashMap<>();
            for (String k : keys) {
                result.put(k, map.get(k));
            }
            return new DataMap(result);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("value", event);
        return new DataMap(result);
    }

    void onWsReady() {
        bridgeReady = true;
        List<Runnable> callbacks;
        synchronized (pageLoadCallbacks) {
            callbacks = new ArrayList<>(pageLoadCallbacks);
            pageLoadCallbacks.clear();
        }
        for (Runnable cb : callbacks) {
            cb.run();
        }
        StringBuilder bu = new StringBuilder();
        String js;
        while ((js = pendingJs.poll()) != null) {
            bu.append(js);
        }
        if (!bu.isEmpty())
            sendEval(bu.toString());
    }

    void close() {
        bridgeReady = false;
        connection = null;
        synchronized (pageLoadCallbacks) {
            pageLoadCallbacks.clear();
        }
        pendingJs.clear();
        if (window != null) {
            window.deattach();
        }
    }
}
