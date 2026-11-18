package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.Logger;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NNGF;

import java.util.HashMap;
import java.util.Map;

public class NStyle {
    private final NComponet parent;
    Map<String, StyleBlock> cssEvents = new HashMap<>();

    public StyleBlock normal = StyleBlock.of();
    public StyleBlock onHover= StyleBlock.of();
    public StyleBlock onClick= StyleBlock.of();

    public NStyle(NComponet parent) {
        this.parent = parent;
        normal.onAdd.thenRun(() -> {
            if (parent.isAttached())
                addStyle(normal);
        });
        onHover.onAdd.thenRun(() -> {
            if (parent.isAttached())
                addStyleEvent("hover",onHover);
        });
        onClick.onAdd.thenRun(() -> {
            if (parent.isAttached())
                addStyleEvent("active",onClick);
        });
    }

    //region CSS : Styles and StyleEvents
    public NStyle addStyle(StyleBlock style) {
        if (style==null || style.styles.isEmpty()) return this;
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style.addPrefix("#" + parent.ID));
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }
        StyleBlock.mergeBlock(cssEvents, style.addPrefix("#" + parent.ID).target, style);
        return this;
    }

    public NStyle addStyle(StyleBlock... style) {
        if (style==null || !(style.length>0)) return this;
        for (StyleBlock s : style) {
            if (parent.isAttached()) {
                parent.window.liveStyle.fromStyle(s.addPrefix("#" + parent.ID));
                //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
            }
                StyleBlock.mergeBlock(cssEvents, s.addPrefix("#" + parent.ID).target, s);
        }
        return this;
    }

    @Deprecated
    public NStyle addStyle(String... dta) {
        StyleBlock bk = StyleBlock.of();
        for (int i = 0; i < dta.length; i++) {
            if ((i + 1) < dta.length) {
                bk.style(dta[i], dta[i + 1]);
            } else {
                Logger.Log("Warn >> registering style got n/2 != 0 amount of args, last one will not be added!" + dta[i], 4);
            }
        }
        return addStyle(bk);
    }

    public NStyle addStyle(StyleBlock style, String topParent) {
        if (style==null || style.styles.isEmpty()) return this;
        style.addPrefix("#" + parent.ID).addPrefix(topParent);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }
        StyleBlock.mergeBlock(cssEvents, style.target, style);
        return this;
    }

    public NStyle addStyleEvent(String event, StyleBlock style) {
        if (style==null || style.styles.isEmpty()) return this;
        style.addPrefix("#" + parent.ID).addSuffix(":" + event);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }StyleBlock.mergeBlock(cssEvents, style.target, style);
        return this;
    }

    public NStyle addStyleEvent(String event, StyleBlock style, String topParent) {
        if (style==null || style.styles.isEmpty()) return this;
        style.addPrefix("#" + parent.ID).addSuffix(":" + event).addPrefix(topParent);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }StyleBlock.mergeBlock(cssEvents, style.target, style);
        return this;
    }
    //endregion

    //region CSS T : Template Styles
    public NStyle addTemplateStyle(StyleBlock style) {
        style.addPrefix(".NNGF-" + parent.elementName);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }
        NNGF.addStyle(style);
        return this;
    }

    public NStyle addTemplateStyleEvent(String event, StyleBlock style) {
        style.addPrefix(".NNGF-" + parent.elementName + ":" + event);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }
        NNGF.addStyle(style);
        return this;
    }

    public NStyle addTemplateStyle(StyleBlock style, String topParent) {
        style.addPrefix(".NNGF-" + parent.elementName).addPrefix(topParent);
        if (parent.isAttached()){
            parent.window.liveStyle.fromStyle(style);
            //throw new LifecycleViolationException("Style Events only supported to set before Attaching component to window. :" +style.target);
        }
        NNGF.addStyle(style);
        return this;
    }
    //endregion

    //region BUILDER : Builder classes for tree building
    public void getCss(Map<String, Map<String, String>> data) {
        addStyle(normal);
        addStyleEvent("hover",onHover);
        addStyleEvent("active",onClick);
        for (Map.Entry<String, StyleBlock> block : cssEvents.entrySet()) {
            StyleBlock.mergeInto(data, block.getKey(), block.getValue().styles);
        }
    }
    //endregion
}
