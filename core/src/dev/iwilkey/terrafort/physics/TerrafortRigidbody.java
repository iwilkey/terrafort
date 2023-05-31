package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

/**
 * Custom rigid body class for Terrafort physics.
 */
public class TerrafortRigidbody extends btRigidBody {
	
	private PhysicsTag tag = PhysicsTag.DEFAULT;

	public TerrafortRigidbody(btRigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
	}

	/**
	 * Sets the physics tag for the rigid body.
	 * 
	 * @param tag The physics tag to set.
	 */
	public void setTag(PhysicsTag tag) {
		this.tag = tag;
	}

	/**
	 * Returns the physics tag of the rigid body.
	 * 
	 * @return The physics tag of the rigid body.
	 */
	public PhysicsTag getTag() {
		return tag;
	}
	
}
