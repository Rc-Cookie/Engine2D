package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.core.stats.Bottleneck;

public class SequentialLoopExecutor extends LoopExecutor {

    private long frameDuration = 0;

    private long nextFrameStartTarget = System.nanoTime();

    // TODO update bottleneck

    @Override
    public Bottleneck runIterationUntimed() {
        long frameStart = System.nanoTime();

        updateTime();
        Application.earlyUpdate.invoke();

        Camera camera = Camera.getActive();

        camera.update.invoke();
        Application.lateUpdate.invoke();

        long renderStart = System.nanoTime();
        camera.prepareRender();
        camera.render();
        long renderDuration = System.nanoTime() - renderStart;

        long delay = getIterationDelay();
        frameDuration = System.nanoTime() - frameStart;

        if(frameDuration - renderDuration < delay && renderDuration < delay)
            return frameDuration < renderDuration ? Bottleneck.THREADING_UPDATE : Bottleneck.THREADING_RENDER;
        else return frameDuration < renderDuration ? Bottleneck.UPDATE : Bottleneck.RENDERING;
    }

    @Override
    public long getFrameDuration() {
        return frameDuration;
    }

    @Override
    public boolean isParallel() {
        return false;
    }
}
