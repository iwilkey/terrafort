package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class Rigidbody extends btRigidBody {
	
	private PhysicsTag tag = PhysicsTag.ALL;

	public Rigidbody(btRigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
	}

	public void setTag(PhysicsTag tag) {
		this.tag = tag;
	}

	public PhysicsTag getTag() {
		return tag;
	}
	
}
