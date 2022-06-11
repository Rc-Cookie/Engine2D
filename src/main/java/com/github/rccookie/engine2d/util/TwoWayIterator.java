package com.github.rccookie.engine2d.util;

import java.util.NoSuchElementException;

import com.github.rccookie.util.EmptyIteratorException;
import com.github.rccookie.util.IterableIterator;

public interface TwoWayIterator<T> extends IterableIterator<T> {


    /**
     * An empty iterable iterator. Use {@link #empty()} to get it casted
     * to a specific type.
     */
    TwoWayIterator<?> EMPTY = new TwoWayIterator<>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new EmptyIteratorException();
        }

        @Override
        public boolean hasPrev() {
            return false;
        }

        @Override
        public Object prev() throws NoSuchElementException {
            throw new EmptyIteratorException();
        }
    };



    boolean hasPrev();

    T prev() throws NoSuchElementException;

    default TwoWayIterator<T> reverse() {
        TwoWayIterator<T> self = this;
        return new TwoWayIterator<>() {
            @Override
            public boolean hasPrev() {
                return self.hasNext();
            }

            @Override
            public T prev() throws NoSuchElementException {
                return self.next();
            }

            @Override
            public boolean hasNext() {
                return self.hasPrev();
            }

            @Override
            public T next() {
                return self.prev();
            }

            @Override
            public TwoWayIterator<T> reverse() {
                return self;
            }
        };
    }

    default void skipToEnd() {
        while(hasNext()) next();
    }

    default void skipToStart() {
        while(hasPrev()) prev();
    }



    /**
     * Returns an empty iterable iterator.
     *
     * @param <T> The type (has no effect)
     * @return {@link #EMPTY}
     */
    @SuppressWarnings("unchecked")
    static <T> TwoWayIterator<T> empty() {
        return (TwoWayIterator<T>) EMPTY;
    }

}
