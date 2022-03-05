package com.github.rccookie.engine2d.util;

import com.github.rccookie.geometry.performance.FastMath;

/**
 * Utility class for converting between systems.
 */
public final class Convert {

    private Convert() {
        throw new UnsupportedOperationException();
    }

    /**
     * Conversion factor between physics units and pixels.
     */
    public static final float UNITS_TO_PIXELS = 100, PIXELS_TO_UNITS = 1 / UNITS_TO_PIXELS;
    /**
     * Factor between radians and degrees.
     */
    public static final float TO_DEGREES = FastMath.RADIANS_TO_DEGREES;
    /**
     * Factor between degrees and radians.
     */
    public static final float TO_RADIANS = FastMath.DEGREES_TO_RADIANS;
}
