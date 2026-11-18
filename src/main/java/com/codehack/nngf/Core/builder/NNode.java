package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.exceptions.ComponentNotAttachedException;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

import java.util.ArrayList;
import java.util.List;

public class NNode {
    protected NWindow window;
    /*private boolean batchActive = false;
    private List<String> batchQuery = new ArrayList<>();
    private boolean blockDirectReads = false;*/

    public NNode() {
    }

    public Object executeJs(String js) throws ComponentNotAttachedException {
        if (window == null)
            throw new ComponentNotAttachedException("Component Not attached for Run JS");

        Logger.Log("Executing JS: " + js, 0);
        try{
            return window.executeJS(js);
        } catch (JsBridgeException e) {
            Logger.Error(e.getMessage(),2);
            throw new ComponentNotAttachedException("Cannot Execute Given JS");
        }
    }

    public Object executeJsRead(String js) throws ComponentNotAttachedException {
        if (window == null)
            throw new ComponentNotAttachedException("Component Not attached for Run JS");

        Logger.Log("Executing read JS: " + js, 0);
        return window.executeJS(js);
    }

    public boolean isAttached() {
        return (window != null);
    }

    public void removeWindow() {
        deattach();
    }

    public void deattach() {
    }

    public NWindow getWindow() {
        return window;
    }

    public void setWindow(NWindow window) {
        this.window = window;
        if (window != null) {
            replayQueuedEvents();
        }
    }

    protected void replayQueuedEvents() {
    }
}
