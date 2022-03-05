package com.github.rccookie.engine2d.impl.greenfoot.offline;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.impl.greenfoot.GreenfootImplementation;
import com.github.rccookie.engine2d.util.Coroutine;
import com.github.rccookie.engine2d.util.Future;
import com.github.rccookie.engine2d.util.FutureImpl;

public class OfflineGreenfootImplementation extends GreenfootImplementation {

    @Override
    public ImageImplFactory getImageFactory() {
        return OfflineGreenfootImageImplFactory.INSTANCE;
    }

    @Override
    public OnlineManager getOnlineManager() {
        return OfflineGreenfootOnlineManager.INSTANCE;
    }

    @Override
    public boolean supportsMultithreading() {
        return true;
    }

    @Override
    public boolean supportsNativeIO() {
        return true;
    }

    @Override
    public boolean supportsAWT() {
        return true;
    }

    @Override
    public boolean supportsSleeping() {
        return true;
    }

    @Override
    public void sleep(long millis, int nanos) {
        try { Thread.sleep(millis, nanos); }
        catch(InterruptedException ignored) { }
    }

    @Override
    public void yield() {
        Thread.yield();
    }

    @Override
    public <T> Future<T> startCoroutine(Coroutine<T> coroutine) {
        FutureImpl<T> future = new FutureImpl<>();
        new Thread(() -> {
            try {
                T result = coroutine.run();
                future.setValue(result);
            } catch(RuntimeException e) {
                future.cancel();
                throw e;
            }
        }).start();
        return future;
    }

    @Override
    public void sleepUntilNextFrame() {
        Thread thread = Thread.currentThread();
        Execute.nextFrame(thread::interrupt);
        try { thread.join(); }
        catch (InterruptedException ignored) { }
    }
}
