package com.github.rccookie.engine2d.util;

@FunctionalInterface
public interface UnaryBoolOperator {

    UnaryBoolOperator IDENTITY = x -> x;

    boolean apply(boolean x);
}
