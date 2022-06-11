package com.github.rccookie.engine2d.input;

public interface InputHandler {

    InputHandler DO_NOTHING = $ -> {};

    void onValueChange(float newValue);
}
