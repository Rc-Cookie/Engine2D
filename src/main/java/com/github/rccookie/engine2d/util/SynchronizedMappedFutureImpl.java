package com.github.rccookie.engine2d.util;

import java.util.function.Consumer;
import java.util.function.Function;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Future;

public class SynchronizedMappedFutureImpl<V,W> implements Future<V> {

    private final Future<W> future;
    private final Function<W,V> mapper;

    public SynchronizedMappedFutureImpl(Future<W> future, Function<W, V> mapper) {
        this.future = Arguments.checkNull(future, "future");
        this.mapper = Arguments.checkNull(mapper, "mapper");
    }

    @Override
    public boolean cancel() {
        return future.cancel();
    }

    @Override
    public boolean isCanceled() {
        return future.isCanceled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public V get() throws IllegalStateException {
        return mapper.apply(future.get());
    }

    @Override
    public V waitFor() throws IllegalStateException, UnsupportedOperationException {
        return mapper.apply(future.waitFor()); // Called from the main thread anyway
    }

    @Override
    public Future<V> then(Consumer<? super V> action) {
        future.then(w -> Execute.synced(() -> action.accept(mapper.apply(w))));
        return this;
    }

    @Override
    public Future<V> onCancel(Consumer<Exception> handler) {
        future.onCancel(e -> Execute.synced(() -> handler.accept(e)));
        return this;
    }
}
