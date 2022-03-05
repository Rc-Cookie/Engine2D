package com.github.rccookie.engine2d.util;

/**
 * A coroutine with no return value.
 */
@FunctionalInterface
@Deprecated
public interface VoidCoroutine extends Coroutine<Object> {

    /**
     * Run the coroutine.
     */
    void runVoid();

    @Override
    default Object run() {
        runVoid();
        return null;
    }
}
