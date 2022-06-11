package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.geometry.performance.int2;

/**
 * A display accepts and renders output produced by a camera.
 */
public interface Display {

    /**
     * Default resolution if no camera has been created yet.
     */
    int2 DEFAULT_RESOLUTION = new int2(600, 400);

    /**
     * Draws the given objects onto the screen with the given background color.
     * Any old drawing should be cleared.
     *
     * @param objects The objects to draw
     * @param background The background color
     */
    void draw(DrawObject[] objects, Color background);

    /**
     * Sets the display's resolution to the specified one
     *
     * @param resolution The resolution to set
     */
    void setResolution(int2 resolution);

    /**
     * Called whenever the allowed state for external resizing
     * has changed.
     *
     * @param allowed Whether external resizing is allowed now
     */
    void allowResizingChanged(boolean allowed);
}
