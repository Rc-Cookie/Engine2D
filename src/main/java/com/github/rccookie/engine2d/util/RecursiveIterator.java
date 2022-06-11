package com.github.rccookie.engine2d.util;

import com.github.rccookie.util.EmptyIteratorException;
import com.github.rccookie.util.IterableIterator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RecursiveIterator<T> implements IterableIterator<T> {

    T next = null;
    boolean nextReady = false;

    @Override
    public boolean hasNext() {
        ensureNext();
        return next != null;
    }

    @Override
    @NotNull
    public T next() {
        ensureNext();
        if(next == null) throw new EmptyIteratorException();
        nextReady = false;
        return next;
    }

    void ensureNext() {
        if(nextReady) return;
        next = getNext();
        nextReady = true;
    }

    @Nullable
    protected abstract T getNext();
}
