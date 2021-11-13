package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.geometry.performance.IVec2;

public class Image {

    public final IVec2 size;
    final ImageImpl impl;

    public Image(IVec2 size) {
        Application.checkSetup();
        this.size = size.clone();
        impl = Application
                .getImplementation()
                .getImageFactory()
                .createNew(size);
    }

    public Image(IVec2 size, Color color) {
        this(size);
        fill(color);
    }

    private Image(ImageImpl impl) {
        this.impl = impl;
        size = impl.getSize();
    }

    public Image(String file) {
        Application.checkSetup();
        impl = Application.getImplementation().getImageFactory().createNew(file);
        size = impl.getSize();
    }

    public void fillRect(IVec2 topLeft, IVec2 size, Color color) {
        impl.fillRect(topLeft, size, color);
    }

    public void fill(Color color) {
        fillRect(IVec2.ZERO, size, color);
    }

    public void drawRect(IVec2 topLeft, IVec2 size, Color color) {
        impl.drawRect(topLeft, size, color);
    }

    public void setPixel(IVec2 location, Color color) {
        impl.setPixel(location, color);
    }

    public Color getPixel(IVec2 location) {
        return impl.getPixel(location);
    }

    public void drawImage(Image image, IVec2 topLeft) {
        impl.drawImage(image.impl, topLeft);
    }

    public Image scaled(IVec2 newSize) {
        return new Image(impl.scaled(newSize));
    }

    public Image scaled(float factor) {
        return scaled(size.scaled(factor).toI());
    }



    public static ImageImpl getImplementation(Image image) {
        return image.impl;
    }
}
