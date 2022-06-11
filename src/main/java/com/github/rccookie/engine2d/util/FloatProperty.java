package com.github.rccookie.engine2d.util;

import java.util.Objects;

import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class FloatProperty {

    float value;
    @NotNull
    FloatUnaryOperator validator;
    @NotNull
    public final BiParamEvent<Float,Float> onChange = new CaughtBiParamEvent<>();


    public FloatProperty() {
        this(0);
    }

    public FloatProperty(float value) {
        this(value, FloatUnaryOperator.IDENTITY);
    }

    public FloatProperty(float value, @NotNull FloatConsumer validator) {
        this(value, createValidator(validator));
    }

    public FloatProperty(float value, @NotNull FloatUnaryOperator validator) {
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
        if(!(o instanceof FloatProperty)) return false;
        FloatProperty that = (FloatProperty) o;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }



    public float get() {
        return value;
    }

    public void set(float value) {
        if(this.value == value) return;

        float old = this.value;
        this.value = validator.apply(value);

        onChange.invoke(this.value, old);
    }



    public void addValidator(FloatConsumer validator) {
        Arguments.checkNull(validator, "validator");
        FloatUnaryOperator old = this.validator;
        this.validator = t -> {
            float x = old.apply(t);
            validator.accept(x);
            return x;
        };
    }



    @NotNull
    private static FloatUnaryOperator createValidator(@NotNull FloatConsumer validator) {
        Arguments.checkNull(validator, "validator");
        return t -> {
            validator.accept(t);
            return t;
        };
    }

    @NotNull
    @Contract("->new")
    public static FloatProperty positive() {
        return positive(0);
    }

    @NotNull
    @Contract("_->new")
    public static FloatProperty positive(float value) {
        return new FloatProperty(value, (FloatUnaryOperator) x -> Arguments.checkExclusive(x, 0f, null));
    }

    @NotNull
    @Contract("->new")
    public static FloatProperty negative() {
        return negative(0);
    }

    @NotNull
    @Contract("_->new")
    public static FloatProperty negative(float value) {
        return new FloatProperty(value, (FloatUnaryOperator) x -> Arguments.checkRange(x, null, 0f));
    }

    @NotNull
    @Contract("->new")
    public static FloatProperty nonNegative() {
        return nonNegative(0);
    }

    @NotNull
    @Contract("_->new")
    public static FloatProperty nonNegative(float value) {
        return new FloatProperty(value, (FloatUnaryOperator) x -> Arguments.checkRange(x, 0f, null));
    }

    @NotNull
    @Contract("->new")
    public static FloatProperty nonPositive() {
        return nonPositive(0);
    }

    @NotNull
    @Contract("_->new")
    public static FloatProperty nonPositive(float value) {
        return new FloatProperty(value, (FloatUnaryOperator) x -> Arguments.checkInclusive(x, null, 0f));
    }
}
