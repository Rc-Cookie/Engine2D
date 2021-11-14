package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.util.Pool;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final List<DrawObject> drawObjects = new ArrayList<>();

    public final Event update = new Event();

    public final LocalInputManager input = new LocalInputManager.Impl(update, () -> Camera.active == this);



    public Camera(IVec2 resolution) {
        Application.checkSetup();

        update.add(this::update);

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

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
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
        if(gameObject != null && gameObject.map != null)
            gameObject.map.update();
        if(ui != null)
            ui.update.invoke();
    }

    /**
     * Should be called only from the update thread.
     */
    public void prepareRender() {

        GameObject[] gameObjects = new GameObject[0];

        if(gameObject != null && gameObject.map != null) {
            // Filter out objects with images that may be on the screen
            float sqrScreenRadius = (halfResolution.x * halfResolution.x) + (halfResolution.y * halfResolution.y);
            Vec2 loc = gameObject.location;
             gameObjects = gameObject.map.paintOrderObjects.stream()
                            .filter(o -> {
                                Image image = o.getImage();
                                if(image == null) return false;
                                if(o == gameObject) return true;

                                // Simple circle collision detection
                                float sqrRadius = (image.size.x * image.size.x + image.size.y * image.size.y) * 0.25f;
                                float sqrDistance = Vec2.sqrDist(loc, o.location);
                                return sqrDistance < sqrRadius + sqrScreenRadius;
                            }).toArray(GameObject[]::new);
        }

        List<UIObject> uiObjects = new ArrayList<>();
        if(ui != null)
            ui.addAllRelevantInPaintOrder(uiObjects);

        int errorMessage = gameObject == null || gameObject.map == null ? 1 : 0;
        int totalDrawCount = gameObjects.length + uiObjects.size() + errorMessage;

//            Console.map("Objects to draw", totalDrawCount);

        // Don't read drawObjects for rendering while it's being edited
        synchronized (drawObjects) {

            // Match the number of draw objects and the number of gameobjects to draw
            if(totalDrawCount < drawObjects.size()) {
                List<DrawObject> additionalObjects = drawObjects.subList(0, drawObjects.size() - totalDrawCount);
                drawObjectPool.returnObjects(additionalObjects);
                additionalObjects.clear();
            }
            else while(totalDrawCount > drawObjects.size())
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
            else {
                DrawObject drawObject = drawObjects.get(0);
                drawObject.image = Application.getImplementation().getImageFactory()
                        .createText(gameObject != null ? "The gameobject is not on a map" : "The camera is not attached to a gameobject",
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
    }

    /**
     * Renders the last prepared state of the camera. The state has to
     * be prepared previously using {@link #prepareRender()}. May be called from
     * any thread at any time.
     */
    public void render() {

        DrawObject[] drawObjects;

        // Possibly wait for prepareRender()
        synchronized (this.drawObjects) {
            // Create a copy to prevent any modifications from other threads
            drawObjects = this.drawObjects.toArray(new DrawObject[0]);
        }

        DISPLAY.draw(drawObjects, backgroundColor);

        // Don't return drawObjects here, the whole list will be reused as much as
        // possible and excess will be returned during the next prepareRender() call
    }



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
