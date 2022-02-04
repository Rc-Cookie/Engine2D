package com.github.rccookie.engine2d.impl.awt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.IVec2;

public class AWTImageImplFactory implements ImageImplFactory {
    @Override
    public ImageImpl createNew(IVec2 size) {
        return new AWTImageImpl(size);
    }

    @Override
    public ImageImpl createNew(String file) throws RuntimeIOException {
        return new AWTImageImpl(file);
    }

    @Override
    public ImageImpl createText(String text, int size, Color color) {
        String[] lines = text.split("\n");
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        Font font = new Font("Segoe UI", Font.PLAIN, size);

        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        int height = metrics.getHeight() * lines.length;
        int width = 1;
        for(String line : lines) {
            int w = (int) Math.ceil(metrics.getStringBounds(line, g).getWidth());
            if(w > width) width = w;
        }

        g.dispose();
        image.flush();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);

        g.setColor(color.getAwtColor());
        int lineHeight = g.getFontMetrics().getHeight(), ascent = g.getFontMetrics().getAscent();
        for(int i=0; i<lines.length; i++)
            g.drawString(lines[i], 0, i * lineHeight + ascent);

        g.dispose();

        return new AWTImageImpl(image);
    }
}
