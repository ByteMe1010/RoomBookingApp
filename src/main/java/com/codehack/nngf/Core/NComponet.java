package com.codehack.nngf.Core;

import com.codehack.nngf.Core.DataModel.DataMap;
import com.codehack.nngf.Core.DataModel.PropertyDescriptor;
import com.codehack.nngf.Core.DataModel.StyleBlock;
import static com.codehack.nngf.Core.DataModel.StyleBlock.mergeAllInto;

import com.codehack.nngf.Core.builder.*;
import com.codehack.nngf.Core.styleQuery.QueryResolver;
import com.codehack.nngf.gui.NullGroup;
import com.codehack.nngf.Core.exceptions.ComponentNotAttachedException;
import com.codehack.nngf.Core.exceptions.IDduplicationException;
import com.codehack.nngf.Core.exceptions.JsBridgeException;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;
import com.codehack.nngf.js.JS;
import org.unbescape.html.HtmlEscape;
import org.unbescape.javascript.JavaScriptEscape;

import java.util.*;
import java.util.function.Consumer;

public abstract class NComponet extends NNode {
    public final String ID;
    public final NEvent events;
    public final NStyle styles;
    public List<NComponet> childs = new ArrayList<>(); //TODO : Guess this is scope Leak, methods exist for child manipulation, but some outside implimentations uses derect access, need to audit it and set this private
    public String elementName;
    public ClassList classList;
    protected String innerContent = "";
    protected boolean isInnerHTML = true;
    protected List<String> classes = new ArrayList<>();
    protected Map<String, String> attributes = new HashMap<>();
    public StyleBlock style = StyleBlock.of();
    private boolean Disabled = false;
    private final List<PropertyDescriptor<?>> editableProperties = new ArrayList<>();

    //region CONST : Constructors and Helper checkID, formatID methods
    public NComponet(String ID, String elementName) {
        super();
        this.ID = formatID(ID);
        this.elementName = elementName;
        events = new NEvent(this);
        styles = new NStyle(this);
        classList = new ClassList();
        checkID(ID);
        registerBaseProperties();
    }
    public NComponet(String ID, String elementName, NWindow window) {
        super();
        if (window == null) {
            Logger.Error("Window is Null! Cannot be Setted", 10);
            throw new NullPointerException("Window is Null and cannot be applyed");
        }
        setWindow(window);
        this.ID = formatID(ID);
        this.elementName = elementName;
        events = new NEvent(this);
        styles = new NStyle(this);
        classList = new ClassList();
        checkID(ID);
        registerBaseProperties();
    }
    private void checkID(String id){
        if (!id.contains(" "))
            return;
        String code = id.substring(id.indexOf(" "));
        QueryResolver.resolve(code,this);
    }
    private String formatID(String id){
        if (!id.contains(" "))
            return id;
        return id.substring(0,id.indexOf(" "));
    }
    //endregion

    //region GUI-Prop : GUI Property system
    protected void registerProperty(PropertyDescriptor<?> prop) {
        editableProperties.add(prop);
    }
    public List<PropertyDescriptor<?>> getEditableProperties() {
        return editableProperties;
    }
    private void registerBaseProperties() {
        registerProperty(PropertyDescriptor.readonly("ID", "ID", ID));
    }
    //endregion

    //region HELPERS : Convenience helpers for NEvent Methods
    public NComponet addStyle(String... dta) {
        StyleBlock bk = StyleBlock.of();
        for (int i = 0; i < dta.length; i++) {
            if ((i + 1) < dta.length) {
                bk.style(dta[i], dta[i + 1]);
            } else {
                Logger.Log("Warn >> registering style got n/2 != 0 amount of args, last one will not be added!" + dta[i], 4);
            }
        }
        styles.addStyle(bk);
        return this;
    }
    public NComponet onClick(NEventDef handler) {
        events.onClick(handler);
        return this;
    }
    public NComponet onClick(Consumer<DataMap> handler, Query... quries) {
        events.addEvent("click",handler,quries);
        return this;
    }
    //endregion

