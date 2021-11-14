package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.impl.*;

public class AWTImplementation implements Implementation {

    private final ImageImplFactory imageFactory = new AWTImageImplFactory();
    private final InputAdapter inputAdapter = new AWTInputAdapter();

    @Override
    public void setDisplayController(DisplayController displayController) {
        AWTDisplay.displayController = displayController;
        AWTDisplay.INSTANCE = new AWTDisplay();
    }

    @Override
    public ImageImplFactory getImageFactory() {
        return imageFactory;
    }

    @Override
    public Display getDisplay() {
        return AWTDisplay.INSTANCE;
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

    @Override
    public boolean supportsSleeping() {
        return true;
    }

    @Override
    public boolean hasExternalUpdateLoop() {
        return false;
    }

    @Override
    public void sleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runExternalUpdateLoop() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
