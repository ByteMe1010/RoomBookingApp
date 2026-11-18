package com.codehack.nngf.Display.FX;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.bridge.BrowserAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFxBrowserAdapter implements BrowserAdapter {
    private final WebView webView;
    private final WebEngine engine;
    private final List<Runnable> pageLoadCallbacks = new ArrayList<>();
    private boolean listenerSetup = false;
    private HttpServer server;
    private String baseUrl;

    public JavaFxBrowserAdapter(WebView webView) {
        this.webView = webView;
        this.engine = webView.getEngine();
        startServer();
    }

    private void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", new ResourceHandler());
            server.setExecutor(null);
            server.start();
            baseUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
            Logger.Log("FX resource server started at: " + baseUrl, 1);
        } catch (IOException e) {
            Logger.Error("Failed to start FX resource server: " + e.getMessage(), 2);
            baseUrl = "http://127.0.0.1:0/";
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private class ResourceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            Logger.Log("FX server request: " + path, 0);
            String resourcePath = "/nngf/server" + path;
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }
            try (InputStream in = url.openStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
                byte[] data = out.toByteArray();
                String ct = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", ct);
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }

        private String getContentType(String path) {
            String p = path.toLowerCase();
            if (p.endsWith(".css")) return "text/css; charset=utf-8";
            if (p.endsWith(".js")) return "application/javascript; charset=utf-8";
            if (p.endsWith(".html")) return "text/html; charset=utf-8";
            if (p.endsWith(".png")) return "image/png";
            if (p.endsWith(".jpg") || p.endsWith(".jpeg")) return "image/jpeg";
            if (p.endsWith(".svg")) return "image/svg+xml";
            if (p.endsWith(".woff")) return "font/woff";
            if (p.endsWith(".woff2")) return "font/woff2";
            if (p.endsWith(".ttf")) return "font/ttf";
            if (p.endsWith(".map")) return "application/json";
            return "application/octet-stream";
        }
    }

    public WebView getWebView() {
        return webView;
    }

    @Override
    public Object executeJS(String js) throws JSException {
        Logger.Log("executeJS called: " + js, 0);
        if (Platform.isFxApplicationThread()) {
            try {
                return engine.executeScript(js);
            } catch (Exception e) {
                Logger.Error("executeJS error (FX Thread): " + e.getMessage(), 2);
                throw new JSException("JS execution failed: \n" + e);
            }
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(engine.executeScript(js));
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.Error("executeJS interrupted: " + e.getMessage(), 2);
            throw new JSException("Interrupted while waiting for JS execution: " + e);
        }

        if (error.get() != null) {
            Logger.Error("executeJS error (Non-FX Thread): " + error.get().getMessage(), 2);
            throw new JSException("JS execution failed: " + error.get());
        }

        return result.get();
    }

    @Override
    public void setJsMember(String name, Object value) {
        JSObject window = (JSObject) engine.executeScript("window");
        window.setMember(name, value);
    }

    @Override
    public synchronized void onPageLoad(Runnable callback) {
        pageLoadCallbacks.add(callback);
    }

    @Override
    public void loadContent(String html) {
        setupLoadListener();
        String modified = html
                .replaceAll("href=\"base/", "href=\"" + baseUrl + "base/")
                .replaceAll("src=\"base/", "src=\"" + baseUrl + "base/");
        modified = modified.replace("<head>", "<head><base href=\"" + baseUrl + "\">");
        engine.loadContent(modified);
    }

    @Override
    public void loadUrl(String url) {
        setupLoadListener();
        engine.load(url);
    }

    @Override
    public DataMap extract(Object event, List<String> keys) {
        Map<String, Object> map = new HashMap<>();
        if (event instanceof JSObject jsObj) {
            for (String key : keys) {
                try {
                    map.put(key, jsObj.getMember(key));
                } catch (JSException e) {
                    map.put(key, null);
                }
            }
        }
        return new DataMap(map);
    }

    private synchronized void setupLoadListener() {
        if (listenerSetup) return;
        listenerSetup = true;
        engine.getLoadWorker().stateProperty().addListener((obs, oldV, newV) -> {
            if (newV == Worker.State.SUCCEEDED) {
                Logger.Log("WebEngine load completed. Firing page load callbacks.", 1);
                List<Runnable> callbacks;
                synchronized (this) {
                    callbacks = new ArrayList<>(pageLoadCallbacks);
                }
                for (Runnable r : callbacks) {
                    try {
                        r.run();
                    } catch (Exception e) {
                        Logger.Error("Page load callback error: " + e.getMessage(), 2);
                    }
                }
                pageLoadCallbacks.clear();
            } else if (newV == Worker.State.FAILED) {
                Logger.Error("WebEngine load failed", 2);
            }
        });
    }
}