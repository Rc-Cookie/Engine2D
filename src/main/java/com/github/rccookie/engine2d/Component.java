package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.util.Arguments;

public abstract class Component {

    public final GameObject gameObject;
    public final LocalInputManager input;
    public final LocalExecutionManager execute;

    public final Event earlyUpdate = new NamedCaughtEvent(false, () -> "Component.earlyUpdate on " + this);
    public final Event update      = new NamedCaughtEvent(false, () -> "Component.update on "      + this);
    public final Event lateUpdate  = new NamedCaughtEvent(false, () -> "Component.lateUpdate on "  + this);

    public Component(GameObject gameObject) {
        this.gameObject = Arguments.checkNull(gameObject);
        input = gameObject.input;
        execute = gameObject.execute; // TODO: Should probably test whether it's not removed yet
        gameObject.components.add(this);
    }
}
