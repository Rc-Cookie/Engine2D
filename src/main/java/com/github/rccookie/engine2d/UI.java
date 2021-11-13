package com.github.rccookie.engine2d;

import com.github.rccookie.geometry.performance.IVec2;

public class UI extends UIObject {

    Camera camera = null;

    @Override
    public UIObject getParent() {
        return null;
    }

    @Override
    public void setParent(UIObject parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UI getUI() {
        return this;
    }

    @Override
    public UIObject getRoot() {
        return this;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public IVec2 getSize() {
        return camera != null ? camera.getResolution() : IVec2.ZERO;
    }

    @Override
    public IVec2 calcScreenPos(boolean cached) {
        return camera != null ? camera.resolution.divided(2) : null;
    }
}
