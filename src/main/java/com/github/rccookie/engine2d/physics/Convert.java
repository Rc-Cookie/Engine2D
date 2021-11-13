package com.github.rccookie.engine2d.physics;

import com.github.rccookie.geometry.performance.FastMath;

public class Convert {

    private Convert() {
        throw new UnsupportedOperationException();
    }

    public static final float UNITS_TO_PIXELS = 100, PIXELS_TO_UNITS = 1 / UNITS_TO_PIXELS;
    public static final float RADIANS_TO_DEGREES = FastMath.RADIANS_TO_DEGREES;
    public static final float DEGREES_TO_RADIANS = FastMath.DEGREES_TO_RADIANS;
}
