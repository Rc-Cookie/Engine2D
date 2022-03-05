package com.github.rccookie.engine2d.ui.debug;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.Text;

/**
 * A text that frequently updates its content to the current
 * framerate.
 */
public class FpsDisplay extends Text {

    /**
     * Creates a new fps display.
     *
     * @param parent The parent for the fps display
     */
    public FpsDisplay(UIObject parent) {
        super(parent, Time.fps() + "");
        execute.repeating(() -> setText(Time.fps() + ""), 1/30f, 0, true);
    }
}
