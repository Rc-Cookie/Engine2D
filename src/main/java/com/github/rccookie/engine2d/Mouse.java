package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.MouseData;
import com.github.rccookie.geometry.performance.IVec2;

public class Mouse {

    public final IVec2 pixel;
    public final boolean pressed;
    public final int button;
    public final boolean emulated;

    Mouse(MouseData data) {
        this(data.screenLoc, data.button, false);
    }

    private Mouse(IVec2 pixel, int button, boolean emulated) {
        this.pixel = pixel.clone();
        this.button = button;
        this.emulated = emulated;
        this.pressed = button != 0;
    }

    public IVec2 mapLoc() {
        // TODO Implement
        return pixel.clone();
    }

    @Override
    public String toString() {
        return "Mouse at " + pixel + (pressed ? "(pressed: " + button + ")" : "");
    }

    public static Mouse getEmulated(IVec2 screenLoc, int button) {
        return new Mouse(screenLoc, button, true);
    }
}
