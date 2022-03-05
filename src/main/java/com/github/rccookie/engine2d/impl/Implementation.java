package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.util.Coroutine;
import com.github.rccookie.engine2d.util.Future;

/**
 * Generic interface for an implementation of an Engine-2D application.
 */
public interface Implementation {

    /**
     * Accepts the given display controller as the currently used
     * display controller.
     *
     * @param displayController The display controller to use
     */
    void setDisplayController(DisplayController displayController);

    /**
     * Returns the implementation's image implementation factory.
     *
     * @return The image factory
     */
    ImageImplFactory getImageFactory();

    /**
     * Returns the implementation's display.
     *
     * @return The display
     */
    Display getDisplay();

    /**
     * Returns the implementation's input adapter.
     *
     * @return The input adapter
     */
    InputAdapter getInputAdapter();

    /**
     * Returns the implementation's online manager.
     *
     * @return The online manager
     */
    OnlineManager getOnlineManager();

    /**
     * Returns whether the implementation supports multithreading.
     *
     * @return Whether multithreading is supported
     */
    boolean supportsMultithreading();

    /**
     * Returns whether {@code java.io} and {@code java.nio} are supported.
     *
     * @return Whether java I/O is supported
     */
    boolean supportsNativeIO();

    /**
     * Returns whether {@code java.awt} is supported.
     *
     * @return Whether AWT is supported
     */
    boolean supportsAWT();

    /**
     * Whether the implementation supports thread sleeping. Using
     * {@code while(System.currentTimeMillis() < targetTime)} is <b>not</b>
     * considered the support of sleeping, sleep should not use up any resources.
     *
     * @return Whether sleep is supported
     */
    boolean supportsSleeping();

    /**
     * Whether the implementation has a "native" update loop that should
     * run the application update loop using {@link DisplayController#runApplicationFrame()}.
     *
     * @return Whether the implementation has an external update loop
     */
    boolean hasExternalUpdateLoop();

    /**
     * Sleeps the specified time. If sleeping is not supported this should run
     * a loop to wait.
     *
     * @param millis The milliseconds to wait
     * @param nanos The nanoseconds to wait, within [0,999999]
     */
    void sleep(long millis, int nanos);

    /**
     * Runs the external update loop which itself then runs the application.
     *
     * @throws UnsupportedOperationException If this implementation does not have
     *                                       or use an external update loop
     */
    void runExternalUpdateLoop() throws UnsupportedOperationException;

    /**
     * Yields this thread, if supported.
     */
    @Deprecated
    void yield();

    /**
     * Starts the given coroutine.
     *
     * @param coroutine The coroutine to start
     * @param <T> The return type
     * @return A future referring to the result of the coroutine
     */
    @Deprecated
    <T> Future<T> startCoroutine(Coroutine<T> coroutine);

    /**
     * Sleeps until the next frame, if supported.
     */
    @Deprecated
    void sleepUntilNextFrame();
}
