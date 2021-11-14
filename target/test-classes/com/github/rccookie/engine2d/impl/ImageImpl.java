package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.geometry.performance.IVec2;

public interface ImageImpl {

    void fillRect(IVec2 topLeft, IVec2 size, Color color);

    void drawRect(IVec2 topLeft, IVec2 size, Color color);

    void setPixel(IVec2 location, Color color);

    Color getPixel(IVec2 location);

    void drawImage(ImageImpl image, IVec2 topLeft);

    IVec2 getSize();

    ImageImpl scaled(IVec2 newSize);
}
