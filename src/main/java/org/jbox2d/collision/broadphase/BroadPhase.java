package org.jbox2d.collision.broadphase;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.callbacks.PairCallback;
import org.jbox2d.callbacks.TreeCallback;
import org.jbox2d.callbacks.TreeRayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import com.github.rccookie.geometry.performance.float2;


/**
 * <p>BroadPhase interface.</p>
 *
 */
public interface BroadPhase {

  /** Constant <code>NULL_PROXY=-1</code> */
  public static final int NULL_PROXY = -1;

  /**
   * Create a proxy with an initial AABB. Pairs are not reported until updatePairs is called.
   *
   * @param aabb a {@link org.jbox2d.collision.AABB} object
   * @param userData a {@link java.lang.Object} object
   * @return a int
   */
  int createProxy(AABB aabb, Object userData);

  /**
   * Destroy a proxy. It is up to the client to remove any pairs.
   *
   * @param proxyId a int
   */
  void destroyProxy(int proxyId);

  /**
   * Call MoveProxy as many times as you like, then when you are done call UpdatePairs to finalized
   * the proxy pairs (for your time step).
   *
   * @param proxyId a int
   * @param aabb a {@link org.jbox2d.collision.AABB} object
   * @param displacement a {@link com.github.rccookie.geometry.performance.float2} object
   */
  void moveProxy(int proxyId, AABB aabb, float2 displacement);

  /**
   * <p>touchProxy.</p>
   *
   * @param proxyId a int
   */
  void touchProxy(int proxyId);

  /**
   * <p>getUserData.</p>
   *
   * @param proxyId a int
   * @return a {@link java.lang.Object} object
   */
  Object getUserData(int proxyId);

  /**
   * <p>getFatAABB.</p>
   *
   * @param proxyId a int
   * @return a {@link org.jbox2d.collision.AABB} object
   */
  AABB getFatAABB(int proxyId);

  /**
   * <p>testOverlap.</p>
   *
   * @param proxyIdA a int
   * @param proxyIdB a int
   * @return a boolean
   */
  boolean testOverlap(int proxyIdA, int proxyIdB);

  /**
   * Get the number of proxies.
   *
   * @return a int
   */
  int getProxyCount();

  /**
   * <p>drawTree.</p>
   *
   * @param argDraw a {@link org.jbox2d.callbacks.DebugDraw} object
   */
  void drawTree(DebugDraw argDraw);

  /**
   * Update the pairs. This results in pair callbacks. This can only add pairs.
   *
   * @param callback a {@link org.jbox2d.callbacks.PairCallback} object
   */
  void updatePairs(PairCallback callback);

  /**
   * Query an AABB for overlapping proxies. The callback class is called for each proxy that
   * overlaps the supplied AABB.
   *
   * @param callback a {@link org.jbox2d.callbacks.TreeCallback} object
   * @param aabb a {@link org.jbox2d.collision.AABB} object
   */
  void query(TreeCallback callback, AABB aabb);

  /**
   * Ray-cast against the proxies in the tree. This relies on the callback to perform a exact
   * ray-cast in the case were the proxy contains a shape. The callback also performs the any
   * collision filtering. This has performance roughly equal to k * log(n), where k is the number of
   * collisions and n is the number of proxies in the tree.
   *
   * @param input the ray-cast input data. The ray extends from p1 to p1 + maxFraction * (p2 - p1).
   * @param callback a callback class that is called for each proxy that is hit by the ray.
   */
  void raycast(TreeRayCastCallback callback, RayCastInput input);

  /**
   * Get the height of the embedded tree.
   *
   * @return a int
   */
  int getTreeHeight();

  /**
   * <p>getTreeBalance.</p>
   *
   * @return a int
   */
  int getTreeBalance();

  /**
   * <p>getTreeQuality.</p>
   *
   * @return a float
   */
  float getTreeQuality();
}
