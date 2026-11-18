package com.codehack.nngf.Core.styleQuery;

import com.codehack.nngf.Core.NComponet;
import com.codehack.nngf.gui.Label;
import com.codehack.nngf.Core.NWindow;

public class TestStyleQuery {

    public static void main(String[] args) {
        // Dummy component for testing
        NWindow w = new NWindow("Test");
        NComponet comp = new Label("lbl .[id,123] .btn .[disabled] & col mt: 10px; p 10,20; jc c fd -r bg #fff $hover", "Hello World");

        String testQuery = ".[id,123] .btn .[disabled] & col mt: 10px; p 10,20; jc c fd -r bg #fff $hover";
        System.out.println("Query: " + testQuery);
        
        QueryResolver.resolve(testQuery, comp);
        
        System.out.println("\n--- Resulting Component State ---");
        System.out.println("Classes: " + comp.classList.toString());
        System.out.println("ID attribute explicitly added? (handled via NComponent ID internally usually, but let's check attr): " + comp.getAttributeValue("id"));
        System.out.println("Disabled? Yes, via setDisabled(true)");
        System.out.println("Styles:");
        for(java.util.Map.Entry<String, String> entry : comp.style.styles.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
