package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;

import org.jetbrains.annotations.NotNull;

/**
 * A button that has a distinct on and off state and switches
 * between them when clicked.
 */
public abstract class Toggle extends Button {

    /**
     * Cached images for the off state.
     */
    private Image plainImageOff, hoveredImageOff, pressedImageOff, disabledImageOff;
    /**
     * Cached images for the on state.
     */
    private Image plainImageOn, hoveredImageOn, pressedImageOn, disabledImageOn;
    /**
     * Indicates whether the respective cached off-state image is ready or needs to be generated first.
     */
    private boolean plainReadyOff = false, hoveredReadyOff = false, pressedReadyOff = false, disabledReadyOff = false;
    /**
     * Indicates whether the respective cached on-state image is ready or needs to be generated first.
     */
    private boolean plainReadyOn = false, hoveredReadyOn = false, pressedReadyOn = false, disabledReadyOn = false;


    /**
     * Is the toggle on?
     */
    boolean on = false;

    /**
     * Invoked whenever the toggle state changes, with the new state as parameter.
     */
    public final ParamEvent<Boolean> onToggle = new CaughtParamEvent<>(false);


    /**
     * Creates a new toggle.
     *
     * @param parent The parent for the toggle
     */
    public Toggle(UIObject parent) {
        super(parent);
        onToggle.add(this::onToggle);
        onClick.add(this::toggle);
    }

    @Override
    public void modified() {
        plainImageOff = hoveredImageOff = pressedImageOff = disabledImageOff = null;
        plainImageOn = hoveredImageOn = pressedImageOn = disabledImageOn = null;
        plainReadyOff = hoveredReadyOff = pressedReadyOff = disabledReadyOff = false;
        plainReadyOn = hoveredReadyOn = pressedReadyOn = disabledReadyOn = false;
        super.modified();
    }

    /**
     * Returns whether the toggle is currently on.
     *
     * @return Whether the toggle is on
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Sets the toggle to be on or off.
     *
     * @param on Whether the toggle should be on or not
     */
    public void setOn(boolean on) {
        if(this.on == on) return;
        onToggle.invoke(on);
    }

    /**
     * Sets the toggle to be on or off, but without firing the {@link #onToggle} event.
     *
     * @param on Whether the toggle should be on or not
     */
    public void silentSetOn(boolean on) {
        onToggle(on);
    }

    /**
     * Toggles the toggle.
     */
    public void toggle() {
        onToggle.invoke(!on);
    }

    /**
     * Changes the toggle state internally.
     *
     * @param on New toggle state
     */
    private void onToggle(boolean on) {
        if(this.on == on) return;
        this.on = on;
        super.modified();
    }

    @Override
    Image getPlainImage() {
        if(on) {
            if(!plainReadyOn) {
                plainImageOn = generatePlainImage(true);
                plainReadyOn = true;
            }
            return plainImageOn;
        }
        else {
            if(!plainReadyOff) {
                plainImageOff = generatePlainImage(false);
                plainReadyOff = true;
            }
            return plainImageOff;
        }
    }

    @Override
    Image getHoveredImage() {
        if(on) {
            if(!hoveredReadyOn) {
                hoveredImageOn = generateHoveredImage(getPlainImage(), on);
                hoveredReadyOn = true;
            }
            return hoveredImageOn;
        }
        else {
            if(!hoveredReadyOff) {
                hoveredImageOff = generateHoveredImage(getPlainImage(), on);
                hoveredReadyOff = true;
            }
            return hoveredImageOff;
        }
    }

    @Override
    Image getPressedImage() {
        if(on) {
            if(!pressedReadyOn) {
                pressedImageOn = generatePressedImage(getPlainImage(), on);
                pressedReadyOn = true;
            }
            return pressedImageOn;
        }
        else {
            if(!pressedReadyOff) {
                pressedImageOff = generatePressedImage(getPlainImage(), on);
                pressedReadyOff = true;
            }
            return pressedImageOff;
        }
    }

    @Override
    Image getDisabledImage() {
        if(on) {
            if(!disabledReadyOn) {
                disabledImageOn = generateDisabledImage(getPlainImage(), on);
                disabledReadyOn = true;
            }
            return disabledImageOn;
        }
        else {
            if(!disabledReadyOff) {
                disabledImageOff = generateDisabledImage(getPlainImage(), on);
                disabledReadyOff = true;
            }
            return disabledImageOff;
        }
    }

    @Override
    protected @NotNull Image generatePlainImage() {
        return generatePlainImage(on);
    }

    /**
     * Generates the toggles plain image, for the specified toggle state.
     *
     * @param on Whether the image should represent the on or off state
     * @return The generated image
     */
    protected abstract Image generatePlainImage(boolean on);

    /**
     * Generates the toggles hovered image from the plain image, for the
     * specified toggle state.
     *
     * @param plain The plain image for the same state as {@code on}
     * @param on Whether the image should represent the on or off state
     * @return The generated image
     */
    protected Image generateHoveredImage(Image plain, boolean on) {
        return generateHoveredImage(plain);
    }

    /**
     * Generates the toggles pressed image from the plain image, for the
     * specified toggle state.
     *
     * @param plain The plain image for the same state as {@code on}
     * @param on Whether the image should represent the on or off state
     * @return The generated image
     */
    protected Image generatePressedImage(Image plain, boolean on) {
        return generatePressedImage(plain);
    }

    /**
     * Generates the toggles disabled image from the plain image, for the
     * specified toggle state.
     *
     * @param plain The plain image for the same state as {@code on}
     * @param on Whether the image should represent the on or off state
     * @return The generated image
     */
    protected Image generateDisabledImage(Image plain, boolean on) {
        return generateDisabledImage(plain);
    }
}
