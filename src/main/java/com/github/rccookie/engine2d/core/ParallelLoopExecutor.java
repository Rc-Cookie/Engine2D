package com.github.rccookie.engine2d.core;

import java.util.function.Consumer;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.core.stats.Bottleneck;

public class ParallelLoopExecutor extends LoopExecutor {

    private volatile boolean startLoops = false;
    private volatile long frameDuration = 0;
    private volatile long renderDuration1 = 0, renderDuration2 = 0;


    LoopThread updateThread = new LoopThread("Update Thread", c -> {
        long frameStart = System.nanoTime();
        updateTime();
        Application.earlyUpdate.invoke();
        if(c != null) {
            c.update.invoke();
            Application.lateUpdate.invoke();
            frameDuration = System.nanoTime() - frameStart;
            renderDuration1 = c.prepareRender();
        }
        else {
            Application.lateUpdate.invoke();
            frameDuration = System.nanoTime() - frameStart;
            renderDuration1 = 0;
        }
    });
    LoopThread renderThread = new LoopThread("Render Thread", c -> {
        if(c != null) renderDuration2 = c.render();
        else renderDuration2 = 0;
    });


    {
        updateThread.start();
        renderThread.start();
    }


    // TODO update bottleneck

    @Override
    public void runIteration() {
        long frameStart = System.nanoTime();
        startLoops = true;
        while(!(updateThread.running && renderThread.running)) Thread.yield();
        startLoops = false;

        long delay = getIterationDelay();
        long remainingDelay = delay - (System.nanoTime() - frameStart);
        if(remainingDelay > 0) {
            if(Application.getImplementation().supportsSleeping() || Application.FORCE_FPS_CAP) {
                Application.getImplementation().sleep(remainingDelay / 1000000, (int) (remainingDelay % 1000000));
                // Parallel execution looses enough time for managing the threads so that an exact waiting is unnecessary
            }
        }

        boolean bottlenecked = updateThread.running || renderThread.running;

        while(updateThread.running || renderThread.running) Thread.yield();
        if(!bottlenecked) setBottleneck(Bottleneck.FPS_CAP);
        else setBottleneck(frameDuration < renderDuration1 + renderDuration2 ? Bottleneck.RENDERING : Bottleneck.UPDATE);
    }

    @Override
    public long getFrameDuration() {
        return frameDuration;
    }

    class LoopThread extends Thread {

        volatile boolean running = false;
        private final Consumer<Camera> loopAction;

        public LoopThread(String name, Consumer<Camera> loopAction) {
            super(name);
            this.loopAction = loopAction;
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                while (!startLoops) Thread.yield();

                running = true;

                Camera camera = Camera.getActive();
                loopAction.accept(camera);

                while (startLoops) Thread.yield();

                running = false;
            }
        }
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}
