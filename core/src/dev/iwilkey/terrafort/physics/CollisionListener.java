package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

public final class CollisionListener extends ContactListener {
	
	@Override
	public void onContactStarted(btCollisionObject obj1, btCollisionObject obj2) {
		Rigidbody robj1 = (Rigidbody)obj1;
		Rigidbody robj2 = (Rigidbody)obj2;
		// Define what happens when two objects just start colliding with each other.
	}
	@Override
	public void onContactProcessed(btCollisionObject obj1, btCollisionObject obj2) {
		Rigidbody robj1 = (Rigidbody)obj1;
		Rigidbody robj2 = (Rigidbody)obj2;
		// Define what happens when two objects continually collide with each other.
	}
	
}
