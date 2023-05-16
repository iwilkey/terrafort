package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

public final class Motion extends btMotionState {
	
	private Matrix4 transform = null;
	
	public Motion() {}
	
	@Override
	public void getWorldTransform(Matrix4 worldTrans) {
		worldTrans.set(transform);
	}
	
	public void setWorldTransform(Matrix4 worldTrans) {
		transform.set(worldTrans);
	}
	
	public Matrix4 getTransform() {
		return transform;
	}
	
	public void setTransform(Matrix4 worldTrans) {
		transform = worldTrans;
	}
	
}
