package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.util.ArgumentOutOfRangeException;

public abstract class LoopExecutor {

    private static Runnable timeUpdate;

    private long iterationDelay = 10000000L;

    public long getIterationDelay() {
        return iterationDelay;
    }

    public float getFps() {
        if(iterationDelay == 0) return 1001;
        return 1000f / iterationDelay;
    }

    public void setFps(float fps) {
        if(fps < 0.1) throw new ArgumentOutOfRangeException(fps, 0.1, null);
        iterationDelay = fps > 1000 ? 0 : (long) (1000000000 / fps);
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while(true) runIteration();
    }

    public abstract void runIteration();


    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    void updateTime() {
        if(timeUpdate == null) Time.time();
        timeUpdate.run();
    }

    public static void setTimeUpdate(Runnable update) {
        if(timeUpdate != null) throw new IllegalStateException();
        timeUpdate = update;
    }
}
