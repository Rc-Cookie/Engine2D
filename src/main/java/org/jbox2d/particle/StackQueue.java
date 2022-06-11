package org.jbox2d.particle;


/**
 * <p>StackQueue class.</p>
 *
 */
public class StackQueue<T> {

  private T[] m_buffer;
  private int m_front;
  private int m_back;
  private int m_end;

  /**
   * <p>Constructor for StackQueue.</p>
   */
  public StackQueue() {}

  /**
   * <p>reset.</p>
   *
   * @param buffer an array of T[] objects
   */
  public void reset(T[] buffer) {
    m_buffer = buffer;
    m_front = 0;
    m_back = 0;
    m_end = buffer.length;
  }

  /**
   * <p>push.</p>
   *
   * @param task a T object
   */
  public void push(T task) {
    if (m_back >= m_end) {
      System.arraycopy(m_buffer, m_front, m_buffer, 0, m_back - m_front);
      m_back -= m_front;
      m_front = 0;
      if (m_back >= m_end) {
        return;
      }
    }
    m_buffer[m_back++] = task;
  }

  /**
   * <p>pop.</p>
   *
   * @return a T object
   */
  public T pop() {
    assert (m_front < m_back);
    return m_buffer[m_front++];
  }

  /**
   * <p>empty.</p>
   *
   * @return a boolean
   */
  public boolean empty() {
    return m_front >= m_back;
  }

  /**
   * <p>front.</p>
   *
   * @return a T object
   */
  public T front() {
    return m_buffer[m_front];
  }
}
