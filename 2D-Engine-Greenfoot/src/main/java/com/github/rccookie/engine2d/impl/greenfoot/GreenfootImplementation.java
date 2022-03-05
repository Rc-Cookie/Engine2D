package com.github.rccookie.engine2d.impl.greenfoot;

import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.engine2d.impl.InputAdapter;
import greenfoot.Greenfoot;

public abstract class GreenfootImplementation implements Implementation {

    private static GreenfootImplementation instance;

    boolean started = false;

    protected GreenfootImplementation() {
        instance = this;
    }

    public static GreenfootImplementation instance() {
        return instance;
    }

    @Override
    public void setDisplayController(DisplayController displayController) {
        GreenfootDisplay.INSTANCE.displayController = displayController;
    }

    @Override
    public Display getDisplay() {
        return GreenfootDisplay.INSTANCE;
    }

    @Override
    public InputAdapter getInputAdapter() {
        return GreenfootInputAdapter.INSTANCE;
    }

    @Override
    public boolean hasExternalUpdateLoop() {
        return true;
    }

    @Override
    public void runExternalUpdateLoop() {
        started = true;
        Greenfoot.setWorld(GreenfootDisplay.INSTANCE.world);
    }
}
