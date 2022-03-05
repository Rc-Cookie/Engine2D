package com.github.rccookie.engine2d.impl.awt;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import com.github.rccookie.engine2d.impl.InputAdapter;
import com.github.rccookie.engine2d.impl.MouseData;
import com.github.rccookie.event.action.BiParamAction;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.int2;

/**
 * AWT implementation of an {@link InputAdapter}.
 */
public class AWTInputAdapter implements InputAdapter {

    @Override
    public void attachKeyEvent(BiParamAction<String, Boolean> event) {
        JFrame window = AWTDisplay.INSTANCE.window;
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                event.run(getKeyString(e), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                event.run(getKeyString(e), false);
            }
        });
    }

    @Override
    public void attachMouseEvent(ParamAction<MouseData> event) {
        JFrame window = AWTDisplay.INSTANCE.window;
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                Point displayOnScreenPoint = AWTDisplay.INSTANCE.getLocationOnScreen();
//                IVec2 pos = new IVec2(e.getXOnScreen() - displayOnScreenPoint.x,
//                        e.getYOnScreen() - displayOnScreenPoint.y);
//                event.accept(new MouseData(pos, e.getButton()));
//                event.accept(new MouseData(pos, 0));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Point displayOnScreenPoint = AWTDisplay.INSTANCE.getLocationOnScreen();
                int2 pos = new int2(e.getXOnScreen() - displayOnScreenPoint.x,
                        e.getYOnScreen() - displayOnScreenPoint.y);
                event.run(new MouseData(pos, e.getButton()));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point displayOnScreenPoint = AWTDisplay.INSTANCE.getLocationOnScreen();
                int2 pos = new int2(e.getXOnScreen() - displayOnScreenPoint.x,
                        e.getYOnScreen() - displayOnScreenPoint.y);
                event.run(new MouseData(pos, 0));
            }
        });
    }

    @Override
    public int2 getMousePos() {
        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point displayOnScreenPoint = AWTDisplay.INSTANCE.getLocationOnScreen();
        return new int2(pointer.getLocation().x - displayOnScreenPoint.x,
                pointer.getLocation().y - displayOnScreenPoint.y);
    }

    @Override
    public boolean isMouseDataAvailable() {
        AWTDisplay.init();
        return true;
    }

    @Override
    public boolean isKeyDataAvailable() {
        AWTDisplay.init();
        return true;
    }


    /**
     * Converts the key code of the given key event to a valid
     * key string.
     *
     * @param event The key event
     * @return The key that was affected
     */
    private static String getKeyString(KeyEvent event) {

        int keyCode = event.getExtendedKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_ESCAPE: return "esc";
            case KeyEvent.VK_DEAD_CIRCUMFLEX: return "^";
            case KeyEvent.VK_BACK_SPACE: return "backspace";
            case KeyEvent.VK_DELETE: return "delete";
            case KeyEvent.VK_ENTER: return "enter";
        }

        char c = event.getKeyChar();
        if(c != KeyEvent.CHAR_UNDEFINED) return Character.toString(c);

        switch (keyCode) {
            case KeyEvent.VK_F1: return "f1";
            case KeyEvent.VK_F2: return "f2";
            case KeyEvent.VK_F3: return "f3";
            case KeyEvent.VK_F4: return "f4";
            case KeyEvent.VK_F5: return "f5";
            case KeyEvent.VK_F6: return "f6";
            case KeyEvent.VK_F7: return "f7";
            case KeyEvent.VK_F8: return "f8";
            case KeyEvent.VK_F9: return "f9";
            case KeyEvent.VK_F10: return "f10";
            case KeyEvent.VK_F11: return "f11";
            case KeyEvent.VK_F12: return "f12";
            case KeyEvent.VK_F13: return "f13";
            case KeyEvent.VK_F14: return "f14";
            case KeyEvent.VK_F15: return "f15";
            case KeyEvent.VK_F16: return "f16";
            case KeyEvent.VK_F17: return "f17";
            case KeyEvent.VK_F18: return "f18";
            case KeyEvent.VK_F19: return "f19";
            case KeyEvent.VK_F20: return "f20";
            case KeyEvent.VK_F21: return "f21";
            case KeyEvent.VK_F22: return "f22";
            case KeyEvent.VK_F23: return "f23";
            case KeyEvent.VK_F24: return "f24";
            case KeyEvent.VK_PRINTSCREEN: return "print";
            case KeyEvent.VK_SCROLL_LOCK: return "scrollLock";
            case KeyEvent.VK_PAUSE: return "pause";
            case KeyEvent.VK_STOP: return "stop";
            case KeyEvent.VK_BACK_SLASH: return "\\";
            case KeyEvent.VK_DEAD_ACUTE: return "´";
            case KeyEvent.VK_INSERT: return "insert";
            case KeyEvent.VK_HOME: return "home";
            case KeyEvent.VK_PAGE_UP: return "pageUp";
            case KeyEvent.VK_NUM_LOCK: return "numLock";
            case KeyEvent.VK_DIVIDE: return "numDivide";
            case KeyEvent.VK_MULTIPLY: return "numMultiply";
            case KeyEvent.VK_MINUS: return "-";
            case KeyEvent.VK_TAB: return "tab";
            case KeyEvent.VK_PLUS: return "+";
            case KeyEvent.VK_END: return "end";
            case KeyEvent.VK_PAGE_DOWN: return "pageDown";
            case KeyEvent.VK_KP_UP: return "numUp";
            case KeyEvent.VK_CAPS_LOCK: return "capsLock";
            case KeyEvent.VK_NUMBER_SIGN: return "#";
            case KeyEvent.VK_KP_LEFT: return "numLeft";
            case KeyEvent.VK_KP_RIGHT: return "numRight";
            case KeyEvent.VK_SHIFT: return "shift";
            case KeyEvent.VK_LESS: return "<";
            case KeyEvent.VK_COMMA: return ",";
            case KeyEvent.VK_PERIOD: return ".";
            case KeyEvent.VK_KP_DOWN: return "numDown";
            case KeyEvent.VK_UP: return "up";
            case KeyEvent.VK_CONTROL: return "ctrl";
            case KeyEvent.VK_WINDOWS: return "windows";
            case KeyEvent.VK_ALT: return "alt";
            case KeyEvent.VK_ALT_GRAPH: return "altGr";
            case KeyEvent.VK_CONTEXT_MENU: return "contextMenu";
            case KeyEvent.VK_LEFT: return "left";
            case KeyEvent.VK_DOWN: return "down";
            case KeyEvent.VK_RIGHT: return "right";
            case KeyEvent.VK_DECIMAL: return "numPoint";
            default: return KeyEvent.getKeyText(keyCode);
        }
    }
}
