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
package org.jbox2d.common;

import java.io.Serializable;

import com.github.rccookie.geometry.performance.float2;

/**
 * Represents a rotation
 *
 * @author Daniel
 * @version $Id: $Id
 */
public class Rot implements Serializable {
  private static final long serialVersionUID = 1L;

  public float s, c; // sin and cos

  /**
   * <p>Constructor for Rot.</p>
   */
  public Rot() {
    setIdentity();
  }

  /**
   * <p>Constructor for Rot.</p>
   *
   * @param angle a float
   */
  public Rot(float angle) {
    set(angle);
  }

  /**
   * <p>getSin.</p>
   *
   * @return a float
   */
  public float getSin() {
    return s;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "Rot(s:" + s + ", c:" + c + ")";
  }

  /**
   * <p>getCos.</p>
   *
   * @return a float
   */
  public float getCos() {
    return c;
  }

  /**
   * <p>set.</p>
   *
   * @param angle a float
   * @return a {@link org.jbox2d.common.Rot} object
   */
  public Rot set(float angle) {
    s = MathUtils.sin(angle);
    c = MathUtils.cos(angle);
    return this;
  }

  /**
   * <p>set.</p>
   *
   * @param other a {@link org.jbox2d.common.Rot} object
   * @return a {@link org.jbox2d.common.Rot} object
   */
  public Rot set(Rot other) {
    s = other.s;
    c = other.c;
    return this;
  }

  /**
   * <p>setIdentity.</p>
   *
   * @return a {@link org.jbox2d.common.Rot} object
   */
  public Rot setIdentity() {
    s = 0;
    c = 1;
    return this;
  }

  /**
   * <p>getAngle.</p>
   *
   * @return a float
   */
  public float getAngle() {
    return MathUtils.atan2(s, c);
  }

  /**
   * <p>getXAxis.</p>
   *
   * @param xAxis a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getXAxis(float2 xAxis) {
    xAxis.set(c, s);
  }

  /**
   * <p>getYAxis.</p>
   *
   * @param yAxis a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getYAxis(float2 yAxis) {
    yAxis.set(-s, c);
  }

  // @Override // annotation omitted for GWT-compatibility
  /**
   * <p>clone.</p>
   *
   * @return a {@link org.jbox2d.common.Rot} object
   */
  public Rot clone() {
    Rot copy = new Rot();
    copy.s = s;
    copy.c = c;
    return copy;
  }

  /**
   * <p>mul.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param r a {@link org.jbox2d.common.Rot} object
   * @param out a {@link org.jbox2d.common.Rot} object
   */
  public static final void mul(Rot q, Rot r, Rot out) {
    float tempc = q.c * r.c - q.s * r.s;
    out.s = q.s * r.c + q.c * r.s;
    out.c = tempc;
  }

  /**
   * <p>mulUnsafe.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param r a {@link org.jbox2d.common.Rot} object
   * @param out a {@link org.jbox2d.common.Rot} object
   */
  public static final void mulUnsafe(Rot q, Rot r, Rot out) {
    assert (r != out);
    assert (q != out);
    // [qc -qs] * [rc -rs] = [qc*rc-qs*rs -qc*rs-qs*rc]
    // [qs qc] [rs rc] [qs*rc+qc*rs -qs*rs+qc*rc]
    // s = qs * rc + qc * rs
    // c = qc * rc - qs * rs
    out.s = q.s * r.c + q.c * r.s;
    out.c = q.c * r.c - q.s * r.s;
  }

  /**
   * <p>mulTrans.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param r a {@link org.jbox2d.common.Rot} object
   * @param out a {@link org.jbox2d.common.Rot} object
   */
  public static final void mulTrans(Rot q, Rot r, Rot out) {
    final float tempc = q.c * r.c + q.s * r.s;
    out.s = q.c * r.s - q.s * r.c;
    out.c = tempc;
  }

  /**
   * <p>mulTransUnsafe.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param r a {@link org.jbox2d.common.Rot} object
   * @param out a {@link org.jbox2d.common.Rot} object
   */
  public static final void mulTransUnsafe(Rot q, Rot r, Rot out) {
    // [ qc qs] * [rc -rs] = [qc*rc+qs*rs -qc*rs+qs*rc]
    // [-qs qc] [rs rc] [-qs*rc+qc*rs qs*rs+qc*rc]
    // s = qc * rs - qs * rc
    // c = qc * rc + qs * rs
    out.s = q.c * r.s - q.s * r.c;
    out.c = q.c * r.c + q.s * r.s;
  }

  /**
   * <p>mulToOut.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mulToOut(Rot q, float2 v, float2 out) {
    float tempy = q.s * v.x + q.c * v.y;
    out.x = q.c * v.x - q.s * v.y;
    out.y = tempy;
  }

  /**
   * <p>mulToOutUnsafe.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mulToOutUnsafe(Rot q, float2 v, float2 out) {
    out.x = q.c * v.x - q.s * v.y;
    out.y = q.s * v.x + q.c * v.y;
  }

  /**
   * <p>mulTrans.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mulTrans(Rot q, float2 v, float2 out) {
    final float tempy = -q.s * v.x + q.c * v.y;
    out.x = q.c * v.x + q.s * v.y;
    out.y = tempy;
  }

  /**
   * <p>mulTransUnsafe.</p>
   *
   * @param q a {@link org.jbox2d.common.Rot} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mulTransUnsafe(Rot q, float2 v, float2 out) {
    out.x = q.c * v.x + q.s * v.y;
    out.y = -q.s * v.x + q.c * v.y;
  }
}
