package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;

public abstract class Button extends UIObject {

    public static final Color HOVER_COLOR = new Color(1f, 0.1f);
    public static final Color PRESS_COLOR = new Color(0f, 0.1f);
    public static final Color DISABLE_COLOR = new Color(0f, 0.1f);

    private Image plainImage, hoveredImage, pressedImage, disabledImage;
    private boolean plainReady = false, hoveredReady = false, pressedReady = false, disabledReady = false;

    private boolean clickable = true;

    public Button(UIObject parent) {
        super(parent);
        onHoverChange.add(super::modified);
        onPress.add(super::modified);
        onRelease.add(super::modified);
    }

    @Override
    protected void modified() {
        plainImage = hoveredImage = pressedImage = disabledImage = null;
        plainReady = hoveredReady = pressedReady = disabledReady = false;
        super.modified();
    }

    @Override
    protected Image generateImage() {
        return isClickable() ? isHovered() ? isPressed() ?
                getPressedImage() : getHoveredImage() : getPlainImage() : getDisabledImage();
    }

    Image getPlainImage() {
        if(!plainReady) {
            plainImage = generatePlainImage();
            plainReady = true;
        }
        return plainImage;
    }

    Image getHoveredImage() {
        if(!hoveredReady) {
            hoveredImage = generateHoveredImage(getPlainImage());
            hoveredReady = true;
        }
        return hoveredImage;
    }

    Image getPressedImage() {
        if(!pressedReady) {
            pressedImage = generatePressedImage(getPlainImage());
            pressedReady = true;
        }
        return pressedImage;
    }

    Image getDisabledImage() {
        if(!disabledReady) {
            disabledImage = generateDisabledImage(getPlainImage());
            disabledReady = true;
        }
        return disabledImage;
    }

    protected abstract Image generatePlainImage();

    protected Image generateHoveredImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(IVec2.ZERO, image.size, HOVER_COLOR);
        return image;
    }

    protected Image generatePressedImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(IVec2.ZERO, image.size, PRESS_COLOR);
        return image;
    }

    protected Image generateDisabledImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(IVec2.ZERO, image.size, DISABLE_COLOR);
        return image;
    }


    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        if(this.clickable == clickable) return;
        this.clickable = clickable;
        super.modified();
    }
}
