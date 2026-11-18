package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.Js;
import com.codehack.nngf.Core.NComponet;
import org.unbescape.javascript.JavaScriptEscape;

public class ElementQuery {
    private final NComponet component;
    private final String key;
    private final String base;

    public ElementQuery(NComponet component, String key) {
        this(component, key, "document.getElementById('" + component.ID + "')");
    }

    private ElementQuery(NComponet component, String key, String base) {
        this.component = component;
        this.key = key;
        this.base = base;
    }

    /**
     * Builder appends 'e.target'
     */
    public ElementQuery target() {
        return new ElementQuery(component, key, "e.target");
    }

    /**
     * Builder appends 'e'
     */
    public ElementQuery event() {
        return new ElementQuery(component, key, "e");
    }

    /**
     * Builder appends '.value'
     */
    public Query value() {
        return term(".value");
    }

    /**
     * Builder appends '.innerText'
     */
    public Query innerText() {
        return term(".innerText");
    }

    /**
     * Builder appends '.textContent'
     */
    public Query textContent() {
        return term(".textContent");
    }

    /**
     * Builder appends '.innerHTML'
     */
    public Query innerHTML() {
        return term(".innerHTML");
    }

    /**
     * Builder appends '.checked'
     */
    public Query checked() {
        return term(".checked");
    }

    public Query className() {
        return term(".className");
    }

    public Query src() {
        return term(".src");
    }

    public Query href() {
        return term(".href");
    }

    public Query disabled() {
        return term(".disabled");
    }

    public Query selectedIndex() {
        return term(".selectedIndex");
    }

    /**
     * Builder appends '.#{name}'
     */
    public Query prop(String name) {
        return new Query(key, base + "." + name);
    }

    /**
     * Builder appends '.getAttribute("#")
     */
    public Query attr(String name) {
        return new Query(key, base + ".getAttribute('" + JavaScriptEscape.escapeJavaScript(name) + "')");
    }

    public Query expr(Js.Expr expr) {
        return new Query(key, expr.toExpression());
    }

    public Query expr(com.codehack.nngf.js.Prop prop) {
        return new Query(key, prop.getExpr());
    }

    /**
     * Builder appends 'e.key'
     */
    public Query eventKey() {
        return new Query(key, "e.key");
    }

    /**
     * Builder appends 'e.code'
     */
    public Query eventCode() {
        return new Query(key, "e.code");
    }

    /**
     * Builder appends 'e.which'
     */
    public Query eventWhich() {
        return new Query(key, "e.which");
    }

    /**
     * Builder appends 'e.target'
     */
    public Query eventTarget() {
        return new Query(key, "e.target");
    }

    /**
     * Builder appends '#suffix'
     */
    private Query term(String suffix) {
        return new Query(key, base + suffix);
    }
}
