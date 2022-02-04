package com.github.rccookie.engine2d.core;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import com.github.rccookie.engine2d.Input;
import com.github.rccookie.engine2d.Mouse;
import com.github.rccookie.engine2d.util.OrderedParamEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public abstract class LocalInputManager {

    public final ParamEvent<String> keyPressed    = new SplitParamEvent<>();
    public final ParamEvent<String> keyReleased   = new SplitParamEvent<>();
    public final ParamEvent<Mouse>  mousePressed  = new SplitParamEvent<>();
    public final ParamEvent<Mouse>  mouseReleased = new SplitParamEvent<>();

    private final Event update;


    public LocalInputManager(@NotNull Event updateEvent) {
        update = Arguments.checkNull(updateEvent);
        Input.keyPressed   .addConsuming(k ->      isInputAvailable() && ((SplitParamEvent<String>) keyPressed)   .invokeConsuming(k));
        Input.keyReleased  .addConsuming(k ->      isInputAvailable() && ((SplitParamEvent<String>) keyReleased)  .invokeConsuming(k));
        Input.mousePressed .addConsuming(m ->      isInputAvailable() && ((SplitParamEvent<Mouse>)  mousePressed) .invokeConsuming(m));
        Input.mouseReleased.addConsuming(m ->      isInputAvailable() && ((SplitParamEvent<Mouse>)  mouseReleased).invokeConsuming(m));
        Input.keyPressed   .add(k -> { if(isInputAvailable())   ((SplitParamEvent<String>) keyPressed)   .invokeNonConsuming(k); });
        Input.keyReleased  .add(k -> { if(isInputAvailable())   ((SplitParamEvent<String>) keyReleased)  .invokeNonConsuming(k); });
        Input.mousePressed .add(k -> { if(isInputAvailable())   ((SplitParamEvent<Mouse>) mousePressed)  .invokeNonConsuming(k); });
        Input.mouseReleased.add(k -> { if(isInputAvailable())   ((SplitParamEvent<Mouse>) mouseReleased) .invokeNonConsuming(k); });
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
        //noinspection DuplicatedCode
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

    public static class Impl extends LocalInputManager {

        private final BooleanSupplier isInputAvailable;

        public Impl(Event updateEvent, BooleanSupplier isInputAvailable) {
            super(updateEvent);
            this.isInputAvailable = isInputAvailable;
        }

        @Override
        protected boolean isInputAvailable() {
            return isInputAvailable.getAsBoolean();
        }
    }



    private static class SplitParamEvent<T> extends OrderedParamEvent<T> {

        @Override
        public boolean invokeConsuming(T info) {
            return super.invokeConsuming(info);
        }

        @Override
        public void invokeNonConsuming(T info) {
            super.invokeNonConsuming(info);
        }

        @Override
        @Deprecated
        public boolean invoke(T info) {
            return super.invoke(info);
        }
    }
}
