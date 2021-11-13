package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.util.Console;

public class SequentialLoopExecutor extends LoopExecutor {

    @Override
    public void runIteration() {
        long frameStart = System.nanoTime();

        updateTime();
        Application.earlyUpdate.invoke();

        Camera camera = Camera.getActive();
        if(camera != null) {
            camera.update();
            Application.update.invoke();
            camera.prepareRender();
            camera.render();
        }
        else Application.update.invoke();

        long delay = getIterationDelay();
        long remainingDelay = delay - (System.nanoTime() - frameStart);
        if(remainingDelay > 0) {
            try {
                Thread.sleep(remainingDelay / 1000000, (int) (remainingDelay % 1000000));
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            // -50000: Some time is lost in between, this matches the target fps pretty well
            while(frameStart + delay - 50000 > System.nanoTime()) Thread.yield();
        }
    }
}
