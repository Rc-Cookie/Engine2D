package com.github.rccookie.engine2d.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class VirtualProperty<T> extends SimpleProperty<T> {

    @NotNull
    private UnaryOperator<T> getter = UnaryOperator.identity();
    @NotNull
    private BiConsumer<Consumer<T>, T> setter = Consumer::accept;

    public VirtualProperty() {
    }

    public VirtualProperty(T value) {
        super(value);
    }

    public VirtualProperty(T value, boolean nullable) {
        super(value, nullable);
    }

    public VirtualProperty(T value, @NotNull Consumer<T> validator) {
        super(value, validator);
    }

    public VirtualProperty(T value, @NotNull UnaryOperator<T> validator) {
        super(value, validator);
    }

    @Override
    public T get() {
        return getter.apply(super.get());
    }

    @Override
    public void set(T value) {
        setter.accept(super::set, value);
    }

    public void setGetter(@NotNull UnaryOperator<T> getter) {
        this.getter = Arguments.checkNull(getter, "getter");
    }

    public void setSetter(@NotNull BiConsumer<Consumer<T>, T> setter) {
        this.setter = Arguments.checkNull(setter, "setter");
    }
}
