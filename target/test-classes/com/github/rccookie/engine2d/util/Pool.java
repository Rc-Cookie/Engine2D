package com.github.rccookie.engine2d.util;

import com.github.rccookie.util.Console;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.function.Supplier;

public class Pool<T> {

    private final Deque<T> objects;
    private final Supplier<T> factory;

    public Pool(Supplier<T> factory) {
        objects = new ArrayDeque<>();
        this.factory = factory;
    }

    public Pool(Supplier<T> factory, int initialCapacity) {
        this.factory = factory;
        objects = new ArrayDeque<>(initialCapacity);
    }

    public Pool(Supplier<T> factory, Collection<? extends T> initialObjects) {
        this.factory = factory;
        objects = new ArrayDeque<>(initialObjects);
    }


    public int size() {
        return objects.size();
    }


    public T get() {
        if(objects.isEmpty()) {
            Console.info("Object pool empty, creating new");
            return factory.get();
        }
        return objects.poll();
    }

    public void returnObject(T object) {
        objects.add(object);
    }

    public void returnObjects(Collection<T> objects) {
        this.objects.addAll(objects);
    }
}
