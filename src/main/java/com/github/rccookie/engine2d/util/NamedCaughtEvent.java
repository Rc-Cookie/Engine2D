package com.github.rccookie.engine2d.util;

import java.util.function.Supplier;

import com.github.rccookie.event.CaughtEvent;
import com.github.rccookie.event.EventInvocationException;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * A caught event that prints out its name when an error occurs.
 */
public class NamedCaughtEvent extends CaughtEvent {

    /**
     * The name generator.
     */
    private final Supplier<String> name;

    /**
     * Creates a new named caught event with the given name.
     *
     * @param consumable Should the event be consumable?
     * @param name The constant name of the event
     */
    public NamedCaughtEvent(boolean consumable, String name) {
        this(consumable, () -> name);
    }

    /**
     * Creates a new named caught event with the given name generator.
     *
     * @param consumable Should the event be consumable?
     * @param name The name generator to use when the name is needed
     */
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
