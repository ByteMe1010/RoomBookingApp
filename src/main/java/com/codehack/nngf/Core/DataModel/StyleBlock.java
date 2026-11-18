package com.codehack.nngf.Core.DataModel;

import com.codehack.nngf.Core.builder.EventGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StyleBlock {
    public String target;
    @Deprecated
    public Map<String, String> styles = new HashMap<>();
    public final EventGroup onAdd = new EventGroup();

    public StyleBlock(String target) {
        this.target = target;
    }

    public StyleBlock(String target, Map<String, String> styles) {
        this.target = target;
        this.styles = styles;
    }

    public static StyleBlock init(String target) {
        return new StyleBlock(target);
    }

    public static StyleBlock of() {
        return new StyleBlock("");
    }

    public static StyleBlock of(String target) {
        return new StyleBlock(target);
    }

    public StyleBlock setTarget(String target) {
        this.target = target;
        return this;
    }

    public StyleBlock style(String key, String value) {
        styles.put(key, value);
        onAdd.complete();
        return this;
    }
    //========== CONVINIENCE =================
    public StyleBlock FullScreenWithNoMargin(){
        height("100%").width("100%").margin("0");
        return this;
    }

    // ========== TEXT & TYPOGRAPHY ==========
    public StyleBlock color(String color) {
        style("color", color);
        return this;
    }

    public StyleBlock backgroundColor(String color) {
        style("background-color", color);
        return this;
    }

    public StyleBlock fontSize(String size) {
        style("font-size", size);
        return this;
    }

    public StyleBlock fontSizePx(int px) {
        return fontSize(px + "px");
    }

    public StyleBlock fontWeight(String weight) {
        style("font-weight", weight);
        return this;
    }

    public StyleBlock fontWeight(int weight) {
        return fontWeight(String.valueOf(weight));
    }

    public StyleBlock fontStyle(String style) {
        style("font-style", style);
        return this;
    }

    public StyleBlock fontFamily(String family) {
        style("font-family", family);
        return this;
    }

    public StyleBlock textAlign(String align) {
        style("text-align", align);
        return this;
    }

    public StyleBlock textDecoration(String decoration) {
        style("text-decoration", decoration);
        return this;
    }

    public StyleBlock textTransform(String transform) {
        style("text-transform", transform);
        return this;
    }

    public StyleBlock lineHeight(String height) {
        style("line-height", height);
        return this;
    }

    public StyleBlock letterSpacing(String spacing) {
        style("letter-spacing", spacing);
        return this;
    }

    // ========== BOX MODEL ==========
    public StyleBlock margin(String value) {
        style("margin", value);
        return this;
    }

    public StyleBlock marginPx(int px) {
        return margin(px + "px");
    }

    public StyleBlock marginTop(String value) {
        style("margin-top", value);
        return this;
    }

    public StyleBlock marginRight(String value) {
        style("margin-right", value);
        return this;
    }

    public StyleBlock marginBottom(String value) {
        style("margin-bottom", value);
        return this;
    }

    public StyleBlock marginLeft(String value) {
        style("margin-left", value);
        return this;
    }

    public StyleBlock padding(String value) {
        style("padding", value);
        return this;
    }

    public StyleBlock paddingPx(int px) {
        return padding(px + "px");
    }

    public StyleBlock paddingTop(String value) {
        style("padding-top", value);
        return this;
    }

    public StyleBlock paddingRight(String value) {
        style("padding-right", value);
        return this;
    }

    public StyleBlock paddingBottom(String value) {
        style("padding-bottom", value);
        return this;
    }

    public StyleBlock paddingLeft(String value) {
        style("padding-left", value);
        return this;
    }

    public StyleBlock width(String value) {
        style("width", value);
        return this;
    }

    public StyleBlock widthPx(int px) {
        return width(px + "px");
    }

    public StyleBlock widthPercent(int percent) {
        return width(percent + "%");
    }

    public StyleBlock height(String value) {
        style("height", value);
        return this;
    }

    public StyleBlock heightPx(int px) {
        return height(px + "px");
    }

    public StyleBlock heightPercent(int percent) {
        return height(percent + "%");
    }

    public StyleBlock maxWidth(String value) {
        style("max-width", value);
        return this;
    }

    public StyleBlock maxHeight(String value) {
        style("max-height", value);
        return this;
    }

    public StyleBlock boxSizing(String value) {
        style("box-sizing", value);
        return this;
    }

    // ========== BORDER ==========
    public StyleBlock border(String value) {
        style("border", value);
        return this;
    }

    public StyleBlock border(String width, String style, String color) {
        return border(width + " " + style + " " + color);
    }

    public StyleBlock borderTop(String value) {
        style("border-top", value);
        return this;
    }

    public StyleBlock borderRight(String value) {
        style("border-right", value);
        return this;
    }

    public StyleBlock borderBottom(String value) {
        style("border-bottom", value);
        return this;
    }

    public StyleBlock borderLeft(String value) {
        style("border-left", value);
        return this;
    }

    public StyleBlock borderRadius(String value) {
        style("border-radius", value);
        return this;
    }

    public StyleBlock borderRadiusPx(int px) {
        return borderRadius(px + "px");
    }

    // ========== LAYOUT & DISPLAY ==========
    public StyleBlock display(String value) {
        style("display", value);
        return this;
    }

    public StyleBlock position(String value) {
        style("position", value);
        return this;
    }

    public StyleBlock top(String value) {
        style("top", value);
        return this;
    }

    public StyleBlock right(String value) {
        style("right", value);
        return this;
    }

    public StyleBlock bottom(String value) {
        style("bottom", value);
        return this;
    }

    public StyleBlock left(String value) {
        style("left", value);
        return this;
    }

    public StyleBlock float_(String value) { // underscore to avoid keyword conflict
        style("float", value);
        return this;
    }

    public StyleBlock clear(String value) {
        style("clear", value);
        return this;
    }

    public StyleBlock overflow(String value) {
        style("overflow", value);
        return this;
    }

    public StyleBlock zIndex(int index) {
        style("z-index", String.valueOf(index));
        return this;
    }

    // ========== FLEXBOX & GRID ==========
    public StyleBlock flex(String value) {
        style("flex", value);
        return this;
    }

    public StyleBlock flexDirection(String value) {
        style("flex-direction", value);
        return this;
    }

    public StyleBlock justifyContent(String value) {
        style("justify-content", value);
        return this;
    }

    public StyleBlock alignItems(String value) {
        style("align-items", value);
        return this;
    }

    public StyleBlock gap(String value) {
        style("gap", value);
        return this;
    }

    public StyleBlock gapPx(int px) {
        return gap(px + "px");
    }

    // ========== VISUAL & EFFECTS ==========
    public StyleBlock opacity(double value) {
        style("opacity", String.valueOf(value));
        return this;
    }

    public StyleBlock visibility(String value) {
        style("visibility", value);
        return this;
    }

    public StyleBlock cursor(String value) {
        style("cursor", value);
        return this;
    }

    public StyleBlock boxShadow(String value) {
        style("box-shadow", value);
        return this;
    }

    public StyleBlock transition(String value) {
        style("transition", value);
        return this;
    }

    public StyleBlock transform(String value) {
        style("transform", value);
        return this;
    }

    // ========== GENERIC ==========

    public StyleBlock addPrefix(String prefix) {
        target = prefix + " " + target;
        target = target.trim();
        return this;
    }

    public StyleBlock addSuffix(String suffix) {
        target = target + suffix;
        target = target.trim();
        return this;
    }

    public StyleBlock propertyGroup(Map<String, String> properties) {
        styles.putAll(properties);
        return this;
    }

    // ========== CASCADE MERGE HELPERS ==========
    public static void mergeInto(Map<String, Map<String, String>> target, String key, Map<String, String> value) {
        target.merge(key, new HashMap<>(value), (old, incoming) -> {
            old.putAll(incoming);
            return old;
        });
    }

    public static void mergeAllInto(Map<String, Map<String, String>> target, Map<String, Map<String, String>> source) {
        for (Map.Entry<String, Map<String, String>> entry : source.entrySet()) {
            mergeInto(target, entry.getKey(), entry.getValue());
        }
    }

    public static void mergeBlock(Map<String, StyleBlock> target, String key, StyleBlock block) {
        StyleBlock existing = target.get(key);
        if (existing != null) {
            existing.styles.putAll(block.styles);
        } else {
            target.put(key, block);
        }
    }

    // ========== OUTPUT HELPERS ==========
    public String getCss() {
        StringBuilder sb = new StringBuilder();
        sb.append(target).append(" { ");
        for (Map.Entry<String, String> entry : styles.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("; ");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return getCss();
    }

    public StyleBlock minWidth(String s) {
        style("min-width",s);
        return this;
    }
    public StyleBlock minWidthPx(Integer s) {
        style("min-width",s+"px");
        return this;
    }
}