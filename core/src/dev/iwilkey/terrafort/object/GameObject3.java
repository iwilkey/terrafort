package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.physics.bullet.BulletMotion;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject3 extends GameObject implements RenderableProvider3 {
	
	private final BoundingBox boundingBox;
	private ModelInstance renderable;
	private Vector3 position;
	private PhysicsIdentity identity;
	private BulletMotion motion;
	private boolean isStatic;
	
	public GameObject3(State state, String pathToLoadedModel, BulletPrimitive primitive, float mass, double... dimensionScaling) {
		super(state);
		double scale;
		if(dimensionScaling.length == 0) {
			scale = 1f;
		} else if(dimensionScaling.length == 1) {
			scale = dimensionScaling[0];
		} else {
			System.out.println("[Terrafort Engine] You are using the dimension scaling parameter incorrectly. There should only be one value! The engine will use the first value.");
			scale = dimensionScaling[0];
		}
		renderable = new ModelInstance(TerrafortAssetHandler.getVoxelModel(pathToLoadedModel));
		boundingBox = new BoundingBox();
		renderable.calculateBoundingBox(boundingBox);
		position = new Vector3();
		identity = new PhysicsIdentity(TerrafortAssetHandler.getVoxelModel(pathToLoadedModel), getDimensions().cpy().scl((float)scale), primitive, mass);
		motion = new BulletMotion();
		motion.setTransform(renderable.transform);
		identity.getBody().setMotionState(motion);
		isStatic = false;
	}
	
	/**
	 * A static 3D object will be rendered in a ModelCache to save resources at rendering time.
	 * Note, it cannot be translated in any way, and if it is, the entire cache must be rebuilt which is
	 * expensive.
	 */
	public void setStatic() {
		isStatic = true;
		setPhysicsBodyType(BulletWrapper.STATIC_FLAG);
		state.getObjectHandler().registerGameObject3StaticOrDynamic(this);
	}
	
	public void setDynamic() {
		isStatic = false;
		setPhysicsBodyType(BulletWrapper.DYNAMIC_FLAG);
		state.getObjectHandler().registerGameObject3StaticOrDynamic(this);
	}
	
	public void setKinematic() {
		isStatic = false;
		setPhysicsBodyType(BulletWrapper.KINEMATIC_FLAG);
		state.getObjectHandler().registerGameObject3StaticOrDynamic(this);
	}
	
	public GameObject3 setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
		return this;
	}
	
	public GameObject3 setPosition(float x, float y, float z) {
		if(isStatic) {
			System.out.println("[Terrafort Engine] You cannot translate a static GameObject3! You must set this objects isStatic flag to false.");
			return this;
		}
		position.set(x, y, z);
		renderable.transform.translate(x - renderable.transform.getTranslation(new Vector3()).x, y - renderable.transform.getTranslation(new Vector3()).y, z - renderable.transform.getTranslation(new Vector3()).z);
		resetPhysicsIdentity();
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
	
	public GameObject3 setRotation(Vector3 axis, float deg) {
		if(isStatic) {
			System.out.println("[Terrafort Engine] You cannot rotate a static GameObject3! You must set this objects isStatic flag to false.");
			return this;
		}
		renderable.transform.rotate(axis, deg);
		resetPhysicsIdentity();
		return this;
	}
	
	public GameObject3 setPhysicsBodyType(byte flag) {
		if(isStatic && flag != BulletWrapper.STATIC_FLAG) {
			System.out.println("[Terrafort Engine] You cannot set a static GameObject3 to any other physics flag besides STATIC_FLAG. You must set this objects isStatic flag to false.");
			return this;
		}
		identity.setBodyType(flag);
		return this;
	}
	
	public GameObject3 setPhysicsTag(BulletPhysicsTag tag) {
		identity.getBody().setTag(tag);
		return this;
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}

	public PhysicsIdentity getPhysicsIdentity() {
		return identity;
	}
	
	public BulletMotion getMotion() {
		return motion;
	}
	
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
