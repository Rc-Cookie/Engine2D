package com.github.rccookie.engine2d;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.rccookie.engine2d.coroutine.Execute;
import com.github.rccookie.engine2d.impl.MouseData;
import com.github.rccookie.engine2d.util.IntWrapper;
import com.github.rccookie.engine2d.util.OrderedParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class for receiving input events.
 */
public enum Input {

    ; // No instance

    /**
     * Whether to report OS-driven repeating key presses, if you
     * hold down a key for long enough. These events may not fire on
     * all platforms and may differ in time until the first repeating
     * press and the interval in which the presses take place.
     * <p>Disabled by default.
     */
    @SuppressWarnings("NonFinalFieldInEnum")
    public static boolean REPORT_KEY_REPEAT = false;

    /**
     * Gets called whenever a key gets pressed down, with that key as parameter.
     */
    public static final ParamEvent<String> keyPressed    = new OrderedParamEvent<>();

    /**
     * Gets called whenever a key gets released, with that key as parameter.
     */
    public static final ParamEvent<String> keyReleased   = new OrderedParamEvent<>();

    /**
     * Gets called whenever a mouse button gets pressed down, with the mouse info as
     * parameter.
     */
    public static final ParamEvent<Mouse>  mousePressed  = new OrderedParamEvent<>();

    /**
     * Gets called whenever a mouse button gets released, with the mouse info as
     * parameter.
     */
    public static final ParamEvent<Mouse>  mouseReleased = new OrderedParamEvent<>();


    /**
     * Last captured mouse state, to be used if no mouse info can be optained.
     */
    private static Mouse lastMouse = Mouse.getEmulated(int2.zero, 0);
    private static long lastMouseFrame = -1;

    /**
     * Set of all keys currently pressed down.
     */
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



    /**
     * Returns information about the current state of the mouse. If no mouse
     * data is available (as can be checked with {@link #isMouseDataAvailable()}
     * the previous mouse data will be returned. If mouse data was never available
     * this will return the mouse to be at the pixel [0|0].
     *
     * @return The current mouse information
     */
    @NotNull
    public static Mouse getMouse() {
        int2 mouseLoc;
        if(Time.frame() == lastMouseFrame || !isMouseDataAvailable() || (mouseLoc = Application.getImplementation().getInputAdapter().getMousePos()) == null)
            return lastMouse;
        Mouse mouse = new Mouse(new MouseData(mouseLoc, lastMouse.button));
        lastMouse = mouse;
        lastMouseFrame = Time.frame();
        return mouse;
    }

    /**
     * Returns whether mouse data is currently available, meaning that the information
     * returned by {@link #getMouse()} is representing the real state of the mouse. This
     * may not be the case for example if the mouse is not within the application window
     * or if the window looses focus.
     *
     * @return Whether mouse information is available
     */
    public static boolean isMouseDataAvailable() {
        return Application.getImplementation().getInputAdapter().isMouseDataAvailable();
    }

    /**
     * Returns the pressed state of the given key.
     *
     * @param key The key to test
     * @return Whether that key is currently pressed down
     */
    public static boolean getKeyState(String key) {
        synchronized (pressedKeys) {
            return pressedKeys.contains(key.toLowerCase());
        }
    }



    /**
     * Runs the given action while any of the specified keys is pressed
     * down.
     *
     * @param action The task to run
     * @param keys The keys of which any should be pressed
     */
    public static void addKeyListener(Runnable action, String... keys) {
        addKeyListener(k -> action.run(), keys);
    }

    /**
     * Runs the given action while any of the specified keys is pressed
     * down.
     *
     * @param action The task to run
     * @param keys The keys of which any should be pressed
     */
    public static void addKeyListener(Consumer<String> action, String... keys) {
        if(checkNotEmpty(keys)) return;
        Application.lateUpdate.add(() -> {
            for(String k : keys) {
                if(getKeyState(k)) {
                    action.accept(k);
                    return;
                }
            }
        });
    }

    /**
     * Runs the given action whenever one of the given keys gets pressed
     * down.
     *
     * @param action The action to run
     * @param keys The keys of which any should be pressed
     */
    public static void addKeyPressListener(Runnable action, String... keys) {
        addKeyPressListener(k -> action.run(), keys);
    }

