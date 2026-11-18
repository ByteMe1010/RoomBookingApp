package com.codehack.nngf.Core.RuntimeStyleSystem;

import com.codehack.nngf.Core.DataModel.StyleBlock;
import com.codehack.nngf.Core.NWindow;
import com.codehack.nngf.Core.builder.ElementQuery;
import com.codehack.nngf.Core.exceptions.LifecycleViolationException;
import com.codehack.nngf.js.JS;

import java.util.Map;

public class RuntimeStyle {
    private NWindow window;

    public RuntimeStyle(NWindow window) {
        if (window==null) throw new NullPointerException("Cannot Create Dynamic Styles for Null Window");
        this.window = window;
        registerStyle();
    }
    private void registerStyle(){
        if (window.isAttached()){
            window.executeJS("""
                    (function() {
                      'use strict';
                    // Guard against double initialization
                      if (window.ruleManager) {
                        console.warn('ruleManager already exists – skipping re-init');
                        return;
                      }
                      // ---------- internal state ----------
                      const ruleCache = new Map();          // selectorText -> [CSSStyleRule, ...]
                      let cacheBuilt = false;
                      let dynamicSheet = null;
                    
                      // ---------- helpers ----------
                      function getDynamicSheet() {
                        if (dynamicSheet) return dynamicSheet;
                        let style = document.getElementById('rule-manager-dynamic');
                        if (!style) {
                          style = document.createElement('style');
                          style.id = 'rule-manager-dynamic';
                          document.head.appendChild(style);
                        }
                        dynamicSheet = style.sheet;
                        return dynamicSheet;
                      }
                    
                      // rebuild the cache from all accessible stylesheets
                      function rebuildCache() {
                        ruleCache.clear();
                        for (const sheet of document.styleSheets) {
                          try {
                            const rules = sheet.cssRules || sheet.rules;
                            for (const rule of rules) {
                              if (rule.selectorText) {
                                if (!ruleCache.has(rule.selectorText)) {
                                  ruleCache.set(rule.selectorText, []);
                                }
                                ruleCache.get(rule.selectorText).push(rule);
                              }
                            }
                          } catch (e) {
                            // cross‑origin stylesheet – skip silently
                          }
                        }
                        cacheBuilt = true;
                      }
                    
                      function ensureCache() {
                        if (!cacheBuilt) rebuildCache();
                      }
                    
                      // search live stylesheets for a selector (updates cache if found)
                      function findAndCacheSelector(selector) {
                        for (const sheet of document.styleSheets) {
                          try {
                            const rules = sheet.cssRules || sheet.rules;
                            for (const rule of rules) {
                              if (rule.selectorText === selector) {
                                // add to cache
                                if (!ruleCache.has(selector)) {
                                  ruleCache.set(selector, []);
                                }
                                ruleCache.get(selector).push(rule);
                                return rule;
                              }
                            }
                          } catch (e) { /* skip */ }
                        }
                        return null;
                      }
                    
                      // ---------- public API ----------
                      window.ruleManager = {
                    
                        // force a full cache rebuild (useful after dynamic stylesheet changes)
                        rebuild: function() {
                          rebuildCache();
                        },
                    
                        // update a CSS property on all rules matching `selector`
                        updateRule: function(selector, prop, value) {
                          ensureCache();
                    
                          let rules = ruleCache.get(selector);
                          if (rules && rules.length) {
                            for (const rule of rules) {
                              rule.style.setProperty(prop, value);
                            }
                            return;
                          }
                    
                          // not in cache – search live stylesheets (maybe a new sheet was added)
                          const found = findAndCacheSelector(selector);
                          if (found) {
                            found.style.setProperty(prop, value);
                            // cache now contains it, but we already updated it
                            return;
                          }
                    
                          // completely new rule – insert into dynamic sheet
                          const sheet = getDynamicSheet();
                          const cssText = `${selector} { ${prop}: ${value}; }`;
                          try {
                            sheet.insertRule(cssText, sheet.cssRules.length);
                            // rebuild cache to include the new rule
                            rebuildCache();
                          } catch (e) {
                            console.warn('ruleManager: failed to insert rule', e);
                          }
                        },
                    
                        // remove a CSS property from all rules matching `selector`
                        removeProperty: function(selector, prop) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (rules) {
                            for (const rule of rules) {
                              rule.style.removeProperty(prop);
                            }
                          }
                        },
                    
                        // delete all rule blocks matching `selector`
                        removeBlock: function(selector) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (!rules) return;
                    
                          // delete from their stylesheets
                          for (const rule of rules) {
                            const sheet = rule.parentStyleSheet;
                            if (!sheet) continue;
                            const index = Array.from(sheet.cssRules).indexOf(rule);
                            if (index !== -1) {
                              try {
                                sheet.deleteRule(index);
                              } catch (e) {
                                // ignore (might be a cross‑origin sheet we can't modify)
                              }
                            }
                          }
                          // rebuild cache (the rules are gone)
                          rebuildCache();
                        },
                    
                        // get the current value of a property from the FIRST matching rule
                        getPropertyValue: function(selector, prop) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (rules && rules.length) {
                            return rules[0].style.getPropertyValue(prop);
                          }
                          return null;
                        },
                            insertCSS: function(cssString) {
                              if (typeof cssString !== 'string' || !cssString.trim()) return;
                              const sheet = getDynamicSheet();
                              const blocks = cssString.split('}').filter(b => b.trim().length > 0);
                              for (const block of blocks) {
                                const ruleText = block.trim() + '}';
                                try {
                                  sheet.insertRule(ruleText, sheet.cssRules.length);
                                } catch (e) {
                                  console.warn('ruleManager: failed to insert rule:', ruleText, e);
                                }
                              }
                              rebuildCache();
                            },
                    
                        // inspect the whole cache (for debugging)
                        getCache: function() {
                          ensureCache();
                          return ruleCache;
                        }
                      };
                    
                      // ---------- initial build ----------
                      rebuildCache();
                    })();
                    """);
        }
        else{
            window.bridgeReady.thenRun(() -> {
                window.executeJS("""
                    (function() {
                      'use strict';
                    // Guard against double initialization
                      if (window.ruleManager) {
                        console.warn('ruleManager already exists – skipping re-init');
                        return;
                      }
                      // ---------- internal state ----------
                      const ruleCache = new Map();          // selectorText -> [CSSStyleRule, ...]
                      let cacheBuilt = false;
                      let dynamicSheet = null;
                    
                      // ---------- helpers ----------
                      function getDynamicSheet() {
                        if (dynamicSheet) return dynamicSheet;
                        let style = document.getElementById('rule-manager-dynamic');
                        if (!style) {
                          style = document.createElement('style');
                          style.id = 'rule-manager-dynamic';
                          document.head.appendChild(style);
                        }
                        dynamicSheet = style.sheet;
                        return dynamicSheet;
                      }
                    
                      // rebuild the cache from all accessible stylesheets
                      function rebuildCache() {
                        ruleCache.clear();
                        for (const sheet of document.styleSheets) {
                          try {
                            const rules = sheet.cssRules || sheet.rules;
                            for (const rule of rules) {
                              if (rule.selectorText) {
                                if (!ruleCache.has(rule.selectorText)) {
                                  ruleCache.set(rule.selectorText, []);
                                }
                                ruleCache.get(rule.selectorText).push(rule);
                              }
                            }
                          } catch (e) {
                            // cross‑origin stylesheet – skip silently
                          }
                        }
                        cacheBuilt = true;
                      }
                    
                      function ensureCache() {
                        if (!cacheBuilt) rebuildCache();
                      }
                    
                      // search live stylesheets for a selector (updates cache if found)
                      function findAndCacheSelector(selector) {
                        for (const sheet of document.styleSheets) {
                          try {
                            const rules = sheet.cssRules || sheet.rules;
                            for (const rule of rules) {
                              if (rule.selectorText === selector) {
                                // add to cache
                                if (!ruleCache.has(selector)) {
                                  ruleCache.set(selector, []);
                                }
                                ruleCache.get(selector).push(rule);
                                return rule;
                              }
                            }
                          } catch (e) { /* skip */ }
                        }
                        return null;
                      }
                    
                      // ---------- public API ----------
                      window.ruleManager = {
                    
                        // force a full cache rebuild (useful after dynamic stylesheet changes)
                        rebuild: function() {
                          rebuildCache();
                        },
                    
                        // update a CSS property on all rules matching `selector`
                        updateRule: function(selector, prop, value) {
                          ensureCache();
                    
                          let rules = ruleCache.get(selector);
                          if (rules && rules.length) {
                            for (const rule of rules) {
                              rule.style.setProperty(prop, value);
                            }
                            return;
                          }
                    
                          // not in cache – search live stylesheets (maybe a new sheet was added)
                          const found = findAndCacheSelector(selector);
                          if (found) {
                            found.style.setProperty(prop, value);
                            // cache now contains it, but we already updated it
                            return;
                          }
                    
                          // completely new rule – insert into dynamic sheet
                          const sheet = getDynamicSheet();
                          const cssText = `${selector} { ${prop}: ${value}; }`;
                          try {
                            sheet.insertRule(cssText, sheet.cssRules.length);
                            // rebuild cache to include the new rule
                            rebuildCache();
                          } catch (e) {
                            console.warn('ruleManager: failed to insert rule', e);
                          }
                        },
                    
                        // remove a CSS property from all rules matching `selector`
                        removeProperty: function(selector, prop) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (rules) {
                            for (const rule of rules) {
                              rule.style.removeProperty(prop);
                            }
                          }
                        },
                    
                        // delete all rule blocks matching `selector`
                        removeBlock: function(selector) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (!rules) return;
                    
                          // delete from their stylesheets
                          for (const rule of rules) {
                            const sheet = rule.parentStyleSheet;
                            if (!sheet) continue;
                            const index = Array.from(sheet.cssRules).indexOf(rule);
                            if (index !== -1) {
                              try {
                                sheet.deleteRule(index);
                              } catch (e) {
                                // ignore (might be a cross‑origin sheet we can't modify)
                              }
                            }
                          }
                          // rebuild cache (the rules are gone)
                          rebuildCache();
                        },
                    
                        // get the current value of a property from the FIRST matching rule
                        getPropertyValue: function(selector, prop) {
                          ensureCache();
                          const rules = ruleCache.get(selector);
                          if (rules && rules.length) {
                            return rules[0].style.getPropertyValue(prop);
                          }
                          return null;
                        },
                            insertCSS: function(cssString) {
                              if (typeof cssString !== 'string' || !cssString.trim()) return;
                              const sheet = getDynamicSheet();
                              const blocks = cssString.split('}').filter(b => b.trim().length > 0);
                              for (const block of blocks) {
                                const ruleText = block.trim() + '}';
                                try {
                                  sheet.insertRule(ruleText, sheet.cssRules.length);
                                } catch (e) {
                                  console.warn('ruleManager: failed to insert rule:', ruleText, e);
                                }
                              }
                              rebuildCache();
                            },
                    
                        // inspect the whole cache (for debugging)
                        getCache: function() {
                          ensureCache();
                          return ruleCache;
                        }
                      };
                    
                      // ---------- initial build ----------
                      rebuildCache();
                    })();
                    """);
            });
        }
    }
    public void setProperty(String rule,String style,String styleValue){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");
        //ruleManager.updateRule('.my-button:hover', 'color', 'red');
        window.executeJS(JS.Prop("ruleManager").method("updateRule",rule,style,styleValue).E().toString());
    }
    public void removeProperty(String rule,String style){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");
        //ruleManager.removeProperty('.my-button:hover', 'color');
        window.executeJS(JS.Prop("ruleManager").method("removeProperty",rule,style).E().toString());
    }
    public void removeRule(String rule){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");
        //ruleManager.removeProperty('.my-button:hover', 'color');
        window.executeJS(JS.Prop("ruleManager").method("removeBlock",rule).E().toString());
    }
    public Object readProperty(String rule,String style){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");
        //ruleManager.getPropertyValue('.my-button:hover', 'background-color');
        return window.executeJS(JS.Prop("ruleManager").method("getPropertyValue",rule,style).E().toString());
    }
    public void forceRebuildCache(){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");

        window.executeJS("ruleManager.rebuild();");
    }
    public String getCache(){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");

        return window.executeJS("JSON.stringify(ruleManager.getCache());").toString();
    }
    public void fromStyle(StyleBlock block){
        String blockRule = block.target;
        for (Map.Entry<String,String> dta:block.styles.entrySet()){
            setProperty(blockRule,dta.getKey(),dta.getValue());
        }
    }
    public void insertBlob(String css){
        if (!window.isAttached())
            throw new LifecycleViolationException("Dom isn't alive yet. use StyleBlock system!");

        window.executeJS(JS.Prop("ruleManager").method("insertCSS",css).E().toString());
    }

    public void fromStyle(String target,StyleBlock block){
        for (Map.Entry<String,String> dta:block.styles.entrySet()){
            setProperty(target,dta.getKey(),dta.getValue());
        }
    }
}
