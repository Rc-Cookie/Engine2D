package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.LoopExecutor;
import com.github.rccookie.util.ArgumentOutOfRangeException;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Time {

    private Time() {
        throw new UnsupportedOperationException();
    }

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

    public static float delta() {
        return timeScale * (fixedDelta == null ? Math.min(delta, maxDelta) : fixedDelta);
    }

    public static float realDelta() {
        return delta;
    }

    public static long frameNanos() {
        return frameNanos;
    }

    public static float time() {
        return time;
    }

    public static float realTime() {
        return realTime;
    }

    public static long frame() {
        return Math.max(0, frame); // In case it's called before the first update
    }

    public static int fps() {
        return lastSecondFrameNanos.size();
    }

    public static float getTimeScale() {
        return timeScale;
    }

    public static void setTimeScale(float timeScale) {
        Time.timeScale = timeScale;
    }

    public static float getMaxDelta() {
        return maxDelta;
    }

    public static void setMaxDelta(float maxDelta) {
        if(maxDelta <= 0)
            throw new ArgumentOutOfRangeException(maxDelta, 0.00000000001, null);
        Time.maxDelta = maxDelta;
    }

    public static Float getFixedDelta() {
        return fixedDelta;
    }

    public static boolean isFixedDelta() {
        return fixedDelta != null;
    }

    public static void setFixedDelta(Float fixedDelta) {
        Time.fixedDelta = fixedDelta;
    }
}
