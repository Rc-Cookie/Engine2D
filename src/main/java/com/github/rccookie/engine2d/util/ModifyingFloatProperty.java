package com.github.rccookie.engine2d.util;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.NotNull;

public class ModifyingFloatProperty extends FloatProperty {

    public ModifyingFloatProperty(UIObject obj) {
        this(obj, 0);
    }

    public ModifyingFloatProperty(UIObject obj, float value) {
        this(obj, value, f->f);
    }

    public ModifyingFloatProperty(UIObject obj, float value, @NotNull FloatConsumer validator) {
        super(value, validator);
        onChange.add(obj::modified);
    }

    public ModifyingFloatProperty(UIObject obj, float value, @NotNull FloatUnaryOperator validator) {
        super(value, validator);
        onChange.add(obj::modified);
    }
}
