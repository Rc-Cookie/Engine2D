package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A ui object that displays text.
 */
public class Text extends UIObject {

    /**
     * The content of this text.
     */
    private String text;
    /**
     * The font size of the text.
     */
    private int fontSize = 16;
    /**
     * The text color.
     */
    private ThemeColor color = ThemeColor.TEXT_FIRST;


    /**
     * Creates a new text with the given content.
     *
     * @param parent The parent for the text
     * @param text The content of the text
     */
    public Text(UIObject parent, String text) {
        super(parent);
        this.text = text;
    }

    @Override
    protected Image generateImage() {
        Image textImage = Image.text(text, fontSize, color.get(getTheme()));
        int2 clampedSize = clampSize(textImage.size);
        if(textImage.size.equals(clampedSize)) return textImage;

        Image image = new Image(textImage.size);
        image.drawImageCr(textImage, image.center);
        return image;
    }

    /**
     * Returns the content of the text.
     *
     * @return The string content
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the current font size of the text.
     *
     * @return The font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Returns the current text color of the text.
     *
     * @return The text color
     */
    public ThemeColor getColor() {
        return color;
    }

    /**
     * Sets the content of the text.
     *
     * @param text The content to use
     */
    public void setText(@NotNull String text) {
        if(Objects.equals(this.text, text)) return;
        this.text = text;
        modified();
    }

    /**
     * Sets the font size of the text.
     *
     * @param fontSize The font size to use
     */
    public void setFontSize(int fontSize) {
        if(this.fontSize == fontSize) return;
        this.fontSize = fontSize;
        modified();
    }

    /**
     * Sets the color of the text.
     *
     * @param color The color to use
     */
    public void setColor(@NotNull Color color) {
        setColor(ThemeColor.of(color));
    }

    /**
     * Sets the color of the text.
     *
     * @param color The theme color to use
     */
    public void setColor(@NotNull ThemeColor color) {
        if(Objects.equals(this.color, Arguments.checkNull(color, "color"))) return;
        this.color = color;
        modified();
    }
}
