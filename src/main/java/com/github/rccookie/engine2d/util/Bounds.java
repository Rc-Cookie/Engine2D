package com.github.rccookie.engine2d.util;

import com.github.rccookie.geometry.performance.int2;

public class Bounds {

    public final int2 min, max;

    public Bounds(int2 min, int2 max) {
        this.min = min.clone();
        this.max = max.clone();
    }

    public int2 size() {
        return max.subed(min);
    }

    public int2 pos() {
        return min.added(max).div(2);
    }
}
