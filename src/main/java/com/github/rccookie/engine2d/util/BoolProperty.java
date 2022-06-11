package com.github.rccookie.engine2d.util;

import java.util.Objects;

import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class BoolProperty {

    boolean value;
    @NotNull
    BoolConsumer validator;
    @NotNull
    public final ParamEvent<Boolean> onChange = new CaughtParamEvent<>();


    public BoolProperty() {
        this(false);
    }

    public BoolProperty(boolean value) {
        this(value, b -> {});
    }

    public BoolProperty(boolean value, @NotNull BoolConsumer validator) {
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
        if(!(o instanceof BoolProperty)) return false;
        BoolProperty that = (BoolProperty) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }



    public boolean get() {
        return value;
    }

    public boolean is() {
        return get();
    }

    public void set(boolean value) {
        if(this.value == value) return;

        validator.accept(value);
        this.value = value;

        onChange.invoke(this.value);
    }



    public void addValidator(BoolConsumer validator) {
        Arguments.checkNull(validator, "validator");
        BoolConsumer old = this.validator;
        this.validator = t -> {
            old.accept(t);
            validator.accept(t);
        };
    }
}
