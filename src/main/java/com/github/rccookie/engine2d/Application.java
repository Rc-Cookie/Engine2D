package com.github.rccookie.engine2d;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.core.LoopExecutor;
import com.github.rccookie.engine2d.core.ParallelLoopExecutor;
import com.github.rccookie.engine2d.core.SequentialLoopExecutor;
import com.github.rccookie.engine2d.core.stats.PerformanceStats;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.util.Coroutine;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.engine2d.util.VoidCoroutine;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Future;

import org.jetbrains.annotations.Blocking;

/**
 * The main control class and entry point of the application. Also serves as
 * connection between the impl API and the actual implementation.
 */
@SuppressWarnings("NonFinalFieldInEnum")
public enum Application {

    ; // No enum instance

    /**
     * Global display controller.
     */
    private static final DisplayController displayController = new DisplayController() {
        @Override
        public boolean setResolution(int2 resolution) {
            if(!Camera.allowsExternalResizing) return false;
            Camera.getActive().setResolution(resolution);
            return true;
        }

        @Override
        public boolean allowsResizing() {
            return Camera.allowsExternalResizing;
        }

        @Override
        public void runApplicationFrame() {
            Application.manualFrame();
        }
    };

    /**
     * Called before all update methods in GameObjects and UIObjects.
     */
    public static final Event earlyUpdate = new NamedCaughtEvent(false, "Application.earlyUpdate");

    /**
     * Called after all update methods in GameObjects and UIObjects.
     */
    public static final Event lateUpdate = new NamedCaughtEvent(false, "Application.lateUpdate");

    /**
     * Executes the update and render threads. May not be used if the implementation
     * has an internal update loop. Gets set from the setup method.
     */
    private static LoopExecutor executor = null;

    /**
     * The "native" implementation of this application.
     */
    private static Implementation implementation = null;

    /**
     * Has the application been started?
     */
    private static boolean running = false;

    /**
     * Application-wide properties for "low-level" information.
     */
    private static final Properties properties = new Properties();

    /**
     * Whether to force the fps cap specified by {@link #setMaxFps(float)},
     * even if the implementation does not support sleeping and waiting will
     * be done using a while-loop.
     * <p>Disabled by default.
     */
    public static boolean FORCE_FPS_CAP = false;

    /**
     * If enabled, the "output framerate" may be reduced down to as low
     * as 1 fps, if nothing on the screen has changed. This <b>does not</b>
     * affect the frequency of {@code update} calls, if at all it may increase
     * it because of shorted render times.
     * <p>Adaptive framerate can reduce cpu and gpu load significantly on ui-based
     * applications where the screen only changed after distinct events. This
     * feature may trick monitoring software into thinking that the application
     * is really running at such a low framerate because of lag, even though it
     * is running at full framerate internally.</p>
     */
    public static boolean ADAPTIVE_FRAMERATE = true;

    /**
     * Set up the application to use the specified "native" implementation.
     * This method must be called before any of the classes can be used,
     * and can only be used once. Otherwise, an {@link IllegalStateException}
     * will be thrown.
     * <p>This method will use multithreading if available.</p>
     *
     * @param implementation The implementation to use
     */
    public static void setup(Implementation implementation) {
        setup(implementation, implementation.supportsMultithreading());
    }

    /**
     * Set up the application to use the specified "native" implementation.
     * This method must be called before any of the classes can be used,
     * and can only be used once. Otherwise, an {@link IllegalStateException}
     * will be thrown.
     *
     * @param implementation The implementation to use
     * @param parallel Whether to allow parallel execution. If the implementation
     *                 does not support multithreading this option will be
     *                 ignored
     */
    public static void setup(Implementation implementation, boolean parallel) {
        if(Application.implementation != null)
            throw new IllegalStateException();

        Application.implementation = implementation;
        properties.set("implementation", implementation.getClass());
        implementation.initProperties(properties);

        implementation.setDisplayController(displayController);
        executor = (parallel && implementation.supportsMultithreading()) ?
                new ParallelLoopExecutor() : new SequentialLoopExecutor();

        if(implementation.supportsAWT()) {
            GraphicsDevice[] monitors = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            int max = DisplayMode.REFRESH_RATE_UNKNOWN;
            for(GraphicsDevice monitor : monitors)
                max = Math.max(max, monitor.getDisplayMode().getRefreshRate());

            if(max == DisplayMode.REFRESH_RATE_UNKNOWN)
                executor.setFps(60);
            else executor.setFps(Math.max(30, max));
        }
        else executor.setFps(60);

        Execute.init();
    }

