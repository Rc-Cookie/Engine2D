package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import org.jetbrains.annotations.NotNull;

/**
 * A checkbox styled toggle.
 */
public class Checkbox extends Toggle {

    /**
     * Size of the checkbox.
     */
    private final int2 size = int2.ONE.scaled(20);

    /**
     * Background color.
     */
    private ThemeColor backgroundColor = ThemeColor.SECOND;
    /**
     * Color of the edge border.
     */
    private ThemeColor borderColor = ThemeColor.of(new Color(0.85f));
    /**
     * Color of the square only shown when active.
     */
    private ThemeColor activeColor = ThemeColor.ACCENT;


    /**
     * Creates a new checkbox.
     *
     * @param parent The parent for the checkbox
     */
    public Checkbox(UIObject parent) {
        super(parent);
    }


    @NotNull
    @Override
    public int2 getSize() {
        return size;
    }

    /**
     * Sets the size of the checkbox.
     *
     * @param size The size to set
     */
    public void setSize(int2 size) {
        this.size.set(size);
        modified();
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        Theme theme = getTheme();
        Image image = new Image(size);

        Color borderColor = this.borderColor.get(theme);

        image.drawRect(int2.ZERO, size, borderColor);
        image.fillRect(int2.ONE, size.added(-2, -2), backgroundColor.get(theme));
        if(on) image.fillRect(new int2(4, 4), size.added(-8, -8), activeColor.get(theme));

        Color cornerColor = borderColor.setAlpha(borderColor.fa * 0.4f);

        // Round corners
        image.setPixel(int2.ZERO,                  cornerColor);
        image.setPixel(new int2(size.x-1, 0), cornerColor);
        image.setPixel(size.subed(int2.ONE),  cornerColor);
        image.setPixel(new int2(0, size.y-1), cornerColor);

        return image;
    }

    /**
     * Returns the color used for the checkbox background.
     *
     * @return The background color
     */
    public ThemeColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the color used for the edge outline of the checkbox.
     *
     * @return The border color
     */
    public ThemeColor getBorderColor() {
        return borderColor;
    }

    /**
     * Returns the color used for the inner rectangle indicating
     * that the checkbox is clicked.
     *
     * @return The active color
     */
    public ThemeColor getActiveColor() {
        return activeColor;
    }

    /**
     * Sets the background color to the specified color.
     *
     * @param backgroundColor The color to set
     */
    public void setBackgroundColor(ThemeColor backgroundColor) {
        if(Objects.equals(this.backgroundColor, backgroundColor)) return;
        this.backgroundColor = backgroundColor;
        modified();
    }

    /**
     * Sets the border color to the specified color.
     *
     * @param borderColor The color to set
     */
    public void setBorderColor(ThemeColor borderColor) {
        if(Objects.equals(this.borderColor, borderColor)) return;
        this.borderColor = borderColor;
        modified();
    }

    /**
     * Sets the active color to the specified color.
     *
     * @param activeColor The color to set
     */
    public void setActiveColor(ThemeColor activeColor) {
        if(Objects.equals(this.activeColor, activeColor)) return;
        this.activeColor = activeColor;
        modified();
    }
}
