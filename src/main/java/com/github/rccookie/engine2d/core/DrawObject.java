package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.geometry.performance.IVec2;

public class DrawObject {

    public final IVec2 screenLocation = IVec2.ZERO.clone();
    public double rotation;
    public ImageImpl image;

    @Override
    public String toString() {
        return "DrawObject at " + screenLocation;
    }
}
