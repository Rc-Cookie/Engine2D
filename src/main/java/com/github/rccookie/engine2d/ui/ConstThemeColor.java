package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * Theme color that has a constant result value.
 */
class ConstThemeColor implements ThemeColor {

    /**
     * The constant result.
     */
    private final Color color;


    /**
     * Creates a new constant theme color.
     *
     * @param color The color to use
     */
    ConstThemeColor(@NotNull Color color) {
        this.color = Arguments.checkNull(color);
    }

    @Override
    public Color get(Theme theme) {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        return o == this ||
                (o instanceof ConstThemeColor && color.equals(((ConstThemeColor) o).color)) ||
                color.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return color.toString();
    }
}
