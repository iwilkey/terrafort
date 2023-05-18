package dev.iwilkey.terrafort.physics.bullet;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public final class BulletCollisionListener extends ContactListener {
	
	@Override
	public void onContactStarted(btCollisionObject obj1, btCollisionObject obj2) {
		BulletRigidbody robj1 = (BulletRigidbody)obj1;
		BulletRigidbody robj2 = (BulletRigidbody)obj2;
		// Define what happens when two objects just start colliding with each other.
	}
	@Override
	public void onContactProcessed(btCollisionObject obj1, btCollisionObject obj2) {
		BulletRigidbody robj1 = (BulletRigidbody)obj1;
		BulletRigidbody robj2 = (BulletRigidbody)obj2;
		// Define what happens when two objects continually collide with each other.
	}
	
}
