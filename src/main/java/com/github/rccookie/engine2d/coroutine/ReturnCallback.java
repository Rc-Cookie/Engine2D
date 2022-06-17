package com.github.rccookie.engine2d.coroutine;

import org.jetbrains.annotations.Contract;

public interface ReturnCallback<T> {

    @Contract("_->fail")
    void _return(T result);

    @Contract("->fail")
    default void _return() {
        _return(null);
    }
}
