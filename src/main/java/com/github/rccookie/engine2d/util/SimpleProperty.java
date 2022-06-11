package com.github.rccookie.engine2d.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class SimpleProperty<T> extends Property<T> {

    protected T value;
    @NotNull
    protected UnaryOperator<T> validator;


    public SimpleProperty() {
        this(null);
    }

    public SimpleProperty(T value) {
        this(value, true);
    }

    public SimpleProperty(T value, boolean nullable) {
        this(nullable ? value : Arguments.checkNull(value, "value"),
                nullable ? UnaryOperator.identity() : (UnaryOperator<T>) Arguments::checkNull);
    }

    public SimpleProperty(T value, @NotNull Consumer<T> validator) {
        this(value, createValidator(validator));
    }

    public SimpleProperty(T value, @NotNull UnaryOperator<T> validator) {
        this.value = value;
        this.validator = Arguments.checkNull(validator, "validator");
    }


    @Override
    public String toString() {
        return Objects.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof SimpleProperty)) return false;
        SimpleProperty<?> property = (SimpleProperty<?>) o;
        return Objects.equals(value, property.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }



    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        if(Objects.equals(this.value, value)) return;

        T old = this.value;
        this.value = validator.apply(value);

        onChange.invoke(this.value, old);
    }



    @Override
    public void addValidator(Consumer<T> validator) {
        Arguments.checkNull(validator, "validator");
        UnaryOperator<T> old = this.validator;
        this.validator = t -> {
            T x = old.apply(t);
            validator.accept(x);
            return x;
        };
    }



    @NotNull
    private static <T> UnaryOperator<T> createValidator(@NotNull Consumer<T> validator) {
        Arguments.checkNull(validator, "validator");
        return t -> {
            validator.accept(t);
            return t;
        };
    }


}
