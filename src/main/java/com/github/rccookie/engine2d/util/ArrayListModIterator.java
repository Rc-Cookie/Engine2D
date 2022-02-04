package com.github.rccookie.engine2d.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Iterator that doesn't fail if the underlying {@link ArrayList} gets
 * modified during iteration. In that case the exact objects that will
 * be iterated is not specifically defined, however, it will iterate at
 * most over as many elements as the list had at least once during the
 * iteration. Note that some elements may be iterated multiple times.
 */
public class ArrayListModIterator<E> implements Iterable<E>, Iterator<E> {

    @NotNull
    private final ArrayList<? extends E> list;
    @Range(from = 0, to = Integer.MAX_VALUE)
    private int index = 0;

    @SuppressWarnings("unchecked")
    public ArrayListModIterator(@NotNull List<? extends E> arrayList) {
        this((ArrayList<? extends E>) arrayList);
    }

    public ArrayListModIterator(@NotNull ArrayList<? extends E> list) {
        this.list = Arguments.checkNull(list);
    }

    @Override
    public Iterator<E> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public E next() {
        if(!hasNext())
            throw new NoSuchElementException();
        return list.get(index++);
    }

    @Override
    public void remove() {
        if(index == 0 || index > list.size())
            throw new IllegalStateException();
        list.remove(--index);
    }
}
