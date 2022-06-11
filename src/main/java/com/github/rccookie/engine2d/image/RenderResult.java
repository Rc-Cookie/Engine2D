package com.github.rccookie.engine2d.image;

public class RenderResult {

    public final String rendered;
    public final String remaining;
    public final Image image;

    RenderResult(String rendered, String remaining, Image image) {
        this.rendered = rendered;
        this.remaining = remaining;
        this.image = image;
    }
}
