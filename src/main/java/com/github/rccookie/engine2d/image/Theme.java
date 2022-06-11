package com.github.rccookie.engine2d.image;

import java.util.Objects;

import com.github.rccookie.util.Arguments;

/**
 * A theme describes a collection of colors.
 */
public class Theme {

    /**
     * A bright theme.
     */
    public static final Theme BRIGHT = new Theme(
            Color.WHITE,
            new Color(0.96f),
            new Color(55, 178, 178),
            new Color(0.08f),
            Color.GRAY,
            new Color(55, 178, 178)
    );
    /**
     * A dark theme.
     */
    public static final Theme DARK = new Theme(
            Color.DARK_GRAY,
            Color.LIGHT_GRAY.darker(),
            new Color(55, 178, 178),
            Color.LIGHT_GRAY,
            Color.BLACK,
            new Color(55, 178, 178)
    );
    /**
     * A debug theme with a lot of contrast.
     */
    public static final Theme DEBUG = new Theme(
            Color.DARK_GRAY,
            Color.BLUE,
            Color.ORANGE,
            Color.WHITE,
            Color.GREEN,
            Color.MAGENTA
    );

    /**
     * The default theme.
     */
    public static final Theme DEFAULT = DARK;


    /**
     * Colors intended for areas.
     */
    public final Color first, second, accent;
    /**
     * Colors intended for text.
     */
    public final Color textFirst, textSecond, textAccent;


    /**
     * Creates a new theme.
     *
     * @param first The main color
     * @param second The second color
     * @param accent The rarely used accent color
     * @param textFirst The main text color
     * @param textSecond The second text color
     * @param textAccent The rarely used accent text color
     */
    public Theme(Color first, Color second, Color accent, Color textFirst, Color textSecond, Color textAccent) {
        this.first      = Arguments.checkNull(first);
        this.second     = Arguments.checkNull(second);
        this.accent     = Arguments.checkNull(accent);
        this.textFirst  = Arguments.checkNull(textFirst);
        this.textSecond = Arguments.checkNull(textSecond);
        this.textAccent = Arguments.checkNull(textAccent);
    }

    @Override
    public String toString() {
        return "Theme{" +
                "first=" + first +
                ", second=" + second +
                ", accent=" + accent +
                ", textFirst=" + textFirst +
                ", textSecond=" + textSecond +
                ", textAccent=" + textAccent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Theme theme = (Theme) o;
        return first.equals(theme.first)
                && second.equals(theme.second)
                && accent.equals(theme.accent)
                && textFirst.equals(theme.textFirst)
                && textSecond.equals(theme.textSecond)
                && textAccent.equals(theme.textAccent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified first color.
     *
     * @param first The color to use as first color
     * @return A new theme with that color as first, otherwise identical
     */
    public Theme setFirst(Color first) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified second color.
     *
     * @param second The color to use as second color
     * @return A new theme with that color as second, otherwise identical
     */
    public Theme setSecond(Color second) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified accent color.
     *
     * @param accent The color to use as accent color
     * @return A new theme with that color as accent, otherwise identical
     */
    public Theme setAccent(Color accent) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified text first color.
     *
     * @param textFirst The color to use as text first color
     * @return A new theme with that color as text first, otherwise identical
     */
    public Theme setTextFirst(Color textFirst) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified text second color.
     *
     * @param textSecond The color to use as text second color
     * @return A new theme with that color as text second, otherwise identical
     */
    public Theme setTextSecond(Color textSecond) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    /**
     * Creates a new theme with the specified text accent color.
     *
     * @param textAccent The color to use as text accent color
     * @return A new theme with that color as text accent, otherwise identical
     */
    public Theme setTextAccent(Color textAccent) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }
}
