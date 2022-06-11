package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.github.rccookie.engine2d.ui.debug.DebugPanel;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.NotNull;

/**
 * Debugging utility class
 */
public enum Debug {

    ; // No instance

    /**
     * Default debug overlay key.
     */
    private static final String DEBUG_OVERLAY_KEY = "f1";

    /**
     * Keys that have the overlay bound to them.
     */
    private static final Set<String> OVERLAY_BIND = new HashSet<>();

    /**
     * Prints the current camera's ui tree into the console.
     */
    public static void printUI() {
        UI ui = Camera.getActive().getUI();
        if(ui != null)
            printUI(ui);
        else Console.log("No UI attached to current camera");
    }

    /**
     * Prints the ui tree starting at the given ui object into the console.
     *
     * @param object The object to print
     */
    public static void printUI(UIObject object) {
        object.printTree(new ArrayList<>());
    }

    /**
     * Toggles the debug overlay on the currently active camera. If the
     * active camera has no ui attached a new one will be created.
     */
    public static void toggleOverlay() {
        Camera camera = Camera.getActive();
        UI ui = camera.getUI();
        if(ui == null)
            ui = new UI(camera);
        toggleOverlay(ui);
    }

    /**
     * Toggles the debug overlay on the given ui.
     *
     * @param ui The ui to toggle the debug overlay on
     */
    public static void toggleOverlay(@NotNull UI ui) {
        Arguments.checkNull(ui);

        DebugPanel debug = null;
        for(UIObject o : ui.getChildren()) {
            if(o instanceof DebugPanel) {
                debug = (DebugPanel)o;
                break;
            }
        }
        if(debug == null)
            new DebugPanel(ui);
        else debug.setEnabled(!debug.isEnabledLocal());
    }

    /**
     * Sets the debug overlay to be on or off on the currently active
     * camera. If the active camera does not have an ui attached to
     * it and the overlay should be on, a new ui will be created.
     *
     * @param on Whether the overlay should be on or not
     */
    public static void setOverlay(boolean on) {
        Camera camera = Camera.getActive();
        UI ui = camera.getUI();
        if(ui == null) {
            if(!on) return;
            ui = new UI(camera);
        }
        setOverlay(ui, on);
    }

    /**
     * Sets the debug overlay to be on or off on the given ui.
     *
     * @param on Whether the overlay should be on or not
     */
    public static void setOverlay(UI ui, boolean on) {
        Arguments.checkNull(ui);

        DebugPanel debug = null;
        for(UIObject o : ui.getChildren()) {
            if(o instanceof DebugPanel) {
                debug = (DebugPanel)o;
                break;
            }
        }
        if(debug == null) {
            if(!on) return;
            new DebugPanel(ui);
        }
        else debug.setEnabled(on);
    }

    /**
     * Binds the debug overlay toggle to the default debug key
     * {@value DEBUG_OVERLAY_KEY}. Multiple calls to this method have
     * no effect.
     */
    public static void bindOverlayToggle() {
        bindOverlayToggle(DEBUG_OVERLAY_KEY);
    }

    /**
     * Binds the debug overlay toggle to the given key. Multiple calls
     * to this method with the same key have no effect. Old bindings to
     * other keys will <i>not</i> be removed.
     *
     * @param key The key to attach to
     */
    public static void bindOverlayToggle(@NotNull String key) {
        Arguments.checkNull(key, "key");
        if(OVERLAY_BIND.contains(key)) return;
        OVERLAY_BIND.add(key);
        Input.addKeyPressListener((Runnable) Debug::toggleOverlay, key);
    }
}
