package com.github.rccookie.engine2d;

import java.util.ArrayDeque;
import java.util.Deque;

import com.github.rccookie.engine2d.core.LoopExecutor;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Nullable;

/**
 * Utility class to get time information.
 */
public enum Time {

    ; // No instance

    static {
        LoopExecutor.setTimeUpdate(Time::update);
    }

    private static long lastNanos = System.nanoTime();

    private static float maxDelta = 0.05f; // Box2D probably does not like <20 fps when designed for 50
    private static Float fixedDelta = null;
    private static float timeScale = 1;

    private static long frameNanos = 20000000L;
    private static float delta = 0.02f;
    private static float time = 0;
    private static float realTime = 0;
    private static long frame = -1; // first frame should be indexed 0, updated before update

    private static final Deque<Long> lastSecondFrameNanos = new ArrayDeque<>();
    private static float lastSecondFrameNanosSpan = 0;

    /**
     * Update the time stats.
     */
    private static void update() {
        long nanos = System.nanoTime();
        frameNanos = nanos - lastNanos;
        delta = frameNanos / 1000000000f;
        lastNanos = nanos;

        realTime += delta;
        time += delta();
        frame++;

        lastSecondFrameNanos.addLast(frameNanos);
        lastSecondFrameNanosSpan += frameNanos;

        while(lastSecondFrameNanosSpan > 1000000000L)
            lastSecondFrameNanosSpan -= lastSecondFrameNanos.removeFirst();
    }

    /**
     * Returns the current time delta. This is the time of the last frame, in seconds. In
     * other words, summing up the delta every frame will exactly count the number of seconds
     * since start of summing up.
     *
     * @return The current time delta
     */
    public static float delta() {
        return timeScale * (fixedDelta == null ? Math.min(delta, maxDelta) : fixedDelta);
    }

    /**
     * Returns the current time delta, unaffected by delta time limit and timescale.
     *
     * @return The current real time delta
     * @see #delta()
     */
    public static float realDelta() {
        return delta;
    }

    /**
     * Returns the length of the last frame, in nanoseconds.
     *
     * @return The length of the last frame
     */
    public static long frameNanos() {
        return frameNanos;
    }

    /**
     * Returns the time in seconds since application start.
     *
     * @return The current time in seconds
     */
    public static float time() {
        return time;
    }

    /**
     * Returns the real current time since application start, unaffected by delta limit
     * and timescale.
     *
     * @return The real time in seconds
     */
    public static float realTime() {
        return realTime;
    }

    /**
     * Returns the current frame index.
     *
     * @return The index of the current frame
     */
    public static long frame() {
        return Math.max(0, frame); // In case it's called before the first update
    }

    /**
     * Returns the number of frames rendered in the last second.
     *
     * @return The current fps
     */
    public static int fps() {
        return lastSecondFrameNanos.size();
    }

    /**
     * Returns the current timescale.
     *
     * @return The current timescale
     */
    public static float getTimeScale() {
        return timeScale;
    }

    /**
     * Sets the timescale for delta and overall time progression. This is intended for
     * slow motion and similar.
     *
     * @param timeScale The timescale to set
     */
    public static void setTimeScale(float timeScale) {
        Time.timeScale = timeScale;
    }

    /**
     * Returns the maximum delta time.
     *
     * @return The current delta limit
     */
    public static float getMaxDelta() {
        return maxDelta;
    }

    /**
     * Sets the delta limit per frame. This is intended to keep the deltas in a reasonable
     * range and prevent unexpected behavior on very low framerates. For example, the physics
     * engine may not be able to operate properly on very big time steps. By default, this is
     * set to {@code 0.05}, meaning that on a framerate lower than 20 fps the application will
     * run in slow motion to match that internal framerate.
     *
     * @param maxDelta The max delta to set
     */
    public static void setMaxDelta(float maxDelta) {
        Arguments.checkExclusive(maxDelta, 0d, null);
        Time.maxDelta = maxDelta;
    }

    /**
     * Returns the fixed delta that is currently set. {@code null} indicates that no fixed
     * time delta is set and the dynamic one is used.
     *
     * @return The currently used fixed time delta
     */
    public static Float getFixedDelta() {
        return fixedDelta;
    }

    /**
     * Returns whether a fixed delta is currently used instead of a dynamically measured time
     * delta.
     *
     * @return Whether a fixed time delta is used
     */
    public static boolean isFixedDelta() {
        return fixedDelta != null;
    }

    /**
     * Sets the fixed time delta to the specified value. {@code null} means to use the
     * dynamically measured time delta. It is recommended to use a dynamic time delta for
     * a smooth experience.
     *
     * @param fixedDelta The fixed time delta to set
     */
    public static void setFixedDelta(@Nullable Float fixedDelta) {
        Time.fixedDelta = fixedDelta;
    }
}
