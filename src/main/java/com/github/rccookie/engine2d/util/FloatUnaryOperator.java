package com.github.rccookie.engine2d.util;

@FunctionalInterface
public interface FloatUnaryOperator {

    FloatUnaryOperator IDENTITY = x -> x;

    float apply(float x);
}
