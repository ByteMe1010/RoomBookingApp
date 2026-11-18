package com.codehack.nngf.Display.Web;

import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NNGF;
import com.codehack.nngf.Core.NWindow;
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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class WebRootWindow {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Object>> sessionStores = new ConcurrentHashMap<>();
    private static final Map<String, Long> pendingCleanup = new ConcurrentHashMap<>();
    private static final long CLEANUP_DELAY_MS = 300_000; // 5 minutes
    // Single-session state
    private static WebBrowserAdapter browser;
    private static NWindow currentWindow;
    private static String title = "";
    // Multi-session state
    private static Supplier<NWindow> sessionFactory;
    private static boolean multiSession = false;
    private static com.sun.net.httpserver.HttpServer httpServer;
    private static WebSocketServer wsServer;
    // Session cleaner
    private static ScheduledExecutorService cleaner;
    // Shared state
    private static int port = 8080;
    private static boolean isActive = false;
    private static boolean started = false;

    public static void setPort(int port) {
        WebRootWindow.port = port;
    }

    public static WebBrowserAdapter getBrowser() {
        return browser;
    }

    @Deprecated
    public static void attach(NWindow window) {
        multiSession = false;
        title = window.title;
        if (currentWindow != null)
            currentWindow.deattach();
        currentWindow = window;
        if (isActive)
            Start();
    }

    public static void navigate(NWindow currentApp, NWindow newApp) {
        if (!multiSession) {
            attach(newApp);
            return;
        }
        for (Map.Entry<String, SessionContext> e : sessions.entrySet()) {
            if (e.getValue().window == currentApp) {
                navigateSession(e.getKey(), newApp);
                return;
            }
        }
        Logger.Error("navigate: currentApp not found in any session", 2);
    }

    public static Map<String, Object> getSessionStore(String sessionId) {
        return sessionStores.computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>());
    }

    public static void setSessionFactory(Supplier<NWindow> factory) {
        sessionFactory = factory;
        multiSession = true;
    }

    public static void start() {
        Start();
    }

    public static void Start() {
        if (multiSession) {
            if (started) {
                Logger.Log("WebRootWindow already started in multi-session mode", 1);
                return;
            }
            startMultiSession();
        } else {
            startSingleSession();
        }
    }

    private static void startSingleSession() {
        if (currentWindow == null)
            throw new NullPointerException("Window is Null");

        if (browser == null) {
            try {
                browser = new WebBrowserAdapter(port);
                NNGF.addJs(browser.generateBridgeJs());
            } catch (Exception e) {
                Logger.Error("WebRootWindow Start failed: " + e.getMessage(), 2);
                throw new RuntimeException(e);
            }
        }

        browser.setWindow(currentWindow);
        currentWindow.start(browser,false);

        if (!started) {
            try {
                browser.start();
                started = true;
                isActive = true;
                Logger.Log("WebRootWindow started — HTTP on " + browser.getHttpPort()
                        + ", WS on " + browser.getWsPort(), 1);
            } catch (Exception e) {
                Logger.Error("WebRootWindow Start failed: " + e.getMessage(), 2);
                throw new RuntimeException(e);
            }
        } else {
            browser.reloadPage();
        }
    }

    private static void startMultiSession() {
        if (sessionFactory == null)
            throw new NullPointerException("SessionFactory is null. Call setSessionFactory() first.");

        int httpPort = port + 1;

        try {
            wsServer = new WebSocketServer(new InetSocketAddress(port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    String resource = handshake.getResourceDescriptor();
                    String sessionId = parseSessionId(resource);
                    if (sessionId != null) {
                        pendingCleanup.remove(sessionId);
                        SessionContext ctx = sessions.get(sessionId);
                        if (ctx != null) {
                            ctx.adapter.setConnection(conn);
                        }
                    }
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
                    handleWsMessage(conn, message);
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    for (Entry<String, SessionContext> e : sessions.entrySet()) {
                        if (e.getValue().adapter.hasConnection(conn)) {
                            pendingCleanup.put(e.getKey(), System.currentTimeMillis());
                            break;
                        }
                    }
                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    Logger.Error("Session WS error: " + ex.getMessage(), 2);
                }

                @Override
                public void onStart() {
                    Logger.Log("WebSocket server started on port " + port, 1);
                }
            };
            wsServer.setReuseAddr(true);
            wsServer.start();

            httpServer = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(httpPort), 0);

            httpServer.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

                // Root: create session, set cookie, redirect
                if (path.equals("/") || path.isEmpty()) {
                    String sessionId = UUID.randomUUID().toString().substring(0, 8);
                    try {
                        createSession(sessionId);
                    } catch (Exception ex) {
                        Logger.Error("Failed to create session: " + ex.getMessage(), 2);
                        ex.printStackTrace();
                        byte[] errBytes = ("Session creation failed: " + ex.getMessage()).getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        exchange.sendResponseHeaders(500, errBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(errBytes);
                        }
                        return;
                    }
                    exchange.getResponseHeaders().set("Set-Cookie",
                            "session=" + sessionId + "; HttpOnly; Path=/; SameSite=Strict");
                    exchange.getResponseHeaders().set("Location", "/app");
                    exchange.sendResponseHeaders(302, -1);
                    return;
                }

                // Serve session page (cookie-authenticated)
                if (path.equals("/app")) {
                    String sessionId = extractSessionIdFromCookie(exchange);
                    if (sessionId == null) {
                        exchange.getResponseHeaders().set("Location", "/");
                        exchange.sendResponseHeaders(302, -1);
                        return;
                    }
                    SessionContext ctx = sessions.get(sessionId);
                    if (ctx == null || ctx.adapter.getContent() == null) {
                        exchange.getResponseHeaders().set("Location", "/");
                        exchange.sendResponseHeaders(302, -1);
                        return;
                    }
                    byte[] bytes = ctx.adapter.getContent().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                    return;
                }

                // Serve static resources — try cookie-matched session first, then all sessions
                String rel = path.startsWith("/") ? path.substring(1) : path;
                String cookieSid = extractSessionIdFromCookie(exchange);
                if (cookieSid != null) {
                    SessionContext sc = sessions.get(cookieSid);
                    if (sc != null && sc.adapter.getResourceBase() != null) {
                        try {
                            URL resourceUrl = new URL(sc.adapter.getResourceBase() + rel);
                            byte[] bytes;
                            try (InputStream is = resourceUrl.openStream()) {
                                bytes = is.readAllBytes();
                            }
                            exchange.getResponseHeaders().set("Content-Type", detectContentType(path));
                            exchange.sendResponseHeaders(200, bytes.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(bytes);
                            }
                            return;
                        } catch (Exception ex) {
                            Logger.Log("Session resource not found in cookie session: " + ex.getMessage(), 4);
                        }
                    }
                }
                for (SessionContext sc : sessions.values()) {
                    String base = sc.adapter.getResourceBase();
                    if (base == null) continue;
                    try {
                        URL resourceUrl = new URL(base + rel);
                        byte[] bytes;
                        try (InputStream is = resourceUrl.openStream()) {
                            bytes = is.readAllBytes();
                        }
                        exchange.getResponseHeaders().set("Content-Type", detectContentType(path));
                        exchange.sendResponseHeaders(200, bytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(bytes);
                        }
                        return;
                    } catch (Exception ex) {
                        Logger.Log("Session resource not found in session " + sc + ": " + ex.getMessage(), 4);
                    }
                }

                // Try nngf/server resource
                try {
                    URL resourceUrl = WebRootWindow.class.getResource("/nngf/server" + path);
                    if (resourceUrl != null) {
                        byte[] bytes;
                        try (InputStream is = resourceUrl.openStream()) {
                            bytes = is.readAllBytes();
                        }
                        exchange.getResponseHeaders().set("Content-Type", detectContentType(path));
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
            });

            httpServer.start();

            cleaner = Executors.newSingleThreadScheduledExecutor();
            cleaner.scheduleAtFixedRate(() -> tickCleaner(), CLEANUP_DELAY_MS, CLEANUP_DELAY_MS, TimeUnit.MILLISECONDS);

            started = true;
            isActive = true;
            Logger.Log("WebRootWindow (multi-session) started — HTTP on " + httpPort
                    + ", WS on " + port, 1);
        } catch (IOException e) {
            Logger.Error("WebRootWindow start failed: " + e.getMessage(), 2);
            throw new RuntimeException(e);
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

    private static void createSession(String sessionId) {
        Logger.Log("createSession: calling sessionFactory.get() for session " + sessionId, 1);
        NWindow win = sessionFactory.get();
        if (win == null) {
            Logger.Error("Session factory returned null NWindow", 10);
            return;
        }
        win.setSessionId(sessionId);
        SessionBrowserAdapter adapter = new SessionBrowserAdapter(port);
        adapter.setWindow(win);
        Logger.Log("createSession: calling win.start() for session " + sessionId, 1);
        win.start(adapter,false);
        Logger.Log("createSession: calling injectSessionBridge() for session " + sessionId, 1);
        adapter.injectSessionBridge(sessionId);
        sessions.put(sessionId, new SessionContext(win, adapter));
        Logger.Log("Created session: " + sessionId, 1);
    }

    private static void navigateSession(String sessionId, NWindow newApp) {
        SessionContext oldCtx = sessions.get(sessionId);
        if (oldCtx == null) return;

        newApp.setSessionId(sessionId);
        SessionBrowserAdapter newAdapter = new SessionBrowserAdapter(port);
        newAdapter.setWindow(newApp);
        newApp.start(newAdapter,false);
        newAdapter.injectSessionBridge(sessionId);

        sessions.put(sessionId, new SessionContext(newApp, newAdapter));

        WebSocket oldConn = oldCtx.adapter.connection;
        if (oldConn != null && oldConn.isOpen()) {
            String js = "location.reload()";
            String escaped = js.replace("\\", "\\\\").replace("\"", "\\\"")
                    .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
            oldConn.send("{\"type\":\"eval\",\"js\":\"" + escaped + "\"}");
        }

        oldCtx.adapter.close();
        Logger.Log("Navigated session " + sessionId + " to new app", 1);
    }

    private static void handleWsMessage(WebSocket conn, String message) {
        try {
            SessionContext ctx = findSessionByConnection(conn);
            if (ctx == null) return;

            if (message.startsWith("{\"type\":\"ready\"}")) {
                Logger.Log("Bridge ready for session", 1);
                ctx.adapter.onWsReady();
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
                default:
                    event = null;
                    break;
            }

            if (ctx.window != null) {
                ctx.window.handleClick(event, uuid);
            }
        } catch (Exception e) {
            Logger.Error("Event handler error: " + e.getMessage(), 2);
        }
    }

    private static SessionContext findSessionByConnection(WebSocket conn) {
        for (SessionContext ctx : sessions.values()) {
            if (ctx.adapter.hasConnection(conn)) return ctx;
        }
        return null;
    }

    private static String parseSessionId(String path) {
        if (path == null) return null;
        int qIdx = path.indexOf('?');
        if (qIdx >= 0) {
            String query = path.substring(qIdx + 1);
            for (String param : query.split("&")) {
                String[] parts = param.split("=", 2);
                if (parts.length == 2 && parts[0].equals("session")) {
                    return parts[1];
                }
            }
        }
        String p = qIdx >= 0 ? path.substring(0, qIdx) : path;
        if (p.startsWith("/") && p.length() > 1) {
            return p.substring(1);
        }
        return null;
    }

    private static String extractSessionIdFromCookie(com.sun.net.httpserver.HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader == null) return null;
        for (String part : cookieHeader.split(";")) {
            part = part.trim();
            if (part.startsWith("session=")) {
                return part.substring("session=".length());
            }
        }
        return null;
    }

    private static void tickCleaner() {
        long now = System.currentTimeMillis();
        Iterator<Entry<String, Long>> it = pendingCleanup.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, Long> e = it.next();
            if (now - e.getValue() >= CLEANUP_DELAY_MS) {
                String sid = e.getKey();
                it.remove();
                sessions.remove(sid);
                sessionStores.remove(sid);
                Logger.Log("Cleaned up session: " + sid, 1);
            }
        }
    }

    public static void close() {
        if (cleaner != null) {
            cleaner.shutdown();
            cleaner = null;
        }
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
        if (wsServer != null) {
            try {
                wsServer.stop();
            } catch (Exception e) {
                Logger.Log("WebRootWindow.close: wsServer stop error: " + e.getMessage(), 3);
            }
            wsServer = null;
        }
        if (browser != null) {
            browser.stop();
            browser = null;
        }
        for (SessionContext ctx : sessions.values()) {
            ctx.adapter.close();
        }
        sessions.clear();
        sessionStores.clear();
        pendingCleanup.clear();
        if (currentWindow != null) {
            currentWindow.deattach();
            currentWindow = null;
        }
        isActive = false;
        started = false;
    }

    public static boolean isActive() {
        return isActive;
    }

    private record SessionContext(NWindow window, SessionBrowserAdapter adapter) {
    }
}
