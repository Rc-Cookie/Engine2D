package com.github.rccookie.engine2d.impl;

import com.github.rccookie.event.action.BiParamAction;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.int2;

/**
 * Generic definition of an input receiver.
 */
public interface InputAdapter {

    /**
     * Attaches the given event to be fired whenever a key event
     * occurs, with the key and the pressed state as parameters.
     *
     * @param event The event to attach
     */
    void attachKeyEvent(BiParamAction<String, Boolean> event);

    /**
     * Attaches the given event to be fired whenever the mouse
     * changes, with the current mouse data as parameter.
     *
     * @param event The event to attach
     */
    void attachMouseEvent(ParamAction<MouseData> event);

    /**
     * Returns the current mouse position, relative to the top left
     * corner of the application window.
     *
     * @return The mouse location
     */
    int2 getMousePos();

    /**
     * Returns whether key input is currently available.
     *
     * @return Whether key input is available
     */
    boolean isKeyDataAvailable();

    /**
     * Returns whether mouse input is currently available.
     *
     * @return Whether mouse input is available
     */
    boolean isMouseDataAvailable();
}
