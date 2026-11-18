package com.codehack.nngf.Core.DataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DataMap {
    private final Map<String, Object> data;

    public DataMap(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        try {
            return Optional.ofNullable((T) data.get(key));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public Optional<String> getString(String key) {
        Object val = data.get(key);
        if (val == null) return Optional.empty();
        return Optional.of(val.toString());
    }
    public String GetString(String key){
        return getString(key).orElse(null);
    }

    public Optional<Integer> getInt(String key) {
        Object val = data.get(key);
        if (val instanceof Number) return Optional.of(((Number) val).intValue());
        if (val instanceof String) {
            try {
                return Optional.of(Integer.parseInt((String) val));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    public int GetInt(String key){
        return getInt(key).orElse(null);
    }

    public Optional<Boolean> getBool(String key) {
        Object val = data.get(key);
        if (val instanceof Boolean) return Optional.of((Boolean) val);
        if (val instanceof String) return Optional.of(Boolean.parseBoolean((String) val));
        return Optional.empty();
    }

    public boolean has(String key) {
        return data.containsKey(key);
    }

    public Set<String> keys() {
        return data.keySet();
    }

    public Map<String, Object> toMap() {
        return new HashMap<>(data);
    }
}
