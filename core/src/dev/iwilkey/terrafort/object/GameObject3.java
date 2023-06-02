package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.asset.registers.VoxelModels;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.physics.TerrafortPhysicsCore;
import dev.iwilkey.terrafort.physics.PhysicsIdentity;
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
	
	// Renderable.
	protected BoundingBox boundingBox;
	protected ModelInstance renderable;
	// Transform.
	protected Transform3 transform;
	// Physics.
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
		// Create the renderable and calculate physical dimensions based on graphical representation.
		renderable = new ModelInstance(TerrafortAssetHandler.getModel(pathToLoadedModel));
		transform = new Transform3(this);
		boundingBox = new BoundingBox();
		renderable.calculateBoundingBox(boundingBox);
		// Add the object to the physics engine by giving it a physics identity.
		identity = new PhysicsIdentity(this, primitive, mass, (float)physicsScale);
		// Set up the unique motion object that syncs transform information across the graphical and physical bodies.
		motion = new TerrafortRigidbodyUniqueMotion();
		motion.setTransform(renderable.transform);
		identity.getBody().setMotionState(motion);
		setDynamic();
		// Transform.
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
	 * Sets the physics body type of the game object.
	 * @param flag the physics body type flag
	 * @return the game object itself
	 */
	public GameObject3 setPhysicsBodyType(byte flag) {
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
	 * Returns the physics identity of the game object.
	 * 
	 * @return the physics identity
	 */
	public PhysicsIdentity getPhysicsIdentity() {
		return identity;
	}
	
	/**
	 * Returns the motion of the game object.
	 * 
	 * @return the motion
	 */
	public TerrafortRigidbodyUniqueMotion getMotion() {
		return motion;
	}
	
	/**
	 * Get the Transform object of the game object. Use this to change the transform properties.
	 * 
	 * @return this objects Transform object
	 */
	public Transform3 getTransform() {
		return transform;
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

		out.x *= transform.getCurrentScale().x;
		out.y *= transform.getCurrentScale().y;
		out.z *= transform.getCurrentScale().z;
		
		return out;
	}
}
