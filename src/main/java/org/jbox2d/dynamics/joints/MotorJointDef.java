package org.jbox2d.dynamics.joints;

import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.dynamics.Body;

/**
 * Motor joint definition.
 *
 * @author dmurph
 * @version $Id: $Id
 */
public class MotorJointDef extends JointDef {
  /**
   * Position of bodyB minus the position of bodyA, in bodyA's frame, in meters.
   */
  public final float2 linearOffset = new float2();

  /**
   * The bodyB angle minus bodyA angle in radians.
   */
  public float angularOffset;

  /**
   * The maximum motor force in N.
   */
  public float maxForce;

  /**
   * The maximum motor torque in N-m.
   */
  public float maxTorque;

  /**
   * Position correction factor in the range [0,1].
   */
  public float correctionFactor;

  /**
   * <p>Constructor for MotorJointDef.</p>
   */
  public MotorJointDef() {
    super(JointType.MOTOR);
    angularOffset = 0;
    maxForce = 1;
    maxTorque = 1;
    correctionFactor = 0.3f;
  }

  /**
   * <p>initialize.</p>
   *
   * @param bA a {@link org.jbox2d.dynamics.Body} object
   * @param bB a {@link org.jbox2d.dynamics.Body} object
   */
  public void initialize(Body bA, Body bB) {
    bodyA = bA;
    bodyB = bB;
    float2 xB = bodyB.getPosition();
    bodyA.getLocalPointToOut(xB, linearOffset);

    float angleA = bodyA.getAngle();
    float angleB = bodyB.getAngle();
    angularOffset = angleB - angleA;
  }
}
