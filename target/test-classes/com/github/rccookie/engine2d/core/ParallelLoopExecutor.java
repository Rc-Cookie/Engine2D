package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;

import java.util.function.Consumer;

public class ParallelLoopExecutor extends LoopExecutor {

    private volatile boolean startLoops = false;


    LoopThread updateThread = new LoopThread(c -> {
        updateTime();
        Application.earlyUpdate.invoke();
        if(c != null) {
            c.update.invoke();
            Application.update.invoke();
            c.prepareRender();
        }
        else Application.update.invoke();
    });
    LoopThread renderThread = new LoopThread(c -> {
        if(c != null) c.render();
    });


    {
        updateThread.start();
        renderThread.start();
    }


    @Override
    public void runIteration() {
        long frameStart = System.nanoTime();
        startLoops = true;
        while(!(updateThread.running && renderThread.running)) Thread.yield();
        startLoops = false;

        long delay = getIterationDelay();
        long remainingDelay = delay - (System.nanoTime() - frameStart);
        if(remainingDelay > 0) {
            if(Application.getImplementation().supportsSleeping()) {
                Application.getImplementation().sleep(remainingDelay / 1000000, (int) (remainingDelay % 1000000));
                // Parallel execution looses enough time for managing the threads so that an exact waiting is unnecessary
            }
            else if(Application.FORCE_FPS_CAP) {
                long targetTime = System.nanoTime() + remainingDelay;
                //noinspection StatementWithEmptyBody
                while(System.nanoTime() < targetTime);
            }
        }

        while(updateThread.running || renderThread.running) Thread.yield();
    }



    class LoopThread extends Thread {

        volatile boolean running = false;
        private final Consumer<Camera> loopAction;

        public LoopThread(Consumer<Camera> loopAction) {
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
}
