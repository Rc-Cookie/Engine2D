package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.Nullable;

/**
 * Abstract type of ui object that has some interactable features.
 */
public abstract class Interactable extends UIObject {

    /**
     * Creates a new ui object with the specified parent.
     *
     * @param parent The ui objects parent, or {@code null} if the ui object
     *               should not have any parent
     */
    public Interactable(@Nullable UIObject parent) {
        super(parent);
    }
}
