package com.github.rccookie.engine2d.util;

import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.NotNull;

public class ModifyingIntProperty extends IntProperty {

    public ModifyingIntProperty(UIObject obj) {
        this(obj, 0);
    }

    public ModifyingIntProperty(UIObject obj, int value) {
        this(obj, value, i->i);
    }

    public ModifyingIntProperty(UIObject obj, int value, @NotNull IntConsumer validator) {
        super(value, validator);
        onChange.add(obj::modified);
    }

    public ModifyingIntProperty(UIObject obj, int value, @NotNull IntUnaryOperator validator) {
        super(value, validator);
        onChange.add(obj::modified);
    }
}
