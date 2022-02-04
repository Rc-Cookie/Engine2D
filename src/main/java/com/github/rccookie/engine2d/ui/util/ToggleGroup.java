package com.github.rccookie.engine2d.ui.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.github.rccookie.engine2d.ui.Toggle;
import com.github.rccookie.util.Arguments;

public class ToggleGroup {

    private final Set<Toggle> toggles = new HashSet<>();
    private Toggle active = null;

    private boolean ignore = false;

    public ToggleGroup(Toggle... toggles) {
        add(toggles);
    }

    public void add(Toggle toggle) {
        if(toggles.add(toggle)) {
            toggle.onToggle.add(() -> { if(!ignore) toggle(toggle); });
            toggle.setOn(toggles.size() == 1);
        }
    }

    public void add(Toggle... toggles) {
        for(Toggle t : toggles) add(t);
    }

    public void toggle(Toggle active) {
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

    public Toggle getActive() {
        return active;
    }
}
