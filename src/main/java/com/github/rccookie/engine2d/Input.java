package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.MouseData;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class Input {

    private Input() {
        throw new UnsupportedOperationException();
    }

    public static final ParamEvent<String> keyPressed = new ParamEvent<>();
    public static final ParamEvent<String> keyReleased = new ParamEvent<>();
    public static final ParamEvent<Mouse> mousePressed = new ParamEvent<>();
    public static final ParamEvent<Mouse> mouseReleased = new ParamEvent<>();

    private static Mouse lastMouse = Mouse.getEmulated(IVec2.ZERO, 0);
    private static final Set<String> pressedKeys = new HashSet<>();

    static {
        Execute.when(() -> Application.getImplementation().getInputAdapter().attachKeyEvent(Input::keyEventReceived),
                Application.getImplementation().getInputAdapter()::isKeyDataAvailable);
        Execute.when(() -> Application.getImplementation().getInputAdapter().attachMouseEvent(Input::mouseEventReceived),
                Application.getImplementation().getInputAdapter()::isMouseDataAvailable);
        keyPressed.add(k -> {
            synchronized (pressedKeys) {
                pressedKeys.add(k);
            }
        });
        keyReleased.add(k -> {
            synchronized (pressedKeys) {
                pressedKeys.remove(k);
            }
        });
    }


    public static Mouse getMouse() {
        IVec2 mouseLoc;
        if(!isMouseDataAvailable() ||
                (mouseLoc = Application.getImplementation().getInputAdapter().getMousePos()) == null) return lastMouse;
        Mouse mouse = new Mouse(new MouseData(mouseLoc, lastMouse.button));
        lastMouse = mouse;
        return mouse;
    }

    public static boolean isMouseDataAvailable() {
        return Application.getImplementation().getInputAdapter().isMouseDataAvailable();
    }

    public static boolean getKeyState(String key) {
        synchronized (pressedKeys) {
            return pressedKeys.contains(key.toLowerCase());
        }
    }



    public static void addKeyListener(Runnable action, String... keys) {
        addKeyListener(k -> action.run(), keys);
    }

    public static void addKeyListener(Consumer<String> action, String... keys) {
        if(keys == null || keys.length == 0) return;
        Application.update.add(() -> {
            for(String k : keys) {
                if(getKeyState(k)) {
                    action.accept(k);
                    return;
                }
            }
        });
    }

    public static void addKeyPressListener(Runnable action, String... keys) {
        addKeyPressListener(k -> action.run(), keys);
    }

    public static void addKeyPressListener(Consumer<String> action, String... keys) {
        addListener(action, keyPressed, keys);
    }

    public static void addKeyReleaseListener(Runnable action, String... keys) {
        addKeyReleaseListener(k -> action.run(), keys);
    }

    public static void addKeyReleaseListener(Consumer<String> action, String... keys) {
        addListener(action, keyReleased, keys);
    }

    private static void addListener(Consumer<String> action, ParamEvent<String> event, String[] keys) {
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


    private static void keyEventReceived(String key, boolean pressed) {
        (pressed ? keyPressed : keyReleased).invoke(key.toLowerCase());
    }

    private static void mouseEventReceived(MouseData data) {
        Mouse mouse = new Mouse(data);
        lastMouse = mouse;
        (data.pressed ? mousePressed : mouseReleased).invoke(mouse);
    }
}
