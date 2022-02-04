package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;

public class KeyValueText extends Text {

    private String key;
    private Object value;

    public KeyValueText(UIObject parent, String key, Object value) {
        super(parent, key + ": " + value);
        this.key = key;
        this.value = value;
    }

    @Override
    public String getText() {
        return getKey();
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void setText(String text) {
        setKey(text);
    }

    public void setKey(String key) {
        this.key = key;
        super.setText(key + ": " + value);
    }

    public void setValue(Object value) {
        this.value = value;
        super.setText(key + ": " + value);
    }
}
