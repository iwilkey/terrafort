package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.collision.ClosestRayResultCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;

import dev.iwilkey.terrafort.gfx.Camera;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.utilities.Pair;

/**
 * Responsible for raycasting in the physics world.
 */
public final class TerrafortPhysicsRaycaster {
	
	private ClosestRayResultCallback raycast(Camera camera, btDynamicsWorld world, float distance) {
		Vector3 rayFrom = new Vector3();
		Vector3 rayTo = new Vector3();
		Vector3 rayFromWorld = new Vector3();
		Vector3 rayToWorld   = new Vector3();
		
		Ray pickRay = camera.getPickRay(Renderer.getWidth() / 2f, Renderer.getHeight() / 2f);
		rayFrom.set(pickRay.origin);
		rayTo.set(pickRay.direction).scl(distance).add(rayFrom);
		
		// Using the ClosestRay callback.
		final ClosestRayResultCallback callback = new ClosestRayResultCallback(rayFrom, rayTo);
		callback.setClosestHitFraction(1f);
		rayFromWorld.set(rayFrom.x, rayFrom.y, rayFrom.z);
		rayToWorld.set(rayTo.x, rayTo.y, rayTo.z);
		world.rayTest(rayFromWorld, rayToWorld, callback);
		
		if(callback.hasHit())
			return callback;
		
		return null;
	}
	
	/**
	 * Performs a raycast and returns the hit point in world coordinates.
	 * 
	 * @param camera The camera used for raycasting.
	 * @param world The dynamics world to perform the raycast in.
	 * @param distance The maximum distance of the raycast.
	 * @return The hit point in world coordinates, or null if no hit occurred.
	 */
	public Vector3 hitPoint(Camera camera, btDynamicsWorld world, float distance) {
		ClosestRayResultCallback result = raycast(camera, world, distance);
		Vector3 ret = new Vector3();
		if(result != null) {
			result.getHitPointWorld(ret);
			return ret;
		}
		return null;
	}
	
	/**
	 * Performs a raycast and returns the rigid body object that was hit.
	 * 
	 * @param camera The camera used for raycasting.
	 * @param world The dynamics world to perform the raycast in.
	 * @param distance The maximum distance of the raycast.
	 * @return The rigid body object that was hit, or null if no hit occurred.
	 */
	public TerrafortRigidbody hitObject(Camera camera, btDynamicsWorld world, float distance) {
		ClosestRayResultCallback result = raycast(camera, world, distance);
		if(result != null) 
			return (TerrafortRigidbody)result.getCollisionObject();
		return null;
	}
	
	/**
	 * Performs a raycast and returns both the hit rigid body object and the hit point in world coordinates.
	 * 
	 * @param camera The camera used for raycasting.
	 * @param world The dynamics world to perform the raycast in.
	 * @param distance The maximum distance of the raycast.
	 * @return A Pair object containing the hit rigid body object and the hit point in world coordinates, or null if no hit occurred.
	 */
	public Pair<TerrafortRigidbody, Vector3> hit(Camera camera, btDynamicsWorld world, float distance) {
		ClosestRayResultCallback result = raycast(camera, world, distance);
		if(result != null) {
			Vector3 hitPoint = new Vector3();
			result.getHitPointWorld(hitPoint);
			return new Pair<TerrafortRigidbody, Vector3>((TerrafortRigidbody)result.getCollisionObject(), hitPoint);
		}
		return null;
	}
}
