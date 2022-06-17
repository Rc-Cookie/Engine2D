package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface ValueTask<R> extends ValueLoopTask<R> {

    R run();

    @Override
    default void runIteration(ReturnCallback<? super R> returnCallback) {
        returnCallback._return(run());
    }
}
