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
package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.Transform;
import com.github.rccookie.geometry.performance.float2;

/**
 * A shape is used for collision detection. You can create a shape however you like. Shapes used for
 * simulation in World are created automatically when a Fixture is created. Shapes may encapsulate a
 * one or more child shapes.
 *
 */
public abstract class Shape {

  public final ShapeType m_type;
  public float m_radius;

  /**
   * <p>Constructor for Shape.</p>
   *
   * @param type a {@link org.jbox2d.collision.shapes.ShapeType} object
   */
  public Shape(ShapeType type) {
    this.m_type = type;
  }

  /**
   * Get the type of this shape. You can use this to down cast to the concrete shape.
   *
   * @return the shape type.
   */
  public ShapeType getType() {
    return m_type;
  }

  /**
   * The radius of the underlying shape. This can refer to different things depending on the shape
   * implementation
   *
   * @return a float
   */
  public float getRadius() {
    return m_radius;
  }

  /**
   * Sets the radius of the underlying shape. This can refer to different things depending on the
   * implementation
   *
   * @param radius a float
   */
  public void setRadius(float radius) {
    this.m_radius = radius;
  }

  /**
   * Get the number of child primitives
   *
   * @return a int
   */
  public abstract int getChildCount();

  /**
   * Test a point for containment in this shape. This only works for convex shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   * @return a boolean
   */
  public abstract boolean testPoint(final Transform xf, final float2 p);

  /**
   * Cast a ray against a child shape.
   *
   * @return if hit
   * @param output a {@link org.jbox2d.collision.RayCastOutput} object
   * @param input a {@link org.jbox2d.collision.RayCastInput} object
   * @param transform a {@link org.jbox2d.common.Transform} object
   * @param childIndex a int
   */
  public abstract boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
      int childIndex);


  /**
   * Given a transform, compute the associated axis aligned bounding box for a child shape.
   *
   * @param aabb a {@link org.jbox2d.collision.AABB} object
   * @param xf a {@link org.jbox2d.common.Transform} object
   * @param childIndex a int
   */
  public abstract void computeAABB(final AABB aabb, final Transform xf, int childIndex);

  /**
   * Compute the mass properties of this shape using its dimensions and density. The inertia tensor
   * is computed about the local origin.
   *
   * @param massData returns the mass data for this shape.
   * @param density the density in kilograms per meter squared.
   */
  public abstract void computeMass(final MassData massData, final float density);

  /**
   * Compute the distance from the current shape to the specified point. This only works for convex
   * shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   * @param normalOut returns the direction in which the distance increases.
   * @return distance returns the distance from the current shape.
   * @param childIndex a int
   */
  public abstract float computeDistanceToOut(Transform xf, float2 p, int childIndex, float2 normalOut);

  /**
   * <p>clone.</p>
   *
   * @return a {@link org.jbox2d.collision.shapes.Shape} object
   */
  public abstract Shape clone();
}
