package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class TextButton extends Button {

    public final Text text;

    private final IVec2 border = new IVec2(8, 4);
    @NotNull
    private ThemeColor backgroundColor = ThemeColor.FIRST;

    public TextButton(UIObject parent, String title) {
        super(parent);

        text = new Text(this, title) {
            @Override
            public void setBackgroundColor(ThemeColor backgroundColor) {
                throw new UnsupportedOperationException();
            }
        };
        text.setVisible(false);
        text.lockStructure();
        text.onChange.add(this::modified);

        setMinSize(new IVec2(70, 30));
    }

    public IVec2 getBorder() {
        return border.divided(2);
    }

    @NotNull
    public ThemeColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBorder(IVec2 border) {
        this.border.set(border.x * 2, border.y * 2);
        modified();
    }

    public void setBackgroundColor(@NotNull ThemeColor backgroundColor) {
        if(this.backgroundColor.equals(Arguments.checkNull(backgroundColor))) return;
        this.backgroundColor = Arguments.checkNull(backgroundColor);
        modified();
    }

    @Override
    protected Image generatePlainImage() {

        Image textImage = this.text.getImage();
        Image image = new Image(clampSize(textImage.size.added(border)), backgroundColor.get(getTheme()));
        image.drawImageCr(textImage, image.center.added(0, -2));
        image.drawRect(IVec2.ZERO, image.size, Color.LIGHT_GRAY);
        image.drawRect(IVec2.ONE, image.size.added(-2, -2), Color.LIGHT_GRAY);
        return image;
    }
}
