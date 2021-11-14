package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.geometry.performance.IVec2;

public interface Display {

    /**
     * Default resolution if no camera has been created yet.
     */
    IVec2 DEFAULT_RESOLUTION = new IVec2(600, 400);

    void draw(DrawObject[] objects, Color background);

    void setResolution(IVec2 resolution);

    void allowResizingChanged(boolean allowed);
}
