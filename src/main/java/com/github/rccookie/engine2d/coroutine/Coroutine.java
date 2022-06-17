package com.github.rccookie.engine2d.coroutine;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.util.CheckReturnValue;
import com.github.rccookie.engine2d.util.IntWrapper;
import com.github.rccookie.engine2d.util.Tuple2;
import com.github.rccookie.util.FloatWrapper;
import com.github.rccookie.util.Future;
import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.LongWrapper;
import com.github.rccookie.util.NoWaitFutureImpl;
import com.github.rccookie.util.Wrapper;

import org.jetbrains.annotations.Contract;

public class Coroutine<T> {

    private final ValueLoopTask<? extends T>/*Consumer<? super ReturnCallback<? super T>>*/ task;
    private final Future<T> result = new NoWaitFutureImpl<>();
    private final BooleanSupplier isExecutionPossible;

    Coroutine(BooleanSupplier isExecutionPossible) {
        task = $ -> { throw new AssertionError(); };
        this.isExecutionPossible = isExecutionPossible;
        ((FutureImpl<?>) result).complete(null);
    }

    @SuppressWarnings("unchecked")
    private <U,V> Coroutine(Coroutine<U> c1, Coroutine<V> c2) {
        isExecutionPossible = c1.isExecutionPossible;
        IntWrapper doneCount = new IntWrapper();
        task = r -> r._return((T) new Tuple2<>(c1.result(), c2.result()));
        c1.listen(() -> {
            if(++doneCount.value == 2)
                Execute.COROUTINES.add(this);
        });
        c2.listen(() -> {
            if(++doneCount.value == 2)
                Execute.COROUTINES.add(this);
        });
        c1.except(((FutureImpl<T>) result)::fail);
        c2.except(((FutureImpl<T>) result)::fail);
        listen(() -> {
            synchronized(Execute.COROUTINES) {
                Execute.COROUTINES.remove(this);
            }
        });
        except(() -> {
            synchronized(Execute.COROUTINES) {
                Execute.COROUTINES.remove(this);
            }
        });
    }

    private <P> Coroutine(Coroutine<P> previous, ParamValueLoopTask<? super P, ? extends T> subTask) {//BiConsumer<? super P, ? super ReturnCallback<T>> subAction) {
        isExecutionPossible = previous.isExecutionPossible;
        task = r -> subTask.runIteration(previous.result(), r);
        previous.listen(() -> {
            synchronized(Execute.COROUTINES) {
                Execute.COROUTINES.add(this);
            }
        });
        previous.except(((FutureImpl<T>) result)::fail);
        listen(() -> {
            synchronized(Execute.COROUTINES) {
                Execute.COROUTINES.remove(this);
            }
        });
        except(() -> {
            synchronized(Execute.COROUTINES) {
                Execute.COROUTINES.remove(this);
            }
        });
    }

    // ------------------------------------------------------------------

    void run() {
        run(r -> {
            ((FutureImpl<T>) result).complete(r);
            throw new Return();
        });
    }

    private void run(ReturnCallback<? super T> onReturn) {
        if(!isRunning()) throw new IllegalStateException("Coroutine already done");
        if(!isExecutionPossible.getAsBoolean()) return;
        try {
            task.runIteration(onReturn);
        } catch(Return ignored) {
        } catch(Exception e) {
            ((FutureImpl<T>) result).fail(e);
        }
    }

    // ------------------------------------------------------------------

    @CheckReturnValue
    public Coroutine<Void> thenRun(Task task) {
        return subroutine(task);
    }

    @CheckReturnValue
    public Coroutine<Void> thenRun(ParamTask<? super T> task) {
        return subroutine(task);
    }

    @CheckReturnValue
    public <R> Coroutine<R> then(ValueTask<? extends R> task) {
        return subroutine(task);
    }

    @CheckReturnValue
    public <R> Coroutine<R> then(ParamValueTask<? super T, ? extends R> task) {
        return subroutine(task);
    }

    // ------------------------------------------------------------------

    @CheckReturnValue
    public <R> Coroutine<R> thenLoop(ValueLoopTask<? extends R> loopTask) {
        return subroutine(loopTask);
    }

    @CheckReturnValue
    public <R> Coroutine<R> thenLoop(ParamValueLoopTask<? super T, ? extends R> loopBody) {
        return subroutine(loopBody);
    }

