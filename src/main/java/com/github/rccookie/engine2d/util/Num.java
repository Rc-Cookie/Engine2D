package com.github.rccookie.engine2d.util;

import org.jetbrains.annotations.Contract;

public enum Num {
    ;

    @Contract(pure = true)
    public static double clamp(double x, double a, double b) {
        return Math.max(a, Math.min(b, x));
    }

    @Contract(pure = true)
    public static float clamp(float x, float a, float b) {
        return Math.max(a, Math.min(b, x));
    }

    @Contract(pure = true)
    public static long clamp(long x, long a, long b) {
        return Math.max(a, Math.min(b, x));
    }

    @Contract(pure = true)
    public static int clamp(int x, int a, int b) {
        return Math.max(a, Math.min(b, x));
    }



    @Contract(pure = true)
    public static float sqrt(float x) {
        // May be replaced by float-optimized formula
        return (float) Math.sqrt(x);
    }
}
