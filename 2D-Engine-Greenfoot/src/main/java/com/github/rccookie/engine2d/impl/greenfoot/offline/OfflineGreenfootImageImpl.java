package com.github.rccookie.engine2d.impl.greenfoot.offline;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.impl.greenfoot.GreenfootImageImpl;
import com.github.rccookie.geometry.performance.int2;
import greenfoot.GreenfootImage;
import org.jetbrains.annotations.NotNull;

public class OfflineGreenfootImageImpl extends GreenfootImageImpl {

    OfflineGreenfootImageImpl(GreenfootImage image) {
        super(image);
    }

    OfflineGreenfootImageImpl(int2 size) {
        super(size);
    }

    OfflineGreenfootImageImpl(String file) {
        super(file);
    }

    @Override
    public @NotNull ImageImpl clone() {
        return new OfflineGreenfootImageImpl(new GreenfootImage(image));
    }

    @Override
    public void fillRect(int2 topLeft, int2 size, Color color) {
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setColor(color.getAwtColor());
        g.fillRect(topLeft.x, topLeft.y, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawRect(int2 topLeft, int2 size, Color color) {
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setColor(color.getAwtColor());
        g.drawRect(topLeft.x, topLeft.y, size.x - 1, size.y - 1);
        g.dispose();
    }

    @Override
    public void fillOval(int2 center, int2 size, Color color) {
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setColor(color.getAwtColor());
        g.fillOval(center.x - size.x / 2, center.y - size.y / 2, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawOval(int2 center, int2 size, Color color) {
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setColor(color.getAwtColor());
        g.drawOval(center.x - size.x / 2, center.y - size.y / 2, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawLine(int2 from, int2 to, Color color) {
        Graphics2D g = image.getAwtImage().createGraphics();
        g.setColor(color.getAwtColor());
        g.drawLine(from.x, from.y, to.x, to.y);
        g.dispose();
    }

    private static Constructor<GreenfootImage> ctor;
    private static Field imageField;

    @Override
    public ImageImpl scaled(int2 newSize, Image.AntialiasingMode aaMode) {
        // GreenfootImage creates a copy of it's BufferedImage and reassigns it to
        // itself. Another copy had to be created to return a new instance. Instead,
        // the copy is created manually and assigned to a fresh GreenfootImage using
        // reflection.
        BufferedImage scaled = new BufferedImage(newSize.x, newSize.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setComposite(AlphaComposite.Src);
        if(aaMode == Image.AntialiasingMode.OFF)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        else if(aaMode == Image.AntialiasingMode.LOW)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        else
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        GreenfootImage scaledGI;
        try {
            if(ctor == null) {
                ctor = GreenfootImage.class.getDeclaredConstructor();
                ctor.setAccessible(true);
                imageField = GreenfootImage.class.getDeclaredField("image");
                imageField.setAccessible(true);
            }
            g.drawImage((BufferedImage) imageField.get(image), 0, 0, newSize.x, newSize.y, null);
            g.dispose();
            scaledGI = ctor.newInstance();
            imageField.set(scaledGI, scaled);
        } catch(Exception e) {
            e.printStackTrace();
            scaledGI = new GreenfootImage(image);
            image.scale(newSize.x, newSize.y);
        }
        return new OfflineGreenfootImageImpl(scaledGI);
    }
}
