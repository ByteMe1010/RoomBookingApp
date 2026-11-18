package com.codehack.nngf.Core.styleQuery;

@FunctionalInterface
public interface Extractor {
    void extract(String cssName, DataPacket packet);
}
