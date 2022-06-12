package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The camera is responsible for rendering the image of the application window.
 * There can always be only one camera active at a time, which can be obtained
 * using {@link #getActive()}. The first camera created will automatically set
 * itself as the active camera, other cameras will have to be set as active
 * manually. Only the active camera gets rendered, so there is no performance
 * penalty for having multiple cameras at the same time. If no camera is active
 * a suitable replacement will render an error message.
 *
 * <p>A camera can render up to two things: a {@link Map}, and a ui tree starting
 * at an instance of {@link UI}. UI is always rendered on top of the map. UI and
 * map can also be used with each other; the camera can also just render one of
 * them. The camera is not attached to a map directly, but rather to a {@link GameObject},
 * and the camera will render the map the gameobject is on at the position and
 * rotation of that gameobject.</p>
 *
 * <p>The camera is also responsible for executing the update loop, or rather,
 * it determines which objects get updated. The update loop will only run on the
 * active camera, the map and the map's gameobjects that the camera is attached
 * to, and the camera's ui tree.</p>
 */
public class Camera {

    static {
        Application.checkSetup();
    }

    /**
     * Whether the next camera is the first camera.
     */
    static boolean first = true;

    /**
     * The currently active camera, or {@link NoCameraCamera#INSTANCE}.
     */
    @SuppressWarnings("StaticInitializerReferencesSubClass")
    @NotNull
    private static Camera active = NoCameraCamera.INSTANCE;

    /**
     * Whether external resizing (i.e. by dragging the window edges) is
     * allowed.
     */
    static boolean allowsExternalResizing = true;


    /**
     * The output target of the application.
     */
    static final Display DISPLAY = Application.getImplementation().getDisplay();

    /**
     * The gameobject the camera is attached to. May be null.
     */
    private GameObject gameObject = null;

    /**
     * The ui of the camera. May be null.
     */
    private UI ui = null;

    /**
     * The camera's resolution, in other words, the resolution of
     * the output display.
     */
    final int2 resolution = int2.zero();

    /**
     * Half of {@link #resolution}.
     */
    final float2 halfResolution = float2.zero();

    /**
     * Rendering background color.
     */
    @NotNull
    private Color backgroundColor = Color.DARK_GRAY;


    /**
     * Draw objects for the current frame.
     */
    final List<DrawObject> drawObjects = new ArrayList<>();

    private int renderHash = -1;
    private int lastRenderHash = -1;
    private float lastRenderTime = -10;


    /**
     * Performance stats (milliseconds).
     */
    long updateDuration = 0, physicsDuration = 0, uiUpdateDuration = 0;

    /**
     * Performance stats (milliseconds).
     */
    long renderPrepDuration = 0, renderDuration = 0;

    /**
     * Number of objects on the screen / potentially on the screen.
     */
    int drawCount = 0;


    /**
     * Main update event of the camera.
     */
    public final Event update = new NamedCaughtEvent(false, () -> "Camera.update on " + this);

    /**
     * Local input only active when the camera is.
     */
    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isActive);

    /**
     * Local executor only active when the camera is.
     */
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isActive);



    /**
     * Creates a new camera with the given resolution.
     *
     * @param resolution The render output resolution
     */
    public Camera(int2 resolution) {
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

    /**
     * Creates a new camera with the given resolution.
     *
     * @param width Output resolution width, in pixels
     * @param height Output resolution height, in pixels
     */
    public Camera(int width, int height) {
        this(new int2(width, height));
    }



    /**
     * Sets the output resolution of this camera.
     *
     * @param resolution The resolution to set
     */
    public void setResolution(int2 resolution) {
        Arguments.checkNull(resolution, "resolution");
        if(this.resolution.equals(resolution)) return;
        if(resolution.x <= 0 || resolution.y <= 0)
            throw new ArgumentOutOfRangeException("Non-positive resolution");

        Execute.nextFrame(() -> {
            if(active != this) return;
            Console.mapDebug("Set resolution", resolution);

            this.resolution.set(resolution);
            this.halfResolution.set(resolution.x * 0.5f, resolution.y * 0.5f);
            DISPLAY.setResolution(resolution);
            if(ui != null)
                ui.onParentSizeChange.invoke(resolution);
        });
    }

    /**
     * Returns the camera's current output resolution.
     *
     * @return The camera's resolution
     */
    public int2 getResolution() {
        return resolution;
    }

    /**
     * Returns whether the application supports external resizing (i.e. dragging
     * edges of the window).
     *
     * @return Whether external resizing is possible
     */
    public static boolean allowsExternalResizing() {
        return allowsExternalResizing;
    }

    /**
     * Sets whether external resizing should be allowed. Note that depending on
     * the underlying implementation may not be possible regardless of the used
     * setting.
     *
     * @param allowExternalResizing Whether to allow external resizing
     */
    public static void setAllowExternalResizing(boolean allowExternalResizing) {
        if(allowsExternalResizing == allowExternalResizing) return;
        allowsExternalResizing = allowExternalResizing;
        DISPLAY.allowResizingChanged(allowsExternalResizing);
    }

    /**
     * Returns the rendering background color.
     *
     * @return The rendering background
     */
    @NotNull
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the rendering background color
     *
     * @param backgroundColor The color to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = Arguments.checkNull(backgroundColor);
    }

    /**
     * Returns the gameobject the camera is currently attached to. {@code null}
     * indicates that the camera is not attached to any gameobject.
     *
     * @return The gameobject of the camera
     */
    public GameObject getGameObject() {
        return gameObject;
    }

    /**
     * Returns the map the camera is currently rendering, or {@code null} if
     * the camera is either not attached to a gameobject or the gameobject
     * is not on a map.
     *
     * @return The map the camera is rendering
     */
    public Map getMap() {
        return gameObject != null ? gameObject.map : null;
    }

    /**
     * Returns the map the camera is currently rendering, cast to the given
     * type.
     *
     * @param type The type of map to cast to
     * @return The map the camera is rendering
     */
    public <M> M getMap(Class<M> type) {
        return gameObject != null ? type.cast(gameObject.map) : null;
    }

    /**
     * Sets the gameobject that the camera is attached to. {@code null}
     * detaches the camera from any gameobject.
     *
     * @param gameObject The gameobject to attach to
     */
    public void setGameObject(@Nullable GameObject gameObject) {
        this.gameObject = gameObject;
    }

    /**
     * Converts the given map-location to the nearest pixel on the screen.
     *
     * @param point The point to convert
     * @return The pixel closest to that point
     */
    public int2 pointToPixel(float2 point) {
        if(gameObject == null || gameObject.map == null)
            throw new IllegalStateException("Cannot convert from map location because the camera is not rendering a map");
        return point.added(halfResolution).subed(gameObject.location).rotateAround(halfResolution, -gameObject.angle).toI();
    }

    /**
     * Converts the given pixel to map coordinates.
     *
     * @param pixel The pixel to convert
     * @return The point under the pixel
     */
    public float2 pixelToPoint(int2 pixel) {
        if(gameObject == null || gameObject.map == null)
            throw new IllegalStateException("Cannot convert to map location because the camera is not rendering a map");
        return pixel.toF().rotateAround(halfResolution, gameObject.angle).add(gameObject.location).sub(halfResolution);
    }

    /**
     * Returns the ui of this camera. {@code null} indicates that this camera currently
     * has no ui.
     *
     * @return The camera's ui
     */
    public UI getUI() {
        return ui;
    }

    /**
     * Sets the ui of this camera. {@code null} will remove any ui.
     *
     * @param ui The ui to set
     */
    public void setUI(@Nullable UI ui) {
        if(this.ui == ui) return;
        if(this.ui != null) this.ui.camera = null;
        this.ui = ui;
        if(ui != null) {
            ui.camera = this;
            ui.modified(); // for potentially setting the background color
        }
    }

    /**
     * Calls update on map and ui, if present.
     */
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
     * Prepares rendering the current state of map and ui by saving its current state.
     *
     * <p>This is an internal method. It should be called from the update thread only.</p>
     *
     * @return The time the preparation took, in milliseconds
     */
    public long prepareRender() {

        long start = System.nanoTime();

        GameObject[] gameObjects = new GameObject[0];

        if(gameObject != null && gameObject.map != null) {
            // Filter out objects with images that may be on the screen
            float2 loc = gameObject.location;
            gameObjects = gameObject.map.paintOrderObjects.stream()
                    .filter(o -> {
                        Image image = o.getImage();
                        if(image == null || Image.definitelyBlank(image)) return false;
                        if(o == gameObject) return true;

                        // Simple circle collision detection
                        float w = image.size.x / 2f + halfResolution.x, h = image.size.y / 2f + halfResolution.y;
                        float maxSqrDistance = w * w + h * h;
                        float sqrDistance = float2.sqrDist(loc, o.location);

                        return sqrDistance < maxSqrDistance;
                    }).toArray(GameObject[]::new);
        }

        List<UIObject> uiObjects = new ArrayList<>();
        if(ui != null)
            for(UIObject o : ((UIObject)ui).paintOrderIterator(true))
                uiObjects.add(o);

        int errorMessage = gameObject != null && gameObject.map == null ? 1 : 0;
        drawCount = gameObjects.length + uiObjects.size() + errorMessage;

//        Console.mapDebug("Objects to draw", drawCount);

        // Don't read drawObjects for rendering while it's being edited
        synchronized (drawObjects) {

            // Match the number of draw objects and the number of gameobjects to draw
            if(drawCount < drawObjects.size()) {
                List<DrawObject> additionalObjects = drawObjects.subList(drawCount, drawObjects.size());
                DrawObject.returnObjects(additionalObjects);
                additionalObjects.clear();
            }
            else while(drawCount > drawObjects.size())
                    drawObjects.add(DrawObject.get());

            if(gameObject != null && gameObject.map != null) {
                float2 screenOffset = halfResolution.subed(gameObject.location);

                // Set a draw object for each gameobject (draw first -> below UI)
                for (int i = 0; i < gameObjects.length; i++) {
                    GameObject g = gameObjects[i];
                    DrawObject drawObject = drawObjects.get(i);

                    drawObject.image = Image.getImplementation(g.getImage());
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

                drawObject.image = Image.getImplementation(u.getImage());
                drawObject.rotation = 0;
                drawObject.screenLocation.set(u.getCachedScreenPos());
            }

            renderHash = Objects.hash(backgroundColor, resolution, drawObjects);
        }

        // Reset the cache immediately after using it to use less resources rather than
        // having it stored unused until the next rendering
        if(ui != null) ((UIObject) ui).resetCache();

        return renderPrepDuration = System.nanoTime() - start;
    }

    /**
     * Renders the last prepared state of the camera. The state has to
     * be prepared previously using {@link #prepareRender()}. May be called from
     * any thread at any time.
     *
     * @return The time the rendering took, in milliseconds
     */
    public long render() {

        long start = System.nanoTime();

        DrawObject[] drawObjects;

        // Possibly wait for prepareRender()
        synchronized (this.drawObjects) {
            // Did anything change?
            if(Time.realTime() - lastRenderTime < 1 && lastRenderHash == (lastRenderHash = renderHash))
                return renderDuration = System.nanoTime() - start;
            // Create a copy to prevent any modifications from other threads
            drawObjects = this.drawObjects.toArray(new DrawObject[0]);
        }
        lastRenderTime = Time.realTime();

        DISPLAY.draw(drawObjects, backgroundColor);

        // Don't return drawObjects here, the whole list will be reused as much as
        // possible and excess will be returned during the next prepareRender() call

        return renderDuration = System.nanoTime() - start;
    }



    /**
     * Returns the number of objects on the screen / potentially on the screen
     * from the last frame.
     *
     * @return The current number of drawn objects
     */
    public int getDrawCount() {
        synchronized (drawObjects) {
            return drawObjects.size();
        }
    }



    /**
     * Returns whether this camera is the currently active camera.
     *
     * @return Whether this camera is active
     */
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
    @NotNull
    public static Camera getActive() {
        return active;
    }

    /**
     * Sets the given camera as active camera. The first camera created will
     * automatically set itself as active, so there is no need to set it
     * active manually. Passing null will stop rendering any camera and show
     * an error message instead.
     *
     * @param active The camera to set
     */
    public static void setActive(@Nullable Camera active) {
        if(active == null) active = NoCameraCamera.INSTANCE;
        if(Camera.active == active) return;
        Camera old = Camera.active;
        Camera.active = active;
        if(!Objects.equals(old.resolution, active.resolution)) {
            if(active == NoCameraCamera.INSTANCE)
                active.setResolution(old.resolution);
            else DISPLAY.setResolution(active.resolution);
            if(active.ui != null)
                active.ui.onParentSizeChange.invoke(active.resolution);
        }
    }
}
