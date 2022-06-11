package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.github.rccookie.engine2d.coroutine.Coroutine;
import com.github.rccookie.engine2d.coroutine.Wait;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.Future;
import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.NoWaitFutureImpl;

/**
 * Utility class to execute tasks repeatedly or delayed.
 */
public enum Execute {

    ; // No instance

    /**
     * Pending one-time tasks.
     */
    private static final Collection<ExecutionTask<Object>> TASKS = new ArrayList<>();

    /**
     * Pending repeating tasks.
     */
    private static final Collection<RepeatingExecutionTask> REPEATING_TASKS = new ArrayList<>();

    static {
        Application.earlyUpdate.add(Execute::runTasks);
        Application.lateUpdate.add(Execute::runAllTasks);
        repeating(() -> Console.write("FPS", Time.fps()), 1, 1, true);
    }


    /**
     * Runs all pending tasks if their requirements are met.
     */
    private static void runAllTasks() {
        runTasks();

        RepeatingExecutionTask[] tasks;
        synchronized (REPEATING_TASKS) {
            tasks = REPEATING_TASKS.toArray(new RepeatingExecutionTask[0]);
        }

        for(RepeatingExecutionTask task : tasks) {
            if(task.requirement.getAsBoolean() && !task.action.getAsBoolean()) {
                synchronized (REPEATING_TASKS) {
                    REPEATING_TASKS.remove(task);
                }
            }
        }
    }

