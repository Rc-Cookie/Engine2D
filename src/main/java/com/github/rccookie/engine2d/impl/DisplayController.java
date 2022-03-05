package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.int2;

/**
 * Allows to control the camera from the display.
 */
public interface DisplayController {

    /**
     * Sets current camera to use the given resolution, if allowed.
     *
     * @param resolution The resolution to set
     * @return Whether the resolution was set
     */
    boolean setResolution(int2 resolution);

    /**
     * Returns whether resizing using a display controller is
     * currently allowed.
     *
     * @return Whether resizing is allowed
     */
    boolean allowsResizing();

    /**
     * Invokes the execution of a full frame. To be used from an
     * external update loop.
     */
    void runApplicationFrame();
}
