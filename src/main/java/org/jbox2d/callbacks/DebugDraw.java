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
/**
 * Created at 4:35:29 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import com.github.rccookie.geometry.performance.float2;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.particle.ParticleColor;

/**
 * Implement this abstract class to allow JBox2d to automatically draw your physics for debugging
 * purposes. Not intended to replace your own custom rendering routines!
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public abstract class DebugDraw {

  /** Draw shapes */
  public static final int e_shapeBit = 1 << 1;
  /** Draw joint connections */
  public static final int e_jointBit = 1 << 2;
  /** Draw axis aligned bounding boxes */
  public static final int e_aabbBit = 1 << 3;
  /** Draw pairs of connected objects */
  public static final int e_pairBit = 1 << 4;
  /** Draw center of mass frame */
  public static final int e_centerOfMassBit = 1 << 5;
  /** Draw dynamic tree */
  public static final int e_dynamicTreeBit = 1 << 6;
  /** Draw only the wireframe for drawing performance */
  public static final int e_wireframeDrawingBit = 1 << 7;


  protected int m_drawFlags;
  protected IViewportTransform viewportTransform;

  /**
   * <p>Constructor for DebugDraw.</p>
   */
  public DebugDraw() {
    this(null);
  }

  /**
   * <p>Constructor for DebugDraw.</p>
   *
   * @param viewport a {@link org.jbox2d.common.IViewportTransform} object
   */
  public DebugDraw(IViewportTransform viewport) {
    m_drawFlags = 0;
    viewportTransform = viewport;
  }

  /**
   * <p>Setter for the field <code>viewportTransform</code>.</p>
   *
   * @param viewportTransform a {@link org.jbox2d.common.IViewportTransform} object
   */
  public void setViewportTransform(IViewportTransform viewportTransform) {
    this.viewportTransform = viewportTransform;
  }

  /**
   * <p>setFlags.</p>
   *
   * @param flags a int
   */
  public void setFlags(int flags) {
    m_drawFlags = flags;
  }

  /**
   * <p>getFlags.</p>
   *
   * @return a int
   */
  public int getFlags() {
    return m_drawFlags;
  }

  /**
   * <p>appendFlags.</p>
   *
   * @param flags a int
   */
  public void appendFlags(int flags) {
    m_drawFlags |= flags;
  }

  /**
   * <p>clearFlags.</p>
   *
   * @param flags a int
   */
  public void clearFlags(int flags) {
    m_drawFlags &= ~flags;
  }

  /**
   * Draw a closed polygon provided in CCW order. This implementation uses
   * {@link #drawSegment(float2, float2, Color3f)} to draw each side of the polygon.
   *
   * @param vertices an array of {@link com.github.rccookie.geometry.performance.float2} objects
   * @param vertexCount a int
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public void drawPolygon(float2[] vertices, int vertexCount, Color3f color) {
    if (vertexCount == 1) {
      drawSegment(vertices[0], vertices[0], color);
      return;
    }

    for (int i = 0; i < vertexCount - 1; i += 1) {
      drawSegment(vertices[i], vertices[i + 1], color);
    }

    if (vertexCount > 2) {
      drawSegment(vertices[vertexCount - 1], vertices[0], color);
    }
  }

  /**
   * <p>drawPoint.</p>
   *
   * @param argPoint a {@link com.github.rccookie.geometry.performance.float2} object
   * @param argRadiusOnScreen a float
   * @param argColor a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawPoint(float2 argPoint, float argRadiusOnScreen, Color3f argColor);

  /**
   * Draw a solid closed polygon provided in CCW order.
   *
   * @param vertices an array of {@link com.github.rccookie.geometry.performance.float2} objects
   * @param vertexCount a int
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawSolidPolygon(float2[] vertices, int vertexCount, Color3f color);

  /**
   * Draw a circle.
   *
   * @param center a {@link com.github.rccookie.geometry.performance.float2} object
   * @param radius a float
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawCircle(float2 center, float radius, Color3f color);

  /**
   * Draws a circle with an axis
   *
   * @param center a {@link com.github.rccookie.geometry.performance.float2} object
   * @param radius a float
   * @param axis a {@link com.github.rccookie.geometry.performance.float2} object
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public void drawCircle(float2 center, float radius, float2 axis, Color3f color) {
    drawCircle(center, radius, color);
  }

  /**
   * Draw a solid circle.
   *
   * @param center a {@link com.github.rccookie.geometry.performance.float2} object
   * @param radius a float
   * @param axis a {@link com.github.rccookie.geometry.performance.float2} object
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawSolidCircle(float2 center, float radius, float2 axis, Color3f color);

  /**
   * Draw a line segment.
   *
   * @param p1 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param p2 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawSegment(float2 p1, float2 p2, Color3f color);

  /**
   * Draw a transform. Choose your own length scale
   *
   * @param xf a {@link org.jbox2d.common.Transform} object
   */
  public abstract void drawTransform(Transform xf);

  /**
   * Draw a string.
   *
   * @param x a float
   * @param y a float
   * @param s a {@link java.lang.String} object
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public abstract void drawString(float x, float y, String s, Color3f color);

  /**
   * Draw a particle array
   *
   * @param colors can be null
   * @param centers an array of {@link com.github.rccookie.geometry.performance.float2} objects
   * @param radius a float
   * @param count a int
   */
  public abstract void drawParticles(float2[] centers, float radius, ParticleColor[] colors, int count);

  /**
   * Draw a particle array
   *
   * @param colors can be null
   * @param centers an array of {@link com.github.rccookie.geometry.performance.float2} objects
   * @param radius a float
   * @param count a int
   */
  public abstract void drawParticlesWireframe(float2[] centers, float radius, ParticleColor[] colors,
                                              int count);

  /**
   * Called at the end of drawing a world
   */
  public void flush() {}

  /**
   * <p>drawString.</p>
   *
   * @param pos a {@link com.github.rccookie.geometry.performance.float2} object
   * @param s a {@link java.lang.String} object
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public void drawString(float2 pos, String s, Color3f color) {
    drawString(pos.x, pos.y, s, color);
  }

  /**
   * <p>getViewportTranform.</p>
   *
   * @return a {@link org.jbox2d.common.IViewportTransform} object
   */
  public IViewportTransform getViewportTranform() {
    return viewportTransform;
  }

  /**
   * <p>setCamera.</p>
   *
   * @param x a float
   * @param y a float
   * @param scale a float
   * @deprecated use the viewport transform in {@link #getViewportTranform()}
   */
  @Deprecated
  public void setCamera(float x, float y, float scale) {
    viewportTransform.setCamera(x, y, scale);
  }


  /**
   * <p>getScreenToWorldToOut.</p>
   *
   * @param argScreen a {@link com.github.rccookie.geometry.performance.float2} object
   * @param argWorld a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getScreenToWorldToOut(float2 argScreen, float2 argWorld) {
    viewportTransform.getScreenToWorld(argScreen, argWorld);
  }

  /**
   * <p>getWorldToScreenToOut.</p>
   *
   * @param argWorld a {@link com.github.rccookie.geometry.performance.float2} object
   * @param argScreen a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getWorldToScreenToOut(float2 argWorld, float2 argScreen) {
    viewportTransform.getWorldToScreen(argWorld, argScreen);
  }

  /**
   * Takes the world coordinates and puts the corresponding screen coordinates in argScreen.
   *
   * @param worldX a float
   * @param worldY a float
   * @param argScreen a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getWorldToScreenToOut(float worldX, float worldY, float2 argScreen) {
    argScreen.set(worldX, worldY);
    viewportTransform.getWorldToScreen(argScreen, argScreen);
  }

  /**
   * takes the world coordinate (argWorld) and returns the screen coordinates.
   *
   * @param argWorld a {@link com.github.rccookie.geometry.performance.float2} object
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getWorldToScreen(float2 argWorld) {
    float2 screen = new float2();
    viewportTransform.getWorldToScreen(argWorld, screen);
    return screen;
  }

  /**
   * Takes the world coordinates and returns the screen coordinates.
   *
   * @param worldX a float
   * @param worldY a float
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getWorldToScreen(float worldX, float worldY) {
    float2 argScreen = new float2(worldX, worldY);
    viewportTransform.getWorldToScreen(argScreen, argScreen);
    return argScreen;
  }

  /**
   * takes the screen coordinates and puts the corresponding world coordinates in argWorld.
   *
   * @param screenX a float
   * @param screenY a float
   * @param argWorld a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void getScreenToWorldToOut(float screenX, float screenY, float2 argWorld) {
    argWorld.set(screenX, screenY);
    viewportTransform.getScreenToWorld(argWorld, argWorld);
  }

  /**
   * takes the screen coordinates (argScreen) and returns the world coordinates
   *
   * @param argScreen a {@link com.github.rccookie.geometry.performance.float2} object
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getScreenToWorld(float2 argScreen) {
    float2 world = new float2();
    viewportTransform.getScreenToWorld(argScreen, world);
    return world;
  }

  /**
   * takes the screen coordinates and returns the world coordinates.
   *
   * @param screenX a float
   * @param screenY a float
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getScreenToWorld(float screenX, float screenY) {
    float2 screen = new float2(screenX, screenY);
    viewportTransform.getScreenToWorld(screen, screen);
    return screen;
  }
}
