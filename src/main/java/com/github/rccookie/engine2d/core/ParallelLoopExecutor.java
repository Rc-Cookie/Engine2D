package com.github.rccookie.engine2d.core;

import java.util.function.Consumer;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Camera;
import com.github.rccookie.engine2d.core.stats.Bottleneck;
import com.github.rccookie.util.Console;

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

    private final Object lock = new Object();

    @Override
    public Bottleneck runIterationUntimed() {

        startLoops = true;
        synchronized(lock) {
            lock.notifyAll();
        }

        while(!(updateThread.running && renderThread.running)) Thread.yield();
        startLoops = false;
        while(updateThread.running || renderThread.running) Thread.yield();

       return frameDuration < renderDuration1 + renderDuration2 ? Bottleneck.RENDERING : Bottleneck.UPDATE;
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
            while (!startLoops) Thread.yield();
            //noinspection InfiniteLoopStatement
            while (true) {

                running = true;

                Camera camera = Camera.getActive();
                loopAction.accept(camera);

                while(startLoops) Thread.yield();

                running = false;
                while(!startLoops) try {
                    synchronized(lock) {
                        if(startLoops) break;
                        lock.wait();
                    }
                } catch(InterruptedException e) {
                    Console.error(e);
                }
            }
        }
    }

    @Override
    public boolean isParallel() {
        return true;
    }
}
