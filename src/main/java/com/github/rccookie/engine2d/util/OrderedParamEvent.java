package com.github.rccookie.engine2d.util;

import java.util.List;

import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.EventInvocationException;
import com.github.rccookie.event.action.Action;
import com.github.rccookie.event.action.IAction;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class OrderedParamEvent<T> extends CaughtParamEvent<T> {

    protected final List<IAction> lateActions = new ModIterableArrayList<>();

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

    protected boolean invokeConsuming(T info) {
        return super.invoke(info);
    }

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
