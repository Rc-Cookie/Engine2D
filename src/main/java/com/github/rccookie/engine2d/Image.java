package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.ILine2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an image.
 */
public class Image implements Cloneable<Image> {

    /**
     * The image's size, in pixels. Do not modify!
     */
    public final int2 size;

    /**
     * The image's center coordinates. Do not modify!
     */
    public final int2 center;

    /**
     * The underlying image implementation.
     */
    final ImageImpl impl;

    /**
     * Saves whether this image is definetly blank, meaning that drawing it somewhere
     * has no effect.
     */
    boolean definitelyBlank = true;


    /**
     * Creates a new image.
     *
     * @param width The image's with, in pixels
     * @param height The image's height, in pixels
     */
    public Image(int width, int height) {
        this(new int2(width, height));
    }

    /**
     * Creates a new image.
     *
     * @param size The image's size, in pixels
     */
    public Image(int2 size) {
        this(Application.getImplementation().getImageFactory().createNew(size), size.clone());
    }

    /**
     * Creates a new image filled with the given color.
     *
     * @param width The image's width, in pixels
     * @param height The image's height, in pixels
     * @param color The base color for the image
     */
    public Image(int width, int height, Color color) {
        this(new int2(width, height), color);
    }

    /**
     * Creates a new image filled with the given color.
     *
     * @param size The image's size, in pixels
     * @param color The base color for the image
     */
    public Image(int2 size, Color color) {
        this(size);
        fill(color);
    }

    /**
     * Creates a new image from the given implementation with the specified size.
     *
     * @param impl The image implementation to use
     * @param size The size, must be equal to the implementation's size
     */
    private Image(ImageImpl impl, int2 size) {
        Application.checkSetup();
        this.impl = impl;
        this.size = size;
        this.center = size.dived(2);
    }

    /**
     * Creates a new image from the given implementation.
     *
     * @param impl The image implementation to use
     */
    private Image(ImageImpl impl) {
        this(impl, impl.getSize());
        definitelyBlank = false;
    }


    /**
     * Returns a copy of this image.
     *
     * @return A new, identical image
     */
    @Override
    @NotNull
    public Image clone() {
        return new Image(impl.clone());
    }

