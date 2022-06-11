package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.Properties;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.IOManager;
import com.github.rccookie.engine2d.impl.ImageManager;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.impl.InputAdapter;
import com.github.rccookie.engine2d.impl.OnlineManager;
import com.github.rccookie.engine2d.util.Coroutine;
import com.github.rccookie.engine2d.util.awt.AWTStartupPrefs;
import com.github.rccookie.util.Future;
import com.github.rccookie.util.FutureImpl;
import com.github.rccookie.util.ThreadedFutureImpl;

/**
 * AWT based pure java Engine2D implementation.
 */
public class AWTImplementation implements Implementation {

    /**
     * The image factory instance.
     */
    private final ImageManager imageFactory = new AWTImageManager();
    /**
     * The input adapter instance.
     */
    private final InputAdapter inputAdapter = new AWTInputAdapter();
    /**
     * The online manager instance.
     */
    private final OnlineManager onlineManager = new AWTOnlineManager();
    /**
     * The file manager instance.
     */
    private final IOManager ioManager = new AWTIOManager();


    /**
     * The startup prefs that were used to start the implementation.
     */
    private final AWTStartupPrefs prefs;


    /**
     * The thread set as main thread.
     */
    private Thread mainThread = null;


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
        AWTDisplay.INSTANCE = new AWTDisplay(prefs.name);
    }

    @Override
    public ImageManager getImageFactory() {
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
    public IOManager getIOManager() {
        return ioManager;
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
        FutureImpl<T> future = new ThreadedFutureImpl<>();
        new Thread(() -> {
            try {
                T result = coroutine.run();
                future.complete(result);
            } catch(RuntimeException e) {
                future.fail(e);
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

    @Override
    public void setMainThread() throws IllegalStateException {
        if(mainThread != null) throw new IllegalStateException("Main thread already set");
        mainThread = Thread.currentThread();
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() == mainThread;
    }

    @Override
    public void initProperties(Properties properties) {
    }
}
