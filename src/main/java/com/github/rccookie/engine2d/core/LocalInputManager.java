package com.github.rccookie.engine2d.core;

import com.github.rccookie.engine2d.Input;
import com.github.rccookie.engine2d.Mouse;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;

import java.util.function.Consumer;

public abstract class LocalInputManager {

    public final ParamEvent<String> keyPressed = new ParamEvent<>();
    public final ParamEvent<String> keyReleased = new ParamEvent<>();
    public final ParamEvent<Mouse> mousePressed = new ParamEvent<>();
    public final ParamEvent<Mouse> mouseReleased = new ParamEvent<>();

    private final Event update;


    public LocalInputManager(Event updateEvent) {
        update = Arguments.checkNull(updateEvent);
        Input.keyPressed.add(k -> {
            if(isInputAvailable()) keyPressed.invoke(k);
        });
        Input.keyReleased.add(k -> {
            if(isInputAvailable()) keyReleased.invoke(k);
        });
        Input.mousePressed.add(k -> {
            if(isInputAvailable()) mousePressed.invoke(k);
        });
        Input.mouseReleased.add(k -> {
            if(isInputAvailable()) mouseReleased.invoke(k);
        });
    }


    protected abstract boolean isInputAvailable();


    public Mouse getMouse() {
        return Input.getMouse();
    }

    public boolean isMouseDataAvailable() {
        return Input.isMouseDataAvailable();
    }

    public static boolean getKeyState(String key) {
        return Input.getKeyState(key);
    }


    public void addKeyListener(Runnable action, String... keys) {
        addKeyListener(k -> action.run(), keys);
    }

    public void addKeyListener(Consumer<String> action, String... keys) {
        if(keys == null || keys.length == 0) return;
        update.add(() -> {
            for(String k : keys) {
                if(getKeyState(k)) {
                    action.accept(k);
                    return;
                }
            }
        });
    }

    public void addKeyPressListener(Runnable action, String... keys) {
        addKeyPressListener(k -> action.run(), keys);
    }

    public void addKeyPressListener(Consumer<String> action, String... keys) {
        addListener(action, keyPressed, keys);
    }

    public void addKeyReleaseListener(Runnable action, String... keys) {
        addKeyReleaseListener(k -> action.run(), keys);
    }

    public void addKeyReleaseListener(Consumer<String> action, String... keys) {
        addListener(action, keyReleased, keys);
    }

    private void addListener(Consumer<String> action, ParamEvent<String> event, String[] keys) {
        if(keys == null || keys.length == 0) return;
        event.add(k -> {
            for(String key : keys) {
                if(k.equalsIgnoreCase(key)) {
                    action.accept(k);
                    return;
                }
            }
        });
    }
}
