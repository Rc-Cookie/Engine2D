package com.github.rccookie.engine2d.ui.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.rccookie.engine2d.coroutine.Execute;
import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;

/**
 * Automatically controls a set of toggles so that exactly one is always on.
 */
public class ToggleGroup<T extends Toggle> {

    /**
     * The toggles.
     */
    private final Set<T> toggles = new HashSet<>();
    /**
     * View of {@link #toggles}.
     */
    private final Set<T> togglesView = Collections.unmodifiableSet(toggles);
    /**
     * The active toggle.
     */
    private T active = null;

    /**
     * Are received toggle events currently ignored?
     */
    private boolean ignore = false;


    /**
     * Invoked whenever the selected toggle changes, with the now
     * active toggle.
     */
    public final ParamEvent<T> onToggle = new CaughtParamEvent<>() {
        @Override
        public boolean invoke(T info) {
            if(active == Arguments.checkNull(info, "info")) {
                Execute.later(() -> {
                    ignore = true;
                    info.setOn(true);
                    ignore = false;
                });
                return false;
            }
            if(!toggles.contains(info))
                throw new IllegalArgumentException("Toggle is not in the toggle group");
            return super.invoke(info);
        }
    };


    /**
     * Creates a new toggle group for the given toggles.
     *
     * @param toggles The toggles to include.
     */
    @SafeVarargs
    public ToggleGroup(T... toggles) {
        onToggle.add(active -> {
                ignore = true;

                T oldActive = this.active;
                this.active = active;
                if(oldActive != null) oldActive.setOn(false);
                active.setOn(true);

                ignore = false;
        });
        add(toggles);
    }

    /**
     * Adds the given toggles to the toggle group.
     *
     * @param toggles The toggles to add
     */
    @SuppressWarnings("unchecked")
    public void add(T... toggles) {
        for(T toggle : toggles) {
            if(this.toggles.add(toggle)) {
                boolean first = this.toggles.size() == 1;
                if(!first)
                    toggle.setOn(false); // Don't fire own onToggle event
                toggle.onToggle.add(() -> {
                    if(!ignore) onToggle.invoke(toggle);
                });
                if(first)
                    toggle.setOn(true); // Fire own onToggle event
            }
        }
    }

    /**
     * Returns the currently active toggle.
     *
     * @return The active toggle
     */
    public T getActive() {
        return active;
    }

    /**
     * Returns a view of the toggles in this toggle group.
     *
     * @return The toggles in this toggle group
     */
    public Set<T> getToggles() {
        return togglesView;
    }
}
