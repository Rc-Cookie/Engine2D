package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface LoopTask extends ParamLoopTask<Object>, ValueLoopTask<Void>, ParamValueLoopTask<Object,Void> {

    void runIteration();

    @Override
    default void runIteration(Object prevResult, ReturnCallback<? super Void> returnCallback) {
        runIteration();
    }

    @Override
    default void runIteration(Object prevResult) {
        runIteration();
    }

    @Override
    default void runIteration(ReturnCallback<? super Void> returnCallback) {
        runIteration();
    }
}
