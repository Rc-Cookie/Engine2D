package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
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
        Image text = Font.MONOSPACE.render("No camera rendering", Color.WHITE);
        TEXT_OBJECT = DrawObject.get();
        TEXT_OBJECT.image = Image.getImplementation(text);
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
