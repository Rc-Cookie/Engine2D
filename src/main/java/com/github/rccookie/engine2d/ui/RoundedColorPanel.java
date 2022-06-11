package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.FloatProperty;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

public class RoundedColorPanel extends ColorPanel {

    public final FloatProperty rounding = FloatProperty.nonNegative();

    {
        rounding.onChange.add(this::modified);
    }

    public RoundedColorPanel(UIObject parent, @NotNull int2 size, float rounding) {
        this(parent, size, ThemeColor.FIRST, rounding);
    }

    public RoundedColorPanel(UIObject parent, @NotNull int2 size, Color color, float rounding) {
        this(parent, size, ThemeColor.of(color), rounding);
    }

    public RoundedColorPanel(UIObject parent, int2 size, ThemeColor color, float rounding) {
        super(parent, size, color);
        this.rounding.set(rounding);
    }


    @Override
    @NotNull
    protected Image generateImage() {
        Image image = super.generateImage();
        image.round(rounding.get());
        return image;
    }
}
