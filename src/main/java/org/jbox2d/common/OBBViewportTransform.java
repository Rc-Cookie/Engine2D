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

/**
 * Orientated bounding box viewport transform
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class OBBViewportTransform implements IViewportTransform {

  public static class OBB {
    public final Mat22 R = new Mat22();
    public final float2 center = new float2();
    public final float2 extents = new float2();
  }

  protected final OBB box = new OBB();
  private boolean yFlip = false;
  private final Mat22 yFlipMat = new Mat22(1, 0, 0, -1);

  /**
   * <p>Constructor for OBBViewportTransform.</p>
   */
  public OBBViewportTransform() {
    box.R.setIdentity();
  }

  /**
   * <p>set.</p>
   *
   * @param vpt a {@link org.jbox2d.common.OBBViewportTransform} object
   */
  public void set(OBBViewportTransform vpt) {
    box.center.set(vpt.box.center);
    box.extents.set(vpt.box.extents);
    box.R.set(vpt.box.R);
    yFlip = vpt.yFlip;
  }

  /** {@inheritDoc} */
  public void setCamera(float x, float y, float scale) {
    box.center.set(x, y);
    Mat22.createScaleTransform(scale, box.R);
  }

  /**
   * <p>getExtents.</p>
   *
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getExtents() {
    return box.extents;
  }
  
  /** {@inheritDoc} */
  @Override
  public Mat22 getMat22Representation() {
    return box.R;
  }

  /** {@inheritDoc} */
  public void setExtents(float2 argExtents) {
    box.extents.set(argExtents);
  }

  /** {@inheritDoc} */
  public void setExtents(float halfWidth, float halfHeight) {
    box.extents.set(halfWidth, halfHeight);
  }

  /**
   * <p>getCenter.</p>
   *
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getCenter() {
    return box.center;
  }

  /** {@inheritDoc} */
  public void setCenter(float2 argPos) {
    box.center.set(argPos);
  }

  /** {@inheritDoc} */
  public void setCenter(float x, float y) {
    box.center.set(x, y);
  }

  /**
   * Gets the transform of the viewport, transforms around the center. Not a copy.
   *
   * @return a {@link org.jbox2d.common.Mat22} object
   */
  public Mat22 getTransform() {
    return box.R;
  }

  /**
   * Sets the transform of the viewport. Transforms about the center.
   *
   * @param transform a {@link org.jbox2d.common.Mat22} object
   */
  public void setTransform(Mat22 transform) {
    box.R.set(transform);
  }

  /**
   * {@inheritDoc}
   *
   * Multiplies the obb transform by the given transform
   */
  @Override
  public void mulByTransform(Mat22 transform) {
    box.R.mulLocal(transform);
  }

  /**
   * <p>isYFlip.</p>
   *
   * @return a boolean
   */
  public boolean isYFlip() {
    return yFlip;
  }

  /** {@inheritDoc} */
  public void setYFlip(boolean yFlip) {
    this.yFlip = yFlip;
  }

  private final Mat22 inv = new Mat22();

  /** {@inheritDoc} */
  public void getScreenVectorToWorld(float2 screen, float2 world) {
    box.R.invertToOut(inv);
    inv.mulToOut(screen, world);
    if (yFlip) {
      yFlipMat.mulToOut(world, world);
    }
  }

  /** {@inheritDoc} */
  public void getWorldVectorToScreen(float2 world, float2 screen) {
    box.R.mulToOut(world, screen);
    if (yFlip) {
      yFlipMat.mulToOut(screen, screen);
    }
  }

  /** {@inheritDoc} */
  public void getWorldToScreen(float2 world, float2 screen) {
    screen.x = world.x - box.center.x;
    screen.y = world.y - box.center.y;
    box.R.mulToOut(screen, screen);
    if (yFlip) {
      yFlipMat.mulToOut(screen, screen);
    }
    screen.x += box.extents.x;
    screen.y += box.extents.y;
  }

  private final Mat22 inv2 = new Mat22();

  /** {@inheritDoc} */
  public void getScreenToWorld(float2 screen, float2 world) {
    world.x = screen.x - box.extents.x;
    world.y = screen.y - box.extents.y;
    if (yFlip) {
      yFlipMat.mulToOut(world, world);
    }
    box.R.invertToOut(inv2);
    inv2.mulToOut(world, world);
    world.x += box.center.x;
    world.y += box.center.y;
  }
}
