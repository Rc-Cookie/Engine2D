package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * Manager class to create {@link ImageImpl} instances and to work with fonts.
 */
public interface ImageManager {

    /**
     * Creates a new image with the given size.
     *
     * @param size The size for the image
     * @return The new image
     */
    @NotNull
    ImageImpl createNew(@NotNull int2 size);

    /**
     * Loads the given image from the given file.
     *
     * @param file The path to the file
     * @return The new image
     * @throws RuntimeIOException If an exception loading the image
     *                            occurs
     */
    @NotNull
    ImageImpl createNew(@NotNull String file) throws RuntimeIOException;

    /**
     * Creates a new image with the given text on transparent background.
     * The image must fit the text exactly.
     *
     * @param text The text for the image
     * @param size The font size to use
     * @param color The text color
     * @return The new image
     */
    @NotNull
    ImageImpl createText(@NotNull String text, int size, @NotNull Color color);

    /**
     * Creates a new image with a single character with the specified font and
     * attributes. The image must fot the character <b>width</b> exactly and must
     * have a height of {@code size}.
     *
     * @param character The character to be on the image
     * @param font The font to use
     * @param color The color the character should be in
     * @return A new image with that character on it
     */
    @NotNull
    ImageImpl createCharacter(char character, @NotNull Font font, @NotNull Color color);

    /**
     * Returns the name of a supported non-serif font. The font should
     * be as 'normal' and general-purpose as possible.
     *
     * @return A supported font name
     */
    @NotNull
    String getDefaultFont();

    /**
     * Returns the name of a supported serif font. The font should
     * be as 'normal' and general-purpose as possible.
     *
     * @return A supported serif font name
     */
    @NotNull
    String getDefaultSerifFont();

    /**
     * Returns the name of a supported monospace font. The font should
     * be as 'normal' and general-purpose as possible.
     *
     * @return A supported monospace font name
     */
    @NotNull
    String getDefaultMonospaceFont();

    /**
     * Returns whether the given font is supported.
     *
     * @param font The font to check
     * @return Whether that font is supported
     */
    boolean isFontSupported(String font);
}
