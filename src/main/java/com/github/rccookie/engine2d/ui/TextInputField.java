package com.github.rccookie.engine2d.ui;

import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.util.TypeWriter;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class TextInputField extends TextButton {

    private static final int CURSOR_WIDTH = 2;
    private static final float SELECTION_ALPHA = 0.3f;

    private boolean hasFocus = false;
    public final TypeWriter writer;

    @NotNull
    private final String defaultString;

    @NotNull
    private ThemeColor cursorColor = t -> t.first.getComplement();
    @NotNull
    private ThemeColor selectionColor = ThemeColor.TEXT_ACCENT;
    private boolean grayDefault = true;

    public final ParamEvent<String> onWrite;
    public final ParamEvent<String> onSubmit;

    public TextInputField(UIObject parent) {
        this(parent, "");
    }

    public TextInputField(UIObject parent, @NotNull String title) {
        this(parent, title, false);
    }

    public TextInputField(UIObject parent, @NotNull String title, boolean allowNewline) {
        this(parent, title, allowNewline, s->s);
    }

    public TextInputField(UIObject parent, @NotNull String title, boolean allowNewline, @NotNull UnaryOperator<String> validator) {
        super(parent, title);

        defaultString = Arguments.checkNull(title, "title");

        // TODO: Implement multiline selection rendering and allow newlines
        writer = new TypeWriter(false, validator);
        writer.setString(title);
        writer.onSubmit.add(() -> setFocus(false));
        writer.onChange.add(this::modified);

        input.mousePressed.add(() -> setFocus(containsMouse(true)));
        input.addKeyPressListener(writer.onSubmit::invoke, "esc");
        input.keyPressed.addConsuming(k -> {
            if(hasFocus) return writer.keyTyped(k);
            return false;
        });

        onWrite = writer.onChange;
        onSubmit = writer.onSubmit;

        setBorder(getBorder().scale(2).add(3, 0));
    }

    public String getText() {
        return text.getText();
    }

    @NotNull
    public ThemeColor getCursorColor() {
        return cursorColor;
    }

    @NotNull
    public ThemeColor getSelectionColor() {
        return selectionColor;
    }

    public boolean isGrayDefault() {
        return grayDefault;
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    @NotNull
    public String getDefaultString() {
        return defaultString;
    }

    public void setCursorColor(@NotNull ThemeColor cursorColor) {
        if(this.cursorColor == Arguments.checkNull(cursorColor)) return;
        this.cursorColor = cursorColor;
        if(hasFocus) modified();
    }

    public void setSelectionColor(@NotNull ThemeColor selectionColor) {
        if(this.selectionColor == Arguments.checkNull(selectionColor)) return;
        this.selectionColor = selectionColor;
        if(hasFocus && writer.hasSelection()) modified();
    }

    public void setValidator(@NotNull UnaryOperator<String> validator) {
        writer.setValidator(validator);
    }

    public void setGrayDefault(boolean grayDefault) {
        if(this.grayDefault == grayDefault) return;
        this.grayDefault = grayDefault;
        if(!hasFocus && writer.toString().equals(defaultString))
            modified();
    }

    public void setFocus(boolean focus) {
        if(hasFocus == focus) return;
        hasFocus = focus;
        if(focus)
            writer.setSelection(0, writer.toString().length());
        modified();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected Image generatePlainImage() {

        String string = writer.toString();
        if(!hasFocus && string.isEmpty())
            writer.setString(string = defaultString);
        text.setText(string);
        int charCount = string.length();
        Theme theme = getTheme();
        Color textColor = text.getTextColor().get(theme);

        if(!hasFocus) {
            if(grayDefault && defaultString.equals(string))
                textColor = textColor.setAlpha(textColor.fa * 0.5f);
            return renderBackground(text.getPartialImage(0, charCount, textColor));
        }

        Image textImage;
        Color cursorColor = this.cursorColor.get(theme);
        int cursor = writer.getCursor();
        if(writer.hasSelection()) {
            IVec2 selection = writer.getSelection();
            if(selection.x == 0) {
                if(selection.y == charCount) {
                    textImage = text.getPartialImage(0, charCount, textColor);
                    textImage.fillRect(cursor == 0 ? IVec2.ZERO : new IVec2(textImage.size.x - CURSOR_WIDTH, 0),
                            new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);
                    textImage.fill(selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
                }
                else {
                    Image selected = text.getPartialImage(0, selection.y, textColor),
                            other = text.getPartialImage(selection.y, charCount, textColor);
                    textImage = new Image(selected.size.added(other.size.x, 0));

                    textImage.drawImage(selected, IVec2.ZERO);
                    textImage.drawImage(other, new IVec2(selected.size.x, 0));

                    textImage.fillRect(new IVec2((cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH / 2, 0),
                            new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                    textImage.fillRect(IVec2.ZERO, selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
                }
            } else if(selection.y == charCount) {
                Image other = text.getPartialImage(0, selection.x, textColor),
                        selected = text.getPartialImage(selection.x, charCount, textColor);
                textImage = new Image(selected.size.added(other.size.x, 0));

                textImage.drawImage(other, IVec2.ZERO);
                textImage.drawImage(selected, new IVec2(other.size.x, 0));

                textImage.fillRect(new IVec2(other.size.x + (cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH / 2, 0),
                        new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                textImage.fillRect(new IVec2(other.size.x, 0), selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
            } else {
                Image first = text.getPartialImage(0, selection.x, textColor),
                        selected = text.getPartialImage(selection.x, selection.y, textColor),
                        second = text.getPartialImage(selection.y, charCount, textColor);
                textImage = new Image(first.size.added(selected.size.x + second.size.x, 0));

                textImage.drawImage(first, IVec2.ZERO);
                textImage.drawImage(selected, new IVec2(first.size.x, 0));
                textImage.drawImage(second, new IVec2(first.size.x + selected.size.x, 0));

                textImage.fillRect(new IVec2(first.size.x + (cursor == selection.x ? 0 : selected.size.x) - CURSOR_WIDTH/2, 0),
                        new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);

                textImage.fillRect(new IVec2(first.size.x, 0), selected.size, selectionColor.get(theme).setAlpha(SELECTION_ALPHA));
            }
        }
        else {
            if(cursor == 0 || cursor == charCount) {
                textImage = text.getPartialImage(0, charCount, textColor); // Don't modify the internal instance
                textImage.fillRect(cursor == 0 ? IVec2.ZERO : new IVec2(textImage.size.x - CURSOR_WIDTH, 0),
                        new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);
            }
            else {
                Image first = text.getPartialImage(0, cursor, textColor), second = text.getPartialImage(cursor, charCount, textColor);
                textImage = new Image(first.size.added(second.size.x, 0));

                textImage.drawImage(first, IVec2.ZERO);
                textImage.drawImage(second, new IVec2(first.size.x, 0));

                textImage.fillRect(new IVec2(first.size.x - CURSOR_WIDTH/2, 0), new IVec2(CURSOR_WIDTH, textImage.size.y), cursorColor);
            }
        }
        return renderBackground(textImage);
    }

    protected Image renderBackground(@NotNull Image textImage) {
        Image image = new Image(clampSize(textImage.size.added(getBorder().scale(2))), getBackgroundColor().get(getTheme()));
        image.drawImageCr(textImage, image.center.added(0, -2));
        image.drawRect(IVec2.ZERO, image.size, Color.LIGHT_GRAY);
        image.drawRect(IVec2.ONE, image.size.added(-2, -2), Color.LIGHT_GRAY);
        return image;
    }
}
