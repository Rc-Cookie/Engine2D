package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.core.stats.Bottleneck;
import com.github.rccookie.util.Arguments;

public abstract class LoopExecutor {

    private Bottleneck bottleneck = Bottleneck.FPS_CAP;

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
        Arguments.checkRange(fps, 0.1f, null);
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

    protected void setBottleneck(Bottleneck bottleneck) {
        this.bottleneck = Arguments.checkNull(bottleneck);
    }

    public Bottleneck getBottleneck() {
        return bottleneck;
    }

    public abstract long getFrameDuration();

    public abstract boolean isParallel();

    public static void setTimeUpdate(Runnable update) {
        if(timeUpdate != null) throw new IllegalStateException();
        timeUpdate = update;
    }
}
