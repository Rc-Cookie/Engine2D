package com.github.rccookie.engine2d.impl.greenfoot.offline;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.impl.greenfoot.GreenfootImageImpl;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.int2;

import greenfoot.GreenfootImage;

enum OfflineGreenfootImageImplFactory implements ImageImplFactory {

    INSTANCE;

    @Override
    public ImageImpl createNew(int2 size) {
        return new OfflineGreenfootImageImpl(size);
    }

    @Override
    public ImageImpl createNew(String file) throws RuntimeIOException {
        return new OfflineGreenfootImageImpl(file);
    }

    @Override
    public ImageImpl createText(String text, int size, Color color) {
        return new OfflineGreenfootImageImpl(new GreenfootImage(text, size, GreenfootImageImpl.toGreenfootColor(color), new greenfoot.Color(0, 0, 0, 0)));
    }
}
