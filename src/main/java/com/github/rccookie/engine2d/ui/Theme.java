package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.util.Arguments;

public class Theme {

    public static final Theme BRIGHT = new Theme(
            Color.WHITE,
            new Color(247),
            new Color(55, 178, 178),
            new Color(20),
            Color.GRAY,
            new Color(55, 178, 178)
    );
    public static final Theme DARK = new Theme(
            Color.DARK_GRAY,
            Color.LIGHT_GRAY.darker(),
            new Color(55, 178, 178),
            Color.LIGHT_GRAY,
            Color.BLACK,
            new Color(55, 178, 178)
    );
    public static final Theme DEBUG = new Theme(
            Color.DARK_GRAY,
            Color.BLUE,
            Color.ORANGE,
            Color.WHITE,
            Color.GREEN,
            Color.MAGENTA
    );

    public static final Theme DEFAULT = DARK;

    public final Color first, second, accent;
    public final Color textFirst, textSecond, textAccent;

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

    public Theme setFirst(Color first) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    public Theme setSecond(Color second) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    public Theme setAccent(Color accent) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    public Theme setTextFirst(Color textFirst) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    public Theme setTextSecond(Color textSecond) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }

    public Theme setTextAccent(Color textAccent) {
        return new Theme(first, second, accent, textFirst, textSecond, textAccent);
    }
}
