package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.geometry.performance.ILine2;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;
import org.jetbrains.annotations.NotNull;

public class Image implements Cloneable<Image> {

    public final IVec2 size, center;
    final ImageImpl impl;

    // Don't draw transparent objects, especially ui objects which use the image
    // as size definition
    boolean definitelyBlank = true;

    public Image(IVec2 size) {
        this(Application.getImplementation().getImageFactory().createNew(size), size.clone());
    }

    public Image(IVec2 size, Color color) {
        this(size);
        fill(color);
    }

    private Image(ImageImpl impl, IVec2 size) {
        Application.checkSetup();
        this.impl = impl;
        this.size = size;
        this.center = size.divided(2);
    }

    private Image(ImageImpl impl) {
        this(impl, impl.getSize());
        definitelyBlank = false;
    }

    public Image(String file) {
        this(Application.getImplementation().getImageFactory().createNew(file));
    }

    @Override
    @NotNull
    public Image clone() {
        return new Image(impl.clone());
    }

    public void fillRect(IVec2 topLeft, IVec2 size, Color color) {
        if(color.a == 0) return;
        impl.fillRect(topLeft, size, color);
        definitelyBlank = false;
    }

    public void fill(Color color) {
        fillRect(IVec2.ZERO, size, color);
    }

    public void drawRect(IVec2 topLeft, IVec2 size, Color color) {
        if(color.a == 0) return;
        impl.drawRect(topLeft, size, color);
        definitelyBlank = false;
    }

    public void setPixel(IVec2 location, Color color) {
        Arguments.checkRange(location.x, 0, size.x);
        Arguments.checkRange(location.y, 0, size.y);
        impl.setPixel(location, color);
        definitelyBlank &= (color.a == 0);
    }

    public Color getPixel(IVec2 location) {
        return impl.getPixel(location);
    }

    public void drawImage(Image image, IVec2 topLeft) {
        if(image.definitelyBlank) return;
        impl.drawImage(image.impl, topLeft);
        definitelyBlank = false;
    }

    public void drawImageCr(Image image, IVec2 center) {
        drawImage(image, image.size.divided(-2).add(center));
    }

    public void drawLine(IVec2 from, IVec2 to, Color color) {
        if(color.a == 0) return;
        impl.drawLine(from, to, color);
        definitelyBlank = false;
    }

    public void drawLine(ILine2 line, Color color) {
        drawLine(line.a, line.b, color);
    }

    public void drawOval(IVec2 center, IVec2 size, Color color) {
        if(color.a == 0) return;
        impl.drawOval(center, size, color);
        definitelyBlank = false;
    }

    public void drawOval(IVec2 center, int radius, Color color) {
        drawOval(center, new IVec2(radius * 2, radius * 2), color);
    }

    public void fillOval(IVec2 center, IVec2 size, Color color) {
        if(color.a == 0) return;
        impl.fillOval(center, size, color);
        definitelyBlank = false;
    }

    public void fillOval(IVec2 center, int radius, Color color) {
        fillOval(center, new IVec2(radius, radius), color);
    }

    public void clear() {
        impl.clear();
        definitelyBlank = true;
    }

    public Image scaled(IVec2 newSize) {
        return scaled(newSize, AntialiasingMode.OFF);
    }

    public Image scaled(IVec2 newSize, AntialiasingMode aaMode) {
        if(newSize.equals(size)) return clone();
        Image scaled = new Image(impl.scaled(newSize, aaMode));
        scaled.definitelyBlank = definitelyBlank;
        return scaled;
    }

    public Image scaled(float factor, AntialiasingMode aaMode) {
        return scaled(size.scaled(factor).toI(), aaMode);
    }

    public Image scaled(float factor) {
        return scaled(factor, AntialiasingMode.OFF);
    }



    public static Image text(String text, int size, Color color) {
        return new Image(Application.getImplementation().getImageFactory().createText(text, size, color));
    }

    public static ImageImpl getImplementation(Image image) {
        return image.impl;
    }



    public enum AntialiasingMode {
        HIGH,
        LOW,
        OFF
    }
}
