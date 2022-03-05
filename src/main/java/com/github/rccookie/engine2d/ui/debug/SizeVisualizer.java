package com.github.rccookie.engine2d.ui.debug;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.ColorPanel;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * A color panel that takes the size of its parent.
 */
public class SizeVisualizer extends ColorPanel {

    /**
     * Creates the new size visualizer in the specified color.
     *
     * @param parent The parent to visualize
     * @param color The color of the panel
     */
    public SizeVisualizer(UIObject parent, Color color) {
        super(null, int2.ONE, color);
        onParentSizeChange.add(super::setSize);
        setParent(parent);
    }

    @Override
    public void setSize(@NotNull int2 size) {
        throw new UnsupportedOperationException("Size gets set automatically");
    }
}
