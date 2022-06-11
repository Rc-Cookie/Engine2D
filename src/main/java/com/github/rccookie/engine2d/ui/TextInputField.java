package com.github.rccookie.engine2d.ui;

import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Theme;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.ui.util.TypeWriter;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * A button that renders a text field that can be edited.
 */
public class TextInputField extends TextButton {

    /**
     * Cursor width in pixels.
     */
    private static final int CURSOR_WIDTH = 2;
    /**
     * Alpha of the selection overlay.
     */
    private static final float SELECTION_ALPHA = 0.3f;


    /**
     * Does the input field have focus?
     */
    private boolean hasFocus = false;
    /**
     * The typewriter used internally.
     */
    public final TypeWriter writer;

    /**
     * The default value if nothing is entered.
     */
    @NotNull
    private final String defaultString;

    /**
     * The color of the cursor.
     */
    public final ColorProperty cursorColor = new ColorProperty(this, ThemeColor.FIRST.complement());
    /**
     * The color of the selection overlay.
     */
    public final ColorProperty selectionColor = new ColorProperty(this, ThemeColor.TEXT_ACCENT);
    /**
     * Whether the default text should be grayed out.
     */
    private boolean grayDefault = true;

    /**
     * Called whenever the user types something or deletes something,
     * with the current content as parameter.
     */
    public final ParamEvent<String> onType;
    /**
     * Called whenever the user types enter or the input field looses
     * focus, with the current content as parameter.
     */
    public final ParamEvent<String> onSubmit;



    /**
     * Creates a new text input field that has no title.
     *
     * @param parent The parent for the input field
     */
    public TextInputField(UIObject parent) {
        this(parent, "");
    }

    /**
     * Creates a new text input field.
     *
     * @param parent The parent for the input field
     * @param title The title and default value
     */
    public TextInputField(UIObject parent, @NotNull String title) {
        this(parent, title, false);
    }

    /**
     * Creates a new text input field.
     *
     * @param parent The parent for the input field
     * @param title The title and default value
     * @param allowNewline Currently not supported
     */
    public TextInputField(UIObject parent, @NotNull String title, boolean allowNewline) {
        super(parent, title);

        defaultString = Arguments.checkNull(title, "title");

        // TODO: Implement multiline selection rendering and allow newlines
        writer = new TypeWriter(false, UnaryOperator.identity());
        writer.setString(title);
        writer.onSubmit.add(() -> setFocus(false));
        writer.onChange.add(this::modified);

        input.mousePressed.addConsuming(() -> setFocus(containsMouse(true), true));
        onClick.add(() -> setFocus(true, true));
        input.addKeyPressListener(writer.onSubmit::invoke, "esc");
        input.keyPressed.addConsuming(k -> {
            if(hasFocus) return writer.keyTyped(k);
            return false;
        });
        onEnable.add(e -> { if(!e) setFocus(false); });

        onType = writer.onChange;
        onSubmit = writer.onSubmit;

        setBorder(getBorder().scale(2).add(3, 0));
    }

    /**
     * Returns the current content of the text input field.
     *
     * @return The current text
     */
    public String getText() {
        return text.getText();
    }

    /**
     * Returns whether the text will be grayed out when the default value is set.
     *
     * @return Whether the default value will be gray
     */
    public boolean isGrayDefault() {
        return grayDefault;
    }

    /**
     * Returns whether the input field currently has focus.
     *
     * @return Whether the input field has focus
     */
    public boolean hasFocus() {
        return hasFocus;
    }

    /**
     * Returns the default value. If none was set this is an empty string.
     *
     * @return The default value
     */
    @NotNull
    public String getDefaultString() {
        return defaultString;
    }

    /**
     * Sets the validator to use to validate typed content.
     *
     * @param validator The validator to use
     */
    public void setValidator(@NotNull UnaryOperator<String> validator) {
        writer.setValidator(validator);
    }

    /**
     * Sets whether the text should be grayed out when the input field has no
     * focus and the value is the default value.
     *
     * @param grayDefault Whether show the default value in gray
     */
    public void setGrayDefault(boolean grayDefault) {
        if(this.grayDefault == grayDefault) return;
        this.grayDefault = grayDefault;
        if(!hasFocus && writer.toString().equals(defaultString))
            modified();
    }

    /**
     * Sets focus or removes focus from the input field.
     *
     * @param focus Whether the input field should have focus or not
     */
    public void setFocus(boolean focus) {
        setFocus(focus, false);
    }

    /**
     * Sets the content of this input field.
     *
     * @param text The content to set
     */
    public void setText(String text) {
        writer.setString(text);
    }

