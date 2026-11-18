module com.codehack.nngf {
    requires javafx.controls;
    requires javafx.web;
    requires jdk.jsobject;
    requires unbescape;
    requires jdk.httpserver;
    requires com.fasterxml.jackson.databind;
    requires org.java_websocket;
    requires com.fasterxml.jackson.core;
    requires java.sql;
    requires java.net.http;
    requires com.google.api.services.gmail;
    requires google.api.client;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.json.gson;
    requires java.desktop;

    exports org.example.gui;

    opens com.codehack.nngf.Core.bridge to javafx.web;
    opens com.codehack.nngf.Core.builder to javafx.web;
    opens com.codehack.nngf.Display.FX to javafx.web;
    opens com.codehack.nngf.Display.Web to javafx.web;

    opens org.example.Core to java.sql;

    exports com.codehack.nngf.Core;
    opens com.codehack.nngf.Core to javafx.web;
    opens com.codehack.nngf.gui to javafx.web;
}
