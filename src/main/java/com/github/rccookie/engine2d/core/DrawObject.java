package com.github.rccookie.engine2d.core;

import java.util.Collection;
import java.util.Objects;

import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.Pool;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Cloneable;

import org.jetbrains.annotations.NotNull;

public class DrawObject implements Cloneable<DrawObject> {

    /**
     * Pool of reusable draw objects.
     */
    private static final Pool<DrawObject> POOL = new Pool<>(DrawObject::new);

    public final int2 screenLocation = int2.zero();
    public float rotation;
    public ImageImpl image;

    private DrawObject() { }

    @Override
    public String toString() {
        return "DrawObject at " + screenLocation;
    }

    @Override
    @NotNull
    public DrawObject clone() {
        DrawObject clone = get();
        clone.screenLocation.set(screenLocation);
        clone.rotation = rotation;
        clone.image = image;
        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof DrawObject)) return false;
        DrawObject that = (DrawObject) o;
        return Float.compare(that.rotation, rotation) == 0 && screenLocation.equals(that.screenLocation) && image.equals(that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenLocation, rotation, image);
    }

    public static DrawObject get() {
        synchronized(POOL) {
            return POOL.get();
        }
    }

    public static void returnObject(DrawObject o) {
        synchronized(POOL) {
            POOL.returnObject(o);
        }
    }

    public static void returnObjects(Collection<DrawObject> os) {
        synchronized(POOL) {
            POOL.returnObjects(os);
        }
    }

    public static int getPoolSize() {
        synchronized(POOL) {
            return POOL.size();
        }
    }
}
