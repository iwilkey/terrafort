package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Custom motion state for Terrafort rigid bodies.
 */
public final class TerrafortRigidbodyUniqueMotion extends btMotionState {
	
	private Matrix4 transform = null;
	
	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(transform);
	}
	
	public void setWorldTransform(Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
	
	/**
	 * Returns the transform matrix of the motion state.
	 * @return The transform matrix of the motion state.
	 */
	public Matrix4 getTransform() {
		return transform;
	}
	
	/**
	 * Sets the transform matrix of the motion state.
	 * @param worldTrans The new transform matrix to set.
	 */
	public void setTransform(Matrix4 worldTrans) {
		transform = worldTrans;
	}
	
}
