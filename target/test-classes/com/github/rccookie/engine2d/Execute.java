package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.util.Future;
import com.github.rccookie.engine2d.util.FutureImpl;
import com.github.rccookie.util.Console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class Execute {

    private static final Collection<ExecutionTask<Object>> TASKS = new ArrayList<>();
    private static final Collection<RepeatingExecutionTask> REPEATING_TASKS = new ArrayList<>();

    static {
        Application.earlyUpdate.add(Execute::runTasks);
        Application.update.add(Execute::runAllTasks);
        repeating(() -> Console.custom("FPS", Time.fps()), 1, 1, true);
    }



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

    @SuppressWarnings("unchecked")
    private static void runTasks() {
        ExecutionTask<Object>[] tasks;
        synchronized (TASKS) {
            tasks = TASKS.toArray(new ExecutionTask[0]);
        }
        for(ExecutionTask<Object> task : tasks) {
            if(task.result.isCancelled()) {
                synchronized (TASKS) {
                    TASKS.remove(task);
                }
            }
            else if(task.requirement.getAsBoolean()) {
                task.result.setValue(task.action.get());
                synchronized (TASKS) {
                    TASKS.remove(task);
                }
            }
        }
    }



    public static void repeating(Runnable task, float delay) {
        Execute.repeating(task, delay, 0);
    }

    public static void repeating(Runnable task, float delay, float initialDelay) {
        Execute.repeating(task, delay, initialDelay, false);
    }

    public static void repeating(Runnable task, float delay, float initialDelay, boolean realTime) {
        Execute.repeating(() -> {
            task.run();
            return true;
        }, delay, initialDelay, realTime);
    }

    public static void repeating(BooleanSupplier task, float delay) {
        Execute.repeating(task, delay, 0);
    }

    public static void repeating(BooleanSupplier task, float delay, float initialDelay) {
        Execute.repeating(task, delay, initialDelay, false);
    }

    public static void repeating(BooleanSupplier task, float delay, float initialDelay, boolean realTime) {
        RepeatingExecutionTask executionTask = new RepeatingExecutionTask(task, new BooleanSupplier() {
            float nextTime = (realTime ? Time.realTime() : Time.time()) + initialDelay;
            @Override
            public boolean getAsBoolean() {
                if((realTime ? Time.realTime() : Time.time()) < nextTime) return false;
                nextTime += delay;
                return true;
            }
        });
        synchronized (REPEATING_TASKS) {
            REPEATING_TASKS.add(executionTask);
        }
    }

    public static void later(Runnable task) {
        Execute.later(task, 0);
    }

    public static void later(Runnable task, float delay) {
        Execute.later(task, delay, false);
    }

    public static void later(Runnable task, float delay, boolean realTime) {
        Execute.later(() -> {
            task.run();
            return null;
        }, delay, realTime);
    }

    public static void nextFrame(Runnable task) {
        Execute.nextFrame(() -> {
            task.run();
            return null;
        });
    }

    public static void when(Runnable task, BooleanSupplier requirement) {
        Execute.when(() -> {
            task.run();
            return null;
        }, requirement);
    }



    public static <R> Future<R> later(Supplier<R> task) {
        return Execute.when(task, () -> true);
    }

    public static <R> Future<R> later(Supplier<R> task, float delay) {
        return Execute.later(task, delay, false);
    }

    public static <R> Future<R> later(Supplier<R> task, float delay, boolean realTime) {
        float executionTime = (realTime ? Time.realTime() : Time.time()) + delay;
        return realTime ?
                Execute.when(task, () -> Time.realTime() >= executionTime) :
                Execute.when(task, () -> Time.time() >= executionTime);
    }

    public static <R> Future<R> nextFrame(Supplier<R> task) {
        long frame = Time.frame();
        return Execute.when(task, () -> Time.frame() > frame);
    }

    @SuppressWarnings("unchecked")
    public static <R> Future<R> when(Supplier<R> task, BooleanSupplier requirement) {
        ExecutionTask<R> executionTask = new ExecutionTask<>(task, requirement);
        synchronized (TASKS) {
            TASKS.add((ExecutionTask<Object>) executionTask);
        }
        return executionTask.result;
    }



    private Execute() {
        throw new UnsupportedOperationException();
    }

    static void init() { }


    private static final class ExecutionTask<T> {

        public final Supplier<T> action;
        public final BooleanSupplier requirement;
        public final FutureImpl<T> result = new FutureImpl<>();

        private ExecutionTask(Supplier<T> action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }

    private static final class RepeatingExecutionTask {
        public final BooleanSupplier action;
        public final BooleanSupplier requirement;

        private RepeatingExecutionTask(BooleanSupplier action, BooleanSupplier requirement) {
            this.action = action;
            this.requirement = requirement;
        }
    }
}
