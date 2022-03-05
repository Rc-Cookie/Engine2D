package com.github.rccookie.engine2d.ui;

import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A canvas is intended for managing multiple "pages" of ui and finds use
 * mainly in applications that are heavily ui focused rather than map-focused,
 * or for example in menus.
 */
public class Canvas extends Structure {

    /**
     * Creates a new canvas with the given name.
     *
     * @param parent The parent for the canvas
     * @param name The name for the canvas
     */
    public Canvas(UIObject parent, String name) {
        super(parent);
        setName(name);
    }


    /**
     * Finds a canvas with the given name in the whole ui tree
     *
     * @param name The name of the canvas to find
     * @return The canvas found
     * @throws NoSuchElementException If no canvas with that name
     *                                exists
     */
    @NotNull
    public Canvas findCanvas(String name) {
        return getRoot().stream().filter(c -> c instanceof Canvas)
                .map(c -> (Canvas) c).findAny().get();
    }

    /**
     * Finds a canvas of the given type with the specified name,
     * or creates a new one. The searched canvas type must therefore
     * have a constructor with exactly a single parameter of type
     * {@link UIObject} which describes the parent for the canvas.
     * The constructor may be private.
     *
     * @param name The name of the canvas to find
     * @param type The type of canvas to find
     * @return A canvas found or a newly created one
     * @deprecated While this method is convenient it requires reflection
     *             which may not be available on all implementations
     */
    @SuppressWarnings("unchecked")
    @Deprecated(forRemoval = true)
    public <C extends Canvas> C findOrCreate(String name, Class<C> type) {
        Arguments.checkNull(type);
        return getRoot().stream().filter(c -> type.isInstance(c) && c.getName().equals(name))
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

    /**
     * Finds a canvas with the specified name, or creates a new one
     * using the specified supplier.
     *
     * @param name The name of the canvas to find
     * @param ctor A supplier of a canvas to use if none is found
     * @return A canvas found or a newly created one
     */
    public Canvas findOrCreate(String name, Supplier<Canvas> ctor) {
        Arguments.checkNull(ctor);
        return getRoot().stream().filter(c -> c instanceof Canvas && c.getName().equals(name))
                .map(c -> (Canvas) c).findAny().orElseGet(ctor);
    }

    /**
     * Finds a canvas with the specified name, or creates a new one
     * using the specified supplier.
     *
     * @param name The name of the canvas to find
     * @param ctor A function from UIObject (the parent to use) to
     *             a canvas to use if none is found
     * @return A canvas found or a newly created one
     */
    public Canvas findOrCreate(String name, Function<UIObject, Canvas> ctor) {
        Arguments.checkNull(ctor);
        return getRoot().stream().filter(c -> c instanceof Canvas && c.getName().equals(name))
                .map(c -> (Canvas) c).findAny().orElseGet(() -> ctor.apply(getParent()));
    }


    /**
     * Finds a canvas with the same parent as this canvas with
     * the specified name and sets it active, while disabling this
     * canvas.
     *
     * @param name The name of the canvas to activate
     * @throws NoSuchElementException If no canvas with that name was
     *                                found
     */
    protected void setActive(String name) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && parallel instanceof Canvas && parallel.getName().equals(name)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Finds a canvas of the specified type and with the same
     * parent as this canvas, and sets it active, while disabling
     * this canvas. If no canvas was found, a new one will be created.
     * The searched canvas type must therefore have a constructor with
     * exactly a single parameter of type {@link UIObject} which
     * describes the parent for the canvas. The constructor may be private.
     *
     * @param type The type of canvas to activate
     * @deprecated While this method is convenient it requires reflection
     *             which may not be available on all implementations
     */
    @Deprecated
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

    /**
     * Finds a canvas with the given name and the same parent as this
     * canvas, and sets it active, while disabling this canvas. If no
     * canvas was found, a new one will be created using the specified
     * supplier.
     *
     * @param name The name of the canvas to activate
     * @param ctor The supplier to use if no canvas was found
     */
    protected void setActive(String name, Supplier<Canvas> ctor) {
        setActive(name, $ -> ctor.get());
    }

    /**
     * Finds a canvas with the given name and the same parent as this
     * canvas, and sets it active, while disabling this canvas. If no
     * canvas was found, a new one will be created using the specified
     * function.
     *
     * @param name The name of the canvas to activate
     * @param ctor A function from UIObject (the parent to use) to
     *             a canvas to use if none is found
     */
    protected void setActive(String name, Function<UIObject, Canvas> ctor) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && parallel instanceof Canvas && parallel.getName().equals(name)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        setActive(ctor.apply(getParent()));
    }

    /**
     * Finds a canvas of the given type and with the same parent as this
     * canvas, and sets it active, while disabling this canvas. If no
     * canvas was found, a new one will be created using the specified
     * function.
     *
     * @param type The type of canvas to activate
     * @param ctor A function from UIObject (the parent to use) to
     *             a canvas to use if none is found
     */
    protected <C extends Canvas> void setActive(Class<C> type, Function<UIObject, C> ctor) {
        for(UIObject parallel : getParent().getChildren()) {
            if(parallel != this && type.isInstance(parallel)) {
                setActive((Canvas) parallel);
                return;
            }
        }
        setActive(ctor.apply(getParent()));
    }


    // Do nothing
    @Override
    protected void updateStructure() { }


    /**
     * Sets the given canvas as active while disabling all other
     * canvases with the same parent.
     *
     * @param canvas The canvas to activate
     */
    public static void setActive(Canvas canvas) {
        for(UIObject parallel : canvas.getParent())
            if(parallel != canvas && parallel instanceof Canvas)
                parallel.setEnabled(false);
        canvas.setEnabled(true);
    }
}
