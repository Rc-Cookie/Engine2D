package com.github.rccookie.engine2d.impl;

public interface Implementation {

    ImageImplFactory getImageFactory();

    DisplayFactory getDisplayFactory();

    InputAdapter getInputAdapter();

    boolean supportsMultithreading();

    boolean supportsNativeIO();

    boolean supportsAWT();
}
