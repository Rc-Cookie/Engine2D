package com.github.rccookie.engine2d.coroutine;

@FunctionalInterface
public interface ParamValueTask<T,R> extends ParamValueLoopTask<T,R> {

    R run(T prevResult);

    @Override
    default void runIteration(T prevResult, ReturnCallback<? super R> returnCallback) {
        returnCallback._return(run(prevResult));
    }
}
