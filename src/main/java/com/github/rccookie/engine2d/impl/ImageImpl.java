package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Cloneable;

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
     * @param center The center coordinates of the oval
     * @param size Width and height of the oval
     * @param color The color to use
     */
    void fillOval(int2 center, int2 size, Color color);

    /**
     * Draws the outline of the given oval.
     *
     * @param center The center coordinates of the oval
     * @param size Width and height of the oval
     * @param color The color to use
     */
    void drawOval(int2 center, int2 size, Color color);

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
}
