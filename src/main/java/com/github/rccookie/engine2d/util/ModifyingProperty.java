package com.github.rccookie.engine2d.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.NotNull;

public class ModifyingProperty<T> extends SimpleProperty<T> {

    public ModifyingProperty(UIObject obj) {
        this(obj, null);
    }

    public ModifyingProperty(UIObject obj, T value) {
        this(obj, value, true);
    }

    public ModifyingProperty(UIObject obj, T value, boolean nullable) {
        super(value, nullable);
        if(obj != null)
            onChange.add(obj::modified);
    }

    public ModifyingProperty(UIObject obj, T value, @NotNull Consumer<T> validator) {
        super(value, validator);
        if(obj != null)
            onChange.add(obj::modified);
    }

    public ModifyingProperty(UIObject obj, T value, @NotNull UnaryOperator<T> validator) {
        super(value, validator);
        if(obj != null)
            onChange.add(obj::modified);
    }
}
