package com.github.rccookie.engine2d.impl;

import com.github.rccookie.geometry.performance.IVec2;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface InputAdapter {

    void attachKeyEvent(BiConsumer<String, Boolean> event);

    void attachMouseEvent(Consumer<MouseData> event);

    IVec2 getMousePos();

    boolean isKeyDataAvailable();

    boolean isMouseDataAvailable();
}
