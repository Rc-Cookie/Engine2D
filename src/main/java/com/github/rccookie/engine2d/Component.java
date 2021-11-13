package com.github.rccookie.engine2d;

import com.github.rccookie.event.Event;
import com.github.rccookie.util.Arguments;

public abstract class Component {

    public final GameObject gameObject;

    public final Event earlyUpdate = new Event();
    public final Event update = new Event();
    public final Event lateUpdate = new Event();

    public Component(GameObject gameObject) {
        this.gameObject = Arguments.checkNull(gameObject);
        gameObject.components.add(this);
    }
}
