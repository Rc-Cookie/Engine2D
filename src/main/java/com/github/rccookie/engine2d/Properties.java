package com.github.rccookie.engine2d;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class Properties {

    private final Map<String,Object> data = new HashMap<>();

    public boolean is(String property) {
        return data.containsKey(property);
    }

    public <T> T get(String property, @NotNull Class<T> type) {
        return type.cast(data.get(property));
    }

    public String get(String property) {
        return get(property, String.class);
    }

    public void set(String property, Object value) {
        data.put(property, value);
    }

    public void set(String property) {
        set(property, null);
    }

    public void remove(String property) {
        data.remove(property);
    }
}
