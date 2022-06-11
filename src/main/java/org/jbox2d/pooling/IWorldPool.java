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
package org.jbox2d.pooling;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Rot;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.float3;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * World pool interface
 *
 * @author Daniel
 * @version $Id: $Id
 */
public interface IWorldPool {

	/**
	 * <p>getPolyContactStack.</p>
	 *
	 * @return a {@link org.jbox2d.pooling.IDynamicStack} object
	 */
	public IDynamicStack<Contact> getPolyContactStack();

	/**
	 * <p>getCircleContactStack.</p>
	 *
	 * @return a {@link org.jbox2d.pooling.IDynamicStack} object
	 */
	public IDynamicStack<Contact> getCircleContactStack();

	/**
	 * <p>getPolyCircleContactStack.</p>
	 *
	 * @return a {@link org.jbox2d.pooling.IDynamicStack} object
	 */
	public IDynamicStack<Contact> getPolyCircleContactStack();
	
    /**
     * <p>getEdgeCircleContactStack.</p>
     *
     * @return a {@link org.jbox2d.pooling.IDynamicStack} object
     */
    public IDynamicStack<Contact> getEdgeCircleContactStack();
    
    /**
     * <p>getEdgePolyContactStack.</p>
     *
     * @return a {@link org.jbox2d.pooling.IDynamicStack} object
     */
    public IDynamicStack<Contact> getEdgePolyContactStack();

    /**
     * <p>getChainCircleContactStack.</p>
     *
     * @return a {@link org.jbox2d.pooling.IDynamicStack} object
     */
    public IDynamicStack<Contact> getChainCircleContactStack();
    
    /**
     * <p>getChainPolyContactStack.</p>
     *
     * @return a {@link org.jbox2d.pooling.IDynamicStack} object
     */
    public IDynamicStack<Contact> getChainPolyContactStack();
    
	/**
	 * <p>popVec2.</p>
	 *
	 * @return a {@link com.github.rccookie.geometry.performance.float2} object
	 */
	public float2 popVec2();

	/**
	 * <p>popVec2.</p>
	 *
	 * @param num a int
	 * @return an array of {@link com.github.rccookie.geometry.performance.float2} objects
	 */
	public float2[] popVec2(int num);

	/**
	 * <p>pushVec2.</p>
	 *
	 * @param num a int
	 */
	public void pushVec2(int num);

	/**
	 * <p>popVec3.</p>
	 *
	 * @return a {@link com.github.rccookie.geometry.performance.float3} object
	 */
	public float3 popVec3();

	/**
	 * <p>popVec3.</p>
	 *
	 * @param num a int
	 * @return an array of {@link com.github.rccookie.geometry.performance.float3} objects
	 */
	public float3[] popVec3(int num);

	/**
	 * <p>pushVec3.</p>
	 *
	 * @param num a int
	 */
	public void pushVec3(int num);

	/**
	 * <p>popMat22.</p>
	 *
	 * @return a {@link org.jbox2d.common.Mat22} object
	 */
	public Mat22 popMat22();

	/**
	 * <p>popMat22.</p>
	 *
	 * @param num a int
	 * @return an array of {@link org.jbox2d.common.Mat22} objects
	 */
	public Mat22[] popMat22(int num);

	/**
	 * <p>pushMat22.</p>
	 *
	 * @param num a int
	 */
	public void pushMat22(int num);
	
	/**
	 * <p>popMat33.</p>
	 *
	 * @return a {@link org.jbox2d.common.Mat33} object
	 */
	public Mat33 popMat33();
	
	/**
	 * <p>pushMat33.</p>
	 *
	 * @param num a int
	 */
	public void pushMat33(int num);

	/**
	 * <p>popAABB.</p>
	 *
	 * @return a {@link org.jbox2d.collision.AABB} object
	 */
	public AABB popAABB();

	/**
	 * <p>popAABB.</p>
	 *
	 * @param num a int
	 * @return an array of {@link org.jbox2d.collision.AABB} objects
	 */
	public AABB[] popAABB(int num);

	/**
	 * <p>pushAABB.</p>
	 *
	 * @param num a int
	 */
	public void pushAABB(int num);
	
	/**
	 * <p>popRot.</p>
	 *
	 * @return a {@link org.jbox2d.common.Rot} object
	 */
	public Rot popRot();

	/**
	 * <p>pushRot.</p>
	 *
	 * @param num a int
	 */
	public void pushRot(int num);
	
	/**
	 * <p>getCollision.</p>
	 *
	 * @return a {@link org.jbox2d.collision.Collision} object
	 */
	public Collision getCollision();

	/**
	 * <p>getTimeOfImpact.</p>
	 *
	 * @return a {@link org.jbox2d.collision.TimeOfImpact} object
	 */
	public TimeOfImpact getTimeOfImpact();

	/**
	 * <p>getDistance.</p>
	 *
	 * @return a {@link org.jbox2d.collision.Distance} object
	 */
	public Distance getDistance();

	/**
	 * <p>getFloatArray.</p>
	 *
	 * @param argLength a int
	 * @return an array of {@link float} objects
	 */
	public float[] getFloatArray(int argLength);

	/**
	 * <p>getIntArray.</p>
	 *
	 * @param argLength a int
	 * @return an array of {@link int} objects
	 */
	public int[] getIntArray(int argLength);

	/**
	 * <p>getVec2Array.</p>
	 *
	 * @param argLength a int
	 * @return an array of {@link com.github.rccookie.geometry.performance.float2} objects
	 */
	public float2[] getVec2Array(int argLength);
}
