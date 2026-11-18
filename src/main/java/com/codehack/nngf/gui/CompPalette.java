package com.codehack.nngf.gui;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.Core.NWindow;

public class CompPalette extends NComponet {
    private final String title;

    public CompPalette(String id, String title) {
        super(id, "CompPalette");
        this.title = title;
        initStyles();
        initEvents();
    }

    public CompPalette(String id, String title, NWindow window) {
        super(id, "CompPalette", window);
        this.title = title;
        initStyles();
        initEvents();
    }

    private void initStyles() {
        styles.addTemplateStyle(StyleBlock.of()
                .style("display", "flex")
                .style("flex-direction", "column")
                .position("relative")
        );
        styles.addTemplateStyle(StyleBlock.of(".cpc-header")
                .style("padding", "8px 12px")
                .style("cursor", "pointer")
                .style("font-weight", "600")
                .style("font-size", "11px")
                .style("text-transform", "uppercase")
                .style("letter-spacing", "0.5px")
                .style("color", "var(--text-secondary, #666)")
        );
        styles.addTemplateStyle(StyleBlock.of(".cpc-header:hover")
                .style("background-color", "var(--hover-bg, rgba(0,0,0,0.05))")
        );
        styles.addTemplateStyle(StyleBlock.of(".cpc-header::before")
                .style("content", "'▼ '")
                .style("font-size", "10px")
                .style("margin-right", "6px")
        );
        styles.addTemplateStyle(StyleBlock.of(".cpc-header.cpc-collapsed::before")
                .style("content", "'▶ '")
        );
        styles.addTemplateStyle(StyleBlock.of(".cpc-content.cpc-collapsed")
                .style("display", "none")
        );
        styles.addTemplateStyle(StyleBlock.of(".comp-item")
                .style("padding", "6px 12px 6px 20px")
                .style("cursor", "pointer")
                .style("color", "var(--text-primary, #333)")
                .style("transition", "background-color 0.15s")
                .style("border-radius", "var(--border-radius, 4px)")
                .style("margin", "1px 4px")
        );
        styles.addTemplateStyle(StyleBlock.of(".comp-item:hover")
                .style("background-color", "var(--hover-bg, rgba(0,0,0,0.05))")
        );
        styles.addTemplateStyle(StyleBlock.of(".comp-item.selected")
                .style("background-color", "var(--accent-bg, rgba(0,120,212,0.1))")
                .style("color", "var(--accent-color, #0078d4)")
                .style("font-weight", "500")
        );
    }

    private void initEvents() {
        events.addJsEvent("click", """
                const header = e.target.closest('.cpc-header');
                if (header) {
                    header.classList.toggle('cpc-collapsed');
                    const content = header.nextElementSibling;
                    if (content) content.classList.toggle('cpc-collapsed');
                }
                """);
    }

    @Override
    public String buildHtml(String childHtml) {
        String h = title != null ? "<div class=\"cpc-header\">" + title + "</div>" : "";
        return "<div>" + h + "<div class=\"cpc-content\">" + childHtml + "</div></div>";
    }
}
