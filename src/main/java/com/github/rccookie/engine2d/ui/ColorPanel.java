package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A simple rectangular panel with adjustable size and a solid color.
 */
public class ColorPanel extends UIObject {

    /**
     * The size of the panel.
     */
    @NotNull
    private final int2 size;
    /**
     * The color of the panel.
     */
    public final ColorProperty color = new ColorProperty(this, ThemeColor.FIRST);


    /**
     * Creates a new color panel using the main color of the current
     * theme.
     *
     * @param parent The parent for the panel
     * @param size The size of the panel
     */
    public ColorPanel(UIObject parent, @NotNull int2 size) {
        this(parent, size, (Color) null);
    }

    /**
     * Creates a new color panel.
     *
     * @param parent The parent for the panel
     * @param size The size of the panel
     * @param color The color for the panel
     */
    public ColorPanel(UIObject parent, @NotNull int2 size, Color color) {
        this(parent, size, color != null ? ThemeColor.of(color) : ThemeColor.FIRST);
    }

    /**
     * Creates a new color panel.
     *
     * @param parent The parent for the panel
     * @param size The size of the panel
     * @param color The theme color for the panel
     */
    public ColorPanel(UIObject parent, int2 size, ThemeColor color) {
        super(parent);
        this.size = size.clone();
        this.color.set(color);
        setFocusable(false);
    }

    @Override
    protected Image generateImage() {
        return new Image(clampSize(getSize()), color.get(getTheme()));
    }

    /**
     * Returns the size of the panel.
     *
     * @return The size of the panel
     */
    @NotNull
    @Override
    public int2 getSize() {
        return size;
    }

    /**
     * Sets the size of the panel.
     *
     * @param size The size to set
     */
    public void setSize(@NotNull int2 size) {
        this.size.set(Arguments.checkNull(size));
        modified();
    }
}
