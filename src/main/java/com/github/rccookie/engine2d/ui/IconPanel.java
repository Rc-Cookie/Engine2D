package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.Nullable;

/**
 * A panel that shows an icon.
 */
public class IconPanel extends UIObject {

    /**
     * The icon of the panel.
     */
    @Nullable
    Image icon;

    /**
     * Creates a new icon panel.
     *
     * @param parent The parent for the panel
     * @param icon The icon to show
     */
    public IconPanel(UIObject parent, @Nullable Image icon) {
        super(parent);
        this.icon = icon;
        setFocusable(false);
    }

    @Override
    protected Image generateImage() {
        return icon;
    }

    /**
     * Returns the panel's icon. Equivalent to {@link #getImage()}.
     *
     * @return The panel's icon
     */
    @SuppressWarnings("NullableProblems")
    public Image getIcon() {
        return icon;
    }

    /**
     * Sets the icon of this panel to the given one.
     *
     * @param icon The icon to use
     */
    public void setIcon(@Nullable Image icon) {
        this.icon = icon;
        modified();
    }
}
