package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import org.jetbrains.annotations.NotNull;

public class Checkbox extends Toggle {

    private final IVec2 size = IVec2.ONE.scaled(20);

    private ThemeColor backgroundColor = ThemeColor.SECOND;
    private ThemeColor borderColor = ThemeColor.of(new Color(220));
    private ThemeColor activeColor = ThemeColor.ACCENT;

    public Checkbox(UIObject parent) {
        super(parent);
    }

    @NotNull
    @Override
    public IVec2 getSize() {
        return size;
    }

    public void setSize(IVec2 size) {
        this.size.set(size);
        modified();
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        Theme theme = getTheme();
        Image image = new Image(size);

        Color borderColor = this.borderColor.get(theme);

        image.drawRect(IVec2.ZERO, size, borderColor);
        image.fillRect(IVec2.ONE, size.added(-2, -2), backgroundColor.get(theme));
        if(on) image.fillRect(new IVec2(4, 4), size.added(-8, -8), activeColor.get(theme));

        Color cornerColor = borderColor.setAlpha(borderColor.fa * 0.4f);

        // Round corners
        image.setPixel(IVec2.ZERO,                  cornerColor);
        image.setPixel(new IVec2(size.x-1, 0), cornerColor);
        image.setPixel(size.subtracted(IVec2.ONE),  cornerColor);
        image.setPixel(new IVec2(0, size.y-1), cornerColor);

        return image;
    }

    public ThemeColor getBackgroundColor() {
        return backgroundColor;
    }

    public ThemeColor getBorderColor() {
        return borderColor;
    }

    public ThemeColor getActiveColor() {
        return activeColor;
    }

    public void setBackgroundColor(ThemeColor backgroundColor) {
        if(Objects.equals(this.backgroundColor, backgroundColor)) return;
        this.backgroundColor = backgroundColor;
        modified();
    }

    public void setBorderColor(ThemeColor borderColor) {
        if(Objects.equals(this.borderColor, borderColor)) return;
        this.borderColor = borderColor;
        modified();
    }

    public void setActiveColor(ThemeColor activeColor) {
        if(Objects.equals(this.activeColor, activeColor)) return;
        this.activeColor = activeColor;
        modified();
    }
}
