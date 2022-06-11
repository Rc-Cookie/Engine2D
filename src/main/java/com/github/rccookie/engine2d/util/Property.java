package com.github.rccookie.engine2d.util;

import java.util.function.Consumer;

import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public abstract class Property<T> {

    @NotNull
    public final BiParamEvent<T,T> onChange;

    protected Property() {
        this(new CaughtBiParamEvent<>());
    }

    protected Property(@NotNull BiParamEvent<T, T> onChange) {
        this.onChange = Arguments.checkNull(onChange, "onChange");
    }

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    public abstract int hashCode();

    public abstract T get();

    public abstract void set(T value);

    public abstract void addValidator(Consumer<T> validator);



    public static void makeUnsupported(Property<?> property) {
        property.addValidator($ -> { throw new UnsupportedOperationException(); });
    }

    public static void makeUnsupported(BoolProperty property) {
        property.addValidator($ -> { throw new UnsupportedOperationException(); });
    }

    public static void makeUnsupported(IntProperty property) {
        property.addValidator($ -> { throw new UnsupportedOperationException(); });
    }

    public static void makeUnsupported(FloatProperty property) {
        property.addValidator($ -> { throw new UnsupportedOperationException(); });
    }



    public static class Wrapper<T> extends Property<T> {

        @NotNull
        private final Property<T> property;

        public Wrapper(@NotNull Property<T> property) {
            super(Arguments.checkNull(property, "property").onChange);
            this.property = property;
        }

        @Override
        public String toString() {
            return property.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return property.equals(obj);
        }

        @Override
        public int hashCode() {
            return property.hashCode();
        }

        @Override
        public T get() {
            return property.get();
        }

        @Override
        public void set(T value) {
            property.set(value);
        }

        @Override
        public void addValidator(Consumer<T> validator) {
            property.addValidator(validator);
        }
    }
}
