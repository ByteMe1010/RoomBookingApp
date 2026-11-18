package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.DataModel.StyleDocument;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NNGF;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.bridge.BrowserAdapter;
import com.codehack.nngf.Core.exceptions.ComponentNotAttachedException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;

import java.util.*;
import java.util.function.Consumer;

public class NEvent {

    //region CONST : Common Dom Event Types
    public static final String CLICK = "click";
    public static final String DBLCLICK = "dblclick";
    public static final String MOUSEDOWN = "mousedown";
    public static final String MOUSEUP = "mouseup";
    public static final String MOUSEOVER = "mouseover";
    public static final String MOUSEOUT = "mouseout";
    public static final String MOUSEMOVE = "mousemove";
    public static final String MOUSEENTER = "mouseenter";
    public static final String MOUSELEAVE = "mouseleave";
    public static final String KEYDOWN = "keydown";
    public static final String KEYUP = "keyup";
    public static final String KEYPRESS = "keypress";
    public static final String FOCUS = "focus";
    public static final String BLUR = "blur";
    public static final String FOCUSIN = "focusin";
    public static final String FOCUSOUT = "focusout";
    public static final String CHANGE = "change";
    public static final String INPUT = "input";
    public static final String SUBMIT = "submit";
    public static final String RESET = "reset";
    public static final String SELECT = "select";
    public static final String SCROLL = "scroll";
    public static final String RESIZE = "resize";
    public static final String LOAD = "load";
    public static final String UNLOAD = "unload";
    public static final String ERROR = "error";
    public static final String COPY = "copy";
    public static final String CUT = "cut";
    public static final String PASTE = "paste";
    public static final String CONTEXTMENU = "contextmenu";
    public static final String DRAG = "drag";
    public static final String DRAGSTART = "dragstart";
    public static final String DRAGEND = "dragend";
    public static final String DRAGOVER = "dragover";
    public static final String DRAGENTER = "dragenter";
    public static final String DRAGLEAVE = "dragleave";
    public static final String DROP = "drop";
    public static final String TOUCHSTART = "touchstart";
    public static final String TOUCHMOVE = "touchmove";
    public static final String TOUCHEND = "touchend";
    public static final String TOUCHCANCEL = "touchcancel";
    public static final String POINTERDOWN = "pointerdown";
    public static final String POINTERUP = "pointerup";
    public static final String POINTERMOVE = "pointermove";
    public static final String POINTEROVER = "pointerover";
    public static final String POINTEROUT = "pointerout";
    public static final String POINTERENTER = "pointerenter";
    public static final String POINTERLEAVE = "pointerleave";
    public static final String WHEEL = "wheel";
    public static final String ANIMATIONSTART = "animationstart";
    public static final String ANIMATIONEND = "animationend";
    public static final String ANIMATIONITERATION = "animationiteration";
    public static final String TRANSITIONSTART = "transitionstart";
    public static final String TRANSITIONEND = "transitionend";
    public static final String TRANSITIONRUN = "transitionrun";
    public static final String TRANSITIONCANCEL = "transitioncancel";
    public static final String BEFOREINPUT = "beforeinput";
    public static final String COMPOSITIONSTART = "compositionstart";
    public static final String COMPOSITIONUPDATE = "compositionupdate";
    public static final String COMPOSITIONEND = "compositionend";
    //endregion

    private final NComponet parent;

    public Map<String, String> eventLink = new HashMap<>(); // UUID - type
    public Map<String, NEventDef> coustemEvents = new HashMap<>(); //UUID - Event
    List<Map.Entry<String, NEventDef>> events = new ArrayList<>(); // Type - event
    List<String> cEventQuery = new ArrayList<>();
    Map<String, String> jsEvents = new HashMap<>();

    private int lastSession = -1;

    public NEvent(NComponet parent) {
        this.parent = parent;
    }

    //region EVT : Java Events
    public NEvent addEvent(String type, Consumer<DataMap> handler, Query... queries) {
        Logger.Log("Registering DataMap event handler for type '" + type + "' on element [" + parent.ID + "]", 0);

        if (queries == null || queries.length == 0) {
            Logger.Log("Warning: No queries provided for DataMap event on element [" + parent.ID + "]", 2);
        }

        StringBuilder modifier = new StringBuilder();
        if (queries!=null){
            modifier.append("ex = {");
            for (int i = 0; i < queries.length; i++) {
                if (queries[i] == null) {
                    throw new IllegalArgumentException("Query at index " + i + " is null for event type '" + type + "' on element [" + parent.ID + "]");
                }
                if (queries[i].key == null || queries[i].key.isEmpty()) {
                    throw new IllegalArgumentException("Query key is null or empty at index " + i + " for event type '" + type + "' on element [" + parent.ID + "]");
                }
                if (queries[i].jsExpression == null || queries[i].jsExpression.isEmpty()) {
                    throw new IllegalArgumentException("Query jsExpression is null or empty at index " + i + " for event type '" + type + "' on element [" + parent.ID + "]");
                }
                if (i > 0) modifier.append(", ");
                modifier.append(queries[i].key).append(": ").append(queries[i].jsExpression);
            }
            modifier.append("};");

        }
        List<String> keys = Arrays.stream(queries).map(q -> q.key).toList();

        return addEvent(type, modifier.toString(), evt -> {
            NWindow win = parent.getWindow();
            if (win == null) {
                Logger.Error("Window is null when executing DataMap event on element [" + parent.ID + "]", 2);
                return;
            }
            BrowserAdapter browser = win.getBrowser();
            if (browser == null) {
                Logger.Error("Browser is null when executing DataMap event on element [" + parent.ID + "]", 2);
                return;
            }
            DataMap data = browser.extract(evt, keys);
            handler.accept(data);
        });
    }

