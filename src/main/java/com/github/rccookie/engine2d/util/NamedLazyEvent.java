package com.github.rccookie.engine2d.util;

import java.util.function.Supplier;

import com.github.rccookie.event.EventInvocationException;
import com.github.rccookie.event.LazyEvent;
import com.github.rccookie.event.internal.IEvent;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A lazy event that prints out its name when an error occurs.
 */
public class NamedLazyEvent extends LazyEvent {

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
    public NamedLazyEvent(Runnable connect, Runnable disconnect, boolean consumable, String name) {
        this(connect, disconnect, consumable, () -> name);
    }

    /**
     * Creates a new named caught event with the given name generator.
     *
     * @param consumable Should the event be consumable?
     * @param name The name generator to use when the name is needed
     */
    public NamedLazyEvent(Runnable connect, Runnable disconnect, boolean consumable, Supplier<String> name) {
        super(connect, disconnect, consumable);
        this.name = Arguments.checkNull(name);
    }

    /**
     * Creates a new named caught event with the given name.
     *
     * @param consumable Should the event be consumable?
     * @param name The constant name of the event
     */
    public NamedLazyEvent(IEvent attachTo, boolean consumable, String name) {
        this(attachTo, consumable, () -> name);
    }

    /**
     * Creates a new named caught event with the given name generator.
     *
     * @param consumable Should the event be consumable?
     * @param name The name generator to use when the name is needed
     */
    public NamedLazyEvent(IEvent attachTo, boolean consumable, Supplier<String> name) {
        super(attachTo, consumable);
        this.name = Arguments.checkNull(name);
    }

    @Override
    protected void handleException(@NotNull EventInvocationException e) {
        System.err.println("Exception occurred invoking event '" + name.get() + "':");
        super.handleException(e);
    }
}
