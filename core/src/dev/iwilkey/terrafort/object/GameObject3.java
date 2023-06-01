package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import dev.iwilkey.terrafort.Terrafort;
import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.asset.registers.VoxelModels;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.physics.TerrafortPhysicsCore;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.physics.PhysicsTag;
import dev.iwilkey.terrafort.physics.TerrafortRigidbodyUniqueMotion;
import dev.iwilkey.terrafort.state.State;

/**
 * A object that is rendered in 3D and is acted on by the physics engine.
 * @author iwilkey
 */
public class GameObject3 extends GameObject implements RenderableProvider3 {
	
	public static final Vector3 NEG_Z = new Vector3(0, 0, -1);
	
	protected BoundingBox boundingBox;
	protected ModelInstance renderable;
	protected Vector3 position;
	protected PhysicsIdentity identity;
	protected TerrafortRigidbodyUniqueMotion motion;
	protected boolean isStatic;
	
	/**
	 * Constructs a GameObject3 instance with the specified parameters.
	 * @param state the state of the game object
	 * @param pathToLoadedModel the path to the loaded model
	 * @param primitive the Terrafort physics primitive
	 * @param mass the mass of the game object
	 * @param dimensionScaling the scaling factor for dimensions (optional)
	 */
	public GameObject3(State state, String pathToLoadedModel, PhysicsPrimitive primitive, float mass, double physicsScale) {
		super(state);
		init(pathToLoadedModel, primitive, mass, physicsScale);
	}
	
	/**
	 * Constructs a GameObject3 instance with the specified parameters.
	 */
	public GameObject3(State state, VoxelModels voxelModel) {
		super(state);
		init(voxelModel.getFileHandle().name(), PhysicsPrimitive.CUBOID, 1.0f, 1.0f);
	}
	
	/**
	 * Constructs a GameObject3 instance with the specified parameters.
	 */
	public GameObject3(State state, VoxelModels voxelModel, PhysicsPrimitive primitive) {
		super(state);
		init(voxelModel.getFileHandle().name(), primitive, 1.0f, 1.0f);
	}
	
	/**
	 * Constructs a GameObject3 instance with the specified parameters.
	 */
	public GameObject3(State state, VoxelModels voxelModel, PhysicsPrimitive primitive, float mass) {
		super(state);
		init(voxelModel.getFileHandle().name(), primitive, mass, 1.0f);
	}
	
	/**
	 * Constructs a GameObject3 instance with the specified parameters.
	 */
	public GameObject3(State state, VoxelModels voxelModel, PhysicsPrimitive primitive, float mass, double physicsScale) {
		super(state);
		init(voxelModel.getFileHandle().name(), primitive, mass, physicsScale);
	}
	
	private void init(String pathToLoadedModel, PhysicsPrimitive primitive, float mass, double physicsScale) {
		renderable = new ModelInstance(TerrafortAssetHandler.getModel(pathToLoadedModel));
		boundingBox = new BoundingBox();
		renderable.calculateBoundingBox(boundingBox);
		position = new Vector3();
		identity = new PhysicsIdentity(TerrafortAssetHandler.getModel(pathToLoadedModel), getDimensions().cpy().scl((float)physicsScale), primitive, mass);
		motion = new TerrafortRigidbodyUniqueMotion();
		motion.setTransform(renderable.transform);
		identity.getBody().setMotionState(motion);
		setDynamic();
	}
	
	/**
	 * Sets the game object as static.
	 * A static 3D object will be rendered in a ModelCache to save resources at rendering time.
	 * Note, it cannot be translated in any way, and if it is, the entire cache must be rebuilt which is expensive.
	 */
	public void setStatic() {
		isStatic = true;
		setPhysicsBodyType(TerrafortPhysicsCore.STATIC_FLAG);
	}
	
	/**
	 * Sets the game object as dynamic.
	 * A dynamic object is subject to physics simulation and can be translated and rotated.
	 */
	public void setDynamic() {
		isStatic = false;
		setPhysicsBodyType(TerrafortPhysicsCore.DYNAMIC_FLAG);
	}
	
