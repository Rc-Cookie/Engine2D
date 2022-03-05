package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;

/**
 * Factory for creating image implementations.
 */
public interface ImageImplFactory {

    /**
     * Creates a new image with the given size.
     *
     * @param size The size for the image
     * @return The new image
     */
    ImageImpl createNew(int2 size);

    /**
     * Loads the given image from the given file.
     *
     * @param file The path to the file
     * @return The new image
     * @throws RuntimeIOException If an exception loading the image
     *                            occurs
     */
    ImageImpl createNew(String file) throws RuntimeIOException;

    /**
     * Creates a new image with the given text on transparent background.
     * The image must fit the text exactly.
     *
     * @param text The text for the image
     * @param size The font size to use
     * @param color The text color
     * @return The new image
     */
    ImageImpl createText(String text, int size, Color color);
}
