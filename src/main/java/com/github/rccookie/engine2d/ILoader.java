package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.ApplicationLoader;

/**
 * This class represents the unified entry point for an application
 * and gets called by an {@link ApplicationLoader}. Every application
 * should have exactly one of these.
 */
public interface ILoader {

    /**
     * Initializes "low-level" settings that don't have anything
     * to do with the application directly, for example setting
     * the log level. This will be called before the application
     * is set up or started, and before {@link #load()}.
     *
     * <p>The default implementation does nothing.
     */
    default void initialize() { };

    /**
     * Creates a starting state of the application, by creating
     * cameras, maps, ui and so on. Called after the application
     * has been set up, but before it is started.
     */
    void load();
}
