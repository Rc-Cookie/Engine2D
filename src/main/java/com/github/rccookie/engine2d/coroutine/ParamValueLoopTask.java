package com.github.rccookie.engine2d.coroutine;

public interface ParamValueLoopTask<T,R> {

    void runIteration(T prevResult, ReturnCallback<? super R> returnCallback);
}
