package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.IVec2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AWTImageImpl implements ImageImpl {

    final BufferedImage image;
    final IVec2 size;

    public AWTImageImpl(IVec2 size) {
        image = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
        this.size = size.clone();
    }

    public AWTImageImpl(String file) throws RuntimeIOException {
        BufferedImage loaded;
        try {
            loaded = ImageIO.read(new File(file));
        } catch (IOException e) { throw new RuntimeIOException(e); }

        image = new BufferedImage(loaded.getWidth(), loaded.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(loaded, 0, 0, null);
        size = new IVec2(image.getWidth(), image.getHeight());
    }

    AWTImageImpl(BufferedImage image) {
        this.image = image;
        size = new IVec2(image.getWidth(), image.getHeight());
    }

    @Override
    public void fillRect(IVec2 topLeft, IVec2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.fillRect(topLeft.x, topLeft.y, size.x, size.y);
        g.dispose();
    }

    @Override
    public void drawRect(IVec2 topLeft, IVec2 size, Color color) {
        Graphics2D g = image.createGraphics();
        g.setColor(color.getAwtColor());
        g.drawRect(topLeft.x, topLeft.y, size.x - 1, size.y - 1);
        g.dispose();
    }

    @Override
    public void setPixel(IVec2 location, Color color) {
        fillRect(location, IVec2.ONE, color);
    }

    @Override
    public Color getPixel(IVec2 location) {
        return new Color(image.getRGB(location.x, location.y));
    }

    @Override
    public void drawImage(ImageImpl image, IVec2 topLeft) {
        Graphics2D g = this.image.createGraphics();
        g.drawImage(((AWTImageImpl) image).image, topLeft.x, topLeft.y, null);
        g.dispose();
    }

    @Override
    public IVec2 getSize() {
        return size;
    }

    @Override
    public ImageImpl scaled(IVec2 newSize) {
        BufferedImage scaled = new BufferedImage(newSize.x, newSize.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return new AWTImageImpl(scaled);
    }
}
