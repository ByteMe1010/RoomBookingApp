package com.codehack.nngf.Core.DataModel;

import com.codehack.nngf.Core.NWindow;

public class Themes {
    private NWindow window;
    public Themes(NWindow window){
        this.window = window;
    }
    public static Themes of(NWindow window){
        return new Themes(window);
    }
    public static void default_theme(NWindow window){
        of(window).default_theme();
    }
    public Themes base(String id){
        return external(id,"base/nngf-base.css");
    }
    public Themes base(){
        return base("nngf-base-css");
    }

    public Themes baseJs(String id){
        window.AddJsFile(id,"base/nngf-cardpane.js");
        return this;
    }
    public Themes baseJs(){
        return baseJs("nngf-cardpane-js");
    }

    public Themes essentials(){
        return base().baseJs();
    }
    public Themes default_theme(){
        return base().baseJs().base_theme();
    }

    public Themes base_theme(String id){
        return external(id,"base/base-theme.css");
    }
    public Themes base_theme(){
        return base_theme("nngf-base-theme-css");
    }

    public Themes external(String id,String url){
        window.AddCssFile(id,url);
        return this;
    }

    public Themes metal_polish(String id){
        return external(id,"base/metal-polish-theme.css");
    }
    public Themes metal_polish(){
        return metal_polish("nngf-metal-polish-theme");
    }

    public Themes cyber(String id){
        return external(id,"base/cyber-theme.css");
    }
    public Themes cyber(){
        return cyber("nngf-cyber-theme");
    }

    public Themes flat(String id){
        return external(id,"base/nngf-flat-theme.css");
    }
    public Themes flat(){
        return flat("nngf-flat-theme");
    }
}