    //region CHILD : Child management Methods and deattach methods
    public void addChild(NComponet child) {
        Logger.Log("Adding child component [" + child.ID + "] to parent [" + ID + "]", 1);
        if (isAttached()){
            try {
                if (childs.stream().anyMatch(c -> c.ID.equals(child.ID)))
                    throw new IDduplicationException(child.ID + " already Exists!");
                Map.Entry<String,Map.Entry<String,String>> entry = child.build(window);
                appendInnerHTML(entry.getKey());
                if (entry.getValue().getKey()!=null && !entry.getValue().getKey().isEmpty()){
                    window.liveStyle.insertBlob(entry.getValue().getKey());
                }
                if (entry.getValue().getValue()!=null && !entry.getValue().getValue().isEmpty()){
                    executeJs(entry.getValue().getValue());
                }
                } catch (ComponentNotAttachedException ex) {
                Logger.Log("Parent [" + ID + "] not attached — children will be built when parent is attached", 1);
            } catch (JsBridgeException e) {
                Logger.Error("JsBridgeException inside addChild for component [" + ID + "]: " + e.getMessage(), 2);
                throw new RuntimeException(e);
            }
        }
        childs.add(child);
    }

    public void addChild(NComponet... child) {
        Logger.Log("Adding " + child.length + " child components to parent [" + ID + "]", 1);
        if (isAttached()){
            NullGroup ng = new NullGroup("none", "none");
            ng.childs(child);
            try {
                for (NComponet c : child) {
                    if (childs.stream().anyMatch(existing -> existing.ID.equals(c.ID)))
                        throw new IDduplicationException(c.ID + " already Exists!");
                }
                var entry = ng.build(window);
                appendInnerHTML(entry.getKey());
                if (entry.getValue().getKey()!=null && !entry.getValue().getKey().isEmpty()){
                    window.liveStyle.insertBlob(entry.getValue().getKey());
                }
                if (entry.getValue().getValue()!=null && !entry.getValue().getValue().isEmpty()){
                    executeJs(entry.getValue().getValue());
                }
            } catch (ComponentNotAttachedException ex) {
                Logger.Log("Parent [" + ID + "] not attached — children will be built when parent is attached", 1);
            } catch (JsBridgeException e) {
                Logger.Error("JsBridgeException inside addChild for component [" + ID + "]: " + e.getMessage(), 2);
                throw new RuntimeException(e);
            }
        }
        childs.addAll(Arrays.stream(child).toList());
    }

    public void addChild(List<NComponet> child) {
        if (child.isEmpty())
            return;
        Logger.Log("Adding " + child.size() + " child components to parent [" + ID + "]", 1);
        if (isAttached()){
            NullGroup ng = new NullGroup("none", "none");
            ng.childs(child);
            try {
                for (NComponet c : child) {
                    if (childs.stream().anyMatch(existing -> existing.ID.equals(c.ID)))
                        throw new IDduplicationException(c.ID + " already Exists!");
                }
                var entry = ng.build(window);
                appendInnerHTML(entry.getKey());
                if (entry.getValue().getKey()!=null && !entry.getValue().getKey().isEmpty()){
                    window.liveStyle.insertBlob(entry.getValue().getKey());
                }
                if (entry.getValue().getValue()!=null && !entry.getValue().getValue().isEmpty()){
                    executeJs(entry.getValue().getValue());
                }

            } catch (ComponentNotAttachedException ex) {
                Logger.Log("Parent [" + ID + "] not attached — children will be built when parent is attached", 1);
            } catch (JsBridgeException e) {
                Logger.Error("JsBridgeException inside addChild for component [" + ID + "]: " + e.getMessage(), 2);
                throw new RuntimeException(e);
            }
        }
        childs.addAll(child);
    }

    public void clearChilds() {
        Logger.Log("Clearing child components of [" + ID + "]", 1);
        deattachChilds();
        childs.clear();
        try {
            setInnerHTML("");
        } catch (JsBridgeException e) {
            Logger.Error("JsBridgeException inside clearChilds for component [" + ID + "]: " + e.getMessage(), 2);
            throw new RuntimeException(e);
        }
    }

