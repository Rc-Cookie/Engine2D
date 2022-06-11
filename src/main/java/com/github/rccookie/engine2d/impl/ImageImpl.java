package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.ArgumentOutOfRangeException;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;

import org.jetbrains.annotations.NotNull;

/**
 * Generic implementation of an image.
 */
public interface ImageImpl extends Cloneable<ImageImpl> {

    /**
     * Fills the given rectangle.
     *
     * @param topLeft The top left of the rectangle
     * @param size The size of the rectangle
     * @param color The color to use
     */
    void fillRect(int2 topLeft, int2 size, Color color);

    /**
     * Draws the outline of the given rectangle.
     *
     * @param topLeft The top left of the rectangle
     * @param size The size of the rectangle
     * @param color The color to use
     */
    void drawRect(int2 topLeft, int2 size, Color color);

    /**
     * Fills the given oval.
     *
     * @param topLeft The top left coordinates of the oval
     * @param size Width and height of the oval
     * @param color The color to use
     */
    void fillOval(int2 topLeft, int2 size, Color color);

    /**
     * Draws the outline of the given oval.
     *
     * @param topLeft The top left coordinates of the oval
     * @param size Width and height of the oval
     * @param color The color to use
     */
    void drawOval(int2 topLeft, int2 size, Color color);

    /**
     * Draws a line from a to b.
     *
     * @param from The start pixel
     * @param to The end pixel
     * @param color The color to use
     */
    void drawLine(int2 from, int2 to, Color color);

    /**
     * Sets the color at the specified pixel.
     *
     * @param location The pixel to set
     * @param color The color to set
     */
    void setPixel(int2 location, Color color);

    /**
     * Returns the color at the specified pixel.
     *
     * @param location The pixel to get
     * @return The color at that pixel
     */
    Color getPixel(int2 location);

    /**
     * Clears the image.
     */
    void clear();

    /**
     * Draws the given image onto this image.
     *
     * @param image The image to draw. This can be expected to be of the
     *              same type as the actual implementation class
     * @param topLeft The top left for the drawn image to be
     */
    void drawImage(ImageImpl image, int2 topLeft);

    /**
     * Returns the image's size.
     *
     * @return The size of the image
     */
    int2 getSize();

    /**
     * Returns a scaled copy of this image.
     *
     * @param newSize The scaled size
     * @param aaMode The antialiasing mode to use, if available
     * @return The scaled copy
     */
    ImageImpl scaled(int2 newSize, Image.AntialiasingMode aaMode);

    final class ZeroSizeImageImpl implements ImageImpl {

        private final int2 size;

        public ZeroSizeImageImpl(int2 size) {
            Arguments.checkRange(size.x, 0, null);
            Arguments.checkRange(size.y, 0, null);
            if(size.x * size.y != 0)
                throw new IllegalArgumentException("Image not zero-sized");
            this.size = size;
        }

        @Override
        public void fillRect(int2 topLeft, int2 size, Color color) {
        }

        @Override
        public void drawRect(int2 topLeft, int2 size, Color color) {
        }

        @Override
        public void fillOval(int2 topLeft, int2 size, Color color) {
        }

        @Override
        public void drawOval(int2 topLeft, int2 size, Color color) {
        }

        @Override
        public void drawLine(int2 from, int2 to, Color color) {
        }

        @Override
        public void setPixel(int2 location, Color color) {
        }

        @Override
        public Color getPixel(int2 location) {
            throw new ArgumentOutOfRangeException();
        }

        @Override
        public void clear() {
        }

        @Override
        public void drawImage(ImageImpl image, int2 topLeft) {
        }

        @Override
        public int2 getSize() {
            return size;
        }

        @Override
        public ImageImpl scaled(int2 newSize, Image.AntialiasingMode aaMode) {
            if(newSize.x * newSize.y == 0)
                return new ZeroSizeImageImpl(newSize);
            return Application.getImplementation().getImageFactory().createNew(newSize);
        }

        @Override
        public @NotNull ImageImpl clone() {
            return this; // Class is immutable, so no need to create copy
        }
    }
}
