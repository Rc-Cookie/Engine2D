package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A toggle that visually switches between two icons.
 */
public class IconToggle extends Toggle {

    /**
     * The icons used for on and off state.
     */
    @NotNull
    private Image onIcon, offIcon;

    /**
     * Creates a new IconToggle.
     *
     * @param parent The parent for the toggle
     * @param onIcon The icon to be shown when the toggle is on
     * @param offIcon The icon to be shown when the toggle is off
     */
    public IconToggle(UIObject parent, @NotNull Image onIcon, @NotNull Image offIcon) {
        super(parent);
        this.onIcon = Arguments.checkNull(onIcon, "onIcon");
        this.offIcon = Arguments.checkNull(offIcon, "offIcon");
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        return on ? onIcon : offIcon;
    }

    /**
     * Returns the icon shown when the toggle is on.
     *
     * @return The on icon
     */
    @NotNull
    public Image getOnIcon() {
        return onIcon;
    }

    /**
     * Returns the icon shown when the toggle is off.
     *
     * @return The off icon
     */
    @NotNull
    public Image getOffIcon() {
        return offIcon;
    }

    /**
     * Sets the icon to be shown when the toggle is on.
     *
     * @param onIcon The icon to set
     */
    public void setOnIcon(@NotNull Image onIcon) {
        if(this.onIcon == Arguments.checkNull(onIcon, "onIcon")) return;
        this.onIcon = onIcon;
        modified();
    }

    /**
     * Sets the icon to be shown when the toggle is off.
     *
     * @param offIcon The icon to set
     */
    public void setOffIcon(@NotNull Image offIcon) {
        if(this.offIcon == Arguments.checkNull(offIcon, "offIcon")) return;
        this.offIcon = offIcon;
        modified();
    }
}
