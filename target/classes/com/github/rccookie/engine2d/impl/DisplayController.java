package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.IVec2;

public interface DisplayController {

    boolean setResolution(IVec2 resolution);

    boolean allowsResizing();

    void runApplicationFrame();
}