	/**
	 * Sets the game object as kinematic.
	 * A kinematic object is not affected by forces or gravity but can be moved programmatically.
	 */
	public void setKinematic() {
		isStatic = false;
		setPhysicsBodyType(TerrafortPhysicsCore.KINEMATIC_FLAG);
	}
	
	/**
	 * Sets the position of the game object.
	 * @param pos the position vector to set
	 * @return the game object itself
	 */
	public GameObject3 setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
		return this;
	}
	
	/**
	 * Sets the position of the game object.
	 * @param x the X coordinate of the position
	 * @param y the Y coordinate of the position
	 * @param z the Z coordinate of the position
	 * @return the game object itself
	 */
	public GameObject3 setPosition(float x, float y, float z) {
		if(isStatic) {
			Terrafort.log("You cannot translate a static GameObject3! You must set this object's isStatic flag to false.");
			return this;
		}
		position.set(x, y, z);
		renderable.transform.setTranslation(x, y, z);
		resetPhysicsIdentity();
		return this;
	}
	
	/**
	 * Move the GameObject in the "forward" direction based on the perspective of the given camera. Will go "backward" if speed is negative.
	 * 
	 * @param camera the given camera.
	 * @param speed the speed at which to move.
	 * @return the game object itself.
	 */
	public GameObject3 moveForwardFromCameraPersp(final Camera camera, float speed) {
		// Get the camera direction vector, keeping the x and z components.
		Vector3 cameraDir = camera.direction.cpy();
		cameraDir.y = 0;
		cameraDir.nor();
		setPosition(getPosition().cpy().add(cameraDir.scl(speed)));
		return this;
	}
	
	/**
	 * Move the GameObject in the "right" direction based on the perspective of the given camera. Will go "left" if speed is negative.
	 * 
	 * @param camera camera the given camera.
	 * @param speed the speed at which to move.
	 * @return the game object itself.
	 */
	public GameObject3 moveRightFromCameraPersp(final Camera camera, float speed) {
		// Get the camera direction vector, keeping the x and z components.
		Vector3 cameraDir = camera.direction.cpy();
		cameraDir.y = 0;
		cameraDir.nor();
		// Get the right vector: cameraDir X (0, 1, 0).
		Vector3 rightVector = cameraDir.crs(Vector3.Y).nor();
		setPosition(getPosition().cpy().add(rightVector.scl(speed)));
		return this;
	}
	
	/**
	 * Move the GameObject in the "up" direction based on (0, 1, 0) as the up vector. Will go "down" if speed is negative.
	 * 
	 * @param speed the speed at which to move.
	 * @return the game object itself.
	 */
	public GameObject moveUp(float speed) {
		setPosition(getPosition().cpy().add(Vector3.Y.cpy().scl(speed)));
		return this;
	}
	
	private void resetPhysicsIdentity() {
		btRigidBody body = identity.getBody();
		body.setLinearVelocity(new Vector3(0, 0, 0));
		body.setAngularVelocity(new Vector3(0, 0, 0));
		body.clearForces();
		identity.getBody().proceedToTransform(renderable.transform);
		body.activate();
	}
	
	/**
	 * Sets the rotation of the game object.
	 * @param axis the rotation axis vector
	 * @param deg the rotation angle in degrees
	 * @return the game object itself
	 */
	public GameObject3 setRotationAxis(Vector3 axis, float deg) {
		if(isStatic) {
			Terrafort.log("You cannot rotate a static GameObject3! You must set this object's isStatic flag to false.");
			return this;
		}
		renderable.transform.rotate(axis, deg);
		resetPhysicsIdentity();
		return this;
	}
	
	/**
	 * Set the rotation directly to yaw, pitch, and roll in degrees.
	 * @param yawDegrees
	 * @param pitchDegrees
	 * @param rollDegrees
	 * @return
	 */
	public GameObject3 setRotation(float yawDegrees, float pitchDegrees, float rollDegrees) {
	    final Quaternion yawQuaternion = new Quaternion(Vector3.Y, yawDegrees + 180.0f);
	    final Quaternion pitchQuaternion = new Quaternion(Vector3.X, pitchDegrees);
	    final Quaternion rollQuaternion = new Quaternion(Vector3.Z, rollDegrees);
	    final Quaternion combined = new Quaternion().set(yawQuaternion).mul(pitchQuaternion).mul(rollQuaternion);
	    renderable.transform.set(getPosition(), combined, new Vector3(1, 1, 1));
	    return this;
	}
	
	/**
	 * Set the GameObject's direction.
	 * @param direction the direction to look in.
	 * @return the game object itself.
	 */
	public GameObject3 setLookAt(Vector3 direction) {
		renderable.transform.setToLookAt(direction, Vector3.Y).trn(position);
		resetPhysicsIdentity();
		return this;
	}
	
	/**
	 * Sets the physics body type of the game object.
	 * @param flag the physics body type flag
	 * @return the game object itself
	 */
	public GameObject3 setPhysicsBodyType(byte flag) {
		if(isStatic) {
			Terrafort.log("You cannot set the physics properties of a static GameObject3. Set the isStatic flag to false.");
			return this;
		}
		identity.setBodyType(flag);
		return this;
	}
	
	private Vector3 force = new Vector3();
	
	/**
	 * Applies a central force to the game object.
	 * @param direction the direction of the force
	 * @param magnitude the magnitude of the force
	 * @return the game object itself
	 */
	public GameObject applyCentralForce(Vector3 direction, float magnitude) {
		force.set(direction);
		force.nor();
		force.scl(magnitude);
		identity.getBody().applyCentralForce(force);
		return this;
	}
	
	/**
	 * Sets the physics tag of the game object.
	 * @param tag the physics tag
	 * @return the game object itself
	 */
	public GameObject3 setPhysicsTag(PhysicsTag tag) {
		identity.getBody().setTag(tag);
		return this;
	}
	
	/**
	 * Returns the position of the game object.
	 * @return the position vector
	 */
	public Vector3 getPosition() {
		return position.cpy(); 
	}
	
	/**
	 * Return the objects current (yaw, pitch, roll) in a Vector3, ordered in that manner.
	 * @return the objects current (yaw, pitch, roll) in a Vector3, ordered in that manner.
	 */
	public Vector3 getYawPitchRoll() {
		Quaternion q = new Quaternion();
	    renderable.transform.getRotation(q);
	    final double yaw = Math.atan2(2.0*(q.y * q.z + q.w * q.x), q.w * q.w - q.x * q.x - q.y * q.y + q.z * q.z);
	    final double pitch = Math.asin(-2.0 * (q.x * q.z - q.w * q.y));
	    final double roll = Math.atan2(2.0 * (q.x * q.y + q.w * q.z), q.w * q.w + q.x * q.x - q.y * q.y - q.z * q.z);
	    return new Vector3((float)yaw * MathUtils.radiansToDegrees, (float)pitch * MathUtils.radiansToDegrees, (float)roll * MathUtils.radiansToDegrees);
	}

	/**
	 * Returns the physics identity of the game object.
	 * @return the physics identity
	 */
	public PhysicsIdentity getPhysicsIdentity() {
		return identity;
	}
	
	/**
	 * Returns the motion of the game object.
	 * @return the motion
	 */
	public TerrafortRigidbodyUniqueMotion getMotion() {
		return motion;
	}
	
	/**
	 * Checks if the game object is static.
	 * @return true if the game object is static, false otherwise
	 */
	public boolean isStatic() {
		return isStatic;
	}

	@Override
	public ModelInstance getModelInstance() {
		return renderable;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	@Override
	public Vector3 getDimensions() {
		Vector3 out = new Vector3();
		boundingBox.getDimensions(out);
		return out;
	}

}
