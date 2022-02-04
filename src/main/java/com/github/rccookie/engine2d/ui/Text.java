package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;

public class Text extends UIObject {

    private String text;
    private int fontSize = 16;
    private ThemeColor textColor = ThemeColor.TEXT_FIRST;
    private ThemeColor backgroundColor = ThemeColor.of(Color.CLEAR);

    public Text(UIObject parent, String text) {
        super(parent);
        this.text = text;
    }

    @Override
    protected Image generateImage() {
        return getPartialImage(0, text.length(), textColor.get(getTheme()));
    }

    public String getText() {
        return text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public ThemeColor getTextColor() {
        return textColor;
    }

    public ThemeColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setText(String text) {
        if(Objects.equals(this.text, text)) return;
        this.text = text;
        modified();
    }

    public void setFontSize(int fontSize) {
        if(this.fontSize == fontSize) return;
        this.fontSize = fontSize;
        modified();
    }

    public void setTextColor(ThemeColor textColor) {
        if(Objects.equals(this.textColor, textColor)) return;
        this.textColor = textColor;
        modified();
    }

    public void setBackgroundColor(ThemeColor backgroundColor) {
        if(Objects.equals(this.backgroundColor, backgroundColor)) return;
        this.backgroundColor = backgroundColor;
        modified();
    }


    Image getPartialImage(int start, int end, Color textColor) {
        Image textImage = Image.text(text.substring(start, end), fontSize, textColor);
        Color background = backgroundColor.get(getTheme());
        if(background == null || background.equals(Color.CLEAR))
            return textImage;
        Image image = new Image(textImage.size, background);
        image.drawImage(textImage, IVec2.ZERO);
        return image;
    }
}