    /**
     * Starts the application on this thread. No further code immediately after
     * the call to this method will execute until the application quits.
     */
    @Blocking
    public static void start() {
        checkSetup();
        synchronized (Application.class) {
            if(running) throw new IllegalStateException();
            running = true;
        }
        runUpdateLoop();
    }

    /**
     * Starts the application on a new thread, if supported, allowing this thread
     * to continue execution below this method call.
     */
    public static void startAsync() {
        checkSetup();
        synchronized (Application.class) {
            if(running) throw new IllegalStateException();
            running = true;
        }
        if(implementation.supportsMultithreading())
            new Thread(Application::runUpdateLoop, "Application Thread").start();
        else runUpdateLoop();
    }

    private static void runUpdateLoop() {
        implementation.setMainThread();

        if(implementation.hasExternalUpdateLoop())
            implementation.runExternalUpdateLoop();
        else executor.run();
    }

    static void manualFrame() {
        checkSetup();
        running = true;
        executor.runIteration();
    }

    /**
     * Starts the given coroutine.
     *
     * @param coroutine The coroutine to start
     * @param <T> The type of result
     * @return A future for the result of the coroutine
     */
    @Deprecated
    public static <T> Future<T> startCoroutine(Coroutine<T> coroutine) {
        checkSetup();
        return implementation.startCoroutine(coroutine);
    }

    /**
     * Starts the given coroutine which has no result.
     *
     * @param coroutine The coroutine to start
     */
    @Deprecated
    public static void startCoroutine(VoidCoroutine coroutine) {
        startCoroutine((Coroutine<Object>) coroutine);
    }



//    public static void yield() {
//        checkSetup();
//        implementation.yield();
//    }

    /**
     * Determines whether this application has been set up using
     * {@link #setup(Implementation)} or {@link #setup(Implementation, boolean)}.
     *
     * @return Whether this application has been set up
     */
    public static boolean isSetup() {
        return implementation != null;
    }

    /**
     * Returns the "native" implementation underlying this application. Interacting
     * with the implementation is usually only for internal purposes.
     *
     * @return The underlying implementation
     */
    public static Implementation getImplementation() {
        checkSetup();
        return implementation;
    }

    /**
     * Returns the display controller controlling the display of this application.
     *
     * @return The display controller of this application
     */
    public static DisplayController getDisplayController() {
        checkSetup();
        return displayController;
    }

    /**
     * Returns the current fps cap.
     *
     * @return The current maximum fps
     */
    public static float getMaxFps() {
        checkSetup();
        return executor.getFps();
    }

    /**
     * Sets the fps cap for the application. Non-positive values will be threatened as
     * no limit.
     * <p>The fps cap defaults to the maximum refresh rate of the monitor, if
     * this information is available. Otherwise it will limit to 60 fps by default.</p>
     *
     * @param fps The new fps cap
     */
    public static void setMaxFps(float fps) {
        checkSetup();
        executor.setFps(fps);
    }

    /**
     * Collects current performance stats.
     *
     * @return The current performance stats
     */
    public static PerformanceStats getPerformanceStats() {
        Camera camera = Camera.getActive();
        return new PerformanceStats(
                executor.getFrameDuration() / 1000000000f,
                camera.renderPrepDuration / 1000000000f,
                camera.renderDuration / 1000000000f,
                camera.drawCount,
                DrawObject.getPoolSize(),
                camera.updateDuration / 1000000000f,
                camera.physicsDuration / 1000000000f,
                camera.uiUpdateDuration / 1000000000f,
                executor.getBottleneck(),
                executor.isParallel()
        );
    }

    /**
     * Returns the application-wide properties.
     *
     * @return The application properties
     */
    public static Properties getProperties() {
        checkSetup();
        return properties;
    }

    /**
     * Asserts that the application has been set up using {@link #setup(Implementation)}.
     *
     * @throws IllegalStateException If the application has not been set up
     */
    public static void checkSetup() {
        if(implementation == null)
            throw new IllegalStateException("The application has to be set up using using Application.setup() before any actions can be performed on it");
    }
}
