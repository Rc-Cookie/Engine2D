package com.github.rccookie.engine2d.input;

import com.github.rccookie.geometry.performance.Interpolation;

public class InputInterpolation {

    public final boolean inverted;
    public final boolean negated;
    public final boolean mirrored;
    public final byte negateMinus;
    public final float deadzoneRest;
    public final float deadzoneEnd;
    public final Interpolation interpolation;

    private final float rangeInverted;

    public InputInterpolation(boolean inverted, boolean negated, boolean mirrored, boolean negateMinus, float deadzoneRest, float deadzoneEnd, Interpolation interpolation) {
        this.inverted = inverted;
        this.negated = negated;
        this.mirrored = mirrored;
        this.negateMinus = negateMinus ? (byte) -1 : (byte) 1;
        this.deadzoneRest = deadzoneRest;
        this.deadzoneEnd = deadzoneEnd;
        this.interpolation = interpolation;

        rangeInverted = deadzoneEnd - deadzoneRest;
    }

    public float get(float x, InputRange range) {
        float y;
        if(range == InputRange.ZERO_TO_ONE || x >= 0) {
            if(x < deadzoneRest) y = 0;
            else if(x > deadzoneEnd) y = 1;
            else y = interpolation.get((x - deadzoneRest) * rangeInverted);
        }
        else {
            if(-x < deadzoneRest) y = 0;
            else if(-x > deadzoneEnd) y = -1;
            else y = negateMinus * interpolation.get(((mirrored ? -x : x) - deadzoneRest) * rangeInverted);
        }
        return (negated ? -1 : 1) * (inverted ? 1 - y : y);
    }
}
