package com.codehack.nngf.Display.Web;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.bridge.BrowserAdapter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WebBrowserAdapter implements BrowserAdapter {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final int wsPort;
    private final int httpPort;
    private final List<Runnable> pageLoadCallbacks = new ArrayList<>();
    private final Queue<String> pendingJs = new ConcurrentLinkedQueue<>();
    private NWindow window;
    private WebSocketServer wsServer;
    private com.sun.net.httpserver.HttpServer httpServer;
    private volatile WebSocket connection;
    private String htmlContent;
    private String resourceBase;

    public WebBrowserAdapter(int wsPort) {
        this.wsPort = wsPort;
        this.httpPort = wsPort + 1;
    }

    void setWindow(NWindow window) {
        this.window = window;
        if (!window.isBatchingOverridden()) window.enableRequestMode();
        synchronized (pageLoadCallbacks) {
            pageLoadCallbacks.clear();
        }
    }

    @Override
    public Object executeJS(String js) {
        if (connection != null && connection.isOpen()) {
            sendEval(js);
        } else {
            pendingJs.add(js);
        }
        return null;
    }

    public void reloadPage() {
        if (connection != null && connection.isOpen()) {
            sendEval("location.reload()");
        } else {
            pendingJs.add("location.reload()");
        }
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
        pageLoadCallbacks.add(callback);
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

            String content;
            try (InputStream is = u.openStream()) {
                content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }

            this.htmlContent = content;
        } catch (IOException e) {
            Logger.Error("Failed to load URL: " + e.getMessage(), 2);
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

    public void start() throws IOException {
        wsServer = new WebSocketServer(new InetSocketAddress(wsPort)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                Logger.Log("Web client connected", 1);
                connection = conn;
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                handleWsMessage(message);
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                Logger.Log("Web client disconnected", 1);
                if (conn == connection) connection = null;
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                Logger.Error("WebSocket error: " + ex.getMessage(), 2);
            }

            @Override
            public void onStart() {
                Logger.Log("WebSocket server started on port " + wsPort, 1);
            }
        };
        wsServer.setReuseAddr(true);
        wsServer.start();

        httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(httpPort), 0);
        httpServer.createContext("/nngf-bridge.js", exchange -> {
            byte[] bytes = generateBridgeJs().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/javascript; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        httpServer.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();

            if (path.equals("/") && htmlContent != null) {
                byte[] bytes = htmlContent.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else if (resourceBase != null) {
                String rel = path.startsWith("/") ? path.substring(1) : path;
                try {
                    URL resourceUrl = new URL(resourceBase + rel);
                    byte[] bytes;
                    try (InputStream is = resourceUrl.openStream()) {
                        bytes = is.readAllBytes();
                    }
                    String ct = "application/octet-stream";
                    if (rel.endsWith(".js")) ct = "application/javascript; charset=UTF-8";
                    else if (rel.endsWith(".css")) ct = "text/css; charset=UTF-8";
                    else if (rel.endsWith(".png")) ct = "image/png";
                    else if (rel.endsWith(".jpg") || rel.endsWith(".jpeg")) ct = "image/jpeg";
                    else if (rel.endsWith(".svg")) ct = "image/svg+xml";
                    else if (rel.endsWith(".html")) ct = "text/html; charset=UTF-8";
                    else if (rel.endsWith(".json")) ct = "application/json";
                    else if (rel.endsWith(".woff2")) ct = "font/woff2";
                    else if (rel.endsWith(".map")) ct = "application/json";
                    exchange.getResponseHeaders().set("Content-Type", ct);
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    String msg = "404 Not Found";
                    exchange.sendResponseHeaders(404, msg.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(msg.getBytes(StandardCharsets.UTF_8));
                    }
                }
            } else {
                // Try nngf/server resource
                try {
                    String rel = path.startsWith("/") ? path.substring(1) : path;
                    URL resourceUrl = WebBrowserAdapter.class.getResource("/nngf/server/" + rel);
                    if (resourceUrl != null) {
                        byte[] bytes;
                        try (InputStream is = resourceUrl.openStream()) {
                            bytes = is.readAllBytes();
                        }
                        String ct = detectContentType(rel);
                        exchange.getResponseHeaders().set("Content-Type", ct);
                        exchange.sendResponseHeaders(200, bytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(bytes);
                        }
                        return;
                    }
                } catch (Exception ex) {
                    Logger.Log("nngf/server resource not found: " + ex.getMessage(), 4);
                }

                String msg = "404 Not Found";
                exchange.sendResponseHeaders(404, msg.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg.getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        httpServer.start();
        Logger.Log("HTTP server started on port " + httpPort, 1);
    }

    private void handleWsMessage(String message) {
        if (message.startsWith("{\"type\":\"ready\"}")) {
            Logger.Log("Bridge ready signal received", 1);
            List<Runnable> callbacks;
            synchronized (pageLoadCallbacks) {
                callbacks = new ArrayList<>(pageLoadCallbacks);
            }
            for (Runnable cb : callbacks) {
                cb.run();
            }
            String js;
            StringBuilder bu = new StringBuilder();
            while ((js = pendingJs.poll()) != null) {
                bu.append(js);
            }
            if (!bu.isEmpty())
                sendEval(bu.toString());
            return;
        }

        int firstSep = message.indexOf('|');
        if (firstSep == -1) return;
        String uuid = message.substring(0, firstSep);
        int restIdx = firstSep + 1;
        int typeSep = message.indexOf('|', restIdx);
        if (typeSep == -1) return;
        int type = Integer.parseInt(message.substring(restIdx, typeSep));
        String dataStr = message.substring(typeSep + 1);

        Object event;
        switch (type) {
            case 0:
                try {
                    event = mapper.readValue(dataStr, new TypeReference<Map<String, Object>>() {
                    });
                } catch (Exception e) {
                    event = dataStr;
                }
                break;
            case 1:
                event = dataStr;
                break;
            case 2:
                try {
                    event = Integer.parseInt(dataStr);
                } catch (NumberFormatException e1) {
                    try {
                        event = Double.parseDouble(dataStr);
                    } catch (NumberFormatException e2) {
                        event = dataStr;
                    }
                }
                break;
            case 3:
                event = Boolean.parseBoolean(dataStr);
                break;
            case 4:
            default:
                event = null;
                break;
        }

        if (window != null) {
            window.handleClick(event, uuid);
        }
    }

    public int getWsPort() {
        return wsPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void stop() {
        if (httpServer != null) httpServer.stop(0);
        if (wsServer != null) {
            try {
                wsServer.stop();
            } catch (Exception e) {
                Logger.Log("WebBrowserAdapter.stop: wsServer stop error: " + e.getMessage(), 3);
            }
        }
    }

    private static String detectContentType(String path) {
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".woff2")) return "font/woff2";
        if (path.endsWith(".map")) return "application/json";
        return "application/octet-stream";
    }

    public String generateBridgeJs() {
        return "(function(){var h=window.location.hostname;var p=" + wsPort
                + ";var ws=new WebSocket('ws://'+h+':'+p+'/');window.java={handleClick:function(ex,uuid)"
                + "{var t=typeof ex;var m=uuid+'|';if(t==='string'){ws.send(m+'1|'+ex)}else if(t==='number')"
                + "{ws.send(m+'2|'+ex)}else if(t==='boolean'){ws.send(m+'3|'+ex)}else if(ex===null)"
                + "{ws.send(m+'4|')}else{ws.send(m+'0|'+JSON.stringify(ex))}}};window.jlog={log:function(m)"
                + "{console.log(m)},error:function(m){console.error(m)}};ws.onopen=function(){"
                + "ws.send('{\\\"type\\\":\\\"ready\\\"}')};ws.onmessage=function(e){var msg=JSON.parse(e.data);"
                + "if(msg.type==='eval'){eval(msg.js)}};})();";
    }
}
