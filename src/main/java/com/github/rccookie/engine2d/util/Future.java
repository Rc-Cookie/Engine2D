//package com.github.rccookie.engine2d.util;
//
//import java.util.function.Consumer;
//
///**
// * Describes a value that will be computed at some time in the
// * future.
// *
// * @param <V> The content type
// */
//public interface Future<V> {
//
//    /**
//     * Attempts to cancel execution of this task. This attempt will
//     * fail if the task has already completed, has already been cancelled,
//     * or could not be cancelled for some other reason. If successful,
//     * and this task has not started when {@code cancel} is called,
//     * this task should never run.
//     *
//     * <p>After this method returns, subsequent calls to {@link #isDone} will
//     * always return {@code true}. Subsequent calls to {@link #isCancelled}
//     * will always return {@code true} if this method returned {@code true}.
//     *
//     * @return {@code false} if the task could not be cancelled,
//     * typically because it has already completed normally;
//     * {@code true} otherwise
//     */
//    boolean cancel();
//
//    /**
//     * Returns {@code true} if this task was cancelled before it completed
//     * normally.
//     *
//     * @return {@code true} if this task was cancelled before it completed
//     */
//    boolean isCancelled();
//
//    /**
//     * Returns {@code true} if this task completed.
//     *
//     * Completion may be due to normal termination, an exception, or
//     * cancellation -- in all of these cases, this method will return
//     * {@code true}.
//     *
//     * @return {@code true} if this task completed
//     */
//    boolean isDone();
//
//    /**
//     * Returns the result.
//     *
//     * @return the computed result
//     * @throws IllegalStateException If the computation is not done yet or
//     *                               has been canceled
//     */
//    V get() throws IllegalStateException;
//
//    /**
//     * Sets the action to be executed when the result is received.
//     *
//     * @param action The action to perform
//     * @return This future itself
//     */
//    Future<V> then(Consumer<? super V> action);
//
//    /**
//     * Sets the action to be executed when the result is received.
//     *
//     * @param action The action to perform
//     * @return This future itself
//     */
//    default Future<V> then(Runnable action) {
//        return then($ -> action.run());
//    }
//
//    /**
//     * Sets the action to be executed when the result gets cancelled.
//     *
//     * @param handler The action to perform
//     * @return This future
//     */
//    Future<V> onCancel(Runnable handler);
//}
