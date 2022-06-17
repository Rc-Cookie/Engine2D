package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface ParamTask<T> extends ParamValueLoopTask<T,Void> {

    void run(T prevResult);

    @Override
    default void runIteration(T prevResult, ReturnCallback<? super Void> returnCallback) {
        run(prevResult);
        returnCallback._return();
    }
}