    /**
     * Runs all pending one-time tasks if their requirements are met.
     */
    @SuppressWarnings("unchecked")
    private static void runTasks() {
        ExecutionTask<Object>[] tasks;
        synchronized (TASKS) {
            tasks = TASKS.toArray(new ExecutionTask[0]);
        }
        for(ExecutionTask<Object> task : tasks) {
            if(task.result.isCanceled()) {
                synchronized (TASKS) {
                    TASKS.remove(task);
                }
            }
            else if(task.requirement.getAsBoolean()) {
                synchronized (TASKS) {
                    TASKS.remove(task);
                }
                try {
                    task.result.complete(task.action.get());
                } catch(Exception e) {
                    task.result.fail(e);
                    System.err.println("Exception occurred while executing Execute task:");
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public static void repeating(Runnable task, float delay) {
        Execute.repeating(task, delay, 0);
    }

    /**
     * Executes the given task repeatedly in the given interval.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public static void repeating(Runnable task, float delay, float initialDelay) {
        Execute.repeating(task, delay, initialDelay, false);
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
    public static void repeating(Runnable task, float delay, float initialDelay, boolean realTime) {
        Execute.repeating(() -> {
            task.run();
            return true;
        }, delay, initialDelay, realTime);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     */
    public static void repeating(BooleanSupplier task, float delay) {
        Execute.repeating(task, delay, 0);
    }

    /**
     * Executes the given task repeatedly in the given interval while the return value
     * is {@code true}.
     *
     * @param task The task to run
     * @param delay The execution interval, in seconds
     * @param initialDelay The delay until the first execution, in seconds
     */
    public static void repeating(BooleanSupplier task, float delay, float initialDelay) {
        Execute.repeating(task, delay, initialDelay, false);
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
    public static void repeating(BooleanSupplier task, float delay, float initialDelay, boolean realTime) {
        RepeatingExecutionTask executionTask = new RepeatingExecutionTask(task, new BooleanSupplier() {
            float nextTime = (realTime ? Time.realTime() : Time.time()) + initialDelay;
            @Override
            public boolean getAsBoolean() {
                if((realTime ? Time.realTime() : Time.time()) < nextTime) return false;
                if(realTime)
                    nextTime = Num.max(Time.realTime() - 1, nextTime);
                nextTime += delay;
                return true;
            }
        });
        synchronized (REPEATING_TASKS) {
            REPEATING_TASKS.add(executionTask);
        }
    }

    /**
     * Executes the given task on the main thread. If this is called from the main thread,
     * the task will be executed immediately, otherwise it will be executed within this or
     * the next frame.
     *
     * @param task The task to run
     */
    public static void synced(Runnable task) {
        if(Application.getImplementation().isMainThread()) {
            try {
                task.run();
            } catch(Exception e) {
                Console.error("Exception executing task:");
                Console.error(e);
            }
        }
        else Execute.later(task);
    }

    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     */
    public static void later(Runnable task) {
        Execute.later(task, 0);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Runnable)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     */
    public static void later(Runnable task, float delay) {
        Execute.later(task, delay, false);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Runnable)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     */
    public static void later(Runnable task, float delay, boolean realTime) {
        Execute.later(() -> {
            task.run();
            return null;
        }, delay, realTime);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     */
    public static void nextFrame(Runnable task) {
        Execute.nextFrame(() -> {
            task.run();
            return null;
        });
    }

    /**
     * Executes the given task when the spcified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     */
    public static void when(Runnable task, BooleanSupplier requirement) {
        Execute.when(() -> {
            task.run();
            return null;
        }, requirement);
    }



    /**
     * Executes the given task <i>soon</i>. This means it will not be executed immediately, but
     * (if called during the update event) before the next frame.
     *
     * @param task The task to execute
     * @return A future referring to the result of the task
     */
    public static <R> Future<R> later(Supplier<R> task) {
        return Execute.when(task, () -> true);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Runnable)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @return A future referring to the result of the task
     */
    public static <R> Future<R> later(Supplier<R> task, float delay) {
        return Execute.later(task, delay, false);
    }

    /**
     * Executes the given task after the given delay. A delay of 0 will not cause immediate execution
     * but will behave equivalently to a call to {@link #later(Runnable)}.
     *
     * @param task The task to run
     * @param delay The delay, in seconds
     * @param realTime Whether real time should be used or the time
     *                 speed set with {@link Time#setTimeScale(float)}
     *                 (which is the default)
     * @return A future referring to the result of the task
     */
    public static <R> Future<R> later(Supplier<R> task, float delay, boolean realTime) {
        float executionTime = (realTime ? Time.realTime() : Time.time()) + delay;
        return realTime ?
                Execute.when(task, () -> Time.realTime() >= executionTime) :
                Execute.when(task, () -> Time.time() >= executionTime);
    }

    /**
     * Executes the given task on the next frame.
     *
     * @param task The task to run
     * @return A future referring to the result of the task
     */
    public static <R> Future<R> nextFrame(Supplier<R> task) {
        long frame = Time.frame();
        return Execute.when(task, () -> Time.frame() != frame);
    }

    /**
     * Executes the given task when the spcified requirement is met.
     *
     * @param task The task to run
     * @param requirement The requirement for the task to run
     * @return A future referring to the result of the task
     */
    @SuppressWarnings("unchecked")
    public static <R> Future<R> when(Supplier<R> task, BooleanSupplier requirement) {
        ExecutionTask<R> executionTask = new ExecutionTask<>(task, requirement);
        synchronized (TASKS) {
            TASKS.add((ExecutionTask<Object>) executionTask);
        }
        return executionTask.result;
    }


    /**
     * Starts the given coroutine.
     *
     * @param coroutine The coroutine to start
     * @param <T> The return type of the coroutine
     * @return A future referring to the result of the coroutine
     */
    public static <T> Future<T> coroutine(Coroutine<T> coroutine) {
        runCoroutine(coroutine, coroutine.runNextChunk());
        return coroutine.getResult();
    }

    /**
     * Run a step of the coroutine.
     *
     * @param coroutine The coroutine to run
     * @param wait The wait condition to wait for
     */
    private static void runCoroutine(Coroutine<?> coroutine, Wait wait) {
        if(wait == null) return;
        Execute.when(() -> runCoroutine(coroutine, coroutine.runNextChunk()), wait::isDone);
    }


    /**
     * Needed to invoke class initialization. ClassLoader may not be available.
     */
    static void init() { }


    /**
     * One-time execution task.
     * @param <T> Result type
     */
    private static final class ExecutionTask<T> {

        public final Supplier<T> action;
        public final BooleanSupplier requirement;
        public final FutureImpl<T> result = new NoWaitFutureImpl<>();

        private ExecutionTask(Supplier<T> action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }

    /**
     * Repeating execution task with requirement.
     */
    private static final class RepeatingExecutionTask {
        public final BooleanSupplier action;
        public final BooleanSupplier requirement;

        private RepeatingExecutionTask(BooleanSupplier action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }
}
