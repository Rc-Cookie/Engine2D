package com.github.rccookie.engine2d.image;

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
     * The color black with an alpha value of 0.
     */
    ThemeColor CLEAR      = of(Color.CLEAR);
    /**
     * The color white.
     */
    ThemeColor WHITE      = of(Color.WHITE);
    /**
     * A light gray.
     */
    ThemeColor LIGHT_GRAY = of(Color.LIGHT_GRAY);
    /**
     * A gray.
     */
    ThemeColor GRAY       = of(Color.GRAY);
    /**
     * A dark gray.
     */
    ThemeColor DARK_GRAY  = of(Color.DARK_GRAY);
    /**
     * The color black.
     */
    ThemeColor BLACK      = of(Color.BLACK);
    /**
     * The color red.
     */
    ThemeColor RED        = of(Color.RED);
    /**
     * An orange.
     */
    ThemeColor ORANGE     = of(Color.ORANGE);
    /**
     * The color yellow.
     */
    ThemeColor YELLOW     = of(Color.YELLOW);
    /**
     * The color green.
     */
    ThemeColor GREEN      = of(Color.GREEN);
    /**
     * The color cyan.
     */
    ThemeColor CYAN       = of(Color.CYAN);
    /**
     * The color blue.
     */
    ThemeColor BLUE       = of(Color.BLUE);
    /**
     * The color magenta.
     */
    ThemeColor MAGENTA    = of(Color.MAGENTA);
    /**
     * A pink.
     */
    ThemeColor PINK       = of(Color.PINK);


    /**
     * Returns the color for the given theme.
     *
     * @param theme The theme to get the color for
     * @return The actual color for that theme
     */
    @NotNull
    Color get(Theme theme);

    /**
     * Returns a new theme color that always returns the complement of the
     * result of this theme color.
     *
     * @return A new theme color that always return the complement
     * @see Color#getComplement()
     */
    default ThemeColor complement() {
        return t -> get(t).getComplement();
    }

    /**
     * Returns a new theme color that always return a contrast color to
     * the result of this theme color.
     *
     * @return A new theme color that always returns a contrast color
     * @see Color#getContrast()
     */
    default ThemeColor contrast() {
        return t -> get(t).getContrast();
    }

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
