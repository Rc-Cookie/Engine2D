package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface ParamLoopTask<T> extends ParamValueLoopTask<T,Void> {

    void runIteration(T prevResult);

    @Override
    default void runIteration(T prevResult, ReturnCallback<? super Void> returnCallback) {
        runIteration(prevResult);
    }
}