    public void removeChild(int index) {
        if (childs.size() >= index) {
            NComponet removed = childs.get(index);
            Logger.Log("Removing child component index " + index + " [" + (removed != null ? removed.ID : "null") + "] from [" + ID + "]", 1);
            childs.remove(index);
            try {
                executeChildJs(Js.el().call("removeChild", Js.raw("element.children[" + index + "]")).end());
            } catch (LifecycleViolationException ex) {
                Logger.Log("Component [" + ID + "] not attached — skipping DOM child removal", 1);
            }
        }
    }

    public void removeSelf() {
        Logger.Log("Removing self component [" + ID + "]", 1);
        if (isAttached()) {
            try {
                executeChildJs(Js.el().call("remove").end());
            } catch (LifecycleViolationException ex) {
                Logger.Log("Component [" + ID + "] not attached — skipping DOM remove", 1);
            }
        }
        removeWindow();
    }

    @Override
    public void deattach() {
        this.events.deattach();
        this.window = null;
        for (NComponet ch : childs) {
            ch.deattach();
        }
    }

    public void deattachChilds() {
        for (NComponet ch : childs) {
            ch.deattach();
        }
    }
    //endregion

    //region DOMJS : Dom caller methods
    public void setDisabled(boolean val){
        Disabled = val;
        if (isAttached()){
            executeChildJs(JS.Element().property("disabled").getExpr()+"="+((val) ? "true;":"false;"));
        }
    }

    public void setAttribute(String key, String value)  {
        Logger.Log("Setting attribute on component [" + ID + "]: " + key + "=" + value, 0);
        try {
            executeJs(Js.id(ID).call("setAttribute", key, value).end());
        } catch (ComponentNotAttachedException ex) {
            Logger.Log("Component [" + ID + "] not attached — local state update only", 1);
        }
        attributes.put(key, value);
    }

    public void removeAttribute(String key)  {
        Logger.Log("Removing attribute on component [" + ID + "]: " + key, 0);
        try {
            executeJs(Js.id(ID).call("removeAttribute", key).end());
        } catch (ComponentNotAttachedException ex) {
            Logger.Log("Component [" + ID + "] not attached — local state update only", 1);
        }
        attributes.remove(key);
    }

    public void hide() {
        setAttribute("style", "display: none;");
    }

    public void show() {
        removeAttribute("style");
    }

    public void setInnerHTML(String HTML)  {
        Logger.Log("Setting inner HTML for component [" + ID + "]", 0);
        try {
            executeJs(Js.id(ID).prop("innerHTML", HTML).end());
        } catch (ComponentNotAttachedException ex) {
            Logger.Log("Component [" + ID + "] not attached — local state update only", 1);
        }
        innerContent = HTML;
        isInnerHTML = true;
        deattachChilds();
    }

    public void appendInnerHTML(String HTML)  {
        Logger.Log("Appending inner HTML to component [" + ID + "]", 0);
        try {
            executeJs(Js.id(ID).call("insertAdjacentHTML", "beforeend", HTML).end());
        } catch (ComponentNotAttachedException ex) {
            Logger.Log("Component [" + ID + "] not attached — local state update only", 1);
        }
        innerContent += HTML;
        isInnerHTML = true;
    }

    public void appendInnerText(String Text)  {
        try {
            executeJs(Js.id(ID).propAppend("innerText", Text).end());
            innerContent += Text;
        } catch (ComponentNotAttachedException ex) {
            innerContent += HtmlEscape.escapeHtml5(Text);
        }
        isInnerHTML = false;
    }

    public void setTextContent(String Text)  {
        try {
            executeJs(Js.id(ID).prop("textContent", Text).end());
            innerContent = Text;
            isInnerHTML = false;
            deattachChilds();
        } catch (ComponentNotAttachedException ex) {
            throw new LifecycleViolationException("Trying to change TextContent on Non Attached Element");
        }
    }

    public String getInnerText()  {
        if (isAttached() && window.isBatchActive()) {
            return innerContent;
        }
        Object obj;
        try {
            obj = executeChildJsRead(Js.el().ret().get("innerText").end());
        } catch (LifecycleViolationException e) {
            return innerContent;
        }
        if (obj == null) return null;
        innerContent = obj.toString();
        return innerContent;
    }

