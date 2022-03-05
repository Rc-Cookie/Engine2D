package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.impl.InputAdapter;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.util.Coroutine;
import com.github.rccookie.engine2d.util.Future;
import com.github.rccookie.engine2d.util.FutureImpl;

/**
 * AWT based pure java Engine2D implementation.
 */
public class AWTImplementation implements Implementation {

    /**
     * The image factory instance.
     */
    private final ImageImplFactory imageFactory = new AWTImageImplFactory();
    /**
     * The input adapter instance.
     */
    private final InputAdapter inputAdapter = new AWTInputAdapter();
    /**
     * The online manager instance.
     */
    private final OnlineManager onlineManager = new AWTOnlineManager();


    /**
     * The startup prefs that were used to start the implementation.
     */
    private final AWTStartupPrefs prefs;


    /**
     * Creates a new AWTImplementation with the given preferences.
     *
     * @param prefs Startup preferences
     */
    public AWTImplementation(AWTStartupPrefs prefs) {
        this.prefs = prefs;
    }

    @Override
    public void setDisplayController(DisplayController displayController) {
        AWTDisplay.displayController = displayController;
        AWTDisplay.INSTANCE = new AWTDisplay(prefs.applicationName);
    }

    @Override
    public ImageImplFactory getImageFactory() {
        return imageFactory;
    }

    @Override
    public Display getDisplay() {
        return AWTDisplay.INSTANCE;
    }

    @Override
    public InputAdapter getInputAdapter() {
        return inputAdapter;
    }

    @Override
    public OnlineManager getOnlineManager() {
        return onlineManager;
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
    public boolean hasExternalUpdateLoop() {
        return false;
    }

    @Override
    public void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runExternalUpdateLoop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public void yield() {
        Thread.yield();
    }

    @Override
    @Deprecated
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
    @Deprecated
    public void sleepUntilNextFrame() {
        Thread thread = Thread.currentThread();
        Execute.nextFrame(thread::interrupt);
        try { thread.join(); }
        catch (InterruptedException ignored) { }
    }
}