    /**
     * Runs the given action whenever one of the given keys gets pressed
     * down.
     *
     * @param action The action to run
     * @param keys The keys of which any should be pressed
     */
    public static void addKeyPressListener(Consumer<String> action, String... keys) {
        addListener(action, keyPressed, keys);
    }

    /**
     * Runs the given action whenever one of the given keys gets released.
     *
     * @param action The action to run
     * @param keys The keys of which any should be released
     */
    public static void addKeyReleaseListener(Runnable action, String... keys) {
        addKeyReleaseListener(k -> action.run(), keys);
    }

    /**
     * Runs the given action whenever one of the given keys gets released.
     *
     * @param action The action to run
     * @param keys The keys of which any should be released
     */
    public static void addKeyReleaseListener(Consumer<String> action, String... keys) {
        addListener(action, keyReleased, keys);
    }

    /**
     * Runs the given action whenever the given keys get pressed down, and when
     * they get released. If one of the keys was already pressed down, a second
     * press will not be reported, until all keys have been released. Same goes
     * for release events.
     *
     * @param action The action to run, with the pressed state of the keys
     *               as parameter
     * @param keys The keys of which any should be pressed to report a key press,
     *             and which all need to be released to report a key release
     */
    public static void addKeyChangeListener(Consumer<Boolean> action, String... keys) {
        addKeyChangeListener((s,k) -> action.accept(s), keys);
    }

    /**
     * Runs the given action whenever the given keys get pressed down, and when
     * they get released. If one of the keys was already pressed down, a second
     * press will not be reported, until all keys have been released. Same goes
     * for release events.
     *
     * @param action The action to run, with the pressed state of the keys
     *               and the key as parameters
     * @param keys The keys of which any should be pressed to report a key press,
     *             and which all need to be released to report a key release
     */
    public static void addKeyChangeListener(BiConsumer<Boolean, String> action, String... keys) {
        IntWrapper pressCount = new IntWrapper(0);
        addListener(k -> {
            if(++pressCount.value == 1)
                action.accept(true, k);
        }, keyPressed, keys);
        addListener(k -> {
            if(--pressCount.value == 0)
                action.accept(false, k);
        }, keyReleased, keys);
    }

    private static void addListener(Consumer<String> action, ParamEvent<String> event, String[] keys) {
        //noinspection DuplicatedCode
        if(checkNotEmpty(keys)) return;
        event.add(k -> {
            for(String key : keys) {
                if(k.equalsIgnoreCase(key)) {
                    action.accept(k);
                    return;
                }
            }
        });
    }

    private static boolean checkNotEmpty(String[] keys) {
        if(Arguments.checkNull(keys, "keys").length == 0) {
            Console.warn("Trying to add key listener but no keys specified");
            Console.printStackTrace("warn");
            return true;
        }
        return false;
    }


    /**
     * Called when a key event was received by the implementation.
     *
     * @param key The key that changed
     * @param pressed Whether the key is now pressed or not
     */
    private static void keyEventReceived(String key, boolean pressed) {
        String lowerKey = key.toLowerCase();
        boolean wasPressed;
        synchronized (pressedKeys) {
            wasPressed = pressedKeys.contains(lowerKey);
        }
        if(pressed) {
            if(wasPressed) {
                if(!REPORT_KEY_REPEAT) return;
                Execute.later(() -> keyReleased.invoke(lowerKey)); // Some events (like the AWT events) may be called from another thread,
                                                                   // but all user code should run on the main thread
            }
            Execute.later(() -> keyPressed.invoke(lowerKey));
        }
        else if(wasPressed) Execute.later(() -> keyReleased.invoke(lowerKey));
    }

    /**
     * Called whenever a change of mouse state was received by the
     * implementation.
     *
     * @param data The current mouse data
     */
    private static void mouseEventReceived(MouseData data) {
        Mouse mouse = new Mouse(data);
        lastMouse = mouse;
        Execute.later(() -> (data.button != 0 ? mousePressed : mouseReleased).invoke(mouse));
    }
}
