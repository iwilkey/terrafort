package dev.iwilkey.terrafort.physics.bullet;

import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

public class BulletRigidbody extends btRigidBody {
	
	private BulletPhysicsTag tag = BulletPhysicsTag.ALL;

	public BulletRigidbody(btRigidBodyConstructionInfo constructionInfo) {
		super(constructionInfo);
	}

	public void setTag(BulletPhysicsTag tag) {
		this.tag = tag;
	}

	public BulletPhysicsTag getTag() {
		return tag;
	}
	
}
