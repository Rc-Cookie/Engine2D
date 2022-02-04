package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;

@FunctionalInterface
public interface ThemeColor {

    ThemeColor FIRST = t -> t.first;
    ThemeColor SECOND = t -> t.second;
    ThemeColor ACCENT = t -> t.accent;
    ThemeColor TEXT_FIRST = t -> t.textFirst;
    ThemeColor TEXT_SECOND = t -> t.textSecond;
    ThemeColor TEXT_ACCENT = t -> t.textAccent;

    Color get(Theme theme);

    static ThemeColor of(Color color) {
        return new ConstThemeColor(color);
    }
}
