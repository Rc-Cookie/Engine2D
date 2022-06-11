package com.github.rccookie.engine2d.util;

import java.util.function.BiConsumer;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class VirtualBoolProperty extends BoolProperty {

    @NotNull
    private UnaryBoolOperator getter = UnaryBoolOperator.IDENTITY;
    @NotNull
    private BiConsumer<BoolConsumer, Boolean> setter = BoolConsumer::accept;

    public VirtualBoolProperty() {
    }

    public VirtualBoolProperty(boolean value) {
        super(value);
    }

    public VirtualBoolProperty(boolean value, @NotNull BoolConsumer validator) {
        super(value, validator);
    }

    @Override
    public boolean get() {
        return getter.apply(super.get());
    }

    @Override
    public void set(boolean value) {
        setter.accept(super::set, value);
    }

    public void setGetter(@NotNull UnaryBoolOperator getter) {
        this.getter = Arguments.checkNull(getter, "getter");
    }

    public void setSetter(@NotNull BiConsumer<BoolConsumer, Boolean> setter) {
        this.setter = Arguments.checkNull(setter, "setter");
    }
}
