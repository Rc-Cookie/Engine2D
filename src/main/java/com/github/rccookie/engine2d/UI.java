package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The ui is a special kind of ui object, it gets attached to a camera and is always
 * the root of its ui tree.
 */
public class UI extends UIObject {

    /**
     * The camera this ui is currently attached to.
     */
    Camera camera = null;
    /**
     * Whether the camera's background should be colored according to the ui's theme.
     */
    private boolean themedCameraBackground = false;

    /**
     * Whether mouse collision should be pixel perfect.
     */
    private boolean pixelPerfectMouse = false;


    /**
     * The objects touching the mouse at the last time of rendering, ordered from top
     * to bottom.
     */
    @NotNull
    final List<UIObject> mouseObjects = new ArrayList<>();
    /**
     * View of {@link #mouseObjects}.
     */
    private final List<UIObject> mouseObjectsView = Collections.unmodifiableList(mouseObjects);


    /**
     * Creates a new ui.
     */
    public UI() {
        this(null);
    }

    /**
     * Creates a new ui and attaches it to the given camera.
     *
     * @param camera The camera to attach to, or {@code null}
     */
    public UI(@Nullable Camera camera) {
        super(null);
        if(camera != null)
            camera.setUI(this);
        update.add(this::updateMouseObjects);
        onParentSizeChange.add(s -> {
            for(UIObject child : getChildren())
                child.onParentSizeChange.invoke(s);
        });
    }


    @NotNull
    @Override
    public String toString() {
        return getName();
    }

    @Nullable
    @Contract("-> null")
    @Override
    public UIObject getParent() {
        return null;
    }

    @Contract("_ -> fail")
    @Override
    public void setParent(UIObject parent) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Contract("-> this")
    @Override
    public UI getUI() {
        return this;
    }

    @NotNull
    @Contract("-> this")
    @Override
    public UIObject getRoot() {
        return this;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @NotNull
    @Override
    public int2 getSize() {
        return camera != null ? camera.getResolution() : int2.ZERO;
    }

    @Override
    public int2 calcScreenPos(boolean cached) {
        return camera != null ? camera.resolution.dived(2) : null;
    }

    @Nullable
    @Contract("-> null")
    @Override
    protected Image generateImage() {
        if(themedCameraBackground && camera != null)
            camera.setBackgroundColor(getTheme().first);
        return null;
    }

    /**
     * Returns whether the camera background gets updated with the theme color.
     *
     * @return Whether this ui manages the camera background color
     */
    public boolean isThemedCameraBackground() {
        return themedCameraBackground;
    }

    /**
     * Sets whether this ui should color the camera's background in the ui's
     * theme.
     *
     * @param themedCameraBackground Whether the camera background should be themed
     */
    public void setThemedCameraBackground(boolean themedCameraBackground) {
        if(this.themedCameraBackground == themedCameraBackground) return;
        this.themedCameraBackground = themedCameraBackground;
        if(themedCameraBackground) modified();
    }

    /**
     * Returns whether mouse collision is pixel perfect, meaning that fully
     * transparent pixels don't count from mouse containment.
     *
     * @return Whether this ui tree uses pixel perfect mouse collision
     */
    public boolean isPixelPerfectMouse() {
        return pixelPerfectMouse;
    }

    /**
     * Sets whether mouse collision should be pixel perfect, meaning that
     * fully transparent pixels don't count for mouse containment.
     *
     * @param pixelPerfectMouse Whether this ui tree should use pixel
     *                          perfect mouse collision
     */
    public void setPixelPerfectMouse(boolean pixelPerfectMouse) {
        this.pixelPerfectMouse = pixelPerfectMouse;
    }


    /**
     * Updates {@link #mouseObjects}.
     */
    private void updateMouseObjects() {
        mouseObjects.clear();
        if(camera == null) return;
        addObjectsAtTo(Input.getMouse().pixel, pixelPerfectMouse, false, mouseObjects);
    }

    /**
     * Returns all ui objects that are touching the mouse pixel, ordered from top to bottom.
     *
     * @return All objects at the mouse pixel
     */
    @NotNull
    public List<UIObject> getObjectsAtMouse() {
        return mouseObjectsView;
    }

    /**
     * Returns all ui objects at the given pixel, ordered from top to bottom.
     *
     * @param pixel The pixel to test
     * @return All objects at that pixel
     */
    @NotNull
    public List<UIObject> getObjectsAt(@NotNull int2 pixel) {
        return getObjectsAt(pixel, pixelPerfectMouse);
    }

    /**
     * Returns all ui objects at the given pixel, ordered from top to bottom.
     *
     * @param pixel The pixel to test
     * @param pixelPerfect Whether pixel-perfect collision should be used
     * @return All objects at that pixel
     */
    @NotNull
    public List<UIObject> getObjectsAt(@NotNull int2 pixel, boolean pixelPerfect) {
        return getObjectsAt(pixel, pixelPerfect, false);
    }

    /**
     * Returns all ui objects at the given pixel, ordered from top to bottom.
     *
     * @param pixel The pixel to test
     * @param pixelPerfect Whether pixel-perfect collision should be used
     * @param includeClickThrough Whether ui objects with click-through enabled
     *                            should be included
     * @return All objects at that pixel
     */
    @NotNull
    public List<UIObject> getObjectsAt(@NotNull int2 pixel, boolean pixelPerfect, boolean includeClickThrough) {
        List<UIObject> objects = new ArrayList<>();
        addObjectsAtTo(pixel, pixelPerfect, includeClickThrough, objects);
        return objects;
    }

    /**
     * Adds all objects that touch the specified pixel to the given list, ordered
     * from top to bottom.
     *
     * @param pixel The pixel to test
     * @param pixelPerfect Whether pixel-perfect collision should be used
     * @param includeClickThrough Whether ui objects with click-through enabled
     *                            should be included
     * @param list Output list
     */
    private void addObjectsAtTo(@NotNull int2 pixel, boolean pixelPerfect, boolean includeClickThrough, @NotNull List<UIObject> list) {
        addAllRelevantInPaintOrder(list);
        for(int i=0; i<list.size(); i++) {
            UIObject o = list.get(i);
            if((!includeClickThrough && o.clickThrough) || !o.contains(pixel, pixelPerfect))
                list.remove(i--);
        }
    }
}
