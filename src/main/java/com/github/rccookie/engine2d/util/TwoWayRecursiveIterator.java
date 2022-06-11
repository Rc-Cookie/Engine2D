package com.github.rccookie.engine2d.util;

import com.github.rccookie.util.EmptyIteratorException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TwoWayRecursiveIterator<T> extends RecursiveIterator<T> implements TwoWayIterator<T> {

    boolean prevReady = false;
    T prev, curr = null;

    @Override
    public boolean hasPrev() {
        ensurePrev();
        return prev != null;
    }

    @Override
    @NotNull
    public T next() throws EmptyIteratorException {
        ensureNext();
        if(next == null) throw new EmptyIteratorException();
        nextReady = prevReady = false;
        return curr = next;
    }

    @Override
    @NotNull
    public T prev() throws EmptyIteratorException {
        ensurePrev();
        if(prev == null) throw new EmptyIteratorException();
        nextReady = prevReady = false;
        return curr = prev;
    }

    private void ensurePrev() {
        if(prevReady) return;
        prev = getPrev();
        prevReady = true;
    }

    @Nullable
    protected abstract T getPrev();
}
