package com.github.rccookie.engine2d.ui.debug;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.UIObject;

public class FpsDisplay extends AutoRefreshText {

    public FpsDisplay(UIObject parent) {
        super(parent, () -> "FPS: " + Time.fps(), 1/30f);
    }
}