    /**
     * Fills the specified rectangle with the given color.
     *
     * @param topLeft The top left of the rectangle
     * @param size Size of the rectangle
     * @param color Color to use
     */
    public void fillRect(int2 topLeft, int2 size, Color color) {
        if(color.a == 0) return;
        impl.fillRect(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Fills the whole image with the given color.
     *
     * @param color The color to use
     */
    public void fill(Color color) {
        fillRect(int2.ZERO, size, color);
    }

    /**
     * Draws the outline of the specified rectangle.
     *
     * @param topLeft The top left of the rectangle
     * @param size The size of the rectangle
     * @param color The color to use
     */
    public void drawRect(int2 topLeft, int2 size, Color color) {
        if(color.a == 0) return;
        impl.drawRect(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Sets the color of the specified pixel.
     *
     * @param location The location to set
     * @param color The color to set
     */
    public void setPixel(int2 location, Color color) {
        Arguments.checkRange(location.x, 0, size.x);
        Arguments.checkRange(location.y, 0, size.y);
        impl.setPixel(location, color);
        definitelyBlank &= (color.a == 0);
    }

    /**
     * Returns the color at the specified pixel.
     *
     * @param location The location of the pixel to get
     * @return The color at that pixel
     */
    public Color getPixel(int2 location) {
        return impl.getPixel(location);
    }

    /**
     * Draws the given image onto this image.
     *
     * @param image The image to draw onto this image
     * @param topLeft The pixel where the top left of the drawn image should
     *                be. Can be out of bounds of this image
     */
    public void drawImage(Image image, int2 topLeft) {
        if(image.definitelyBlank) return;
        impl.drawImage(image.impl, topLeft);
        definitelyBlank = false;
    }

    /**
     * Draws the given image onto this image.
     *
     * @param image The image to draw onto this image
     * @param center The pixel where the center of the drawn image should
     *               be. Can be out of bounds of this image
     */
    public void drawImageCr(Image image, int2 center) {
        drawImage(image, image.size.dived(-2).add(center));
    }

    /**
     * Draws a line from a to b.
     *
     * @param from Start pixel
     * @param to End pixel
     * @param color The color to use
     */
    public void drawLine(int2 from, int2 to, Color color) {
        if(color.a == 0) return;
        impl.drawLine(from, to, color);
        definitelyBlank = false;
    }

    /**
     * Draws a line from a to b.
     *
     * @param line The line to draw
     * @param color The color to use
     */
    public void drawLine(ILine2 line, Color color) {
        drawLine(line.a, line.b, color);
    }

    /**
     * Draws the outline of an oval onto this image.
     *
     * @param center The center of the oval
     * @param size The size of the oval
     * @param color The color to use
     */
    public void drawOval(int2 center, int2 size, Color color) {
        if(color.a == 0) return;
        impl.drawOval(center, size, color);
        definitelyBlank = false;
    }

    /**
     * Draws the outline of a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void drawCircle(int2 center, int radius, Color color) {
        drawOval(center, new int2(radius * 2, radius * 2), color);
    }

    /**
     * Draws an oval onto this image.
     *
     * @param center The center of the oval
     * @param size The size of the oval
     * @param color The color to use
     */
    public void fillOval(int2 center, int2 size, Color color) {
        if(color.a == 0) return;
        impl.fillOval(center, size, color);
        definitelyBlank = false;
    }

    /**
     * Draws a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void fillCircle(int2 center, int radius, Color color) {
        fillOval(center, new int2(radius, radius), color);
    }

    /**
     * Clears this image.
     */
    public void clear() {
        impl.clear();
        definitelyBlank = true;
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param newSize The size to scale to
     * @return The scaled copy
     */
    public Image scaled(int2 newSize) {
        return scaled(newSize, AntialiasingMode.OFF);
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param newSize The size to scale to
     * @param aaMode The anti aliasing mode, if available
     * @return The scaled copy
     */
    public Image scaled(int2 newSize, AntialiasingMode aaMode) {
        if(newSize.equals(size)) return clone();
        Image scaled = new Image(impl.scaled(newSize, aaMode));
        scaled.definitelyBlank = definitelyBlank;
        return scaled;
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param factor The factor to scale by
     * @param aaMode The anti aliasing mode, if available
     * @return The scaled copy
     */
    public Image scaled(float factor, AntialiasingMode aaMode) {
        return scaled(size.scaled(factor).toI(), aaMode);
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param factor The factor to scale by
     */
    public Image scaled(float factor) {
        return scaled(factor, AntialiasingMode.OFF);
    }

    /**
     * Returns a scaled copy of this image scaled to the given width
     * while remaining the aspect ratio (as good as possible, in int space).
     *
     * @param width The target width
     * @return The scaled copy
     */
    public Image scaledToWidth(int width) {
        return scaled(new int2(width, Math.max(1, size.y * width / size.x)));
    }

    /**
     * Returns a scaled copy of this image scaled to the given height
     * while remaining the aspect ratio (as good as possible, in int space).
     *
     * @param height The target height
     * @return The scaled copy
     */
    public Image scaledToHeight(int height) {
        return scaled(new int2(Math.max(1, size.x * height / size.y), height));
    }


    /**
     * Creates an image with text on a transparent background. The image will be sized to fit the
     * text exactly.
     *
     * @param text The text to be drawn
     * @param size The font size
     * @param color The text color
     * @return A new image with that text
     */
    public static Image text(String text, int size, Color color) {
        return new Image(Application.getImplementation().getImageFactory().createText(text, size, color));
    }

    /**
     * Creates a new image from the given file in png or jpg format.
     *
     * @param file The file to load from
     * @return The loaded image
     * @throws RuntimeIOException If an exception occurs loading the file
     */
    public static Image load(String file) {
        return new Image(Application.getImplementation().getImageFactory().createNew(file));
    }

    /**
     * Returns the underlying image implementation of the image. This is an
     * internal method.
     *
     * @param image The image to get the implementation from
     * @return The image implementation of the image
     */
    public static ImageImpl getImplementation(Image image) {
        return image.impl;
    }


    /**
     * Antialiasing mode. Clears up jagged edges. Warning: May not be available on all
     * implementations, and disclaimer: Java AA is horrible.
     */
    public enum AntialiasingMode {
        /**
         * Highest quality antialiasing.
         */
        HIGH,
        /**
         * Performance antialiasing.
         */
        LOW,
        /**
         * No antialiasing.
         */
        OFF
    }
}
