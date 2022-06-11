package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A color panel that moves itself to the background automatically and always
 * covers the same size as its parent.
 */
public class BackgroundPanel extends ColorPanel {

    /**
     * Creates a new background panel in the specified color.
     *
     * @param parent The parent for the panel
     * @param color The color of the panel
     */
    public BackgroundPanel(UIObject parent, Color color) {
        this(parent, ThemeColor.of(color));
    }

    /**
     * Creates a new background panel in the specified color.
     *
     * @param parent The parent for the panel
     * @param color The theme color of the panel
     */
    public BackgroundPanel(UIObject parent, ThemeColor color) {
        super(parent, int2.one, color);
        if(parent != null) {
            moveToBack();
            super.setSize(parent.getSize());
        }

        onParentSizeChange.add(super::setSize);
        onParentChange.add((p,t) -> { if(t == ChangeType.ADDED) moveToBack(); });
    }

    /**
     * Throws an {@link UnsupportedOperationException}: the size get's updated
     * automatically.
     *
     * @throws UnsupportedOperationException Always.
     */
    @Override
    @Contract(value = "_->fail")
    public void setSize(@NotNull int2 size) {
        throw new UnsupportedOperationException("The size of a background panel gets updated automatically");
    }

    @Override
    public boolean isFocusable() {
        return super.isFocusable() && getParent().childCount() == 1;
    }
}
