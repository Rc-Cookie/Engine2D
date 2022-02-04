package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ColorPanel extends UIObject {

    @NotNull
    private final IVec2 size;
    @NotNull
    private ThemeColor color;

    public ColorPanel(UIObject parent, @NotNull IVec2 size) {
        this(parent, size, (Color) null);
    }

    public ColorPanel(UIObject parent, @NotNull IVec2 size, Color color) {
        this(parent, size, color != null ? ThemeColor.of(color) : ThemeColor.FIRST);
    }

    public ColorPanel(UIObject parent, IVec2 size, ThemeColor color) {
        super(parent);
        this.size = size.clone();
        this.color = Arguments.checkNull(color);
    }

    @Override
    protected Image generateImage() {
        return new Image(clampSize(getSize()), color.get(getTheme()));
    }

    @NotNull
    @Override
    public IVec2 getSize() {
        return size;
    }

    public void setColor(@Nullable Color color) {
        setColor(color != null ? t -> color : t -> t.first);
    }

    public void setColor(@NotNull ThemeColor color) {
        this.color = Arguments.checkNull(color);
        modified();
    }

    public void setSize(@NotNull IVec2 size) {
        this.size.set(Arguments.checkNull(size));
        modified();
    }
}
