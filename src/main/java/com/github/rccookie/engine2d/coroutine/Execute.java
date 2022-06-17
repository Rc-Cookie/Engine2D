package com.github.rccookie.engine2d.coroutine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Future;

/**
 * Utility class to execute tasks repeatedly or delayed.
 */
public enum Execute {

    ; // No instance

    static final Collection<Coroutine<?>> COROUTINES = new ArrayList<>();

    private static final LocalExecutionManager EXECUTOR = new LocalExecutionManager(() -> true);

    static {
        Application.earlyUpdate.add(Execute::runTasks);
        Application.lateUpdate.add(Execute::runTasks);
    }



    /**
     * Runs all pending coroutines.
     */
    private static void runTasks() {
        Coroutine<?>[] coroutines;
        synchronized(COROUTINES) {
            coroutines = COROUTINES.toArray(Coroutine<?>[]::new);
        }
        for(Coroutine<?> coroutine : coroutines) {
            try {
                coroutine.run();
            } catch(Exception e) {
                Console.error("Exception in coroutine:");
                Console.error(e);
            }
        }
    }



    public static Coroutine<Void> coroutine() {
        return EXECUTOR.coroutine();
    }

    public static <T> Coroutine<T> coroutine(T startValue) {
        return EXECUTOR.coroutine(startValue);
    }



    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public static Coroutine<Void> repeating(LoopTask task, float delay) {
        return EXECUTOR.repeating(task, delay);
    }

    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public static Coroutine<Void> repeating(LoopTask task, float delay, float initialDelay) {
        return EXECUTOR.repeating(task, delay, initialDelay);
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
    public static Coroutine<Void> repeating(LoopTask task, float delay, float initialDelay, boolean realTime) {
        return EXECUTOR.repeating(task, delay, initialDelay, realTime);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public static <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay) {
        return EXECUTOR.repeating(task, delay);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public static <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay, float initialDelay) {
        return EXECUTOR.repeating(task, delay, initialDelay);
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
    public static <T> Coroutine<T> repeating(ValueLoopTask<? extends T> task, float delay, float initialDelay, boolean realTime) {
        return EXECUTOR.repeating(task, delay, initialDelay, realTime);
    }

    public static <T> Coroutine<T> everyFrame(ValueLoopTask<? extends T> task) {
        return EXECUTOR.everyFrame(task);
    }

    /**
     * Executes the given task on the main thread. If this is called from the main thread,
     * the task will be executed immediately, otherwise it will be executed within this or
     * the next frame.
     *
     * @param task The task to run
     */
    public static Coroutine<Void> synced(Task task) {
        if(Application.getImplementation().isMainThread()) {
            try {
                task.run();
                return EXECUTOR.coroutine();
            } catch(Exception e) {
                Console.error("Exception executing task:");
                Console.error(e);
                Coroutine<Void> c = EXECUTOR.coroutine();
                c.stop();
                return c;
            }
        }
        else return Execute.later(task);
    }

    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     */
    public static Coroutine<Void> later(Task task) {
        return EXECUTOR.later(task);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     */
    public static Coroutine<Void> later(Task task, float delay) {
        return EXECUTOR.later(task, delay);
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
    public static Coroutine<Void> later(Task task, float delay, boolean realTime) {
        return EXECUTOR.later(task, delay, realTime);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     */
    public static Coroutine<Void> nextFrame(Task task) {
        return EXECUTOR.nextFrame(task);
    }

    /**
     * Executes the given task when the specified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     */
    public static Coroutine<Void> when(Task task, BooleanSupplier requirement) {
        return EXECUTOR.when(task, requirement);
    }



    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     * @return A future referring to the result of the task
     */
    public static <R> Coroutine<R> later(ValueTask<? extends R> task) {
        return EXECUTOR.later(task);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Task)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @return A future referring to the result of the task
     */
    public static <R> Coroutine<R> later(ValueTask<? extends R> task, float delay) {
        return EXECUTOR.later(task, delay);
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
    public static <R> Coroutine<R> later(ValueTask<? extends R> task, float delay, boolean realTime) {
        return EXECUTOR.later(task, delay, realTime);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     * @return A future referring to the result of the task
     */
    public static <R> Coroutine<R> nextFrame(ValueTask<? extends R> task) {
        return EXECUTOR.nextFrame(task);
    }

    /**
     * Executes the given task when the specified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     * @return A future referring to the result of the task
     */
    public static <R> Coroutine<R> when(ValueTask<? extends R> task, BooleanSupplier requirement) {
        return EXECUTOR.when(task, requirement);
    }

    public static <R> Coroutine<R> when(Future<? extends R> future) {
        return EXECUTOR.when(future);
    }


    public static <T> Coroutine<T> loop(ValueLoopTask<? extends T> loopTask) {
        return EXECUTOR.loop(loopTask);
    }
}