    /**
     * Sets focus or removes focus from the input field.
     *
     * @param focus Whether the input field should have focus or not
     * @param becauseSubmit Whether the focus state changed because of
     *                      an enter press
     * @return The value of {@code focus}
     */
    private boolean setFocus(boolean focus, boolean becauseSubmit) {
        if(hasFocus == focus) return focus;
        hasFocus = focus;
        if(focus)
            writer.setSelection(0, writer.toString().length());
        else if(!becauseSubmit)
            onSubmit.invoke(writer.toString());
        modified();
        return focus;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected @NotNull Image generatePlainImage() {

        String string = writer.toString();
        if(!hasFocus && string.isEmpty())
            writer.setString(string = defaultString);
        text.setText(string);
        int charCount = string.length();
        Theme theme = getTheme();
        Color textColor = text.color.get(theme);

        if(!hasFocus) {
            if(grayDefault && defaultString.equals(string))
                textColor = textColor.setAlpha(textColor.fa * 0.5f);
            return renderBackground(getPartialTextImage(0, charCount, textColor));
        }

        Image textImage;
        Color cursorColor = this.cursorColor.get();
        int cursor = writer.getCursor();
        if(writer.hasSelection()) {
            int2 selection = writer.getSelection();
            if(selection.x == 0) {
                if(selection.y == charCount) {
                    textImage = getPartialTextImage(0, charCount, textColor);
                    textImage.fillRect(cursor == 0 ? int2.zero : new int2(textImage.size.x - CURSOR_WIDTH, 0),
                            new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);
                    textImage.fill(selectionColor.get().setAlpha(SELECTION_ALPHA));
                }
                else {
                    Image selected = getPartialTextImage(0, selection.y, textColor),
                            other = getPartialTextImage(selection.y, charCount, textColor);
                    textImage = new Image(selected.size.added(other.size.x, 0));

                    textImage.drawImage(selected, int2.zero);
                    textImage.drawImage(other, new int2(selected.size.x, 0));

                    textImage.fillRect(new int2((cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH / 2, 0),
                            new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                    textImage.fillRect(int2.zero, selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
                }
            } else if(selection.y == charCount) {
                Image other = getPartialTextImage(0, selection.x, textColor),
                        selected = getPartialTextImage(selection.x, charCount, textColor);
                textImage = new Image(selected.size.added(other.size.x, 0));

                textImage.drawImage(other, int2.zero);
                textImage.drawImage(selected, new int2(other.size.x, 0));

                textImage.fillRect(new int2(other.size.x + (cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH / 2, 0),
                        new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                textImage.fillRect(new int2(other.size.x, 0), selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
            } else {
                Image first = getPartialTextImage(0, selection.x, textColor),
                        selected = getPartialTextImage(selection.x, selection.y, textColor),
                        second = getPartialTextImage(selection.y, charCount, textColor);
                textImage = new Image(first.size.added(selected.size.x + second.size.x, 0));

                textImage.drawImage(first, int2.zero);
                textImage.drawImage(selected, new int2(first.size.x, 0));
                textImage.drawImage(second, new int2(first.size.x + selected.size.x, 0));

                textImage.fillRect(new int2(first.size.x + (cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH/2, 0),
                        new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                textImage.fillRect(new int2(first.size.x, 0), selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
            }
        }
        else {
            if(cursor == 0 || cursor == charCount) {
                textImage = getPartialTextImage(0, charCount, textColor); // Don't modify the internal instance
                textImage.fillRect(cursor == 0 ? int2.zero : new int2(textImage.size.x - CURSOR_WIDTH, 0),
                        new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);
            }
            else {
                Image first = getPartialTextImage(0, cursor, textColor), second = getPartialTextImage(cursor, charCount, textColor);
                textImage = new Image(first.size.added(second.size.x, 0));

                textImage.drawImage(first, int2.zero);
                textImage.drawImage(second, new int2(first.size.x, 0));

                textImage.fillRect(new int2(first.size.x - CURSOR_WIDTH/2, 0), new int2(CURSOR_WIDTH, textImage.size.y), cursorColor);
            }
        }
        return renderBackground(textImage);
    }

    private Image getPartialTextImage(int start, int end, Color textColor) {
        return text.getFont().render(text.getText().substring(start, end), textColor);
    }

    /**
     * Draws the background and border for a given text image.
     *
     * @param textImage The text as image
     * @return A new image with background behind the text
     */
    protected Image renderBackground(@NotNull Image textImage) {
        Image image = new Image(clampSize(textImage.size.added(getBorder().scale(2))), backgroundColor.get());
        image.drawImageCr(textImage, image.center.added(0, -2));
        Color border = borderColor.get();
        image.drawRect(int2.zero, image.size, border);
        image.drawRect(int2.one, image.size.added(-2, -2), border);
        return image;
    }
}
