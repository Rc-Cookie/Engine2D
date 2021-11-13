package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.impl.CameraControls;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.util.Pool;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Camera {

    private static Camera active = null;
    private static boolean first = true;

    private GameObject gameObject = null;
    private UI ui = null;
    final IVec2 resolution = IVec2.ZERO.clone();
    final Vec2 halfResolution = Vec2.ZERO.clone();
    private final Display display;
    private Color backgroundColor = Color.WHITE;

    private final Pool<DrawObject> drawObjectPool = new Pool<>(DrawObject::new);
    private final List<DrawObject> drawObjects = new ArrayList<>();

    private boolean autoResizable = true;

    public Camera(IVec2 resolution) {
        Application.checkSetup();
        display = Application.getImplementation()
                .getDisplayFactory()
                .createNew(resolution, new CameraControlsImpl());
        setResolution(resolution);

        synchronized (Camera.class) {
            if(first) {
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
            Console.map("Set resolution", resolution);

            this.resolution.set(resolution);
            this.halfResolution.set(resolution.x * 0.5f, resolution.y * 0.5f);
            display.setResolution(resolution);
        });
    }

    public IVec2 getResolution() {
        return resolution;
    }

    public boolean isAutoResizable() {
        return autoResizable;
    }

    public void setAutoResizable(boolean autoResizable) {
        if(this.autoResizable == autoResizable) return;
        this.autoResizable = autoResizable;
        display.allowResizingChanged(autoResizable);
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

    public void update() {
        if(gameObject != null)
            gameObject.map.update();
        if(ui != null)
            ui.update.invoke();
    }

    /**
     * Should be called only from the update thread.
     */
    public void prepareRender() {
        if(gameObject != null && gameObject.map != null) {

            // Filter out objects with images that may be on the screen
            float sqrScreenRadius = (halfResolution.x * halfResolution.x) + (halfResolution.y * halfResolution.y);
            Vec2 loc = gameObject.location;
            GameObject[] gameObjects = gameObject.map.paintOrderObjects.stream()
                    .filter(o -> {
                        Image image = o.getImage();
                        if(image == null) return false;
                        if(o == gameObject) return true;

                        // Simple circle collision detection
                        float sqrRadius = (image.size.x * image.size.x + image.size.y * image.size.y) * 0.25f;
                        float sqrDistance = Vec2.sqrDist(loc, o.location);
                        return sqrDistance < sqrRadius + sqrScreenRadius;
                    }).toArray(GameObject[]::new);

            List<UIObject> uiObjects;
            if(ui == null) uiObjects = List.of();
            else {
                uiObjects = new ArrayList<>();
                ui.addAllRelevantInPaintOrder(uiObjects);
            }

            int totalDrawCount = gameObjects.length + uiObjects.size();

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

                Vec2 screenOffset = halfResolution.subtracted(gameObject.location);

                // Set a draw object for each gameobject (draw first -> below UI)
                for(int i=0; i<gameObjects.length; i++) {
                    GameObject g = gameObjects[i];
                    DrawObject drawObject = drawObjects.get(i);

                    drawObject.image = g.getImage().impl;
                    drawObject.rotation = g.angle - gameObject.angle;
                    // Translate world position to screen position
                    drawObject.screenLocation.set(g.location.added(screenOffset).rotateAround(halfResolution, -gameObject.angle).toI());
                }

                for(int i=0; i<uiObjects.size(); i++) {
                    UIObject u = uiObjects.get(uiObjects.size() - i - 1);
                    DrawObject drawObject = drawObjects.get(i + gameObjects.length);

                    drawObject.image = u.getImage().impl;
                    drawObject.rotation = 0;
                    drawObject.screenLocation.set(u.getCachedScreenPos());
                }
            }

            // Reset the cache immediately after using it to use less resources rather than
            // having it stored unused until the next rendering
            if(ui != null) ui.resetCache();
        }
    }

    /**
     * Renders the last prepared state of the camera. The state has to
     * be prepared previously using {@link #prepareRender()}. May be called from
     * any thread at any time.
     */
    public void render() {

        List<DrawObject> drawObjects;

        // Possibly wait for prepareRender()
        synchronized (this.drawObjects) {
            // Create a copy to prevent any modifications from other threads
            drawObjects = Arrays.asList(this.drawObjects.toArray(new DrawObject[0]));
        }

        display.draw(drawObjects, backgroundColor);

        // Don't return drawObjects here, the whole list will be reused as much as
        // possible and excess will be returned during the next prepareRender() call
    }



    public static Camera getActive() {
        return active;
    }

    public static void setActive(Camera active) {
        Camera.active = active;
    }

    private class CameraControlsImpl implements CameraControls {

        @Override
        public boolean setResolution(IVec2 resolution) {
            Execute.nextFrame(() -> Camera.this.setResolution(autoResizable ? resolution : Camera.this.resolution));
            return autoResizable;
        }

        @Override
        public boolean allowsResizing() {
            return autoResizable;
        }
    }
}