    public void setInnerText(String Text)  {
        Logger.Log("Setting inner text for component [" + ID + "]: " + Text, 0);
        try {
            executeJs(Js.id(ID).prop("innerText", Text).end());
            innerContent = Text;
        } catch (ComponentNotAttachedException ex) {
            innerContent = HtmlEscape.escapeHtml5(Text);
        }
        isInnerHTML = false;
        deattachChilds();
    }

    public String getAttributeValue(String attr)  {
        if (isAttached()&&window.isBatchActive()) {
            return attributes.get(attr);
        }
        Object obj;
        try {
            obj = executeChildJsRead(Js.el().ret().call("getAttribute", attr).end());
        } catch (LifecycleViolationException e) {
            return attributes.get(attr);
        }
        if (obj == null) return null;
        attributes.put(attr, obj.toString());
        return attributes.get(attr);
    }
    //endregion

    //region JS : Js execution Methods
    public Object executeChildJs(String js) throws ComponentNotAttachedException {
        String jsToExecute = String.format(
                "(function() {" +
                        "   var element = document.getElementById('%s');" +
                        "   if(!element) return null;" +
                        "   %s" +
                        "})();",
                ID, js
        );
        Logger.Log("Execute Child JS Executing: " + jsToExecute, -1);
        return executeJs(jsToExecute);
    }

    public Object executeChildJsRead(String js) throws ComponentNotAttachedException {
        String jsToExecute = String.format(
                "(function() {" +
                        "   var element = document.getElementById('%s');" +
                        "   if(!element) return null;" +
                        "   %s" +
                        "})();",
                ID, js
        );
        return executeJsRead(jsToExecute);
    }
    //endregion

    //region BUILDER : Framework Tree Builder methods
    public abstract String buildHtml(String childHtml);

    public String buildHtmlDoc(NWindow rootWindow, List<String> activeIDs) {
        if (!style.styles.isEmpty())
            styles.addStyle(style);
        if (activeIDs.contains(ID))
            throw new IDduplicationException(ID + " Already Exists!");
        activeIDs.add(ID);
        setWindow(rootWindow);
        StringBuilder childCode = new StringBuilder();
        childCode.append(innerContent);
        if (!childs.isEmpty()) {
            for (NComponet ch : childs) {
                childCode.append(ch.buildHtmlDoc(rootWindow, activeIDs));
            }
        }
        StringBuilder cz = new StringBuilder();
        cz.append("NNGF-" + elementName).append(" ");
        for (String c : classes) {
            cz.append(c).append(" ");
        }
        attributes.put("class", cz.toString().strip());
        return setAttributesToFirstTag(buildHtml(childCode.toString()), ID, attributes,Disabled);
    }

    public void buildCss(Map<String, Map<String, String>> map) {
    }

    public Map<String, Map<String, String>> collectCss() {
        Map<String, Map<String, String>> dta = new HashMap<>();
        if (!childs.isEmpty()) {
            for (NComponet ch : childs) {
                mergeAllInto(dta, ch.collectCss());
            }
        }
        buildCss(dta);
        styles.getCss(dta);
        return dta;
    }

    public void buildJs(List<String> js) {

    }

    public List<String> collectJs() {
        List<String> dta = new ArrayList<>();
        if (!childs.isEmpty()) {
            for (NComponet ch : childs) {
                dta.addAll(ch.collectJs());
            }
        }
        buildJs(dta);
        String w = events.getJs();
        if (!w.isEmpty())
            dta.add(w);
        return dta;
    }

    public void getEventBindings(Map<String, NEvent> binds) {
        if (!childs.isEmpty()) {
            for (NComponet ch : childs) {
                ch.getEventBindings(binds);
            }
        }
        for (Map.Entry<String, String> f : events.eventLink.entrySet()) {
            binds.put(f.getKey(), events);
        }
        for (String uuid : events.coustemEvents.keySet()) {
            binds.put(uuid, events);
        }
    }

