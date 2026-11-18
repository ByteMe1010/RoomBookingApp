package com.codehack.nngf.Core.bridge;

import com.codehack.nngf.Core.DataModel.DataMap;

import java.util.List;

public interface BrowserAdapter {
    /**this is a Last place of execute Js flow. script that comes to this method need to be sent for frontend to execute.
     * @param script is a js code that can be directly sent to interface for execution
     * @return is a any object returned from running that script
     */
    Object executeJS(String script);


    /**
     * in webview based models we're setting some java methods as js accessable classes. if plugin support that functions this can be used. else this can be kept default because nngf.js script already enables communication to main functions
     *
     * @param name is the name that js side will use
     * @param value is the class we use (example.class)
     */
    default void setJsMember(String name, Object value) {}

    /**this method need to registar events that will run when page is successfully loaded.
     * @param callback is a java Runnable that need to be run after the page load.
     */
    void onPageLoad(Runnable callback);

    /** this method will be called when new page need to be loaded via html blob. implimentatoin need to reload page to given html content
     * @param html is a HTML content that need to be loaded.
     */
    void loadContent(String html);

    /** this method will be called when page need to loaded by a URL.
     * @param url is the URL of page that need to be loaded
     */
    void loadUrl(String url);

    /** this is a helper method that not intended to make a network call. this connects with the datamap flow,
     * when executeJs returns a object and if it's a datamap, framework will automatically send it to this method with
     * keys that need to extract data. then this method need to take the keys and return the datamap. Object data format in the docs
     * @param event is the Object that sent to extract data from
     * @param keys are the key names and data types that used to extract the data
     * @return the datamap object containing the extracted data
     */
    DataMap extract(Object event, List<String> keys);
}
