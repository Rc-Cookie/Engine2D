package com.github.rccookie.engine2d.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.util.Cloneable;

import org.jetbrains.annotations.NotNull;

public class CloningProperty<T extends Cloneable<T>> extends ModifyingProperty<T> {

    public CloningProperty(UIObject obj) {
        super(obj);
    }

    public CloningProperty(UIObject obj, T value) {
        super(obj, value);
    }

    public CloningProperty(UIObject obj, T value, boolean nullable) {
        super(obj, value, nullable);
    }

    public CloningProperty(UIObject obj, T value, @NotNull Consumer<T> validator) {
        super(obj, value, validator);
    }

    public CloningProperty(UIObject obj, T value, @NotNull UnaryOperator<T> validator) {
        super(obj, value, validator);
    }

    @Override
    public T get() {
        T value = super.get();
        return value != null ? value.clone() : null;
    }

    @Override
    public void set(T value) {
        if(value == null)
            super.set(null);
        else super.set(value.clone());
    }
}
