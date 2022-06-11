package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.ui.IconPanel;
import com.github.rccookie.engine2d.ui.Structure;
import com.github.rccookie.engine2d.util.Bounds;
import com.github.rccookie.engine2d.util.TwoWayIterator;
import com.github.rccookie.engine2d.util.annotations.Constant;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The ui is a special kind of ui object, it gets attached to a camera and is always
 * the root of its ui tree.
 */
public class UI extends Structure {

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


    private final Focus focus = new Focus(null);


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

        input.addKeyPressListener(() -> {
            if(focus.getFocused() != null) return;
            focus.setFocused(this);
            focus.tab();
            if(focus.getFocused() == this) focus.remove();
            focus.ignoreNextTab = true;
        }, "tab");
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
        return camera != null ? camera.getResolution() : int2.zero;
    }



    @Override
    protected void updateStructure() {
        if(themedCameraBackground && camera != null)
            camera.setBackgroundColor(getTheme().first);
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
     * Returns the ui object that is touching the mouse pixel and at the very top.
     * If no object is found, {@code null} is returned.
     *
     * @return The object at the top at the mouse
     */
    public UIObject getObjectAtMouse() {
        return mouseObjects.isEmpty() ? null : mouseObjects.get(0);
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
        return getObjectsAt(pixel, false);
    }

    public UIObject getObjectAt(@NotNull int2 pixel) {
        return getObjectAt(pixel, false);
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

    public UIObject getObjectAt(@NotNull int2 pixel, boolean pixelPerfect) {
        return getObjectAt(pixel, pixelPerfect, false);
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

    public UIObject getObjectAt(@NotNull int2 pixel, boolean pixelPerfect, boolean includeClickThrough) {
        for(UIObject o : ((UIObject)this).paintOrderIterator(false))
            if((!o.clickThrough || includeClickThrough) && o.contains(pixel, pixelPerfect))
                return o;
        return null;
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
        for(UIObject o : ((UIObject)this).paintOrderIterator(false))
            if((!o.clickThrough || includeClickThrough) && o.contains(pixel, pixelPerfect))
                list.add(o);
    }


    @Constant
    @NotNull
    public Focus getFocus() {
        focus.removeIfFalseUI();
        return focus;
    }

    public UIObject getFocused() {
        return getFocus().getFocused();
    }

    public void setFocused(@Nullable UIObject o) {
        getFocus().setFocused(o);
    }

    @Override
    public void setFocusable(boolean focusable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFocusable() {
        return false;
    }

    @Override
    public Bounds getBounds() {
        if(camera == null) return null;
        return new Bounds(int2.zero, camera.resolution);
    }

    @Override
    protected @Nullable int2 calcScreenPos(boolean useCache) {
        return camera != null ? camera.resolution.dived(2) : null;
    }

    public class Focus extends IconPanel {

        private static final int BORDER_SIZE = 2;

        private boolean ignoreNextTab = false;
        private TwoWayIterator<UIObject> iterator;

        Focus(UIObject parent) {
            super(parent, new Image(1, 1));
            setFocusable(false);
            setClickThrough(true);

            if(parent != null) {
                if(!parent.isFocusable()) throw new IllegalArgumentException("Parent is not focusable");
                adjustImage();
                moveToTop();
            }

            onParentChange.add((p,s) -> {if(s!=ChangeType.REMOVED) moveToTop();});
            onParentSizeChange.add(this::adjustImage);
            input.addKeyPressListener(this::onTab, "tab");
            input.addKeyPressListener(this::select, "enter", " ");
            input.addKeyPressListener(this::remove, "esc");
        }

        private void onTab() {
            if(ignoreNextTab)
                ignoreNextTab = false;
            else tab(!Input.getKeyState("shift"));
        }

        public void tab() {
            tab(true);
        }

        public void tabBack() {
            tab(false);
        }

        public void tab(boolean forwards) {
            if(removeIfFalseUI()) return;

            if(iterator == null || (forwards && !iterator.hasNext()))
                iterator = ((UIObject) UI.this).focusIterator();
            else if(!forwards && !iterator.hasPrev()) {
                iterator = ((UIObject) UI.this).focusIterator();
                iterator.skipToEnd();
            }

            if((forwards && !iterator.hasNext()) || (!forwards && !iterator.hasPrev()))
                remove();
            else setFocused(forwards ? iterator.next() : iterator.prev());
        }

        public void select() {
            if(!removeIfFalseUI())
                getFocused().onClick.invoke();
        }

        @Override
        public boolean isFocusable() {
            return false;
        }

        public UIObject getFocused() {
            if(removeIfFalseUI()) return null;
            return getParent();
        }

        public void setFocused(@Nullable UIObject o) {
            if(o != null) {
                if(o != UI.this && !o.isFocusable())
                    throw new IllegalArgumentException("Object not focusable");
                if(o.getUI() != UI.this)
                    throw new IllegalArgumentException("Object must be part of the UI " + UI.this);
            }
            Console.mapDebug("Focused", o);
            if(o == null) iterator = null;
            super.setParent(o);
        }

        @Override
        public void setParent(@Nullable UIObject parent) {
            setFocused(parent);
        }


        private boolean removeIfFalseUI() {
            if(getUI() != UI.this) {
                remove();
                return true;
            }
            return false;
        }

        private void adjustImage() {

            UIObject parent = getParent();
            if(parent == null) return;

            Image image = new Image(int2.min(UI.this.getSize(), parent.getSize().added(2 * (BORDER_SIZE+1), 2 * (BORDER_SIZE+1))));
            Color color = getTheme().accent;

            for(int i=0; i<BORDER_SIZE; i++)
                image.drawRect(int2.zero.added(i,i), image.size.subed(2*i,2*i), color);
            image.round(BORDER_SIZE);

            setIcon(image);
        }
    }
}
