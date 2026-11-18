package com.codehack.nngf.Core.builder;

@FunctionalInterface
public interface NEventDef {
    void handleEvent(Object evt);
}
