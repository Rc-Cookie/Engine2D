package com.github.rccookie.engine2d.util;

import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IntProperty {

    int value;
    @NotNull
    IntUnaryOperator validator;
    @NotNull
    public final BiParamEvent<Integer,Integer> onChange = new CaughtBiParamEvent<>();


    public IntProperty() {
        this(0);
    }

    public IntProperty(int value) {
        this(value, IntUnaryOperator.identity());
    }

    public IntProperty(int value, @NotNull IntConsumer validator) {
        this(value, createValidator(validator));
    }

    public IntProperty(int value, @NotNull IntUnaryOperator validator) {
        this.value = value;
        this.validator = Arguments.checkNull(validator, "validator");
    }


    @Override
    public String toString() {
        return value + "";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof IntProperty)) return false;
        IntProperty that = (IntProperty) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }



    public int get() {
        return value;
    }

    public void set(int value) {
        if(this.value == value) return;

        int old = this.value;
        this.value = validator.applyAsInt(value);

        onChange.invoke(this.value, old);
    }



    public void addValidator(IntConsumer validator) {
        Arguments.checkNull(validator, "validator");
        IntUnaryOperator old = this.validator;
        this.validator = t -> {
            int x = old.applyAsInt(t);
            validator.accept(x);
            return x;
        };
    }



    @NotNull
    private static IntUnaryOperator createValidator(@NotNull IntConsumer validator) {
        Arguments.checkNull(validator, "validator");
        return t -> {
            validator.accept(t);
            return t;
        };
    }

    @NotNull
    @Contract("->new")
    public static IntProperty positive() {
        return positive(0);
    }

    @NotNull
    @Contract("_->new")
    public static IntProperty positive(int value) {
        return new IntProperty(value, (IntUnaryOperator) x -> Arguments.checkRange(x, 1, null));
    }

    @NotNull
    @Contract("->new")
    public static IntProperty negative() {
        return negative(0);
    }

    @NotNull
    @Contract("_->new")
    public static IntProperty negative(int value) {
        return new IntProperty(value, (IntUnaryOperator) x -> Arguments.checkRange(x, null, 0));
    }

    @NotNull
    @Contract("->new")
    public static IntProperty nonNegative() {
        return nonNegative(0);
    }

    @NotNull
    @Contract("_->new")
    public static IntProperty nonNegative(int value) {
        return new IntProperty(value, (IntUnaryOperator) x -> Arguments.checkRange(x, 0, null));
    }

    @NotNull
    @Contract("->new")
    public static IntProperty nonPositive() {
        return nonPositive(0);
    }

    @NotNull
    @Contract("_->new")
    public static IntProperty nonPositive(int value) {
        return new IntProperty(value, (IntUnaryOperator) x -> Arguments.checkRange(x, null, 1));
    }
}
