package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.geometry.performance.int2;

/**
 * Used when no camera is set.
 */
final class NoCameraCamera extends Camera {

    /**
     * The instance.
     */
    static final NoCameraCamera INSTANCE = new NoCameraCamera();

    static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
    private static final DrawObject[] DRAW_OBJECTS;
    private static final DrawObject TEXT_OBJECT;
    static {
        Image text = Image.text("No camera rendering", 20, Color.WHITE);
        TEXT_OBJECT = new DrawObject();
        TEXT_OBJECT.image = text.impl;
        DRAW_OBJECTS = new DrawObject[] { TEXT_OBJECT };
    }

    private NoCameraCamera() {
        super(new int2(600, 400));
    }

    // No need to do anything
    @Override
    protected void update() { }

    @Override
    public long prepareRender() {
        // Nothing required here
        return 0;
    }

    @Override
    public long render() {
        long start = System.nanoTime();
        TEXT_OBJECT.screenLocation.set(halfResolution.toI());
        DISPLAY.draw(DRAW_OBJECTS, BACKGROUND_COLOR);
        return System.nanoTime() - start;
    }
}
