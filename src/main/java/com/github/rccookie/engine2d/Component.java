package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.util.Arguments;

/**
 * A component gets attached to a gameobject and should be seen as a sort-of
 * controller that can be enabled and disabled, and reused across different
 * use-cases without the need of multi-inheritance and an excessive overhead
 * on the base class with many functions mostly not needed. Components are always
 * attached to one and only one gameobject and never change which object they
 * are attached to. They can however be - once and for all - be removed from
 * their gameobject.
 */
public abstract class Component {

    /**
     * The gameobject this component is attached to.
     */
    public final GameObject gameObject;
    /**
     * Local input manager only active when component and gameobject are.
     */
    public final LocalInputManager input;
    /**
     * Local execution manager only active when component and gameobject are.
     */
    public final LocalExecutionManager execute;

    /**
     * Called once per frame before the attached gameobject's update event.
     */
    public final Event earlyUpdate = new NamedCaughtEvent(false, () -> "Component.earlyUpdate on " + this);
    /**
     * Called once per frame after the attached gameobject's update event.
     */
    public final Event update      = new NamedCaughtEvent(false, () -> "Component.update on "      + this);
    /**
     * Called once per frame after the attached gameobject's lateUpdate event.
     */
    public final Event lateUpdate  = new NamedCaughtEvent(false, () -> "Component.lateUpdate on "  + this);


    /**
     * Whether this component is currently enabled.
     */
    boolean enabled = true;

    /**
     * Creates a new component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach to
     */
    public Component(GameObject gameObject) {
        this.gameObject = Arguments.checkNull(gameObject);
        input = gameObject.input;
        execute = gameObject.execute; // TODO: Should probably test whether it's not removed yet
        gameObject.components.add(this);
    }

    /**
     * Sets whether this component should be enabled and thus receives events.
     *
     * @param enabled Whether this component should be enabled or not
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether this component is currently enabled and thus receives events.
     *
     * @return Whether this component is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
}
