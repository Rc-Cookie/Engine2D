package com.github.rccookie.engine2d.impl.awt;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * AWT implementation of {@link ImageImpl} using {@link BufferedImage}.
 */
public class AWTImageImpl implements ImageImpl {

    /**
     * The internal image backing this image implementation.
     */
    final BufferedImage image;
    /**
     * The image size, cached.
     */
    final int2 size;


    /**
     * Creates a new AWTImageImpl.
     *
     * @param size The size of the image
     */
    public AWTImageImpl(int2 size) {
        image = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
        this.size = size.clone();
    }

    /**
     * Creates a new AWTImageImpl by loading from the specified file.
     *
     * @param file The path to the file to load
     * @throws RuntimeIOException If an exception occurs reading the file
     */
    public AWTImageImpl(String file) throws RuntimeIOException {
        BufferedImage loaded;
        try {
            loaded = ImageIO.read(new File(file));
        } catch (IOException e) { throw new RuntimeIOException(e); }

        image = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(loaded, 0, 0, null);
        size = new int2(image.getWidth(), image.getHeight());
    }

    /**
     * Creates a new AWTImageImpl backed with the given BufferedImage.
     *
     * @param image The BufferedImage used to back this image
     */
    AWTImageImpl(BufferedImage image) {
        this.image = image;
        size = new int2(image.getWidth(), image.getHeight());
    }


    @Override
    public @NotNull ImageImpl clone() {
        BufferedImage imageClone = new BufferedImage(size.x, size.y, image.getType());
        Graphics2D g = imageClone.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return new AWTImageImpl(imageClone);
    }


    @Override
    public void fillRect(int2 topLeft, int2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.fillRect(topLeft.x, topLeft.y, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawRect(int2 topLeft, int2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.drawRect(topLeft.x, topLeft.y, size.x - 1, size.y - 1);
        g.dispose();
    }

    @Override
    public void fillOval(int2 center, int2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.fillOval(center.x - size.x / 2, center.y - size.y / 2, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawOval(int2 center, int2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.drawOval(center.x - size.x / 2, center.y - size.y / 2, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawLine(int2 from, int2 to, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.drawLine(from.x, from.y, to.x, to.y);
        g.dispose();
    }

    @Override
    public void setPixel(int2 location, Color color) {
        image.setRGB(location.x, location.y, color.getRGB());
    }

    @Override
    public void clear() {
        Graphics2D g = image.createGraphics();
        g.clearRect(0, 0, size.x, size.y);
        g.dispose();
    }

    @Override
    public Color getPixel(int2 location) {
        return new Color(image.getRGB(location.x, location.y), true);
    }

    @Override
    public void drawImage(ImageImpl image, int2 topLeft) {
        Graphics2D g = this.image.createGraphics();
        g.drawImage(((AWTImageImpl) image).image, topLeft.x, topLeft.y, null);
        g.dispose();
    }

    @Override
    public int2 getSize() {
        return size;
    }

    @Override
    public ImageImpl scaled(int2 newSize, Image.AntialiasingMode aaMode) {
        BufferedImage scaled = new BufferedImage(newSize.x, newSize.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setComposite(AlphaComposite.Src);
        if(aaMode == Image.AntialiasingMode.OFF)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        else if(aaMode == Image.AntialiasingMode.LOW)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        else
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, newSize.x, newSize.y, null);
        g.dispose();
        return new AWTImageImpl(scaled);
    }
}
