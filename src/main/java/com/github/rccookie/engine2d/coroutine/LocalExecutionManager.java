package com.github.rccookie.engine2d.coroutine;

import java.util.function.BooleanSupplier;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.util.Future;

public class LocalExecutionManager {

    private final BooleanSupplier isExecutionPossible;

    public LocalExecutionManager(BooleanSupplier isExecutionPossible) {
        this.isExecutionPossible = isExecutionPossible;
    }



    public Coroutine<Void> coroutine() {
        return new Coroutine<>(isExecutionPossible);
    }

    public <T> Coroutine<T> coroutine(T startValue) {
        return coroutine().then(() -> startValue);
    }



    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public Coroutine<Void> repeating(LoopTask task, float delay) {
        return coroutine().thenForever(task, delay);
    }

    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public Coroutine<Void> repeating(LoopTask task, float delay, float initialDelay) {
        return repeating(task, delay, initialDelay, false);
    }

    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     */
    public Coroutine<Void> repeating(LoopTask task, float delay, float initialDelay, boolean realTime) {
        return coroutine().thenWaitFor(initialDelay, realTime).thenForever(task, delay, realTime);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay) {
        return coroutine().thenLoop(task, delay);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay, float initialDelay) {
        return repeating(task, delay, initialDelay, false);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     */
    public <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay, float initialDelay, boolean realTime) {
        return coroutine().thenWaitFor(initialDelay, realTime).thenLoop(task, delay, realTime);
    }

    public <T> Coroutine<T> everyFrame(ValueLoopTask<? extends T> task) {
        return coroutine().thenLoopEveryFrame(task);
    }

    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     */
    public Coroutine<Void> later(Task task) {
        return coroutine().thenRun(task);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     */
    public Coroutine<Void> later(Task task, float delay) {
        return later(task, delay, false);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     */
    public Coroutine<Void> later(Task task, float delay, boolean realTime) {
        return coroutine().thenWaitFor(delay, realTime).thenRun(task);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     */
    public Coroutine<Void> nextFrame(Task task) {
        return coroutine().thenWaitForNextFrame().thenRun(task);
    }

    /**
     * Executes the given task when the specified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     */
    public Coroutine<Void> when(Task task, BooleanSupplier requirement) {
        return coroutine().thenWait(requirement).thenRun(task);
    }



    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     * @return A future referring to the result of the task
     */
    public <R> Coroutine<R> later(ValueTask<? extends R> task) {
        return coroutine().then(task);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @return A future referring to the result of the task
     */
    public <R> Coroutine<R> later(ValueTask<? extends R> task, float delay) {
        return later(task, delay, false);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     * @return A future referring to the result of the task
     */
    public <R> Coroutine<R> later(ValueTask<? extends R> task, float delay, boolean realTime) {
        return coroutine().thenWaitFor(delay, realTime).then(task);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     * @return A future referring to the result of the task
     */
    public <R> Coroutine<R> nextFrame(ValueTask<? extends R> task) {
        return coroutine().thenWaitForNextFrame().then(task);
    }

    /**
     * Executes the given task when the specified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     * @return A future referring to the result of the task
     */
    public <R> Coroutine<R> when(ValueTask<? extends R> task, BooleanSupplier requirement) {
        return coroutine().thenWait(requirement).then(task);
    }

    public <R> Coroutine<R> when(Future<? extends R> future) {
        return coroutine().thenWaitFor(future);
    }


    public <T> Coroutine<T> loop(ValueLoopTask<? extends T> loop) {
        return coroutine().thenLoop(loop);
    }
}
