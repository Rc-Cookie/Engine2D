package com.github.rccookie.engine2d.util;

import java.util.*;

/**
 * Utility class to work with lists.
 */
public enum Lists {
    ;

    /**
     * Creates an immutable list view of the given array.
     *
     * @param elements The array to wrap
     * @param <E> The array content type
     * @return A list view of the elements
     */
    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        return new ImmutableArrayReferenceList<>(elements);
    }

    /**
     * List view of array.
     *
     * @param <E> Array type
     */
    private static class ImmutableArrayReferenceList<E> implements List<E> {

        private final E[] array;

        final int low, high, size;

        ImmutableArrayReferenceList(E[] array) {
            this.array = array;
            low = 0;
            high = array.length;
            size = high - low;
        }

        private ImmutableArrayReferenceList(E[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
            size = high - low;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            for(int i=low; i<high; i++)
                if(Objects.equals(array[i], o)) return true;
            return false;
        }

        @Override
        public ListIterator<E> iterator() {
            return listIterator();
        }

        @Override
        public Object[] toArray() {
            Object[] out = new Object[size];
            System.arraycopy(array, low, out, 0, size);
            return out;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            return (T[]) toArray();
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for(int i=low; i<high; i++)
                if(!contains(array[i])) return false;
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E get(int index) {
            return array[low + index];
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            for(int i=low; i<high; i++)
                if(Objects.equals(array[i], o)) return i - low;
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            for(int i=high-1; i>=low; i--)
                if(Objects.equals(array[i], o)) return i - low;
            return -1;
        }

        @Override
        public ListIterator<E> listIterator() {
            return new ListIteratorImpl(0);
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return new ListIteratorImpl(index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new ImmutableArrayReferenceList<>(array, low + fromIndex, low + toIndex);
        }

        private class ListIteratorImpl implements ListIterator<E> {

            int i;
            ListIteratorImpl(int i) {
                this.i = i-1;
            }
            @Override
            public boolean hasNext() {
                return i < size - 1;
            }

            @Override
            public E next() {
                if(!hasNext()) throw new NoSuchElementException();
                return array[++i - low];
            }

            @Override
            public boolean hasPrevious() {
                return i > 0;
            }

            @Override
            public E previous() {
                if(!hasPrevious()) throw new NoSuchElementException();
                return array[--i - low];
            }

            @Override
            public int nextIndex() {
                return i+1;
            }

            @Override
            public int previousIndex() {
                return i-1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(E e) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(E e) {
                throw new UnsupportedOperationException();
            }
        }
    }
}
