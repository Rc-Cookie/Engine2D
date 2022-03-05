package com.github.rccookie.engine2d.util;

import org.jetbrains.annotations.NotNull;

/**
 * Mutable wrapper class for an int value.
 */
@NotNull
public class IntWrapper {

    /**
     * The value.
     */
    public int value;

    /**
     * Creates a new int wrapped with the given value.
     *
     * @param value The initial value for the int wrapper
     */
    public IntWrapper(int value) {
        this.value = value;
    }

    /**
     * Creates a new int wrapped with the default int value (0).
     */
    public IntWrapper() {
        this(0);
    }
}
