package com.github.rccookie.engine2d.core.stats;

public enum Bottleneck {
    UPDATE("update"),
    RENDERING("rendering"),
    FPS_CAP("fps cap"),
    THREADING_UPDATE("threading (update)"),
    THREADING_RENDER("threading (render)");

    private final String description;

    Bottleneck(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
