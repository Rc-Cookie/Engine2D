package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;

public class IconButton extends Button {

    private Image icon;

    public IconButton(UIObject parent, Image icon) {
        super(parent);
        this.icon = icon;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
        modified();
    }

    public void updateIcon() {
        modified();
    }

    @Override
    protected Image generatePlainImage() {
        Image image = new Image(clampSize(icon.size));
        image.drawImageCr(icon, image.center);
        return image;
    }
}
