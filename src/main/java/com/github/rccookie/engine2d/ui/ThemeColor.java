package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;

import org.jetbrains.annotations.NotNull;

/**
 * A theme color is a color that depends on the theme used.
 */
@FunctionalInterface
public interface ThemeColor {

    /**
     * Theme color that always returns the first color of the theme.
     */
    ThemeColor FIRST = t -> t.first;

    /**
     * Theme color that always returns the second color of the theme.
     */
    ThemeColor SECOND = t -> t.second;

    /**
     * Theme color that always returns the accent color of the theme.
     */
    ThemeColor ACCENT = t -> t.accent;

    /**
     * Theme color that always returns the first text color of the theme.
     */
    ThemeColor TEXT_FIRST = t -> t.textFirst;

    /**
     * Theme color that always returns the second text color of the theme.
     */
    ThemeColor TEXT_SECOND = t -> t.textSecond;

    /**
     * Theme color that always returns the text accent color of the theme.
     */
    ThemeColor TEXT_ACCENT = t -> t.textAccent;


    /**
     * Returns the color for the given theme.
     *
     * @param theme The theme to get the color for
     * @return The actual color for that theme
     */
    Color get(Theme theme);

    /**
     * Converts the given color to a theme color that always returns that
     * color independent of the theme.
     *
     * @param color The constant value of the theme color
     * @return The theme color for the given color
     */
    static ThemeColor of(@NotNull Color color) {
        return new ConstThemeColor(color);
    }
}
