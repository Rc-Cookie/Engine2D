package com.github.rccookie.engine2d.core.stats;

public final class PerformanceStats {

    public final float frameDuration;

    public final float renderPrepDuration;

    public final float renderDuration;

    public final int drawCount;

    public final int poolSize;

    public final float updateDuration;

    public final float physicsDuration;

    public final float uiUpdateDuration;

    public final float otherDuration;

    public final Bottleneck bottleneck;

    public final boolean parallel;

    public PerformanceStats(float frameDuration, float renderPrepDuration, float renderDuration, int drawCount, int poolSize,
                            float updateDuration,float physicsDuration, float uiUpdateDuration, Bottleneck bottleneck, boolean parallel) {
        this.frameDuration = frameDuration;
        this.renderPrepDuration = renderPrepDuration;
        this.renderDuration = renderDuration;
        this.drawCount = drawCount;
        this.poolSize = poolSize;
        this.updateDuration = updateDuration;
        this.physicsDuration = physicsDuration;
        this.uiUpdateDuration = uiUpdateDuration;
        this.otherDuration = frameDuration - (updateDuration + physicsDuration + uiUpdateDuration);
        this.bottleneck = bottleneck;
        this.parallel = parallel;
    }
}
