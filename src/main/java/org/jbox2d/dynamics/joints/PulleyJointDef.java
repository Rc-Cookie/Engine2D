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
 * Created at 12:11:41 PM Jan 23, 2011
 */
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Settings;
import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.dynamics.Body;

/**
 * Pulley joint definition. This requires two ground anchors, two dynamic body anchor points, and a
 * pulley ratio.
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class PulleyJointDef extends JointDef {

  /**
   * The first ground anchor in world coordinates. This point never moves.
   */
  public float2 groundAnchorA;

  /**
   * The second ground anchor in world coordinates. This point never moves.
   */
  public float2 groundAnchorB;

  /**
   * The local anchor point relative to bodyA's origin.
   */
  public float2 localAnchorA;

  /**
   * The local anchor point relative to bodyB's origin.
   */
  public float2 localAnchorB;

  /**
   * The a reference length for the segment attached to bodyA.
   */
  public float lengthA;

  /**
   * The a reference length for the segment attached to bodyB.
   */
  public float lengthB;

  /**
   * The pulley ratio, used to simulate a block-and-tackle.
   */
  public float ratio;

  /**
   * <p>Constructor for PulleyJointDef.</p>
   */
  public PulleyJointDef() {
    super(JointType.PULLEY);
    groundAnchorA = new float2(-1.0f, 1.0f);
    groundAnchorB = new float2(1.0f, 1.0f);
    localAnchorA = new float2(-1.0f, 0.0f);
    localAnchorB = new float2(1.0f, 0.0f);
    lengthA = 0.0f;
    lengthB = 0.0f;
    ratio = 1.0f;
    collideConnected = true;
  }

  /**
   * Initialize the bodies, anchors, lengths, max lengths, and ratio using the world anchors.
   *
   * @param b1 a {@link org.jbox2d.dynamics.Body} object
   * @param b2 a {@link org.jbox2d.dynamics.Body} object
   * @param ga1 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param ga2 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param anchor1 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param anchor2 a {@link com.github.rccookie.geometry.performance.float2} object
   * @param r a float
   */
  public void initialize(Body b1, Body b2, float2 ga1, float2 ga2, float2 anchor1, float2 anchor2, float r) {
    bodyA = b1;
    bodyB = b2;
    groundAnchorA = ga1;
    groundAnchorB = ga2;
    localAnchorA = bodyA.getLocalPoint(anchor1);
    localAnchorB = bodyB.getLocalPoint(anchor2);
    float2 d1 = anchor1.subed(ga1);
    lengthA = d1.abs();
    float2 d2 = anchor2.subed(ga2);
    lengthB = d2.abs();
    ratio = r;
    assert (ratio > Settings.EPSILON);
  }
}
