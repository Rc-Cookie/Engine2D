package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A button that has an icon as design.
 */
public class IconButton extends Button {

    /**
     * The icon of this button.
     */
    @NotNull
    private Image icon;

    /**
     * Creates a new icon button.
     *
     * @param parent The parent for the button
     * @param icon The icon to use
     */
    public IconButton(UIObject parent, @NotNull Image icon) {
        super(parent);
        this.icon = Arguments.checkNull(icon, "icon");
    }

    /**
     * Returns the icon of the button.
     *
     * @return The current icon
     */
    @NotNull
    public Image getIcon() {
        return icon;
    }

    /**
     * Sets the icon of the button.
     *
     * @param icon The icon to set
     */
    public void setIcon(@NotNull Image icon) {
        this.icon = Arguments.checkNull(icon, "icon");
        modified();
    }

    /**
     * Refreshes the icon shown. If the image used as icon has been
     * changed, changes may not be reflected automatically in the button,
     * if the image had to be copied in the process. By calling this
     * method it will be ensured that the icon shown matches the current
     * image state of the icon.
     */
    public void updateIcon() {
        modified();
    }

    @Override
    protected @NotNull Image generatePlainImage() {
        int2 clampedSize = clampSize(icon.size);
        if(clampedSize.equals(icon.size)) return icon;

        Image image = new Image(clampedSize);
        image.drawImageCr(icon, image.center);
        return image;
    }
}
