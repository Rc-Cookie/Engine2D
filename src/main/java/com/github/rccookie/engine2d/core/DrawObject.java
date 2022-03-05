package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.geometry.performance.int2;

public class DrawObject {

    public final int2 screenLocation = int2.ZERO.clone();
    public float rotation;
    public ImageImpl image;

    @Override
    public String toString() {
        return "DrawObject at " + screenLocation;
    }
}
