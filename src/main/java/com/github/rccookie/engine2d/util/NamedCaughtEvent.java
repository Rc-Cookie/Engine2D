package com.github.rccookie.engine2d.util;

import java.util.function.Supplier;

import com.github.rccookie.event.CaughtEvent;
import com.github.rccookie.event.EventInvocationException;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class NamedCaughtEvent extends CaughtEvent {

    private final Supplier<String> name;

    public NamedCaughtEvent(boolean consumable, String name) {
        this(consumable, () -> name);
    }

    public NamedCaughtEvent(boolean consumable, Supplier<String> name) {
        super(consumable);
        this.name = Arguments.checkNull(name);
    }

    @Override
    protected void handleException(@NotNull EventInvocationException e) {
        System.err.println("Exception occurred invoking event '" + name.get() + "':");
        super.handleException(e);
    }
}
