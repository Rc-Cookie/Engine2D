package com.github.rccookie.engine2d.util;

public class FutureImpl<T> implements Future<T> {
    private boolean canceled = false;
    private boolean done = false;
    private T value = null;

    public void setValue(T value) {
        if(isDone()) throw new IllegalStateException("The value cannot be set because the computation is already done");
        this.value = value;
        done = true;
    }

    @Override
    public boolean cancel() {
        boolean out = !canceled && !done;
        canceled = done = true;
        return out;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public T get() throws IllegalStateException {
        if(!done) throw new IllegalStateException("Result is not yet computed");
        if(canceled) throw new IllegalStateException("Execution has been canceled");
        return value;
    }
}
