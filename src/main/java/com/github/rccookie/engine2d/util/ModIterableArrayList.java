package com.github.rccookie.engine2d.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jetbrains.annotations.NotNull;

/**
 * Subclass of {@link ArrayList} that returns an {@link ArrayListModIterator}
 * as iterator.
 */
public class ModIterableArrayList<E> extends ArrayList<E> {

    public ModIterableArrayList() {
    }

    public ModIterableArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public ModIterableArrayList(@NotNull Collection<? extends E> c) {
        super(c);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new ArrayListModIterator<>(this);
    }
}
