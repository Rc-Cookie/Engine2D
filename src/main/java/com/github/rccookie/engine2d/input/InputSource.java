package com.github.rccookie.engine2d.input;

import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;

import org.jetbrains.annotations.NotNull;

public abstract class InputSource {

    @NotNull
    public final InputRange range;

    public final ParamEvent<Float> onChange = new CaughtParamEvent<>();

    protected InputSource(@NotNull InputRange range) {
        this.range = range;
    }

    public abstract float get();
}
