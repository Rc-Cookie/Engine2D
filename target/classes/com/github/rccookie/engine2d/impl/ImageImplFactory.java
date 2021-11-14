package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.IVec2;

public interface ImageImplFactory {

    ImageImpl createNew(IVec2 size);

    ImageImpl createNew(String file) throws RuntimeIOException;

    ImageImpl createText(String text, int size, Color color);
}
