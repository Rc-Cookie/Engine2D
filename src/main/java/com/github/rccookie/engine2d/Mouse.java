package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.MouseData;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;

/**
 * Represents the state of the mouse at a given point in time.
 */
public class Mouse {

    /**
     * The left mouse button.
     */
    public static final int LEFT_BUTTON = 1;
    /**
     * The middle mouse button.
     */
    public static final int MIDDLE_BUTTON = 2;
    /**
     * The right mouse button.
     */
    public static final int RIGHT_BUTTON = 3;



    /**
     * The pixel location of the mouse, relative to the application window.
     */
    public final int2 pixel;

    /**
     * Whether a mouse button is pressed down.
     */
    public final boolean pressed;

    /**
     * The in of the button pressed down.
     */
    public final int button;

    /**
     * Whether this mouse information was emulated and is not
     * based on the actual mouse state.
     */
    public final boolean emulated;

    /**
     * Creates a new mouse info from the given mouse data.
     *
     * @param data The mouse info data
     */
    Mouse(MouseData data) {
        this(data.screenLoc, data.button, false);
    }

    /**
     * Creates a new mouse info.
     *
     * @param pixel The mouse position
     * @param button The button pressed down; 0 means no button is pressed
     * @param emulated Whether to set emulated to {@code true} or not
     */
    private Mouse(int2 pixel, int button, boolean emulated) {
        this.pixel = pixel.clone();
        this.button = button;
        this.emulated = emulated;
        this.pressed = button != 0;
    }


    /**
     * Converts the mouse position to a location on the currently shown map.
     *
     * @return The mouse position, in map coordinates
     */
    public float2 mapLoc() {
        return Camera.getActive().pixelToPoint(pixel);
    }

    /**
     * Converts this mouse to a string representation.
     *
     * @return A string representation of this object
     */
    @Override
    public String toString() {
        return "Mouse at " + pixel + (pressed ? "(pressed: " + button + ")" : "");
    }

    /**
     * Creates a mouse object that emulates the given state.
     *
     * @param screenLoc The pixel on the screen, relative to the application window
     * @param button The button pressed, 0 means no button is pressed
     * @return The mouse object
     */
    public static Mouse getEmulated(int2 screenLoc, int button) {
        return new Mouse(screenLoc, button, true);
    }
}
