package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Range;

public class RadioButton extends Toggle {

    /**
     * Diameter of the button.
     */
    private int diameter = 20;

    public ColorProperty backgroundColor = new ColorProperty(this, ThemeColor.CLEAR);
    public ColorProperty borderColor = new ColorProperty(this, ThemeColor.of(new Color(0.85f)));
    public ColorProperty activeColor = new ColorProperty(this, ThemeColor.ACCENT);

    /**
     * Creates a new toggle.
     *
     * @param parent The parent for the toggle
     */
    public RadioButton(UIObject parent) {
        super(parent);
    }

    @Range(from = 1, to = Integer.MAX_VALUE)
    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(@Range(from = 1, to = Integer.MAX_VALUE) int diameter) {
        if(this.diameter == Arguments.checkRange(diameter, 1, null)) return;
        this.diameter = diameter;
        modified();
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        Image image = new Image(diameter, diameter);

        Color backgroundColor = this.backgroundColor.get(), activeColor = this.activeColor.get();
        Color borderColor = on ? activeColor : this.borderColor.get();

        int borderWidth = diameter / 10;
        for(int i=0; i<borderWidth; i++)
            image.drawCircle(new int2(i,i), diameter - 2*i, borderColor);

        if(backgroundColor.a != 0)
            //noinspection SuspiciousNameCombination
            image.fillCircle(new int2(borderWidth, borderWidth), diameter - 2*borderWidth, backgroundColor);

        if(on) {
            float innerRadius = diameter / 4f;
            image.fillCircleCr(image.size.scaled(0.5f), innerRadius, activeColor);
        }

        return image;
    }

    @Override
    protected Image generateHoveredImage(Image plain, boolean on) {
        Image image = plain.clone();
        image.fillCircle(int2.zero, diameter, hoverColor.get());
        return image;
    }

    @Override
    protected Image generatePressedImage(Image plain, boolean on) {
        Image image = plain.clone();
        image.fillCircle(int2.zero, diameter, pressedColor.get());
        return image;
    }

    @Override
    protected Image generateDisabledImage(Image plain, boolean on) {
        Image image = plain.clone();
        image.fillCircle(int2.zero, diameter, disabledColor.get());
        return image;
    }
}
