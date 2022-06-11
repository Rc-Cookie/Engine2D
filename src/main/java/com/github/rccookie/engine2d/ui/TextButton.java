package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;

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
    public final ColorProperty backgroundColor = new ColorProperty(this, ThemeColor.FIRST);
    public final ColorProperty borderColor = new ColorProperty(this, ThemeColor.LIGHT_GRAY);


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

        String name = getClass().toString();
        name = name.substring(name.lastIndexOf('.') + 1);
        setName(name + " '" + (title.length() <= 15 ? title : title.substring(0, 13) + "...") + "'");

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
     * Sets the minimum border size around the text itself.
     *
     * @param border The border to use
     */
    public void setBorder(int2 border) {
        this.border.set(border.x * 2, border.y * 2);
        modified();
    }

    @Override
    @NotNull
    protected Image generatePlainImage() {
        Image textImage = this.text.getImage();
        Image image = new Image(clampSize(textImage.size.added(border)), backgroundColor.get());
        image.drawImageCr(textImage, image.center.added(0, -2));
        Color border = borderColor.get();
        image.drawRect(int2.zero, image.size, border);
        image.drawRect(int2.one, image.size.added(-2, -2), border);
        return image;
    }
}
