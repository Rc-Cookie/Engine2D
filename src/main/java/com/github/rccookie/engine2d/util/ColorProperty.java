package com.github.rccookie.engine2d.util;

import java.util.Objects;
import java.util.function.Consumer;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Theme;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

public class ColorProperty extends Property<Color> {

    private final UIObject obj;
    @NotNull
    private ThemeColor themeColor;
    private boolean immutable = false;

    public ColorProperty(UIObject obj, ThemeColor color) {
        this.obj = Arguments.checkNull(obj, "obj");
        this.themeColor = Arguments.checkNull(color, "color");
    }

    @Override
    @NotNull
    public Color get() {
        return get(obj.getTheme());
    }

    @NotNull
    public ThemeColor getThemeColor() {
        return themeColor;
    }

    @NotNull
    public Color get(Theme theme) {
        return getThemeColor().get(theme);
    }


    @Override
    public void set(@NotNull Color color) {
        set(ThemeColor.of(color));
    }

    public void set(@NotNull ThemeColor color) {
        if(immutable)
            throw new UnsupportedOperationException("Color property is immutable");
        if(this.themeColor.equals(color)) return;

        Theme theme = obj.getTheme();
        Color old = this.themeColor.get(theme);
        this.themeColor = color;

        obj.modified();
        onChange.invoke(color.get(theme), old);
    }

    @Override
    public String toString() {
        return "[" + get() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ColorProperty)) return false;
        ColorProperty that = (ColorProperty) o;
        return Objects.equals(obj, that.obj) && Objects.equals(themeColor, that.themeColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj, themeColor);
    }

    @Override
    public void addValidator(Consumer<Color> validator) {
        throw new UnsupportedOperationException();
    }



    public static void makeImmutable(ColorProperty property) {
        property.immutable = true;
    }
}
