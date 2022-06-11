package com.github.rccookie.engine2d.coroutine;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.NoWaitFutureImpl;

public abstract class Coroutine<T> {

    private final FutureImpl<T> result = new NoWaitFutureImpl<>();

    protected abstract Wait getNextChunk();

    public final Wait runNextChunk() {
        if(result.isDone())
            throw new IllegalStateException("Coroutine has ended");
        try {
            Wait wait = getNextChunk();
            return result.isDone() ? null : wait;
        } catch(RuntimeException e) {
            System.err.println("Exception occurred in coroutine execution:");
            e.printStackTrace();
            if(!result.isDone())
                result.fail(e);
            return null;
        }
    }

    protected void setResult(T result) {
        this.result.complete(result);
    }

    public FutureImpl<T> getResult() {
        return result;
    }



    public static Coroutine<Object> loop(BooleanSupplier condition, Supplier<Wait> action) {
        return new Coroutine<>() {
            @Override
            protected Wait getNextChunk() {
                if(!condition.getAsBoolean()) return null;
                return action.get();
            }
        };
    }

    public static <T> Coroutine<Object> forI(T init, Function<T,Boolean> condition, UnaryOperator<T> inc, Function<T,Wait> action) {
        return new Coroutine<>() {
            private T i = init;
            @Override
            protected Wait getNextChunk() {
                if(!condition.apply(i)) return null;
                Wait wait = action.apply(i);
                i = inc.apply(i);
                return wait;
            }
        };
    }

    public static <T> Coroutine<Object> foreach(Iterable<T> over, Function<T,Wait> action) {
        Iterator<T> it = over.iterator();
        return new Coroutine<>() {
            @Override
            protected Wait getNextChunk() {
                if(!it.hasNext()) return null;
                return action.apply(it.next());
            }
        };
    }
}
