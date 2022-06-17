package com.github.rccookie.engine2d.coroutine;

import java.util.function.Function;

public interface InfiniteTimedLoop<T> extends ParamLoopTask<T>, Function<T,Float> {

    float getInterval(T result);

    @Override
    default Float apply(T t) {
        return getInterval(t);
    }
}
