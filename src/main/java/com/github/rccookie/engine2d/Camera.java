package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.engine2d.util.Pool;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

public class Camera {

    static {
        Application.checkSetup();
    }

    static boolean first = true;
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    private static Camera active = NoCameraCamera.INSTANCE;

    static boolean allowsExternalResizing = true;


    static final Display DISPLAY = Application.getImplementation().getDisplay();

    private GameObject gameObject = null;
    private UI ui = null;
    final IVec2 resolution = IVec2.ZERO.clone();
    final Vec2 halfResolution = Vec2.ZERO.clone();
    private Color backgroundColor = Color.WHITE;

    private final Pool<DrawObject> drawObjectPool = new Pool<>(DrawObject::new);
    final List<DrawObject> drawObjects = new ArrayList<>();
    long updateDuration = 0, physicsDuration = 0, uiUpdateDuration = 0;
    long renderPrepDuration = 0;
    long renderDuration = 0;
    int drawCount = 0;

    public final Event update = new NamedCaughtEvent(false, () -> "Camera.update on " + this);

    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isActive);
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isActive);



    public Camera(IVec2 resolution) {
        Application.checkSetup();

        update.add(this::update);

        // To prevent a [0|0] default value for the first frame
        this.resolution.set(resolution);
        this.halfResolution.set(resolution.scaled(0.5f));

        setResolution(resolution);

        synchronized (Camera.class) {
            if(getClass() != NoCameraCamera.class && first) {
                first = false;
                setActive(this);
            }
        }
    }

    public void setResolution(IVec2 resolution) {
        if(this.resolution.equals(resolution)) return;
        Arguments.checkNull(resolution);
        if(resolution.x < 0 || resolution.y < 0)
            throw new ArgumentOutOfRangeException();

        Execute.nextFrame(() -> {
            if(active != this) return;
            Console.map("Set resolution", resolution);

            this.resolution.set(resolution);
            this.halfResolution.set(resolution.x * 0.5f, resolution.y * 0.5f);
            DISPLAY.setResolution(resolution);
        });
    }

    public IVec2 getResolution() {
        return resolution;
    }

    public static boolean allowsExternalResizing() {
        return allowsExternalResizing;
    }

    public static void setAllowExternalResizing(boolean allowExternalResizing) {
        if(allowsExternalResizing == allowExternalResizing) return;
        allowsExternalResizing = allowExternalResizing;
        DISPLAY.allowResizingChanged(allowsExternalResizing);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = Arguments.checkNull(backgroundColor);
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public Map getMap() {
        return gameObject != null ? gameObject.map : null;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public IVec2 pointToPixel(Vec2 location) {
        if(gameObject == null || gameObject.map == null)
            throw new IllegalStateException();
        return location.added(halfResolution).subtracted(gameObject.location).rotateAround(halfResolution, -gameObject.angle).toI();
    }

    public Vec2 pixelToPoint(IVec2 pixel) {
        if(gameObject == null || gameObject.map == null)
            throw new IllegalStateException();
        return pixel.toF().rotateAround(halfResolution, gameObject.angle).add(gameObject.location).subtract(halfResolution);
    }

    public UI getUI() {
        return ui;
    }

    public void setUI(UI ui) {
        if(this.ui == ui) return;
        if(this.ui != null) this.ui.camera = null;
        this.ui = ui;
        if(ui != null) ui.camera = this;
    }

    protected void update() {
        if(gameObject != null && gameObject.map != null) {
            gameObject.map.update();
            updateDuration = gameObject.map.updateDuration;
            physicsDuration = gameObject.map.physicsDuration;
        }
        if(ui != null) {
            long start = System.nanoTime();
            ui.update.invoke();
            uiUpdateDuration = System.nanoTime() - start;
        }
        else uiUpdateDuration = 0;
    }

    /**
     * Should be called only from the update thread.
     */
    public long prepareRender() {

        long start = System.nanoTime();

        GameObject[] gameObjects = new GameObject[0];

        if(gameObject != null && gameObject.map != null) {
            // Filter out objects with images that may be on the screen
            Vec2 loc = gameObject.location;
            gameObjects = gameObject.map.paintOrderObjects.stream()
                    .filter(o -> {
                        Image image = o.getImage();
                        if(image == null || image.definitelyBlank) return false;
                        if(o == gameObject) return true;

                        // Simple circle collision detection
                        float w = image.size.x / 2f + halfResolution.x, h = image.size.y / 2f + halfResolution.y;
                        float maxSqrDistance = w * w + h * h;
                        float sqrDistance = Vec2.sqrDist(loc, o.location);

                        return sqrDistance < maxSqrDistance;
                    }).toArray(GameObject[]::new);
        }

        List<UIObject> uiObjects = new ArrayList<>();
        if(ui != null)
            ui.addAllRelevantInPaintOrder(uiObjects);

        int errorMessage = gameObject != null && gameObject.map == null ? 1 : 0;
        drawCount = gameObjects.length + uiObjects.size() + errorMessage;

//            Console.map("Objects to draw", drawCount);

        // Don't read drawObjects for rendering while it's being edited
        synchronized (drawObjects) {

            // Match the number of draw objects and the number of gameobjects to draw
            if(drawCount < drawObjects.size()) {
                List<DrawObject> additionalObjects = drawObjects.subList(0, drawObjects.size() - drawCount);
                drawObjectPool.returnObjects(additionalObjects);
                additionalObjects.clear();
            }
            else while(drawCount > drawObjects.size())
                    drawObjects.add(drawObjectPool.get());

            if(gameObject != null && gameObject.map != null) {
                Vec2 screenOffset = halfResolution.subtracted(gameObject.location);

                // Set a draw object for each gameobject (draw first -> below UI)
                for (int i = 0; i < gameObjects.length; i++) {
                    GameObject g = gameObjects[i];
                    DrawObject drawObject = drawObjects.get(i);

                    drawObject.image = g.getImage().impl;
                    drawObject.rotation = g.angle - gameObject.angle;
                    // Translate world position to screen position
                    drawObject.screenLocation.set(g.location.added(screenOffset).rotateAround(halfResolution, -gameObject.angle).toI());
                }
            }
            else if(gameObject != null) {
                DrawObject drawObject = drawObjects.get(0);

                drawObject.image = Application.getImplementation().getImageFactory()
                    .createText("The gameobject is not on a map",
                            20, backgroundColor.getContrast().setAlpha(1f));

                drawObject.rotation = 0;
                drawObject.screenLocation.set(halfResolution.toI());
            }

            for(int i=0; i<uiObjects.size(); i++) {
                UIObject u = uiObjects.get(uiObjects.size() - i - 1);
                DrawObject drawObject = drawObjects.get(i + gameObjects.length + errorMessage);

                drawObject.image = u.getImage().impl;
                drawObject.rotation = 0;
                drawObject.screenLocation.set(u.getCachedScreenPos());
            }
        }

        // Reset the cache immediately after using it to use less resources rather than
        // having it stored unused until the next rendering
        if(ui != null) ui.resetCache();

        return renderPrepDuration = System.nanoTime() - start;
    }

    /**
     * Renders the last prepared state of the camera. The state has to
     * be prepared previously using {@link #prepareRender()}. May be called from
     * any thread at any time.
     */
    public long render() {

        long start = System.nanoTime();

        DrawObject[] drawObjects;

        // Possibly wait for prepareRender()
        synchronized (this.drawObjects) {
            // Create a copy to prevent any modifications from other threads
            drawObjects = this.drawObjects.toArray(new DrawObject[0]);
        }

        DISPLAY.draw(drawObjects, backgroundColor);

        // Don't return drawObjects here, the whole list will be reused as much as
        // possible and excess will be returned during the next prepareRender() call

        return renderDuration = System.nanoTime() - start;
    }



    public int getDrawCount() {
        synchronized (drawObjects) {
            return drawObjects.size();
        }
    }

    int getPoolSize() {
        synchronized (drawObjects) {
            return drawObjectPool.size();
        }
    }



    public boolean isActive() {
        return this == Camera.active;
    }

    /**
     * Returns the camera that is currently being shown. This is never
     * {@code null}, a suitable replacement will be used if no camera
     * has been set yet, or it has been removed.
     *
     * @return The currently shown camera
     */
    public static Camera getActive() {
        return active;
    }

    public static void setActive(Camera active) {
        if(active == null) active = NoCameraCamera.INSTANCE;
        if(Camera.active == active) return;
        Camera old = Camera.active;
        Camera.active = active;
        if(!Objects.equals(old.resolution, active.resolution)) {
            if(active == NoCameraCamera.INSTANCE)
                active.setResolution(old.resolution);
            else DISPLAY.setResolution(active.resolution);
        }
    }
}
