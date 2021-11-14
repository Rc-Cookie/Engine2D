package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.IVec2;

public interface DisplayFactory {

    Display createNew(IVec2 resolution, CameraControls cameraControls);
}
