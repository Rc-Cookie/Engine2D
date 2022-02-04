package com.github.rccookie.engine2d;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.github.rccookie.engine2d.core.LoopExecutor;
import com.github.rccookie.engine2d.core.ParallelLoopExecutor;
import com.github.rccookie.engine2d.core.SequentialLoopExecutor;
import com.github.rccookie.engine2d.core.stats.PerformanceStats;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.IVec2;
import org.jetbrains.annotations.Blocking;

public enum Application {

    ; // No enum instance

    private static final DisplayController displayController = new DisplayController() {
        @Override
        public boolean setResolution(IVec2 resolution) {
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

    private static LoopExecutor executor = null;
    private static Implementation implementation = null;
    private static boolean running = false;

    /**
     * Whether to force the fps cap specified by {@link #setMaxFps(float)},
     * even if the implementation does not support sleeping and waiting will
     * be done using a while-loop.
     * <p>Disabled by default.
     */
    public static boolean FORCE_FPS_CAP = false;

    public static void setup(Implementation implementation) {
        setup(implementation, implementation.supportsMultithreading());
    }

    public static void setup(Implementation implementation, boolean parallel) {
        if(Application.implementation != null)
            throw new IllegalStateException();

        Application.implementation = implementation;
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
        if(Camera.getActive() == null)
            Camera.setActive(NoCameraCamera.INSTANCE);

        if(implementation.hasExternalUpdateLoop())
            implementation.runExternalUpdateLoop();
        else executor.run();
    }

    static void manualFrame() {
        checkSetup();
        running = true;
        executor.runIteration();
    }

    public static boolean isSetup() {
        return implementation != null;
    }

    public static Implementation getImplementation() {
        checkSetup();
        return implementation;
    }

    public static DisplayController getDisplayController() {
        checkSetup();
        return displayController;
    }

    public static float getMaxFps() {
        checkSetup();
        return executor.getFps();
    }

    public static void setMaxFps(float fps) {
        checkSetup();
        executor.setFps(fps);
    }

    public static PerformanceStats getPerformanceStats() {
        Camera camera = Camera.getActive();
        return new PerformanceStats(
                executor.getFrameDuration() / 1000000000f,
                camera.renderPrepDuration / 1000000000f,
                camera.renderDuration / 1000000000f,
                camera.drawCount,
                camera.getPoolSize(),
                camera.updateDuration / 1000000000f,
                camera.physicsDuration / 1000000000f,
                camera.uiUpdateDuration / 1000000000f,
                executor.getBottleneck(),
                executor.isParallel()
        );
    }

    static void checkSetup() {
        if(implementation == null)
            throw new IllegalStateException("The application has to be set up using using Application.setup() before any actions can be performed on it");
    }
}
