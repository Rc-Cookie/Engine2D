package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Image;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Cloneable;

public interface ImageImpl extends Cloneable<ImageImpl> {

    void fillRect(IVec2 topLeft, IVec2 size, Color color);

    void drawRect(IVec2 topLeft, IVec2 size, Color color);

    void fillOval(IVec2 center, IVec2 size, Color color);

    void drawOval(IVec2 center, IVec2 size, Color color);

    void drawLine(IVec2 from, IVec2 to, Color color);

    void setPixel(IVec2 location, Color color);

    void clear();

    Color getPixel(IVec2 location);

    void drawImage(ImageImpl image, IVec2 topLeft);

    IVec2 getSize();

    ImageImpl scaled(IVec2 newSize, Image.AntialiasingMode aaMode);
}