    public NEvent addEvent(String type, NEventDef e) {
        Logger.Log("Registering Java event handler for type '" + type + "' on element [" + parent.ID + "]", 0);

        if (parent.isAttached()) {
            int session = parent.window.getBridgeSession();
            if (session != lastSession) {
                for (String uuid : eventLink.keySet())
                    parent.window.lm.removeEvent(uuid);
                for (String uuid : coustemEvents.keySet())
                    parent.window.lm.removeEvent(uuid);
                events.clear();
                eventLink.clear();
                coustemEvents.clear();
                jsEvents.clear();
                cEventQuery.clear();
                lastSession = session;
            }
        }

        if (!eventLink.containsValue(type)) {
            String evtuuid = getUUID();
            try {
                events.add(Map.entry(type, e));
                eventLink.put(evtuuid, type);
                parent.executeChildJs("element.addEventListener('" + type + "', (e) => {java.handleClick(e,'" + evtuuid + "');});");
                parent.window.lm.registerEvent(evtuuid, this);
            } catch (LifecycleViolationException ex) {
                events.removeIf(entry -> entry.getKey().equals(type) && entry.getValue() == e);
                eventLink.remove(evtuuid);
                cEventQuery.add("element.addEventListener('" + type + "', (e) => {java.handleClick(e,'" + evtuuid + "');});");
            } catch (ComponentNotAttachedException ex) {
                events.removeIf(entry -> entry.getKey().equals(type) && entry.getValue() == e);
                eventLink.remove(evtuuid);
                cEventQuery.add("element.addEventListener('" + type + "', (e) => {java.handleClick(e,'" + evtuuid + "');});");
                Logger.Log("Queued event '" + type + "' on element [" + parent.ID + "] for replay when attached", 0);
            }
        } else {
            events.add(Map.entry(type, e));
        }
        return this;
    }

    /**
     * this allows to bind java events with intermediate js code. there are two variables,<br>
     * e = this is the event object of the triggered event.<br>
     * ex= this is the input object to java lambda as the e.<br>
     * initically ex = e;<br> and ex can be changed to any value in this intermediate code.
     * also element refers to the current element that execute the code on.
     *
     */
    public NEvent addEvent(String type, String e_to_ex_modifier, NEventDef e) {
        Logger.Log("Registering custom Java event handler for type '" + type + "' on element [" + parent.ID + "] with modifier: " + e_to_ex_modifier, 0);

        if (parent.isAttached()) {
            int session = parent.window.getBridgeSession();
            if (session != lastSession) {
                for (String uuid : eventLink.keySet())
                    parent.window.lm.removeEvent(uuid);
                for (String uuid : coustemEvents.keySet())
                    parent.window.lm.removeEvent(uuid);
                events.clear();
                eventLink.clear();
                coustemEvents.clear();
                jsEvents.clear();
                cEventQuery.clear();
                lastSession = session;
            }
        }

        String UUID = getUUID();
        try {
            coustemEvents.put(UUID, e);
            parent.executeChildJs("element.addEventListener('" + type + "', (e) => {" +
                    "let ex = e;" +
                    e_to_ex_modifier +
                    "java.handleClick(ex,'" + UUID + "');" +
                    "});");
            parent.window.lm.registerEvent(UUID, this);
        } catch (LifecycleViolationException ex) {
//            coustemEvents.remove(UUID);
            cEventQuery.add("element.addEventListener('" + type + "', (e) => {" +
                    "let ex = e;" +
                    e_to_ex_modifier +
                    "java.handleClick(ex,'" + UUID + "');" +
                    "});");
        } catch (ComponentNotAttachedException ex) {
//            coustemEvents.remove(UUID);
            cEventQuery.add("element.addEventListener('" + type + "', (e) => {" +
                    "let ex = e;" +
                    e_to_ex_modifier +
                    "java.handleClick(ex,'" + UUID + "');" +
                    "});");
            Logger.Log("Queued custom event '" + type + "' on element [" + parent.ID + "] for replay when attached", 0);
        }

        return this;
    }

