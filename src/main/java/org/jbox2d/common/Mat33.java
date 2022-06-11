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

import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.float3;

import java.io.Serializable;

/**
 * A 3-by-3 matrix. Stored in column-major order.
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class Mat33 implements Serializable {
  private static final long serialVersionUID = 2L;

  /** Constant <code>IDENTITY</code> */
  public static final Mat33 IDENTITY = new Mat33(new float3(1, 0, 0), new float3(0, 1, 0), new float3(0,
      0, 1));

  public final float3 ex, ey, ez;

  /**
   * <p>Constructor for Mat33.</p>
   */
  public Mat33() {
    ex = new float3();
    ey = new float3();
    ez = new float3();
  }

  /**
   * <p>Constructor for Mat33.</p>
   *
   * @param exx a float
   * @param exy a float
   * @param exz a float
   * @param eyx a float
   * @param eyy a float
   * @param eyz a float
   * @param ezx a float
   * @param ezy a float
   * @param ezz a float
   */
  public Mat33(float exx, float exy, float exz, float eyx, float eyy, float eyz, float ezx,
      float ezy, float ezz) {
    ex = new float3(exx, exy, exz);
    ey = new float3(eyx, eyy, eyz);
    ez = new float3(ezx, ezy, ezz);
  }

  /**
   * <p>Constructor for Mat33.</p>
   *
   * @param argCol1 a {@link com.github.rccookie.geometry.performance.float3} object
   * @param argCol2 a {@link com.github.rccookie.geometry.performance.float3} object
   * @param argCol3 a {@link com.github.rccookie.geometry.performance.float3} object
   */
  public Mat33(float3 argCol1, float3 argCol2, float3 argCol3) {
    ex = argCol1.clone();
    ey = argCol2.clone();
    ez = argCol3.clone();
  }

  /**
   * <p>setZero.</p>
   */
  public void setZero() {
    ex.setZero();
    ey.setZero();
    ez.setZero();
  }

  /**
   * <p>set.</p>
   *
   * @param exx a float
   * @param exy a float
   * @param exz a float
   * @param eyx a float
   * @param eyy a float
   * @param eyz a float
   * @param ezx a float
   * @param ezy a float
   * @param ezz a float
   */
  public void set(float exx, float exy, float exz, float eyx, float eyy, float eyz, float ezx,
      float ezy, float ezz) {
    ex.x = exx;
    ex.y = exy;
    ex.z = exz;
    ey.x = eyx;
    ey.y = eyy;
    ey.z = eyz;
    ez.x = eyx;
    ez.y = eyy;
    ez.z = eyz;
  }

  /**
   * <p>set.</p>
   *
   * @param mat a {@link org.jbox2d.common.Mat33} object
   */
  public void set(Mat33 mat) {
    float3 vec = mat.ex;
    ex.x = vec.x;
    ex.y = vec.y;
    ex.z = vec.z;
    float3 vec1 = mat.ey;
    ey.x = vec1.x;
    ey.y = vec1.y;
    ey.z = vec1.z;
    float3 vec2 = mat.ez;
    ez.x = vec2.x;
    ez.y = vec2.y;
    ez.z = vec2.z;
  }

  /**
   * <p>setIdentity.</p>
   */
  public void setIdentity() {
    ex.x = (float) 1;
    ex.y = (float) 0;
    ex.z = (float) 0;
    ey.x = (float) 0;
    ey.y = (float) 1;
    ey.z = (float) 0;
    ez.x = (float) 0;
    ez.y = (float) 0;
    ez.z = (float) 1;
  }

  // / Multiply a matrix times a vector.
  /**
   * <p>mul.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float3} object
   * @return a {@link com.github.rccookie.geometry.performance.float3} object
   */
  public static final float3 mul(Mat33 A, float3 v) {
    return new float3(v.x * A.ex.x + v.y * A.ey.x + v.z + A.ez.x, v.x * A.ex.y + v.y * A.ey.y + v.z
        * A.ez.y, v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z);
  }

  /**
   * <p>mul22.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final float2 mul22(Mat33 A, float2 v) {
    return new float2(A.ex.x * v.x + A.ey.x * v.y, A.ex.y * v.x + A.ey.y * v.y);
  }

  /**
   * <p>mul22ToOut.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mul22ToOut(Mat33 A, float2 v, float2 out) {
    final float tempx = A.ex.x * v.x + A.ey.x * v.y;
    out.y = A.ex.y * v.x + A.ey.y * v.y;
    out.x = tempx;
  }

  /**
   * <p>mul22ToOutUnsafe.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public static final void mul22ToOutUnsafe(Mat33 A, float2 v, float2 out) {
    assert (v != out);
    out.y = A.ex.y * v.x + A.ey.y * v.y;
    out.x = A.ex.x * v.x + A.ey.x * v.y;
  }

  /**
   * <p>mulToOut.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float3} object
   * @param out a {@link com.github.rccookie.geometry.performance.float3} object
   */
  public static final void mulToOut(Mat33 A, float3 v, float3 out) {
    final float tempy = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
    final float tempz = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
    out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
    out.y = tempy;
    out.z = tempz;
  }

  /**
   * <p>mulToOutUnsafe.</p>
   *
   * @param A a {@link org.jbox2d.common.Mat33} object
   * @param v a {@link com.github.rccookie.geometry.performance.float3} object
   * @param out a {@link com.github.rccookie.geometry.performance.float3} object
   */
  public static final void mulToOutUnsafe(Mat33 A, float3 v, float3 out) {
    assert (out != v);
    out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
    out.y = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
    out.z = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
  }

  /**
   * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
   * in one-shot cases.
   *
   * @param b a {@link com.github.rccookie.geometry.performance.float2} object
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public final float2 solve22(float2 b) {
    float2 x = new float2();
    solve22ToOut(b, x);
    return x;
  }

  /**
   * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
   * in one-shot cases.
   *
   * @param b a {@link com.github.rccookie.geometry.performance.float2} object
   * @param out a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public final void solve22ToOut(float2 b, float2 out) {
    final float a11 = ex.x, a12 = ey.x, a21 = ex.y, a22 = ey.y;
    float det = a11 * a22 - a12 * a21;
    if (det != 0.0f) {
      det = 1.0f / det;
    }
    out.x = det * (a22 * b.x - a12 * b.y);
    out.y = det * (a11 * b.y - a21 * b.x);
  }

  // djm pooling from below
  /**
   * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
   * in one-shot cases.
   *
   * @param b a {@link com.github.rccookie.geometry.performance.float3} object
   * @return a {@link com.github.rccookie.geometry.performance.float3} object
   */
  public final float3 solve33(float3 b) {
    float3 x = new float3();
    solve33ToOut(b, x);
    return x;
  }

  /**
   * Solve A * x = b, where b is a column vector. This is more efficient than computing the inverse
   * in one-shot cases.
   *
   * @param b a {@link com.github.rccookie.geometry.performance.float3} object
   * @param out the result
   */
  public final void solve33ToOut(float3 b, float3 out) {
    assert (b != out);
    float3.cross(ey, ez, out);
    float det = ex.dot(out);
    if (det != 0.0f) {
      det = 1.0f / det;
    }
    float3.cross(ey, ez, out);
    final float x = det * b.dot(out);
    float3.cross(b, ez, out);
    final float y = det * ex.dot(out);
    float3.cross(ey, b, out);
    float z = det * ex.dot(out);
    out.x = x;
    out.y = y;
    out.z = z;
  }

  /**
   * <p>getInverse22.</p>
   *
   * @param M a {@link org.jbox2d.common.Mat33} object
   */
  public void getInverse22(Mat33 M) {
    float a = ex.x, b = ey.x, c = ex.y, d = ey.y;
    float det = a * d - b * c;
    if (det != 0.0f) {
      det = 1.0f / det;
    }

    M.ex.x = det * d;
    M.ey.x = -det * b;
    M.ex.z = 0.0f;
    M.ex.y = -det * c;
    M.ey.y = det * a;
    M.ey.z = 0.0f;
    M.ez.x = 0.0f;
    M.ez.y = 0.0f;
    M.ez.z = 0.0f;
  }

  // / Returns the zero matrix if singular.
  /**
   * <p>getSymInverse33.</p>
   *
   * @param M a {@link org.jbox2d.common.Mat33} object
   */
  public void getSymInverse33(Mat33 M) {
    float bx = ey.y * ez.z - ey.z * ez.y;
    float by = ey.z * ez.x - ey.x * ez.z;
    float bz = ey.x * ez.y - ey.y * ez.x;
    float det = ex.x * bx + ex.y * by + ex.z * bz;
    if (det != 0.0f) {
      det = 1.0f / det;
    }

    float a11 = ex.x, a12 = ey.x, a13 = ez.x;
    float a22 = ey.y, a23 = ez.y;
    float a33 = ez.z;

    M.ex.x = det * (a22 * a33 - a23 * a23);
    M.ex.y = det * (a13 * a23 - a12 * a33);
    M.ex.z = det * (a12 * a23 - a13 * a22);

    M.ey.x = M.ex.y;
    M.ey.y = det * (a11 * a33 - a13 * a13);
    M.ey.z = det * (a13 * a12 - a11 * a23);

    M.ez.x = M.ex.z;
    M.ez.y = M.ey.z;
    M.ez.z = det * (a11 * a22 - a12 * a12);
  }


  /**
   * <p>setScaleTransform.</p>
   *
   * @param scale a float
   * @param out a {@link org.jbox2d.common.Mat33} object
   */
  public final static void setScaleTransform(float scale, Mat33 out) {
    out.ex.x = scale;
    out.ey.y = scale;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ex == null) ? 0 : ex.hashCode());
    result = prime * result + ((ey == null) ? 0 : ey.hashCode());
    result = prime * result + ((ez == null) ? 0 : ez.hashCode());
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Mat33 other = (Mat33) obj;
    if (ex == null) {
      if (other.ex != null) return false;
    } else if (!ex.equals(other.ex)) return false;
    if (ey == null) {
      if (other.ey != null) return false;
    } else if (!ey.equals(other.ey)) return false;
    if (ez == null) {
      if (other.ez != null) return false;
    } else if (!ez.equals(other.ez)) return false;
    return true;
  }
}
