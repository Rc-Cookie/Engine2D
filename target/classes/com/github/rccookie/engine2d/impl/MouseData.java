package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.IVec2;

public class MouseData {

    public final IVec2 screenLoc;
    public final int button;
    public final boolean pressed;

    public MouseData(IVec2 screenLoc, int button) {
        this.screenLoc = screenLoc;
        this.button = button;
        this.pressed = button != 0;
    }
}
