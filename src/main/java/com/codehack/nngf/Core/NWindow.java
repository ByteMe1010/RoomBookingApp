package com.codehack.nngf.Core;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.RuntimeStyleSystem.RuntimeStyle;
import com.codehack.nngf.Core.bridge.BrowserAdapter;
import com.codehack.nngf.Core.builder.EventGroup;
import com.codehack.nngf.Core.builder.LanguageManager;
import com.codehack.nngf.Core.exceptions.ComponentNotAttachedException;
import com.codehack.nngf.Core.exceptions.JsBridgeException;

import java.util.ArrayList;
import java.util.List;

public class NWindow {
    //Core
    public final EventGroup bridgeReady = new EventGroup();
    public LanguageManager lm;
    public String title = "";
    private BrowserAdapter browser;
    //Session System
    private String sessionId;
    private boolean started = false;
    private int bridgeSession = 0;
    //Batch System
    private boolean isBatchActive = false;
    private final List<String> batchQueries = new ArrayList<>();
    private boolean requestMode = false;
    private int requestModeOverride=0;
    //Loader Data
    private boolean ExternalDomLoad;
    private loaderType type = loaderType.Node;
    private String Url;
    private boolean baseSet = false;
    //StyleSystem
    public final RuntimeStyle liveStyle;

    //region CONST : Constents
    public static final String HTML_ID = "nngf-document-html";
    public static final String HEAD_ID = "nngf-document-head";
    public static final String ROOT_CSS_ID = "nngf-document-root-css";
    public static final String ROOT_JS_ID = "nngf-document-root-js";
    public static final String BODY_ID = "nngf-document-body";
    //endregion

    public NWindow(String title) {
        this.title = title;
        lm = new LanguageManager(this);
        liveStyle = new RuntimeStyle(this);
    }

    public String getSessionId() { return sessionId; }

    public void setSessionId(String id) { sessionId = id; }

    //region CSS JS : Static Js and css Systems
    public void AddStaticJS(String js) {
        lm.addStaticJs(js);
    }

    public void AddCss(StyleBlock... styles) {
        for (StyleBlock st : styles) {
            lm.addCssStyle(st.target, st.styles);
        }
    }

    public void AddStaticCss(String css) {
        lm.addStaticCss(css);
    }

    public void AddJsFile(String id,String url) {
        lm.addJsFile(id,url);
    }

    public void AddCssFile(String id,String url) {
        lm.addCssFile(id,url);
    }
    //endregion

    //region BATCH : Batching (requestMode Batch)
    public boolean isBatchingOverridden(){
        return (requestModeOverride>0);
    }
    public void enableRequestMode() {
        requestModeOverride=0;
        requestMode = true;
    }

    public void disableRequestMode() {
        requestModeOverride++;
        commitBatch();
        isBatchActive=false;
        requestMode = false;
    }

    void startBatch() {
        if (!requestMode)
            return;
        if (isBatchActive) {
            Logger.Error("Warn : Batch already Active!", 2);
            return;
        }
        batchQueries.clear();
        isBatchActive = true;
    }

    void invalidateBatch() {
        batchQueries.clear();
    }

    void commitBatch() {
        if (!requestMode)
            return;
        if (browser == null)
            return;
        StringBuilder bu = new StringBuilder();
        for (String batch : batchQueries) {
            if (batch.startsWith("return")) {
                Logger.Error("Warn : Return Statement in Batch Found, excluding : " + batch, 2);
                continue;
            }
            bu.append("\n").append(batch);
        }
        isBatchActive = false;
        batchQueries.clear();
        browser.executeJS(bu.toString());
    }

    public boolean isBatchActive(){
        return isBatchActive;
    }
    //endregion

    //region CORE : Core Methods
    public int getBridgeSession() {
        return bridgeSession;
    }

    public BrowserAdapter getBrowser() {
        return browser;
    }

    public void deattach() {
        lm.deattach();
    }

    public boolean isAttached(){
        return started;
    }

    public void setView(NComponet root) {
        Logger.Log("Setting window view to root component: " + root.ID, 1);
        lm.setRoot(root);
        if (started) {
            setDocument(lm.build(baseSet));
        }
        ExternalDomLoad = false;
        type = loaderType.Node;
    }

    public Object executeJS(String js)  {
        if (isBatchActive) {
            batchQueries.add(js);
            return null;
        }
        if (browser== null) throw new ComponentNotAttachedException("Brouser is Null");
        return browser.executeJS(js);
    }

    public void setDocument(String doc) {
        Logger.Log("Loading document content into WebEngine", 1);
        Url = doc;
        type = loaderType.File;
        ExternalDomLoad = false;
        lm.clearEvents();
    }

    public void setExternalDocument(String url) {
        Logger.Log("Loading external document URL: " + url, 1);
        Url = url;
        type = loaderType.WebPage;
        ExternalDomLoad = true;
        lm.clearEvents();
    }
    @Deprecated(forRemoval = false, since = "JDoc update. use setExternalDocument instead.")
    public void loadResource(String resourceUrl) throws NullPointerException {
        setExternalDocument(resourceUrl);
    }

    public void handleClick(Object event, String uuid) {
        Logger.Log("Received event callback from JS bridge. UUID: " + uuid, 1);
        startBatch();
        try {
            lm.routeEvent(uuid, event);
        } finally {
            commitBatch();
        }
    }

    public void start(BrowserAdapter browser,boolean addbase) {
        baseSet=addbase;
        this.browser = browser;
        String document = "";
        if (type == loaderType.Node) {
            document = lm.build(baseSet);
        } else if (type == loaderType.File) {
            document = Url;
        } else if (type == loaderType.WebPage) {
            browser.loadUrl(Url);
        }
        Logger.Log("Starting NWindow: '" + title + "'", 1);
        System.out.println(document);
        Runnable onPageReady = () -> {
            startBatch();
            bridgeSession++;
            browser.setJsMember("java", this);
            browser.setJsMember("jlog", new logBridge());
            executeJS("""
                    document.dispatchEvent(new Event('nngf-bridge-ready'));
                    console.log = function(msg){try{jlog.log(msg);}catch(e){}};
                    console.error=function(msg){try{jlog.error(msg);}catch(e){}};
                    window.onerror = function(message, source, lineno, colno, error) {
                        try{jlog.error("Uncaught JS Error: " + message + " at " + source + ":" + lineno + ":" + colno);}catch(e){}
                        return false;
                    };
                    """);
            bridgeReady.complete();
            commitBatch();
        };
        browser.onPageLoad(onPageReady);

        if (!ExternalDomLoad) {
            browser.loadContent(document);
        }

        started = true;
    }
    //endregion

    private enum loaderType {
        Node, File, WebPage
    }

    public class logBridge {
        public void log(String msg) {
            Logger.Log("JS Console Log: " + msg, 0);
        }

        public void error(String msg) {
            Logger.Error("JS Console Error: " + msg, 2);
        }
    }
}
