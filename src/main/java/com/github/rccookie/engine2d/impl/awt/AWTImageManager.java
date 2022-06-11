package com.github.rccookie.engine2d.impl.awt;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.impl.ImageManager;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for {@link AWTImageImpl}s.
 */
public class AWTImageManager implements ImageManager {

    private final BufferedImage graphicsProvider = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    @Override
    public @NotNull AWTImageImpl createNew(@NotNull int2 size) {
        return new AWTImageImpl(size);
    }

    @Override
    public @NotNull AWTImageImpl createNew(@NotNull String file) throws RuntimeIOException {
        return new AWTImageImpl(file);
    }

    @Override
    public @NotNull AWTImageImpl createText(@NotNull String text, int size, @NotNull Color color) {
        Graphics2D g = graphicsProvider.createGraphics();
        Font font = new Font("Segoe UI Unicode", Font.PLAIN, size);

        FontMetrics metrics = g.getFontMetrics(font);
        font = font.deriveFont(size * size / (float) metrics.getHeight());
        g.setFont(font);
        metrics = g.getFontMetrics();

        String[] lines = text.split("\n");
        int height = metrics.getHeight() * lines.length;

        int width = 1;
        for(String line : lines) {
            int w = (int) Math.ceil(metrics.getStringBounds(line, g).getWidth());
            if(w > width) width = w;
        }

        g.dispose();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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

    @Override
    public @NotNull ImageImpl createCharacter(char character, @NotNull com.github.rccookie.engine2d.image.Font font, @NotNull Color color) {

        int flags = Font.PLAIN;
        if(font.bold) flags |= Font.BOLD;
        if(font.italic) flags |= Font.ITALIC;

        Font f = new Font(font.name, flags, font.size);

        Graphics2D g = graphicsProvider.createGraphics();
        FontMetrics metrics = g.getFontMetrics(f);
        f = f.deriveFont(font.size * font.size / (float) metrics.getHeight());
        g.setFont(f);
        metrics = g.getFontMetrics();

        int width = metrics.charWidth(character);
        if(width == 0) return new ImageImpl.ZeroSizeImageImpl(new int2(0, font.size));

        g.dispose();

        BufferedImage image = new BufferedImage(width, font.size, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(f);

        g.setColor(color.getAwtColor());
        g.drawString(character+"", 0, metrics.getAscent());

        int count = font.size / (font.bold ? 5 : 8);
        if(font.strikethrough)
            g.fillRect(0, font.size/2 - 1, width, count-1);
//            for(int i=0; i<count; i++)
//                g.drawLine(0, font.size/2 + i-1, width, font.size/2 + i-1);
        if(font.underlined)
            g.fillRect(0, metrics.getAscent() + 1, width, count-1);
//            for(int i=0; i<count; i++)
//                g.drawLine(0, metrics.getAscent() + i+1, width, metrics.getAscent() + i+1);

        g.dispose();

        return new AWTImageImpl(image);
    }

    @Override
    public @NotNull String getDefaultFont() {
        return "Segoe UI";
    }

    @Override
    public @NotNull String getDefaultSerifFont() {
        return "Sans Serif";
    }

    @Override
    public @NotNull String getDefaultMonospaceFont() {
        return "Consolas";
    }

    @Override
    public boolean isFontSupported(String font) {
        font = font.toLowerCase();
        for(String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            if(font.equals(f.toLowerCase())) return true;
        return false;
    }
}
