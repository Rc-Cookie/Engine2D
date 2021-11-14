package com.github.rccookie.engine2d.impl;

public interface Implementation {

    void setDisplayController(DisplayController displayController);

    ImageImplFactory getImageFactory();

    Display getDisplay();

    InputAdapter getInputAdapter();

    boolean supportsMultithreading();

    boolean supportsNativeIO();

    boolean supportsAWT();

    boolean supportsSleeping();

    boolean hasExternalUpdateLoop();

    void sleep(long millis, int nanos);

    void runExternalUpdateLoop() throws UnsupportedOperationException;
}
