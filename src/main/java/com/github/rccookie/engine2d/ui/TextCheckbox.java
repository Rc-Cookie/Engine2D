package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.int2;

/**
 * A checkbox with a text next to it.
 */
public class TextCheckbox extends Dimension {

    /**
     * The number of pixels between text and checkbox.
     */
    private static final int SPACING = 5;

    /**
     * The text.
     */
    public final Text text;
    /**
     * The checkbox.
     */
    public final Checkbox checkbox;

    /**
     * Identical to {@link Checkbox#onToggle} of {@link #checkbox}.
     */
    public final ParamEvent<Boolean> onToggle;


    /**
     * Creates a new text checkbox.
     *
     * @param parent The parent for the text checkbox
     * @param title The text content next to the checkbox
     */
    public TextCheckbox(UIObject parent, String title) {
        super(parent);

        checkbox = new Checkbox(this);
        checkbox.relativeLoc.x = -1;

        onToggle = checkbox.onToggle;
        checkbox.onClick.add(onClick::invoke);
        checkbox.onPress.add(onPress::invoke);
        checkbox.onHover.add(onHover::invoke);
        checkbox.onHoverChange.add(onHoverChange::invoke);

        text = new Text(this, title);
        text.relativeLoc.x = 1;
        text.onClick.add(checkbox::toggle);
    }

    @Override
    protected void updateStructure() {
        Image textImage = text.getImage();
        Image checkboxImage = checkbox.getImage();

        setSize(new int2(
                textImage.size.x + checkboxImage.size.x + SPACING,
                Math.max(textImage.size.y, checkboxImage.size.y)));
    }

    /**
     * Returns whether the checkbox is currently on.
     *
     * @return Whether the checkbox is on
     */
    public boolean isOn() {
        return checkbox.isOn();
    }

    /**
     * Sets the checkbox to be on or off.
     *
     * @param on Whether the checkbox should be on or off
     */
    public void setOn(boolean on) {
        checkbox.setOn(on);
    }

    /**
     * Sets the checkbox to be on or off without firing any events.
     *
     * @param on Whether the checkbox should be on or off
     */
    public void silentSetOn(boolean on) {
        checkbox.silentSetOn(on);
    }
}
