package org.jbox2d.common;

import java.lang.reflect.Array;

/**
 * <p>BufferUtils class.</p>
 *
 */
public class BufferUtils {
  /**
   * Reallocate a buffer.
   *
   * @param klass a {@link java.lang.Class} object
   * @param oldBuffer an array of T[] objects
   * @param oldCapacity a int
   * @param newCapacity a int
   * @param <T> a T class
   * @return an array of T[] objects
   */
  @SuppressWarnings("deprecation")
  public static <T> T[] reallocateBuffer(Class<T> klass, T[] oldBuffer, int oldCapacity,
                                         int newCapacity) {
    assert (newCapacity > oldCapacity);
    @SuppressWarnings("unchecked")
    T[] newBuffer = (T[]) Array.newInstance(klass, newCapacity);
    if (oldBuffer != null) {
      System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
    }
    for (int i = oldCapacity; i < newCapacity; i++) {
      try {
        newBuffer[i] = klass.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return newBuffer;
  }

  /**
   * Reallocate a buffer.
   *
   * @param oldBuffer an array of {@link int} objects
   * @param oldCapacity a int
   * @param newCapacity a int
   * @return an array of {@link int} objects
   */
  public static int[] reallocateBuffer(int[] oldBuffer, int oldCapacity, int newCapacity) {
    assert (newCapacity > oldCapacity);
    int[] newBuffer = new int[newCapacity];
    if (oldBuffer != null) {
      System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
    }
    return newBuffer;
  }

  /**
   * Reallocate a buffer.
   *
   * @param oldBuffer an array of {@link float} objects
   * @param oldCapacity a int
   * @param newCapacity a int
   * @return an array of {@link float} objects
   */
  public static float[] reallocateBuffer(float[] oldBuffer, int oldCapacity, int newCapacity) {
    assert (newCapacity > oldCapacity);
    float[] newBuffer = new float[newCapacity];
    if (oldBuffer != null) {
      System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
    }
    return newBuffer;
  }

  /**
   * Reallocate a buffer. A 'deferred' buffer is reallocated only if it is not NULL. If
   * 'userSuppliedCapacity' is not zero, buffer is user supplied and must be kept.
   *
   * @param klass a {@link java.lang.Class} object
   * @param buffer an array of T[] objects
   * @param userSuppliedCapacity a int
   * @param oldCapacity a int
   * @param newCapacity a int
   * @param deferred a boolean
   * @param <T> a T class
   * @return an array of T[] objects
   */
  public static <T> T[] reallocateBuffer(Class<T> klass, T[] buffer, int userSuppliedCapacity,
      int oldCapacity, int newCapacity, boolean deferred) {
    assert (newCapacity > oldCapacity);
    assert (userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity);
    if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
      buffer = reallocateBuffer(klass, buffer, oldCapacity, newCapacity);
    }
    return buffer;
  }

  /**
   * Reallocate an int buffer. A 'deferred' buffer is reallocated only if it is not NULL. If
   * 'userSuppliedCapacity' is not zero, buffer is user supplied and must be kept.
   *
   * @param buffer an array of {@link int} objects
   * @param userSuppliedCapacity a int
   * @param oldCapacity a int
   * @param newCapacity a int
   * @param deferred a boolean
   * @return an array of {@link int} objects
   */
  public static int[] reallocateBuffer(int[] buffer, int userSuppliedCapacity, int oldCapacity,
      int newCapacity, boolean deferred) {
    assert (newCapacity > oldCapacity);
    assert (userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity);
    if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
      buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
    }
    return buffer;
  }

  /**
   * Reallocate a float buffer. A 'deferred' buffer is reallocated only if it is not NULL. If
   * 'userSuppliedCapacity' is not zero, buffer is user supplied and must be kept.
   *
   * @param buffer an array of {@link float} objects
   * @param userSuppliedCapacity a int
   * @param oldCapacity a int
   * @param newCapacity a int
   * @param deferred a boolean
   * @return an array of {@link float} objects
   */
  public static float[] reallocateBuffer(float[] buffer, int userSuppliedCapacity, int oldCapacity,
      int newCapacity, boolean deferred) {
    assert (newCapacity > oldCapacity);
    assert (userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity);
    if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
      buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
    }
    return buffer;
  }

  /**
   * Rotate an array, see std::rotate
   *
   * @param ray an array of T[] objects
   * @param first a int
   * @param new_first a int
   * @param last a int
   * @param <T> a T class
   */
  public static <T> void rotate(T[] ray, int first, int new_first, int last) {
    int next = new_first;
    while (next != first) {
      T temp = ray[first];
      ray[first] = ray[next];
      ray[next] = temp;
      first++;
      next++;
      if (next == last) {
        next = new_first;
      } else if (first == new_first) {
        new_first = next;
      }
    }
  }

  /**
   * Rotate an array, see std::rotate
   *
   * @param ray an array of {@link int} objects
   * @param first a int
   * @param new_first a int
   * @param last a int
   */
  public static void rotate(int[] ray, int first, int new_first, int last) {
    int next = new_first;
    while (next != first) {
      int temp = ray[first];
      ray[first] = ray[next];
      ray[next] = temp;
      first++;
      next++;
      if (next == last) {
        next = new_first;
      } else if (first == new_first) {
        new_first = next;
      }
    }
  }

  /**
   * Rotate an array, see std::rotate
   *
   * @param ray an array of {@link float} objects
   * @param first a int
   * @param new_first a int
   * @param last a int
   */
  public static void rotate(float[] ray, int first, int new_first, int last) {
    int next = new_first;
    while (next != first) {
      float temp = ray[first];
      ray[first] = ray[next];
      ray[next] = temp;
      first++;
      next++;
      if (next == last) {
        next = new_first;
      } else if (first == new_first) {
        new_first = next;
      }
    }
  }
}
