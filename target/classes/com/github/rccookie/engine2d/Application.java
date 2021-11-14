package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.LoopExecutor;
import com.github.rccookie.engine2d.core.ParallelLoopExecutor;
import com.github.rccookie.engine2d.core.SequentialLoopExecutor;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.impl.awt.AWTImplementation;
import com.github.rccookie.engine2d.physics.BoxCollider;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.Console;

import java.awt.*;

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

    public static final Event earlyUpdate = new Event();
    public static final Event update = new Event();

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
            new Thread(Application::runUpdateLoop).start();
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

    static void checkSetup() {
        if(implementation == null)
            throw new IllegalStateException("The application has to be set up using using Application.setup() before any actions can be performed on it");
    }



    public static void main(String[] args) {

        Application.setup(new AWTImplementation(), false);

        Map map = new Map();
        map.setGravity(new Vec2(0, -9.81f));
        map.setAllowPhysicsSleep(false);

        GameObject cameraObject = new GameObject();
        cameraObject.setImage(new Image(new IVec2(20, 40), Color.RED));
        cameraObject.location.x = 250;
        cameraObject.velocity.x = -100;
        cameraObject.usePhysics(true);
        new BoxCollider(cameraObject, Vec2.ONE.scaled(10));
        cameraObject.setMap(map);
        cameraObject.rotation = 90;

        GameObject gameObject = new GameObject();
        gameObject.setImage(new Image(IVec2.ONE.scaled(32), Color.BLUE));
        gameObject.setImage(Image.text("Hello\nWorld!", 32, Color.BLUE));
        gameObject.usePhysics(true);
        gameObject.getComponent(Collider.class).setRestitution(1);
        gameObject.setMap(map);

//        Settings.maxTranslation = 200f;
//        Settings.maxTranslationSquared = 200f * 200f;

        UI ui = new UI();
        UIObject uiObject = new UIObject(ui);
        uiObject.setImage(new Image(new IVec2(400, 100), Color.DARK_GRAY));
        uiObject.relativeLoc.y = 1;
        UIObject uiObject1 = new UIObject(uiObject);
        uiObject1.setImage(new Image(IVec2.ONE.scaled(16), Color.PINK));
        uiObject1.relativeLoc.set(-1, -1);

        Camera camera = new Camera(new IVec2(600, 400));

        GameObject cameraManager = new GameObject();
        cameraManager.setImage(new Image(IVec2.ONE.scaled(4), Color.BLACK));
        cameraManager.getComponent(Collider.class).setRestitution(0);
        cameraManager.setMap(map);
        Input.addKeyListener(() -> camera.setGameObject(cameraObject), "1");
        Input.addKeyListener(() -> camera.setGameObject(gameObject), "2");
        cameraManager.input.addKeyListener(() -> Console.map("Velocity", cameraObject.velocity, cameraObject.rotation), "t");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.y += 300, "s", "down");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.y -= 300, "w", "up");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.x += 300, "d", "right");
        cameraManager.input.addKeyPressListener(() -> cameraManager.velocity.x -= 300, "a", "left");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.y += -300, "s", "down");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.y -= -300, "w", "up");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.x += -300, "d", "right");
        cameraManager.input.addKeyReleaseListener(() -> cameraManager.velocity.x -= -300, "a", "left");
        cameraManager.input.addKeyListener(() -> cameraManager.angle += 90 * Time.delta(), "e");
        cameraManager.input.addKeyListener(() -> cameraManager.angle -= 90 * Time.delta(), "q");

        camera.setGameObject(cameraManager);
        camera.setUI(ui);

        Application.startAsync();

        Execute.when(() -> uiObject1.update.add(() -> uiObject1.relativeLoc.x = Math.min(1, uiObject1.relativeLoc.x + Time.delta())), () -> Input.getKeyState(" "));
        Execute.when(cameraObject::remove, () -> Input.getKeyState("r"));
        Execute.when(() -> camera.setBackgroundColor(Color.DARK_GRAY.setBlue(1f)), () -> Input.getKeyState("c"));
        Execute.when(() -> camera.setGameObject(null), () -> Input.getKeyState("n"));

        Application.update.add(() -> Console.map("Velocity", cameraManager.velocity));
    }
}
