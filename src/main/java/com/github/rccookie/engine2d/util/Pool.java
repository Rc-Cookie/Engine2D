package com.github.rccookie.engine2d.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.function.Supplier;

import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A pool is a factory for objects of a given type that can be reused
 * after they were used and returned to the pool to avoid mass object
 * creation.
 *
 * @param <T> Content type of the pool
 */
public class Pool<T> {

    /**
     * Objects that are currently unused and ready to be used.
     */
    private final Deque<T> objects;
    /**
     * Factory function to create new objects if the pool is empty.
     */
    private final Supplier<T> factory;


    /**
     * Creates a new pool using the given object factory.
     *
     * @param factory The factory function used to create new objects if the
     *                pool is empty and more objects are requested
     */
    public Pool(@NotNull Supplier<T> factory) {
        this.factory = Arguments.checkNull(factory, "factory");
        objects = new ArrayDeque<>();
    }

    /**
     * Creates a new pool and fills it with the specified number of objects.
     *
     * @param factory The factory function to create new objects
     * @param initialCapacity The initial amount of objects in the pool
     */
    public Pool(@NotNull Supplier<T> factory, int initialCapacity) {
        this.factory = Arguments.checkNull(factory, "factory");
        objects = new ArrayDeque<>(initialCapacity);
        for(int i=0; i<initialCapacity; i++)
            objects.add(factory.get());
    }

    /**
     * Creates a new pool filled with the given objects.
     *
     * @param factory The factory function used to create new objects if the
     *                pool is empty and more objects are requested
     * @param initialObjects Objects to be initially in the pool
     */
    public Pool(@NotNull Supplier<T> factory, @NotNull Collection<? extends T> initialObjects) {
        this.factory = Arguments.checkNull(factory, "factory");
        objects = new ArrayDeque<>(Arguments.checkNull(initialObjects, "initialObjects"));
    }


    /**
     * Returns the number of objects currently in the pool.
     *
     * @return The pool size
     */
    public int size() {
        return objects.size();
    }

    /**
     * Returns an object from the pool or creates a new one if the pool
     * is empty.
     *
     * @return An object of the pool type
     */
    public T get() {
        if(objects.isEmpty())
            return factory.get();
        return objects.poll();
    }

    /**
     * Returns the given object to be reused when needed.
     *
     * @param object The object to be returned to the pool
     */
    public void returnObject(T object) {
        objects.add(object);
    }

    /**
     * Returns all the given objects to be reused when needed.
     *
     * @param objects The objects to be returned to the pool
     */
    public void returnObjects(@NotNull Collection<T> objects) {
        this.objects.addAll(Arguments.checkNull(objects, "objects"));
    }
}