    public Map.Entry<String, Map.Entry<String,String>> build(NWindow window) {
        Logger.Log("Building component HTML/CSS/JS for component [" + ID + "]", 1);
        if (window == null) return Map.entry("", Map.entry("",""));
        String html = buildHtmlDoc(window, new ArrayList<String>());
        List<String> js = collectJs();
        Map<String, Map<String, String>> styles = collectCss();
        window.lm.collectEventBindings(this);

        StringBuilder css = new StringBuilder();
        for (Map.Entry<String, Map<String, String>> mp : styles.entrySet()) {
            css.append(mp.getKey()).append("{\n");
            for (Map.Entry<String, String> sp : mp.getValue().entrySet()) {
                css.append(sp.getKey()).append(" : ").append(sp.getValue()).append(";\n");
            }
            css.append("}").append("\n");
        }
        StringBuilder jss = new StringBuilder();
        for (String j : js) {
            jss.append(j).append("\n");
        }
        String out = html;
        return Map.entry(out, Map.entry(css.toString(),jss.toString()));
    }
    //endregion

    //region HELPER : Helpers and Query Builders
    public static String setAttributesToFirstTag(String html, String id, Map<String, String> attributes,boolean disabled) {
        if (html == null || html.isEmpty()) return html;

        int openIdx = html.indexOf('<');
        if (openIdx == -1) return html;

        int closeIdx = findClosingTagBracket(html, openIdx);
        if (closeIdx == -1) return html;

        StringBuilder attrBuilder = new StringBuilder();
        if (id != null && !id.trim().isEmpty()) {
            attrBuilder.append("id=\"").append(id).append("\"");
        }
        if (attributes != null) {
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && value != null) {
                    if (attrBuilder.length() > 0) attrBuilder.append(' ');
                    attrBuilder.append(key).append("=\"").append(value).append("\"");
                }
            }
        }
        if (disabled)
            attrBuilder.append(" disabled");
        String attrString = attrBuilder.toString();
        if (attrString.isEmpty()) return html;

        char prevChar = html.charAt(closeIdx - 1);
        StringBuilder result = new StringBuilder(html);
        if (prevChar != ' ' && prevChar != '<') {
            result.insert(closeIdx, " " + attrString);
        } else {
            result.insert(closeIdx, attrString);
        }
        return result.toString();
    }

    private static int findClosingTagBracket(String html, int start) {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = start + 1; i < html.length(); i++) {
            char c = html.charAt(i);
            if (c == '\'' && !inDoubleQuote) inSingleQuote = !inSingleQuote;
            else if (c == '"' && !inSingleQuote) inDoubleQuote = !inDoubleQuote;
            else if (c == '>' && !inSingleQuote && !inDoubleQuote) {
                return i;
            }
        }
        return -1;
    }

    public String jsStr(String unsafe) {
        return JavaScriptEscape.escapeJavaScript(unsafe);
    }

    public ElementQuery q(String key) {
        return new ElementQuery(this, key);
    }
    //endregion

    public class ClassList {
        public void add(String clasz) {
            try {
                executeChildJs(Js.el().call("classList.add", clasz).end());
            } catch (ComponentNotAttachedException | LifecycleViolationException ex) {
                Logger.Log("ClassList.add failed for [" + ID + "]: " + ex.getMessage(), 1);
            }
            classes.add(clasz);
        }

        public void remove(String clasz) {
            try {
                executeChildJs(Js.el().call("classList.remove", clasz).end());
            } catch (ComponentNotAttachedException | LifecycleViolationException ex) {
                Logger.Log("ClassList.remove failed for [" + ID + "]: " + ex.getMessage(), 1);
            }
            classes.remove(clasz);
        }

        public void toggle(String clasz) {
            try {
                executeChildJs(Js.el().call("classList.toggle", clasz).end());
            } catch (ComponentNotAttachedException | LifecycleViolationException ex) {
                Logger.Log("ClassList.toggle failed for [" + ID + "]: " + ex.getMessage(), 1);
            }
            if (classes.contains(clasz)) {
                classes.remove(clasz);
            } else {
                classes.add(clasz);
            }
        }
    }

    public class cache {
        public String getID() {
            return ID;
        }

        public Set<Map.Entry<String, String>> getAttributes() {
            return attributes.entrySet();
        }

        public String getInnerContent() {
            return innerContent;
        }

        public List<String> classList() {
            return classes;
        }
    }
}
