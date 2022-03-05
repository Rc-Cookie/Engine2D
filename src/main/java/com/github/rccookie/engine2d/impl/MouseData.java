package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.int2;

/**
 * Describes the state of the mouse at a certain point in
 * time.
 */
public class MouseData {

    /**
     * The pixel on the screen, relative to the top left
     * corner of the application.
     */
    public final int2 screenLoc;
    /**
     * The button which is currently pressed down (0 means no button).
     */
    public final int button;

    /**
     * Creates a new mouse data.
     *
     * @param screenLoc The screen position
     * @param button The button pressed, or 0 for no button
     */
    public MouseData(int2 screenLoc, int button) {
        this.screenLoc = screenLoc;
        this.button = button;
    }
}
