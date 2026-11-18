package com.codehack.nngf.Core.builder;

import java.util.AbstractMap;
import java.util.Map;

public class Query {
    final String key;
    final String jsExpression;

    Query(String key, String jsExpression) {
        this.key = key;
        this.jsExpression = jsExpression;
    }

    public Map.Entry<String, String> get() {
        return new AbstractMap.SimpleEntry<>(key, jsExpression);
    }
}
