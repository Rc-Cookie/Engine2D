package com.github.rccookie.engine2d.input;

import com.github.rccookie.engine2d.Input;

public class KeyInput extends InputSource {

    public final String key;

    public KeyInput(String key) {
        super(InputRange.ZERO_TO_ONE);
        this.key = key;
//        Input.addKeyChangeListener(s -> (s?1:0), key);
    }

    @Override
    public float get() {
        return Input.getKeyState(key) ? 1 : 0;
    }
}
