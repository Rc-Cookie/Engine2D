package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface Task extends ValueLoopTask<Void> {

    void run();

    @Override
    default void runIteration(ReturnCallback<? super Void> returnCallback) {
        run();
        returnCallback._return();
    }
}
