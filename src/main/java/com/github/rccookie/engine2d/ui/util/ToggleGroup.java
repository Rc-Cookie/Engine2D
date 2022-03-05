package com.github.rccookie.engine2d.ui.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * Automatically controls a set of toggles so that exactly one is always on.
 */
public class ToggleGroup {

    /**
     * The toggles.
     */
    private final Set<Toggle> toggles = new HashSet<>();
    /**
     * The active toggle.
     */
    private Toggle active = null;

    /**
     * Are received toggle events currently ignored?
     */
    private boolean ignore = false;


    /**
     * Creates a new toggle group for the given toggles.
     *
     * @param toggles The toggles to include.
     */
    public ToggleGroup(Toggle... toggles) {
        add(toggles);
    }

    /**
     * Adds the given toggles to the toggle group.
     *
     * @param toggles The toggles to add
     */
    public void add(Toggle... toggles) {
        for(Toggle toggle : toggles) {
            if(this.toggles.add(toggle)) {
                toggle.onToggle.add(() -> {
                    if(!ignore) toggle(toggle);
                });
                toggle.setOn(this.toggles.size() == 1);
            }
        }
    }

    /**
     * Toggles the toggle group so that the given toggle is on.
     *
     * @param active The toggle to be on
     */
    public void toggle(@NotNull Toggle active) {
        Arguments.checkNull(active);
        if(!toggles.contains(active))
            throw new IllegalArgumentException("Toggle is not in toggle group");

        ignore = true;
        this.active = active;

        for(Toggle t : toggles.toArray(new Toggle[0]))
            if(!Objects.equals(t, active))
                t.setOn(false);

        active.setOn(true);

        ignore = false;
    }

    /**
     * Returns the currently active toggle.
     *
     * @return The active toggle
     */
    public Toggle getActive() {
        return active;
    }
}
