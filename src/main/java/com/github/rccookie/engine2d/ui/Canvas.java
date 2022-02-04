package com.github.rccookie.engine2d.ui;

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.util.Arguments;

public class Canvas extends Structure {

    public Canvas(UIObject parent, String name) {
        super(parent);
        setName(name);
    }

    public Canvas findCanvas(String name) {
        return getUI().stream().filter(c -> c instanceof Canvas)
                .map(c -> (Canvas) c).findAny().get();
    }

    @SuppressWarnings("unchecked")
    public <C extends Canvas> C findOrCreate(String name, Class<C> type) {
        Arguments.checkNull(type);
        return getUI().stream().filter(c -> type.isInstance(c) && c.getName().equals(name))
                .map(c -> (C) c).findAny().orElseGet(() -> {
                    try {
                        Constructor<C> ctor = type.getDeclaredConstructor(UIObject.class);
                        ctor.setAccessible(true);
                        return ctor.newInstance(getParent());
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public Canvas findOrCreate(String name, Supplier<Canvas> ctor) {
        Arguments.checkNull(ctor);
        return getUI().stream().filter(c -> c instanceof Canvas && c.getName().equals(name))
                .map(c -> (Canvas) c).findAny().orElseGet(ctor);
    }

    public Canvas findOrCreate(String name, Function<UIObject, Canvas> ctor) {
        Arguments.checkNull(ctor);
        return getUI().stream().filter(c -> c instanceof Canvas && c.getName().equals(name))
                .map(c -> (Canvas) c).findAny().orElseGet(() -> ctor.apply(getParent()));
    }


    protected void setActive(String name) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && parallel instanceof Canvas && parallel.getName().equals(name)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        throw new NoSuchElementException();
    }

    protected void setActive(Class<? extends Canvas> type) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && type.isInstance(parallel)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        try {
            Constructor<? extends Canvas> ctor = type.getDeclaredConstructor(UIObject.class);
            ctor.setAccessible(true);
            setActive(ctor.newInstance(getParent()));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setActive(String name, Supplier<Canvas> ctor) {
        setActive(name, $ -> ctor.get());
    }

    protected void setActive(String name, Function<UIObject, Canvas> ctor) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && parallel instanceof Canvas && parallel.getName().equals(name)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        setActive(ctor.apply(getParent()));
    }

    protected <C extends Canvas> void setActive(Class<C> type, Function<UIObject, C> ctor) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && type.isInstance(parallel)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        setActive(ctor.apply(getParent()));
    }


    @Override
    protected void updateStructure() { }


    public static void setActive(Canvas canvas) {
        for(UIObject parallel : canvas.getParent())
            if(parallel != canvas && parallel instanceof Canvas)
                parallel.setEnabled(false);
        canvas.setEnabled(true);
    }
}
