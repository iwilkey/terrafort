package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import dev.iwilkey.terrafort.physics.PhysicsIdentity;

public final class Transform3 {
	
	private final GameObject3 provider;
	private final Vector3 position = new Vector3();
	private final Quaternion rotation = new Quaternion();
	private final Vector3 scale = new Vector3();
	
	public Transform3(final GameObject3 provider) {
		this.provider = provider;
		// Sync initial GameObject transform to these values.
		final Matrix4 transform = provider.getModelInstance().transform;
		transform.getTranslation(position);
		transform.getRotation(rotation);
		transform.getScale(scale);
	}
	
	/**
	 * Begin Position methods.
	 */
	
	public GameObject3 positionAbsolute(Vector3 position) {
		return positionAbsolute(position.x, position.y, position.z);
	}
	
	public GameObject3 positionAbsolute(float x, float y, float z) {
		position.set(x, y, z);
		sync();
		return provider;
	}
	
	public GameObject3 positionRelative(Vector3 deltaPosition) {
		return positionRelative(deltaPosition.x, deltaPosition.y, deltaPosition.z);
	}
	
	public GameObject3 positionRelative(float dx, float dy, float dz) {
		position.add(dx, dy, dz);
		sync();
		return provider;
	}
	
	public GameObject3 translateForwardFromCameraPersp(final Camera camera, float speed) {
		// Get the camera direction vector, keeping the x and z components.
		final Vector3 cameraDir = camera.direction.cpy().nor();
		cameraDir.y = 0;
		positionRelative(cameraDir.scl(speed));
		return provider;
	}
	
	public GameObject3 translateRightFromCameraPersp(final Camera camera, float speed) {
		final Vector3 cameraDir = camera.direction.cpy().nor();
		cameraDir.y = 0;
		// Get the right vector: cameraDir X (0, 1, 0).
		final Vector3 rightVector = cameraDir.crs(Vector3.Y).nor();
		positionRelative(rightVector.scl(speed));
		return provider;
	}
	
	public GameObject3 translateUp(float speed) {
		positionRelative(Vector3.Y.cpy().scl(speed));
		return provider;
	}
	
	public Vector3 getCurrentPosition() {
		return position.cpy();
	}

	/**
	 * End Position methods.
	 */
	
	/**
	 * Begin Rotation methods.
	 */
	
	public GameObject3 rotateAbsolute(Quaternion rotation) {
		return rotateAbsolute(rotation.x, rotation.y, rotation.z, rotation.w);
	}
	
	public GameObject3 rotateAbsolute(float x, float y, float z, float w) {
		rotation.set(x, y, z, w);
		sync();
		return provider;
	}
	
	public GameObject3 rotateRelativeAxisDegrees(final Vector3 axis, float deg) {
		axis.nor();
		final Matrix4 transform = new Matrix4(position, rotation, scale);
		transform.rotate(axis, deg);
		transform.getTranslation(position);
		transform.getRotation(rotation);
		transform.getScale(scale);
		sync();
		return provider;
	}
	
	public GameObject3 rotateAbsoluteToYawPitchRollDegrees(float yawDegrees, float pitchDegrees, float rollDegrees) {
		final Quaternion yawQuaternion = new Quaternion(Vector3.Y, yawDegrees + 180.0f);
	    final Quaternion pitchQuaternion = new Quaternion(Vector3.X, pitchDegrees);
	    final Quaternion rollQuaternion = new Quaternion(Vector3.Z, rollDegrees);
	    final Quaternion combined = yawQuaternion.mul(pitchQuaternion).mul(rollQuaternion);
	    rotation.set(combined);
	    sync();
	    return provider;
	}
	 
	public GameObject3 rotateAbsoluteToLookAt(final Vector3 direction) {
		final Matrix4 transform = new Matrix4(position, rotation, scale);
		transform.setToLookAt(direction, Vector3.Y).trn(position);
		transform.getTranslation(position);
		transform.getRotation(rotation);
		transform.getScale(scale);
		sync();
		return provider;
	}
	
	public Quaternion getCurrentRotation() {
		return rotation.cpy();
	}
	
	public float getCurrentYawDegrees() {
		return rotation.getYaw();
	}
	
	public float getCurrentPitchDegrees() {
		return rotation.getPitch();
	}
	
	public float getCurrentRollDegrees() {
		return rotation.getPitch();
	}
	
	public float getCurrentAngleAroundAxisDegrees(final Vector3 axis) {
		axis.nor();
		return rotation.getAngleAround(axis);
	}
	
	/**
	 * End Rotation methods.
	 */
	
	/**
	 * Begin Scale methods.
	 */
	
	public GameObject3 scaleAbsolute(float scaleFactor) {
		scale.set(scaleFactor, scaleFactor, scaleFactor);
		sync();
		return provider;
	}
	
	public GameObject3 scaleRelative(float scaleFactor) {
		scale.add(scaleFactor);
		sync();
		return provider;
	}
	
	public Vector3 getCurrentScale() {
		return scale.cpy();
	}
	
	/**
	 * End Scale methods.
	 */
	
	public GameObject3 getProvider() {
		return provider;
	}
	
	private void sync() {
		// byte flag = provider.getPhysicsIdentity().getBodyType();
		// Switch body to dynamic to edit the transform of the Rigidbody.
		provider.setKinematic();
		// Sync abstract values to graphical representation.
		final Matrix4 transform = provider.getModelInstance().transform;
		transform.set(position, rotation, scale);
		System.out.println(provider.getDimensions().toString());
		// Sync abstract values to physical representation.
		final PhysicsIdentity identity = provider.getPhysicsIdentity();
		final btRigidBody body = identity.getBody();
	    // Clear forces.
	    body.setLinearVelocity(new Vector3(0, 0, 0));
	    body.setAngularVelocity(new Vector3(0, 0, 0));
	    body.clearForces();
	    // Sync the graphical transform to the physical transform.
	    body.setWorldTransform(transform);
	    body.getMotionState().setWorldTransform(transform);
	    provider.getPhysicsIdentity().setDimensions(provider);
	   
	}
}
