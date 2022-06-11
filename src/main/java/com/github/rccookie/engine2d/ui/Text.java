package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.FontAlignment;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A ui object that displays text.
 */
public class Text extends UIObject {

    /**
     * The content of this text.
     */
    @NotNull
    private String text;
    /**
     * The font of the text.
     */
    @NotNull
    private Font font = Font.DEFAULT;
    /**
     * Whether to soft-wrap long lines.
     */
    private boolean softWrap = false;
    /**
     * The text color.
     */
    public final ColorProperty color = new ColorProperty(this, ThemeColor.TEXT_FIRST);
    /**
     * Listener to update the text's max size according to the parent's
     * size. {@code null} means that there is currently no listener
     * attached.
     */
    @Nullable
    private ParamAction<int2> parentSizeChangeListener = null;


    // TODO: Implement mouse selection rendering and ability for ctrl+c


    /**
     * Creates a new text with the given content.
     *
     * @param parent The parent for the text
     * @param text The content of the text
     */
    public Text(UIObject parent, @NotNull String text) {
        super(parent);
        this.text = Arguments.checkNull(text, "text");
        setFocusable(false);
    }

    @Override
    protected Image generateImage() {
        Color color = this.color.get();

        Image textImage;
        if(softWrap)
            textImage = font.render(text, color, getMaxSize().x);
        else
            textImage = font.render(text, color);

        int2 clampedSize = clampSize(textImage.size);
        if(textImage.size.equals(clampedSize)) return textImage;

        int overflowX;
        if(font.alignment == FontAlignment.LEFT)
            overflowX = 0;
        else if(font.alignment == FontAlignment.CENTER)
            overflowX = (clampedSize.x - textImage.size.x) / 2;
        else // font.alignment == FontAlignment.RIGHT
            overflowX = clampedSize.x - textImage.size.x;

        Image image = new Image(clampedSize);
        image.drawImage(textImage, new int2(overflowX, 0));
//        image.drawImageCr(textImage, image.center);
        return image;
    }

    /**
     * Returns the content of the text.
     *
     * @return The string content
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * Returns the current font size of the text.
     *
     * @return The font size
     */
    @NotNull
    public Font getFont() {
        return font;
    }

    /**
     * Returns whether soft-wrap for long lines is enabled.
     *
     * @return Whether soft-wrap is enabled
     */
    public boolean isSoftWrap() {
        return softWrap;
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
     * Sets the font to use for the text rendering.
     *
     * @param font The font to use
     */
    public void setFont(Font font) {
        if(this.font.equals(Arguments.checkNull(font, "font"))) return;
        this.font = font;
        modified();
    }

    /**
     * Shorthand for setting a copy of the current font with the given font size.
     *
     * @param fontSize The font size to use
     */
    public void setFontSize(int fontSize) {
        setFont(getFont().setSize(fontSize));
    }

    /**
     * Sets whether soft-wrap should be used (off by default). Soft-wrap
     * means that lines that exceed the max width specified using
     * {@link #setMaxSize(int2)} will automatically be wrapped using a
     * newline.
     *
     * @param softWrap Whether to use soft-wrap
     */
    public void setSoftWrap(boolean softWrap) {
        if(this.softWrap == softWrap) return;
        this.softWrap = softWrap;
        modified();
    }

    /**
     * Sets this text to use soft-wrap based on the parent's size.
     * <p>When enabled, this will do the following three things:</p>
     * <ul>
     *     <li>Enable soft-wrap</li>
     *     <li>Set the text's max size to the parent's size (if a
     *     parent is present)</li>
     *     <li>Attach a listener to {@link #onParentSizeChange} that updates
     *     the text's max size to the current parent's size</li>
     * </ul>
     * <p>When disabled, any attached listeners will be removed, and
     * soft-wrap will be disabled. <b>The initial max size and soft-wrap
     * state will not be restored.</b></p>
     *
     * @param softWrap Whether to use soft-wrap
     */
    public void setSoftWrapToParent(boolean softWrap) {
        if(this.softWrap == softWrap && softWrap == (parentSizeChangeListener != null)) return;
        setSoftWrap(softWrap);
        if(softWrap) {
            parentSizeChangeListener = onParentSizeChange.add(this::setMaxSize);
            UIObject parent = getParent();
            if(parent != null)
                setMaxSize(parent.getSize());
        }
        else {
            onParentSizeChange.remove(parentSizeChangeListener);
            parentSizeChangeListener = null;
        }
    }
}
