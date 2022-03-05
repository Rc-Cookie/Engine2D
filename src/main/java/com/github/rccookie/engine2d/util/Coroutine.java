package com.github.rccookie.engine2d.util;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Time;

/**
 * A piece of code to be executed asynchronously.
 *
 * @param <T> The return type
 */
@FunctionalInterface
@Deprecated
public interface Coroutine<T> {

    /**
     * Runs the coroutine. This method should regularly call the
     * {@link #yield()} method to ensure other threads can execute, too.
     *
     * @return The result
     */
    T run();


    /**
     * Yields the current thread.
     */
    @Deprecated
    static void yield() {
//        Application.yield();
    }

    /**
     * Waits the specified number of seconds.
     *
     * @param seconds The time to wait
     */
    static void wait(double seconds) {
        long millis = (long)(seconds * 1000);
        int nanos = (int)((seconds * 1000000000) % 1000000000);
        Application.getImplementation().sleep(millis, nanos);
    }

    /**
     * Waits until a specified time stamp, in milliseconds.
     *
     * @param time The target time
     */
    static void waitUntil(long time) {
        Application.getImplementation().sleep(time - System.currentTimeMillis(), 0);
    }

    /**
     * Wait until a specified time stamp, in seconds measured in application
     * local time.
     *
     * @param time The target time
     * @see Time#time()
     */
    static void waitUntil(float time) {
        // TODO: Implement
    }

    /**
     * Waits until the next frame has started.
     */
    static void waitForNextFrame() {
        Application.getImplementation().sleepUntilNextFrame();
    }
}
