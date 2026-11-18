package com.codehack.nngf.Display.FX;

import com.codehack.nngf.Core.NWindow;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class RootWindow {
    private static final WebView webView = new WebView();
    private static JavaFxBrowserAdapter browser;
    private static boolean isActive = false;
    private static NWindow currentWindow;
    private static Stage stage;
    private static boolean settedUp = false;
    private static String title = "";

    public static void setup(Stage stage) {
        RootWindow.stage = stage;
        webView.setContextMenuEnabled(false);
        browser = new JavaFxBrowserAdapter(webView);
    }

    public static void attach(NWindow window) {
        title = window.title;
        if (currentWindow != null)
            currentWindow.deattach();
        currentWindow = window;
        if (isActive)
            Start();
    }

    public static String getHTML() {
        return (String) browser.executeJS("document.documentElement.outerHTML");
    }

    public static void setTitle(String title) {
        RootWindow.title = title;
        if (isActive)
            stage.setTitle(title);
    }

    public static void Start() {
        if (currentWindow == null)
            throw new NullPointerException("Window is Null");
        settedUp = true;

        currentWindow.start(browser,false);
        if (!stage.isShowing()) {
            Scene scene = new Scene(webView);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
            isActive = true;
        }
    }

    public static void close() {
        stage.close();
        isActive = false;
        settedUp = false;
    }

    public static void hide() {
        stage.hide();
        isActive = false;
    }

    public static void show() {
        stage.show();
        isActive = true;
    }
}
