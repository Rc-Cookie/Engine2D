package com.github.rccookie.engine2d.ui;

import java.util.Objects;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * Abstract definition of an animated button that created different
 * animation stages on demand and caches them.
 */
public abstract class Button extends UIObject {

    /**
     * Default color overlay when the button is hovered.
     */
    public static final Color HOVER_COLOR = new Color(1f, 0.1f);
    /**
     * Default color overlay when the button is pressed.
     */
    public static final Color PRESS_COLOR = new Color(0f, 0.1f);
    /**
     * Default color overlay when the button is disabled.
     */
    public static final Color DISABLE_COLOR = new Color(0f, 0.1f);


    /**
     * Cached image states.
     */
    private Image plainImage, hoveredImage, pressedImage, disabledImage;
    /**
     * Whether the cache for that image is ready, or the image needs
     * to be generated when needed.
     */
    private boolean plainReady = false, hoveredReady = false, pressedReady = false, disabledReady = false;


    /**
     * Is this button clickable? (Identical to clickability in {@link UIObject}).
     */
    private boolean clickable = true;


    /**
     * Creates a new button.
     *
     * @param parent The parent to use
     */
    public Button(UIObject parent) {
        super(parent);
        onHoverChange.add(super::modified);
        onPress.add(super::modified);
        onRelease.add(super::modified);
    }

    @Override
    public void modified() {
        plainImage = hoveredImage = pressedImage = disabledImage = null;
        plainReady = hoveredReady = pressedReady = disabledReady = false;
        super.modified();
    }

    @Override
    protected Image generateImage() {
        return isClickable() ? isHovered() ? isPressed() ?
                getPressedImage() : getHoveredImage() : getPlainImage() : getDisabledImage();
    }

    /**
     * Gets the plain image cache or generates it if needed.
     *
     * @return The plain image
     */
    Image getPlainImage() {
        if(!plainReady) {
            plainImage = Objects.requireNonNull(generatePlainImage(), "The button image may not be null");
            plainReady = true;
        }
        return plainImage;
    }

    /**
     * Gets the hovered image cache or generates it if needed.
     *
     * @return The hovered image
     */
    Image getHoveredImage() {
        if(!hoveredReady) {
            hoveredImage = Objects.requireNonNull(generateHoveredImage(getPlainImage()), "The button image may not be null");
            hoveredReady = true;
        }
        return hoveredImage;
    }

    /**
     * Gets the pressed image cache or generates it if needed.
     *
     * @return The pressed image
     */
    Image getPressedImage() {
        if(!pressedReady) {
            pressedImage = Objects.requireNonNull(generatePressedImage(getPlainImage()), "The button image may not be null");
            pressedReady = true;
        }
        return pressedImage;
    }

    /**
     * Gets the disabled image cache or generates it if needed.
     *
     * @return The disabled image
     */
    Image getDisabledImage() {
        if(!disabledReady) {
            disabledImage = Objects.requireNonNull(generateDisabledImage(getPlainImage()), "The button image may not be null");
            disabledReady = true;
        }
        return disabledImage;
    }

    /**
     * Generates the plain image for the button when it is not
     * touched or pressed.
     *
     * @return The button's image
     */
    @NotNull
    protected abstract Image generatePlainImage();

    /**
     * Generates the image when the button is hovered from the
     * plain image.
     *
     * @param plain The plain image (don't modify!)
     * @return The hovered image
     */
    protected Image generateHoveredImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(int2.ZERO, image.size, HOVER_COLOR);
        return image;
    }

    /**
     * Generates the image when the button is clicked from the
     * plain image.
     *
     * @param plain The plain image (don't modify!)
     * @return The clicked image
     */
    protected Image generatePressedImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(int2.ZERO, image.size, PRESS_COLOR);
        return image;
    }

    /**
     * Generates the image when the button is disabled from the
     * plain image.
     *
     * @param plain The plain image (don't modify!)
     * @return The disabled image
     */
    protected Image generateDisabledImage(Image plain) {
        Image image = plain.clone();
        image.fillRect(int2.ZERO, image.size, DISABLE_COLOR);
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
        super.modified(); // <- this is the only reason for the override: don't clear the image cache!
    }
}
