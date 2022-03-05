package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * A button with a text on it.
 */
public class TextButton extends Button {

    /**
     * The text on the button.
     */
    public final Text text;

    /**
     * The border size around the text.
     */
    private final int2 border = new int2(8, 4);
    /**
     * The background color of the button.
     */
    @NotNull
    private ThemeColor backgroundColor = ThemeColor.FIRST;


    /**
     * Creates a new text button with the given title.
     *
     * @param parent The parent for the button
     * @param title The title for the button
     */
    public TextButton(UIObject parent, String title) {
        super(parent);
        setRenderOrder(RenderOrder.AFTER_CHILDREN);

        text = new Text(this, title);
        text.setVisible(false);
        text.lockStructure();
        text.onChange.add(this::modified);

        setMinSize(new int2(70, 30));
    }

    /**
     * Returns the minimum border size around the text itself.
     *
     * @return The border size
     */
    public int2 getBorder() {
        return border.dived(2);
    }

    /**
     * Returns the current background color for the button.
     *
     * @return The current background color
     */
    @NotNull
    public ThemeColor getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Sets the minimum border size around the text itself.
     *
     * @param border The border to use
     */
    public void setBorder(int2 border) {
        this.border.set(border.x * 2, border.y * 2);
        modified();
    }

    /**
     * Sets the background color of the button.
     *
     * @param backgroundColor The background color to use
     */
    public void setBackgroundColor(@NotNull ThemeColor backgroundColor) {
        if(this.backgroundColor.equals(Arguments.checkNull(backgroundColor))) return;
        this.backgroundColor = Arguments.checkNull(backgroundColor);
        modified();
    }

    @Override
    @NotNull
    protected Image generatePlainImage() {
        Image textImage = this.text.getImage();
        Image image = new Image(clampSize(textImage.size.added(border)), backgroundColor.get(getTheme()));
        image.drawImageCr(textImage, image.center.added(0, -2));
        image.drawRect(int2.ZERO, image.size, Color.LIGHT_GRAY);
        image.drawRect(int2.ONE, image.size.added(-2, -2), Color.LIGHT_GRAY);
        return image;
    }
}
