package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class Switch extends Toggle {

    /**
     * Size of the switch. x must be greater or equal to y.
     */
    private final int2 size = new int2(42, 24);

    public final ColorProperty backgroundColor = new ColorProperty(this, ThemeColor.SECOND);
    public final ColorProperty handleColor = new ColorProperty(this, ThemeColor.of(new Color(0.85f)));
    public final ColorProperty activeColor = new ColorProperty(this, ThemeColor.ACCENT);

    /**
     * Creates a new toggle.
     *
     * @param parent The parent for the toggle
     */
    public Switch(UIObject parent) {
        super(parent);
    }

    @NotNull
    @Override
    public int2 getSize() {
        return size.clone();
    }

    public void setSize(@NotNull int2 size) {
        if(this.size.equals(Arguments.checkNull(size, "size"))) return;
        Arguments.checkRange(size.x, 3, null);
        Arguments.checkRange(size.y, 3, null);
        if(size.x < size.y) throw new ArgumentOutOfRangeException("Height cannot be greater than width");
        this.size.set(size);
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        Color background = on ? activeColor.get() : backgroundColor.get();
        Color foreground = handleColor.get();

        int border = size.y / 15;

        Image image = new Image(size, background);
        image.round(size.y * 0.5f);
        image.fillCircle(new int2(border + (on?size.x-size.y:0), border), size.y - 2*border, foreground);

        return image;
    }

    @Override
    protected Image generateHoveredImage(Image plain, boolean on) {
        Image image = super.generateHoveredImage(plain, on);
        image.round(size.y * 0.5f);
        return image;
    }

    @Override
    protected Image generatePressedImage(Image plain, boolean on) {
        Image image = super.generatePressedImage(plain, on);
        image.round(size.y * 0.5f);
        return image;
    }

    @Override
    protected Image generateDisabledImage(Image plain, boolean on) {
        Image image = super.generateDisabledImage(plain, on);
        image.round(size.y * 0.5f);
        return image;
    }
}
