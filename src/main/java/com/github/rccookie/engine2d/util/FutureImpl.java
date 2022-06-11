//package com.github.rccookie.engine2d.util;
//
//import java.util.function.Consumer;
//
//import org.jetbrains.annotations.Nullable;
//
///**
// * General-purpose implementation of {@link Future}.
// *
// * @param <T> The content type
// */
//public class FutureImpl<T> implements Future<T> {
//
//    /**
//     * Whether the computation has been cancelled.
//     */
//    private boolean canceled = false;
//    /**
//     * Whether the computation has ended, either successful
//     * or unsuccessful.
//     */
//    private boolean done = false;
//    /**
//     * The result value.
//     */
//    private T value = null;
//    /**
//     * The function to be called when the result is available.
//     */
//    private Consumer<? super T> then = null;
//    /**
//     * The function to be called when the result gets cancelled.
//     */
//    private Runnable onCancel = null;
//
//
//    /**
//     * Sets the result of the future.
//     *
//     * @param value The result to set
//     * @throws IllegalStateException If the result is already set or
//     *                               the future has been cancelled
//     */
//    public void setValue(T value) {
//        if(isDone()) throw new IllegalStateException("The value cannot be set because the computation is already done");
//        this.value = value;
//        done = true;
//        if(then != null)
//            then.accept(value);
//    }
//
//    @Override
//    public boolean cancel() {
//        boolean out = !canceled && !done;
//        canceled = done = true;
//        if(out && onCancel != null) onCancel.run();
//        return out;
//    }
//
//    @Override
//    public boolean isCancelled() {
//        return canceled;
//    }
//
//    @Override
//    public boolean isDone() {
//        return done;
//    }
//
//    @Override
//    public T get() throws IllegalStateException {
//        if(!done) throw new IllegalStateException("Result is not yet computed");
//        if(canceled) throw new IllegalStateException("Execution has been canceled");
//        return value;
//    }
//
//    @Override
//    public Future<T> then(@Nullable Consumer<? super T> action) {
//        then = action;
//        if(action != null && done && !canceled)
//            action.accept(value);
//        return this;
//    }
//
//    @Override
//    public Future<T> onCancel(@Nullable Runnable action) {
//        onCancel = action;
//        if(onCancel != null && canceled)
//            action.run();
//        return this;
//    }
//}
