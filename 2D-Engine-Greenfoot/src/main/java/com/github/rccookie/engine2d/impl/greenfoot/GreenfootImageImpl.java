package com.github.rccookie.engine2d.impl.greenfoot;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;
import greenfoot.GreenfootImage;
import org.jetbrains.annotations.NotNull;

public abstract class GreenfootImageImpl implements ImageImpl {

    protected final GreenfootImage image;
    protected final int2 size;

    protected GreenfootImageImpl(GreenfootImage image) {
        this.image = image;
        size = new int2(image.getWidth(), image.getHeight());
    }

    protected GreenfootImageImpl(int2 size) {
        this(new GreenfootImage(size.x, size.y));
    }

    protected GreenfootImageImpl(String file) {
        this(getImageFromFile(file));
    }

    @Override
    public abstract @NotNull ImageImpl clone();

    @Override
    public void setPixel(int2 location, Color color) {
        image.setColorAt(location.x, location.y, toGreenfootColor(color));
    }

    @Override
    public void clear() {
        image.clear();
    }

    @Override
    public Color getPixel(int2 location) {
        return fromGreenfootColor(image.getColorAt(location.x, location.y));
    }

    @Override
    public void drawImage(ImageImpl image, int2 topLeft) {
        this.image.drawImage(((GreenfootImageImpl) image).image, topLeft.x, topLeft.y);
    }

    @Override
    public int2 getSize() {
        return size;
    }


    public static greenfoot.Color toGreenfootColor(Color color) {
        return new greenfoot.Color(color.r, color.g, color.b, color.a);
    }

    public static Color fromGreenfootColor(greenfoot.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static GreenfootImage getImageFromFile(String file) throws RuntimeIOException {
        try {
            return new GreenfootImage(file);
        } catch (IllegalArgumentException e) {
            throw new RuntimeIOException(e);
        }
    }
}
