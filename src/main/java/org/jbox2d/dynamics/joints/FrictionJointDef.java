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
 * Created at 7:23:39 AM Jan 20, 2011
 */
package org.jbox2d.dynamics.joints;

import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.dynamics.Body;

/**
 * Friction joint definition.
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class FrictionJointDef extends JointDef {


  /**
   * The local anchor point relative to bodyA's origin.
   */
  public final float2 localAnchorA;

  /**
   * The local anchor point relative to bodyB's origin.
   */
  public final float2 localAnchorB;

  /**
   * The maximum friction force in N.
   */
  public float maxForce;

  /**
   * The maximum friction torque in N-m.
   */
  public float maxTorque;

  /**
   * <p>Constructor for FrictionJointDef.</p>
   */
  public FrictionJointDef() {
    super(JointType.FRICTION);
    localAnchorA = new float2();
    localAnchorB = new float2();
    maxForce = 0f;
    maxTorque = 0f;
  }

  /**
   * Initialize the bodies, anchors, axis, and reference angle using the world anchor and world
   * axis.
   *
   * @param bA a {@link org.jbox2d.dynamics.Body} object
   * @param bB a {@link org.jbox2d.dynamics.Body} object
   * @param anchor a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void initialize(Body bA, Body bB, float2 anchor) {
    bodyA = bA;
    bodyB = bB;
    bA.getLocalPointToOut(anchor, localAnchorA);
    bB.getLocalPointToOut(anchor, localAnchorB);
  }
}
