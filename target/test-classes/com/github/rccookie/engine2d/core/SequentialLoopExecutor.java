package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;

public class SequentialLoopExecutor extends LoopExecutor {

    @Override
    public void runIteration() {
        long frameStart = System.nanoTime();

        updateTime();
        Application.earlyUpdate.invoke();

        Camera camera = Camera.getActive();
        if(camera != null) {
            camera.update.invoke();
            Application.update.invoke();
            camera.prepareRender();
            camera.render();
        }
        else Application.update.invoke();

        long delay = getIterationDelay();
        long remainingDelay = delay - (System.nanoTime() - frameStart);
        if(remainingDelay > 0) {
            if(Application.getImplementation().supportsSleeping()) {
                Application.getImplementation().sleep(remainingDelay / 1000000, (int) (remainingDelay % 1000000));
                // -50000: Some time is lost in between, this matches the target fps pretty well
                while (frameStart + delay - 50000 > System.nanoTime()) Thread.yield();
            }
            else if(Application.FORCE_FPS_CAP) {
                long targetTime = System.nanoTime() + remainingDelay;
                //noinspection StatementWithEmptyBody
                while(System.nanoTime() < targetTime);
            }
        }
    }
}
