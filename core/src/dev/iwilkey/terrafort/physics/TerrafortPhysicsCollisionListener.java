package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;

/**
 * Represents a collision listener for the TerrafortPhysicsCore.
 */
public final class TerrafortPhysicsCollisionListener extends ContactListener {
	
	@Override
	public void onContactStarted(btCollisionObject obj1, btCollisionObject obj2) {
		TerrafortRigidbody robj1 = (TerrafortRigidbody)obj1;
		TerrafortRigidbody robj2 = (TerrafortRigidbody)obj2;
		// Define what happens when two objects just start colliding with each other.
	}
	@Override
	public void onContactProcessed(btCollisionObject obj1, btCollisionObject obj2) {
		TerrafortRigidbody robj1 = (TerrafortRigidbody)obj1;
		TerrafortRigidbody robj2 = (TerrafortRigidbody)obj2;
		// Define what happens when two objects continually collide with each other.
	}
	
}
