package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.IVec2;

public class AWTImageImplFactory implements ImageImplFactory {
    @Override
    public ImageImpl createNew(IVec2 size) {
        return new AWTImageImpl(size);
    }

    @Override
    public ImageImpl createNew(String file) throws RuntimeIOException {
        return new AWTImageImpl(file);
    }
}
