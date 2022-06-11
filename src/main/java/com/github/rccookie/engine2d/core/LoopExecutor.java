package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.core.stats.Bottleneck;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.util.Arguments;

public abstract class LoopExecutor {

    private static final float DELAY_FACTOR = 0.999925f;

    private static Runnable timeUpdate;

    private Bottleneck bottleneck = Bottleneck.FPS_CAP;

    private long iterationDelay = 10000000L;
//    private long delayOffset = 0;
    private long nextFrameStartTarget = System.nanoTime();

    private final boolean sleepingSupported = Application.getImplementation().supportsSleeping();

    private final Object waitLock = new Object();


    public long getIterationDelay() {
        return iterationDelay;
    }

    public float getFps() {
        return 1000f / iterationDelay;
    }

    public void setFps(float fps) {
        iterationDelay = fps <= 0 ? 0 : (long) (1000000000 / (fps+0.5f));
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while(true) runIteration();
    }

    public void runIteration() {

        long time;
//        if(nextFrameStartTarget - (time = System.nanoTime()) > 2000000 && (sleepingSupported || Application.FORCE_FPS_CAP))
//            Application.getImplementation().sleep((nextFrameStartTarget - time - 2000000) / 1000000, 0);
//        while(System.nanoTime() < nextFrameStartTarget) Thread.yield();
//        while((time = System.nanoTime()) < nextFrameStartTarget);

        if(nextFrameStartTarget - (time = System.nanoTime()) > 0 && (sleepingSupported || Application.FORCE_FPS_CAP))
            Application.getImplementation().sleep((nextFrameStartTarget - time) / 1000000, 0);

        long expectedDuration = getIterationDelay();
        // Try to recover at most the frames lost in the last half second
        long frameStart = expectedDuration == 0 ? time : Num.max(nextFrameStartTarget, time - 500000000);
        nextFrameStartTarget = frameStart + (long) (expectedDuration * DELAY_FACTOR);

        Bottleneck potentialBottleneck = runIterationUntimed();

        if(System.nanoTime() < nextFrameStartTarget) setBottleneck(Bottleneck.FPS_CAP);
        else setBottleneck(potentialBottleneck);
    }

    protected abstract Bottleneck runIterationUntimed();


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