    // ------------------------------------------------------------------

    public <R> Coroutine<R> thenLoop(ValueLoopTask<? extends R> loopBody, float interval) {
        return thenLoop(loopBody, interval, false);
    }

    public <R> Coroutine<R> thenLoop(ValueLoopTask<? extends R> loopBody, float interval, boolean realtime) {
        return thenLoop((ParamValueLoopTask<? super T, ? extends R>) loopBody, interval, realtime);
    }

    public <R> Coroutine<R> thenLoop(ParamValueLoopTask<? super T, ? extends R> loopBody, float interval) {
        return thenLoop(loopBody, interval, false);
    }

    public <R> Coroutine<R> thenLoop(ParamValueLoopTask<? super T, ? extends R> loopBody, float interval, boolean realtime) {
        return thenLoop(loopBody, $ -> interval, realtime);
    }

    public <R> Coroutine<R> thenLoop(ParamValueLoopTask<? super T, ? extends R> loopBody, Function<? super T,Float> intervalGetter, boolean realtime) {
        Supplier<Float> time = realtime ? Time::realTime : Time::time;
        FloatWrapper interval = new FloatWrapper();
        FloatWrapper nextExec = new FloatWrapper();
        listen(t -> { interval.value = intervalGetter.apply(t); nextExec.value = time.get(); });
        return subroutine((t,r) -> {
            if(time.get() < nextExec.value) return;
            loopBody.runIteration(t,r);
            nextExec.value += interval.value;
        });
    }

    public <R> Coroutine<R> thenLoop(TimedLoop<T, R> loop, boolean realtime) {
        return thenLoop(loop, loop, realtime);
    }

    // ------------------------------------------------------------------

    public <R> Coroutine<R> thenLoopEveryFrame(ValueLoopTask<? extends R> loopBody) {
        return thenLoopEveryFrame((ParamValueLoopTask<? super T, ? extends R>) loopBody);
    }

    public <R> Coroutine<R> thenLoopEveryFrame(ParamValueLoopTask<? super T, ? extends R> loopBody) {
        LongWrapper lastFrame = new LongWrapper(Time.frame());
        return thenLoop((r,t) -> {
            if(lastFrame.value == Time.frame()) return;
            loopBody.runIteration(r,t);
            lastFrame.value++;
        });
    }

    // ------------------------------------------------------------------

    public Coroutine<Void> thenForever(LoopTask loopBody) {
        return subroutine(loopBody);
    }

    public Coroutine<Void> thenForever(ParamLoopTask<? super T> loopBody) {
        return subroutine(loopBody);
    }

    // ------------------------------------------------------------------

    public Coroutine<Void> thenForever(LoopTask loopBody, float interval) {
        return thenForever(loopBody, interval, false);
    }

    public Coroutine<Void> thenForever(LoopTask loopBody, float interval, boolean realtime) {
        return thenForever((ParamLoopTask<? super T>) loopBody, interval, realtime);
    }

    public Coroutine<Void> thenForever(ParamLoopTask<? super T> loopBody, float interval) {
        return thenForever(loopBody, interval, false);
    }

    public Coroutine<Void> thenForever(ParamLoopTask<? super T> loopBody, float interval, boolean realtime) {
        return thenForever(loopBody, $ -> interval, realtime);
    }

    public Coroutine<Void> thenForever(ParamLoopTask<? super T> loopBody, Function<? super T, Float> intervalGetter, boolean realtime) {
        return thenLoop(loopBody, intervalGetter, realtime);
    }

    public Coroutine<Void> thenForever(InfiniteTimedLoop<? super T> loop, boolean realtime) {
        return thenForever(loop, loop, realtime);
    }

    // ------------------------------------------------------------------

    public Coroutine<Void> thenForeverEveryFrame(LoopTask loopBody) {
        return thenLoopEveryFrame(loopBody);
    }

    public Coroutine<Void> thenForeverEveryFrame(ParamLoopTask<? super T> loopBody) {
        return thenLoopEveryFrame(loopBody);
    }

    // ------------------------------------------------------------------

