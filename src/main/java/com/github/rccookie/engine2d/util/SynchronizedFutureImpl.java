package com.github.rccookie.engine2d.util;

import java.util.function.Consumer;
import java.util.function.Function;

import com.github.rccookie.engine2d.coroutine.Execute;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Future;

public class SynchronizedFutureImpl<V> implements Future<V> {

    private final Future<? extends V> future;

    public SynchronizedFutureImpl(Future<? extends V> future) {
        this.future = Arguments.checkNull(future, "future");
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
        return future.get();
    }

    @Override
    public V waitFor() throws IllegalStateException, UnsupportedOperationException {
        return future.waitFor(); // Called from the main thread anyway
    }

    @Override
    public Future<V> then(Consumer<? super V> action) {
        future.then(w -> Execute.synced(() -> action.accept(w)));
        return this;
    }

    @Override
    public Future<V> except(Consumer<? super Exception> handler) {
        future.except(e -> Execute.synced(() -> handler.accept(e)));
        return this;
    }

    @Override
    public <T> Future<T> map(Function<? super V, ? extends T> mapper) {
        return new SynchronizedFutureImpl<>(future.map(mapper));
    }

    @Override
    public <T> Future<T> flatMap(Function<? super V, ? extends Future<T>> mapper) {
        return new SynchronizedFutureImpl<>(future.flatMap(mapper));
    }
}
