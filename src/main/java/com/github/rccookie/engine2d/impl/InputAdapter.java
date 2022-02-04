package com.github.rccookie.engine2d.impl;

import com.github.rccookie.event.action.BiParamAction;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.IVec2;

public interface InputAdapter {

    void attachKeyEvent(BiParamAction<String, Boolean> event);

    void attachMouseEvent(ParamAction<MouseData> event);

    IVec2 getMousePos();

    boolean isKeyDataAvailable();

    boolean isMouseDataAvailable();
}
