package com.github.rccookie.engine2d.util;

import java.util.List;

import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.EventInvocationException;
import com.github.rccookie.event.action.Action;
import com.github.rccookie.event.action.IAction;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.ModIterableArrayList;

import org.jetbrains.annotations.NotNull;

/**
 * A consumable event that first invokes all consuming events
 * and only if not consumed then the non-consuming events.
 *
 * @param <T> Content type of the event
 */
public class OrderedParamEvent<T> extends CaughtParamEvent<T> {

    /**
     * Non-consuming actions.
     */
    protected final List<IAction> lateActions = new ModIterableArrayList<>();

    /**
     * Creates a new ordered parameter event.
     */
    public OrderedParamEvent() {
        super(true);
    }

    @Override
    public @NotNull ParamAction<T> add(@NotNull ParamAction<T> action) {
        return add1(action);
    }

    @Override
    public @NotNull Action add(@NotNull Action action) {
        return add1(action);
    }

    /**
     * Adds the given non-consuming action to the event.
     *
     * @param action The action to be added
     * @param <E> The type of action to be added
     * @return The action added
     */
    @NotNull
    private <E extends IAction> E add1(@NotNull E action) {
        lateActions.add(Arguments.checkNull(action));
        return action;
    }

    @Override
    public boolean invoke(T info) {
        if(invokeConsuming(info)) return true;
        invokeNonConsuming(info);
        return false;
    }

    /**
     * Invokes the consuming actions.
     *
     * @param info The event parameter
     * @return Whether the event was consumed
     */
    protected boolean invokeConsuming(T info) {
        return super.invoke(info);
    }

    /**
     * Invokes the non-consuming actions.
     *
     * @param info The event parameter
     */
    protected void invokeNonConsuming(T info) {
        Object[] params = { info };
        EventInvocationException exception = null;
        for(IAction action : lateActions) {
            try {
                action.run(params);
            } catch(Exception e) {
                if(exception == null)
                    exception = new EventInvocationException(e);
                else exception.addSuppressed(e);
            }
        }
        if(exception != null) handleException(exception);
    }
}
