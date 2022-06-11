package org.jbox2d.dynamics.joints;

import com.github.rccookie.geometry.performance.float2;

/**
 * Rope joint definition. This requires two body anchor points and a maximum lengths. Note: by
 * default the connected objects will not collide. see collideConnected in b2JointDef.
 *
 * @author Daniel Murphy
 * @version $Id: $Id
 */
public class RopeJointDef extends JointDef {

  /**
   * The local anchor point relative to bodyA's origin.
   */
  public final float2 localAnchorA = new float2();

  /**
   * The local anchor point relative to bodyB's origin.
   */
  public final float2 localAnchorB = new float2();

  /**
   * The maximum length of the rope. Warning: this must be larger than b2_linearSlop or the joint
   * will have no effect.
   */
  public float maxLength;

  /**
   * <p>Constructor for RopeJointDef.</p>
   */
  public RopeJointDef() {
    super(JointType.ROPE);
    localAnchorA.set(-1.0f, 0.0f);
    localAnchorB.set(1.0f, 0.0f);
  }
}