    public NEvent onClick(NEventDef e) {
        return addEvent("click", e);
    }

    public NEvent onClick(String e_to_ex_modifier, NEventDef e) {
        return addEvent("click", e_to_ex_modifier, e);
    }
    //endregion

    //region EVT : JS Events
    public NEvent addJsEvent(String type, String event) {
        Logger.Log("Registering JS-only event handler for type '" + type + "' on element [" + parent.ID + "]: " + event, 0);
        try {
            parent.executeChildJs("element.addEventListener('" + type + "', (e) => {" + event + "});");
        } catch (ComponentNotAttachedException ex) {
            jsEvents.put(type, event);
        }
        return this;
    }
    //endregion

    //region BUILDER : Builder classes for tree building
    public String getJs() {
        StringBuilder js = new StringBuilder();
        for (Map.Entry<String, String> evt : eventLink.entrySet()) { //UUID,evtName
            js.append("element.addEventListener('")
                    .append(evt.getValue())
                    .append("', (e) => {java.handleClick(e,'")
                    .append(evt.getKey()).append("');});")
                    .append("\n");
            if (parent.isAttached()) {
                parent.window.lm.registerEvent(evt.getKey(), this);
            }
        }
        for (Map.Entry<String, String> evt : jsEvents.entrySet()) { //UUID,evtName
            js.append("element.addEventListener('")
                    .append(evt.getKey())
                    .append("', (e) => {")
                    .append(evt.getValue()).append("});")
                    .append("\n");
        }
        for (String cd : cEventQuery) {
            js.append(cd).append("\n");
        }
        if (parent.isAttached()) {
            for (String uuid : coustemEvents.keySet()) {
                parent.window.lm.registerEvent(uuid, this);
            }
        }
        if (js.isEmpty())
            return "";
        return String.format(
                "(function() {" +
                        "   var element = document.getElementById('%s');" +
                        "   if(!element) return null;\n" +
                        "   %s" +
                        "})();",
                parent.ID, js
        );

    }

    private static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public void deattach() {
        if (parent.window == null) return;
        for (String u : eventLink.keySet()) {
            parent.window.lm.removeEvent(u);
        }
        for (String u : coustemEvents.keySet()) {
            parent.window.lm.removeEvent(u);
        }
        eventLink.clear();
        coustemEvents.clear();
        cEventQuery.clear();
        lastSession = -1;
    }
    //endregion

    public void clearEvents(String type) {
        events.removeIf(entry -> entry.getKey().equals(type));
        cleanOrphanedEventLinks();
    }

    public void clearAllEvents() {
        events.clear();
        cleanOrphanedEventLinks();
    }

    private void cleanOrphanedEventLinks() {
        Set<String> activeTypes = new HashSet<>();
        for (Map.Entry<String, NEventDef> entry : events) {
            activeTypes.add(entry.getKey());
        }

        Iterator<Map.Entry<String, String>> linkIt = eventLink.entrySet().iterator();
        while (linkIt.hasNext()) {
            Map.Entry<String, String> link = linkIt.next();
            if (!activeTypes.contains(link.getValue())) {
                if (parent.window != null) {
                    parent.window.lm.removeEvent(link.getKey());
                }
                linkIt.remove();
            }
        }

        Set<String> activeCustomUUIDs = new HashSet<>();
        for (String uuid : coustemEvents.keySet()) {
            boolean found = false;
            for (Map.Entry<String, NEventDef> entry : events) {
                if (coustemEvents.get(uuid) == entry.getValue()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (parent.window != null) {
                    parent.window.lm.removeEvent(uuid);
                }
                activeCustomUUIDs.add(uuid);
            }
        }
        for (String uuid : activeCustomUUIDs) {
            coustemEvents.remove(uuid);
        }
    }

    public void executeEvent(Object event, String UUID) {
        Logger.Log("Executing registered Java event for element [" + parent.ID + "]. UUID: " + UUID, 1);
        if (!eventLink.containsKey(UUID) && !coustemEvents.containsKey(UUID)) {
            Logger.Log("No matched event link or custom event found for UUID: " + UUID, 1);
            return;
        }

        String evt = eventLink.get(UUID);
        if (eventLink.containsKey(UUID)) {
            for (Map.Entry<String, NEventDef> out : events) {
                if (out.getKey().equals(evt)) {
                    Logger.Log("Executing event handler for type: '" + evt + "'", 1);
                    out.getValue().handleEvent(event);
                }
            }
        } else {
            NEventDef st = coustemEvents.get(UUID);
            if (st != null) {
                Logger.Log("Executing custom event handler", 1);
                st.handleEvent(event);
            }
        }
    }
}
