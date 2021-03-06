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
 * Created at 7:27:31 AM Jan 21, 2011
 */
package org.jbox2d.dynamics.joints;

import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.dynamics.Body;

/**
 * Wheel joint definition. This requires defining a line of motion using an axis and an anchor
 * point. The definition uses local anchor points and a local axis so that the initial configuration
 * can violate the constraint slightly. The joint translation is zero when the local anchor points
 * coincide in world space. Using local anchors and a local axis helps when saving and loading a
 * game.
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class WheelJointDef extends JointDef {

  /**
   * The local anchor point relative to body1's origin.
   */
  public final float2 localAnchorA = new float2();

  /**
   * The local anchor point relative to body2's origin.
   */
  public final float2 localAnchorB = new float2();

  /**
   * The local translation axis in body1.
   */
  public final float2 localAxisA = new float2();

  /**
   * Enable/disable the joint motor.
   */
  public boolean enableMotor;

  /**
   * The maximum motor torque, usually in N-m.
   */
  public float maxMotorTorque;

  /**
   * The desired motor speed in radians per second.
   */
  public float motorSpeed;

  /**
   * Suspension frequency, zero indicates no suspension
   */
  public float frequencyHz;

  /**
   * Suspension damping ratio, one indicates critical damping
   */
  public float dampingRatio;

  /**
   * <p>Constructor for WheelJointDef.</p>
   */
  public WheelJointDef() {
    super(JointType.WHEEL);
    localAxisA.set(1, 0);
    enableMotor = false;
    maxMotorTorque = 0f;
    motorSpeed = 0f;
  }

  /**
   * <p>initialize.</p>
   *
   * @param b1 a {@link org.jbox2d.dynamics.Body} object
   * @param b2 a {@link org.jbox2d.dynamics.Body} object
   * @param anchor a {@link com.github.rccookie.geometry.performance.float2} object
   * @param axis a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public void initialize(Body b1, Body b2, float2 anchor, float2 axis) {
    bodyA = b1;
    bodyB = b2;
    b1.getLocalPointToOut(anchor, localAnchorA);
    b2.getLocalPointToOut(anchor, localAnchorB);
    bodyA.getLocalVectorToOut(axis, localAxisA);
  }
}
