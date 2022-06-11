package com.github.rccookie.engine2d.input;

import com.github.rccookie.event.action.IAction;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public final class InputBinding {

    @NotNull
    public final InputSource source;
    @NotNull
    public final InputInterpolation interpolation;

    private boolean ready = false;
    private float value;

    public InputBinding(@NotNull InputSource source, @NotNull InputInterpolation interpolation) {
        this.source = Arguments.checkNull(source, "source");
        this.interpolation = Arguments.checkNull(interpolation, "interpolation");
        this.source.onChange.add(v -> {
            value = v;
            ready = true;
        });
    }

    public float get() {
        if(!ready) {
            ready = true;
            return value = interpolation.get(source.get(), source.range);
        }
        return value;
    }

    IAction bind(InputHandler handler) {
        return source.onChange.add(handler::onValueChange);
    }

    void unbind(IAction handler) {
        source.onChange.remove(handler);
    }
}
