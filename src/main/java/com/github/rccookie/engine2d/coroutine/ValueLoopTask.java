package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface ValueLoopTask<R> extends ParamValueLoopTask<Object,R> {

    void runIteration(ReturnCallback<? super R> returnCallback);

    @Override
    default void runIteration(Object prevResult, ReturnCallback<? super R> returnCallback) {
        runIteration(returnCallback);
    }
}