    @CheckReturnValue
    public Coroutine<T> thenWaitForNextFrame() {
        long frame = Time.frame();
        return thenWait(() -> Time.frame() != frame);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitFor(float seconds) {
        return thenWaitFor(seconds, false);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitFor(float seconds, boolean realtime) {
        return thenWaitFor(() -> seconds, realtime);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitFor(Supplier<Float> seconds, boolean realtime) {
        return thenWaitFor($ -> seconds.get(), realtime);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitFor(Function<? super T, Float> seconds, boolean realtime) {
        return thenWaitUntil(t -> seconds.apply(t) + (realtime ? Time.realTime() : Time.time()), realtime);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitUntil(float timestamp) {
        return thenWaitUntil(timestamp, false);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitUntil(float timestamp, boolean realtime) {
        return thenWaitUntil(() -> timestamp, realtime);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitUntil(Supplier<Float> timestamp, boolean realtime) {
        return thenWaitUntil($ -> timestamp.get(), realtime);
    }

    @CheckReturnValue
    public Coroutine<T> thenWaitUntil(Function<? super T, Float> timestamp, boolean realtime) {
        Supplier<Float> time = realtime ? Time::realTime : Time::time;
        FloatWrapper endTime = new FloatWrapper();
        listen(t -> endTime.value = time.get() + timestamp.apply(t));
        return thenWait(() -> endTime.value <= time.get());
    }

    @CheckReturnValue
    public Coroutine<T> thenWait(BooleanSupplier until) {
        return thenWait($ -> until.getAsBoolean());
    }

    @CheckReturnValue
    public Coroutine<T> thenWait(Predicate<? super T> until) {
        return new Coroutine<>(this, (t,r) -> { if(until.test(t)) r._return(t); });
    }

    public <R> Coroutine<R> thenWaitFor() {
        //noinspection unchecked
        return thenWaitFor(t -> (Future<? extends R>) t);
    }

    public <R> Coroutine<R> thenWaitFor(Function<? super T, ? extends Future<? extends R>> futureGetter) {
        Wrapper<Future<? extends R>> future = new Wrapper<>();
        listen(t -> future.value = futureGetter.apply(t));
        return thenWait(() -> future.value.isDone()).then(() -> future.value.get());
    }

    public <R> Coroutine<R> thenWaitFor(Future<? extends R> future) {
        return thenWaitFor(future, ($, r) -> r);
    }

    public <U,R> Coroutine<R> thenWaitFor(Future<? extends U> future, BiFunction<? super T, ? super U, ? extends R> combiner) {
        return thenWait(future::isDone).then(t -> combiner.apply(t, future.get()));
    }

    public <R> Coroutine<R> map(Function<? super T, ? extends Coroutine<R>> mapper) {
        Wrapper<Coroutine<R>> other = new Wrapper<>();
        listen(t -> other.value = mapper.apply(t));
        return new Coroutine<>(this, ($,r) -> other.value.run(r));
    }

    @CheckReturnValue
    public <U> Coroutine<Tuple2<T,U>> and(Coroutine<? extends U> other) {
        return new Coroutine<>(this, other);
    }

    public Coroutine<T> listen(Runnable listener) {
        result.then(listener);
        return this;
    }

    @Contract(value = "_->this")
    public Coroutine<T> listen(Consumer<? super T> listener) {
        result.then(listener);
        return this;
    }

    public Coroutine<T> except(Runnable handler) {
        result.except(handler);
        return this;
    }

    public Coroutine<T> except(Consumer<? super Exception> handler) {
        result.except(handler);
        return this;
    }

    @CheckReturnValue
    public boolean isRunning() {
        return !result.isDone();
    }

    @CheckReturnValue
    public boolean isStopped() {
        return result.isCanceled();
    }

    @CheckReturnValue
    public boolean isDone() {
        return result.isDone() && !result.isCanceled();
    }

    public boolean stop() {
        return result.cancel();
    }

    @CheckReturnValue
    public T result() {
        if(result.isCanceled())
            throw new IllegalStateException("Coroutine has been stopped");
        if(!result.isDone())
            throw new IllegalStateException("Coroutine is still running");
        return result.get();
    }

    @CheckReturnValue
    public Future<T> future() {
        return result;
    }


    private <R> Coroutine<R> subroutine(ParamValueLoopTask<? super T, ? extends R> task) {
        return new Coroutine<>(this, task);
    }


    private static class Return extends Error {
        Return() {
            super(null, null, false, false);
        }
    }
}
