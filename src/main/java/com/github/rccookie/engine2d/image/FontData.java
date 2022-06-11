package com.github.rccookie.engine2d.image;

import java.util.HashMap;
import java.util.Map;

import com.github.rccookie.engine2d.Application;

class FontData {

    private static final Map<Font, FontData> INSTANCES = new HashMap<>();

    private final Map<Long, Image> chars = new HashMap<>();
    private final Font font;

    private FontData(Font font) {
        this.font = font;
    }

    Image getChar(char c, Color color) {
        return chars.computeIfAbsent((((long)c) << 32) ^ color.rgb, ch -> new Image(Application.getImplementation().getImageFactory().createCharacter(c, font, color)));
    }

    static FontData getInstance(Font font) {
        return INSTANCES.computeIfAbsent(font, FontData::new);
    }
}
