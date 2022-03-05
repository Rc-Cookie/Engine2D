package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.Nullable;

/**
 * A text that always shows a key-value pair, seperated with
 * {@code ": "}.
 */
public class KeyValueText extends Text {

    /**
     * The key.
     */
    private String key;
    /**
     * The value.
     */
    private Object value;

    /**
     * Creates a new key-value text.
     *
     * @param parent The parent for the text
     * @param key The key
     * @param value The value. Will be converted to a string using
     *              {@link Object#toString()} when needed
     */
    public KeyValueText(UIObject parent, String key, Object value) {
        super(parent, key + ": " + value);
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of the key-value text.
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the value of the key-value text.
     *
     * @return The value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Equivalent to {@link #setKey(String)}.
     *
     * @param text The key to set
     */
    @Override
    public void setText(@Nullable String text) {
        setKey(text);
    }

    /**
     * Sets the key to the specified string.
     *
     * @param key The key to use
     */
    public void setKey(String key) {
        this.key = key;
        super.setText(key + ": " + value);
    }

    /**
     * Sets the value to the specified object.
     *
     * @param value The value to use
     */
    public void setValue(Object value) {
        this.value = value;
        super.setText(key + ": " + value);
    }
}
