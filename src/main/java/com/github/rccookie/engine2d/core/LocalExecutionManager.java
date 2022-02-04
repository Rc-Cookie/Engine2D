package com.github.rccookie.engine2d.core;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.util.Future;

public class LocalExecutionManager {

    private final BooleanSupplier isExecutionPossible;

    public LocalExecutionManager(BooleanSupplier isExecutionPossible) {
        this.isExecutionPossible = isExecutionPossible;
    }



    public void repeating(Runnable task, float delay) {
        Execute.repeating(new LocalTask(task), delay);
    }

    public void repeating(Runnable task, float delay, float initialDelay) {
        Execute.repeating(new LocalTask(task), delay, initialDelay);
    }

    public void repeating(Runnable task, float delay, float initialDelay, boolean realTime) {
        Execute.repeating(new LocalTask(task), delay, initialDelay, realTime);
    }

    public void repeating(BooleanSupplier task, float delay) {
        Execute.repeating(new LocalBoolTask(task), delay);
    }

    public void repeating(BooleanSupplier task, float delay, float initialDelay) {
        Execute.repeating(new LocalBoolTask(task), delay, initialDelay);
    }

    public void repeating(BooleanSupplier task, float delay, float initialDelay, boolean realTime) {
        Execute.repeating(new LocalBoolTask(task), delay, initialDelay, realTime);
    }



    public void later(Runnable task) {
        later(task, 0);
    }

    public void later(Runnable task, float delay) {
        later(task, delay, false);
    }

    public void later(Runnable task, float delay, boolean realTime) {
        later(() -> {
            task.run();
            return null;
        }, delay, realTime);
    }

    public void nextFrame(Runnable task) {
        nextFrame(() -> {
            task.run();
            return null;
        });
    }

    public void when(Runnable task, BooleanSupplier requirement) {
        Execute.when(() -> {
            task.run();
            return null;
        }, requirement);
    }



    public <R> Future<R> later(Supplier<R> task) {
        return when(task, () -> true);
    }

    public <R> Future<R> later(Supplier<R> task, float delay) {
        return later(task, delay, false);
    }

    public <R> Future<R> later(Supplier<R> task, float delay, boolean realTime) {
        float executionTime = (realTime ? Time.realTime() : Time.time()) + delay;
        return realTime ?
                Execute.when(task, () -> isExecutionPossible.getAsBoolean() && Time.realTime() >= executionTime) :
                Execute.when(task, () -> isExecutionPossible.getAsBoolean() && Time.time() >= executionTime);
    }

    public <R> Future<R> nextFrame(Supplier<R> task) {
        long frame = Time.frame();
        return Execute.when(task, () -> isExecutionPossible.getAsBoolean() && Time.frame() > frame);
    }

    public <R> Future<R> when(Supplier<R> task, BooleanSupplier requirement) {
        return Execute.when(task, () -> isExecutionPossible.getAsBoolean() && requirement.getAsBoolean());
    }



    private class LocalTask implements Runnable {

        private final Runnable task;

        LocalTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            if(isExecutionPossible.getAsBoolean())
                task.run();
        }
    }

    private class LocalBoolTask implements BooleanSupplier {

        private final BooleanSupplier task;

        LocalBoolTask(BooleanSupplier task) {
            this.task = task;
        }

        @Override
        public boolean getAsBoolean() {
            return isExecutionPossible.getAsBoolean() && task.getAsBoolean();
        }
    }
}
