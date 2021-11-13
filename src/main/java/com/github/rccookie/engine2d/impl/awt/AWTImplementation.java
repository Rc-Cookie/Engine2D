package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.impl.DisplayFactory;
import com.github.rccookie.engine2d.impl.ImageImplFactory;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.impl.InputAdapter;

public class AWTImplementation implements Implementation {

    private final ImageImplFactory imageFactory = new AWTImageImplFactory();
    private final DisplayFactory displayFactory = AWTDisplay::new;
    private final InputAdapter inputAdapter = new AWTInputAdapter();

    @Override
    public ImageImplFactory getImageFactory() {
        return imageFactory;
    }

    @Override
    public DisplayFactory getDisplayFactory() {
        return displayFactory;
    }

    @Override
    public InputAdapter getInputAdapter() {
        return inputAdapter;
    }

    @Override
    public boolean supportsMultithreading() {
        return true;
    }

    @Override
    public boolean supportsNativeIO() {
        return true;
    }

    @Override
    public boolean supportsAWT() {
        return true;
    }
}
