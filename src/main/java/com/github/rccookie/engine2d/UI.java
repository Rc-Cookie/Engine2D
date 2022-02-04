package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.List;

import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Console;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UI extends UIObject {

    Camera camera = null;
    private boolean themedCameraBackground = false;

    private boolean pixelPerfectMouse = false;

    @NotNull
    final List<UIObject> mouseObjects = new ArrayList<>();

    public UI() {
        this(null);
    }

    public UI(@Nullable Camera camera) {
        super(null);
        if(camera != null)
            camera.setUI(this);
        update.add(this::updateMouseObjects);

        input.addKeyPressListener(() -> Console.info(mouseObjects), "m");
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
    @Contract("_> this")
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
    public IVec2 getSize() {
        return camera != null ? camera.getResolution() : IVec2.ZERO;
    }

    @Override
    public IVec2 calcScreenPos(boolean cached) {
        return camera != null ? camera.resolution.divided(2) : null;
    }

    @Nullable
    @Contract("-> null")
    @Override
    protected Image generateImage() {
        if(themedCameraBackground && camera != null)
            camera.setBackgroundColor(getTheme().first);
        return null;
    }

    public boolean isThemedCameraBackground() {
        return themedCameraBackground;
    }

    public void setThemedCameraBackground(boolean themedCameraBackground) {
        if(this.themedCameraBackground == themedCameraBackground) return;
        this.themedCameraBackground = themedCameraBackground;
        modified();
    }

    public boolean isPixelPerfectMouse() {
        return pixelPerfectMouse;
    }

    public void setPixelPerfectMouse(boolean pixelPerfectMouse) {
        this.pixelPerfectMouse = pixelPerfectMouse;
    }

    private void updateMouseObjects() {
        mouseObjects.clear();
        if(camera == null) return;
        addObjectsAtTo(Input.getMouse().pixel, pixelPerfectMouse, mouseObjects);
    }

    @NotNull
    public List<UIObject> getObjectsAt(@NotNull IVec2 pixel, boolean pixelPerfect) {
        List<UIObject> objects = new ArrayList<>();
        addObjectsAtTo(pixel, pixelPerfect, objects);
        return objects;
    }

    private void addObjectsAtTo(@NotNull IVec2 pixel, boolean pixelPerfect, @NotNull List<? super UIObject> list) {
        addAllRelevantInPaintOrder(list);
        for(int i=0; i<mouseObjects.size(); i++) {
            UIObject o = mouseObjects.get(i);
            if(o.clickThrough || !o.contains(pixel, pixelPerfect))
                mouseObjects.remove(i--);
        }
    }
}
