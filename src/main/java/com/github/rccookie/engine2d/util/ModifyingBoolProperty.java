package com.github.rccookie.engine2d.util;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.NotNull;

public class ModifyingBoolProperty extends BoolProperty {

    public ModifyingBoolProperty(UIObject obj) {
        this(obj, false);
    }

    public ModifyingBoolProperty(UIObject obj, boolean value) {
        this(obj, false, b -> {});
    }

    public ModifyingBoolProperty(UIObject obj, boolean value, @NotNull BoolConsumer validator) {
        super(value, validator);
        onChange.add(obj::modified);
    }
}
