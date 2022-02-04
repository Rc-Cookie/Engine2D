package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.rccookie.engine2d.ui.debug.DebugPanel;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;

public enum Debug {

    ;

    private static final Map<String, Boolean> OVERLAY_BIND = new HashMap<>();

    public static void printUI() {
        UI ui = Camera.getActive().getUI();
        if(ui != null)
            printUI(ui);
        else Console.info("No UI attached to current camera");
    }

    public static void printUI(UIObject object) {
        object.printTree(new ArrayList<>());
    }

    public static void toggleOverlay() {
        Camera camera = Camera.getActive();
        UI ui = camera.getUI();
        if(ui == null)
            ui = new UI(camera);
        toggleOverlay(ui);
    }

    public static void toggleOverlay(UI ui) {
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

    public static void setOverlay(boolean on) {
        Camera camera = Camera.getActive();
        UI ui = camera.getUI();
        if(ui == null) {
            if(!on) return;
            ui = new UI(camera);
        }
        setOverlay(ui, on);
    }

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

    public static void bindOverlayToggle() {
        bindOverlayToggle("f1");
    }

    public static void bindOverlayToggle(String key) {
        if(OVERLAY_BIND.containsKey(key)) return;
        OVERLAY_BIND.put(key, true);
        Input.addKeyPressListener((Runnable) Debug::toggleOverlay, key);
    }
}
