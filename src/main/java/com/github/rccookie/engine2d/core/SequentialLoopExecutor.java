package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.core.stats.Bottleneck;

public class SequentialLoopExecutor extends LoopExecutor {

    private long frameDuration = 0;

    // TODO update bottleneck

    @Override
    public void runIteration() {
        long frameStart = System.nanoTime();

        updateTime();
        Application.earlyUpdate.invoke();

        Camera camera = Camera.getActive();
        long renderDuration;
        if(camera != null) {
            camera.update.invoke();
            Application.lateUpdate.invoke();
            frameDuration = System.nanoTime() - frameStart;
            long renderStart = System.nanoTime();
            camera.prepareRender();
            camera.render();
            renderDuration = System.nanoTime() - renderStart;
        }
        else {
            Application.lateUpdate.invoke();
            frameDuration = System.nanoTime() - frameStart;
            renderDuration = 0;
        }

        long delay = getIterationDelay();
        frameDuration = System.nanoTime() - frameStart;
        long remainingDelay = delay - (System.nanoTime() - frameStart);
        if(remainingDelay > 0) {
            setBottleneck(Bottleneck.FPS_CAP);
            if(Application.getImplementation().supportsSleeping() || Application.FORCE_FPS_CAP) {
                remainingDelay = delay - (System.nanoTime() - frameStart);
                Application.getImplementation().sleep(remainingDelay / 1000000, (int) (remainingDelay % 1000000));
                // -50000: Some time is lost in between, this matches the target fps pretty well
                if(Application.getImplementation().supportsSleeping())
                    while (frameStart + delay - 50000 > System.nanoTime()) Thread.yield();
            }
        }
        else if(frameDuration < delay && renderDuration < delay)
            setBottleneck(frameDuration < renderDuration ? Bottleneck.THREADING_UPDATE : Bottleneck.THREADING_RENDER);
        else setBottleneck(frameDuration < renderDuration ? Bottleneck.UPDATE : Bottleneck.RENDERING);
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
