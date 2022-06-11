/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright notice,
 * 	  this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright notice,
 * 	  this list of conditions and the following disclaimer in the documentation
 * 	  and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d.pooling.stacks;

/**
 * <p>DynamicIntStack class.</p>
 *
 */
public class DynamicIntStack {

  private int[] stack;
  private int size;
  private int position;

  /**
   * <p>Constructor for DynamicIntStack.</p>
   *
   * @param initialSize a int
   */
  public DynamicIntStack(int initialSize) {
    stack = new int[initialSize];
    position = 0;
    size = initialSize;
  }

  /**
   * <p>reset.</p>
   */
  public void reset() {
    position = 0;
  }

  /**
   * <p>pop.</p>
   *
   * @return a int
   */
  public int pop() {
    assert (position > 0);
    return stack[--position];
  }

  /**
   * <p>push.</p>
   *
   * @param i a int
   */
  public void push(int i) {
    if (position == size) {
      int[] old = stack;
      stack = new int[size * 2];
      size = stack.length;
      System.arraycopy(old, 0, stack, 0, old.length);
    }
    stack[position++] = i;
  }

  /**
   * <p>getCount.</p>
   *
   * @return a int
   */
  public int getCount() {
    return position;
  }
}
