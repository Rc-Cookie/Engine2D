package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;

public class ImagePanel extends UIObject {

    Image image;

    public ImagePanel(UIObject parent, Image image) {
        super(parent);
        this.image = image;
    }

    @Override
    protected Image generateImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        modified();
    }
}
