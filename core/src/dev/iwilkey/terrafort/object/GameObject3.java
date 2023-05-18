package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;

import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.physics.bullet.BulletMotion;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject3 extends GameObject implements RenderableProvider3 {
	
	private final BoundingBox boundingBox;
	private final  ModelInstance renderable;
	private Vector3 position;
	private PhysicsIdentity identity;
	private BulletMotion motion;
	
	public GameObject3(State state, String pathToLoadedModel, BulletPrimitive primitive, float mass) {
		super(state);
		renderable = new ModelInstance(state.getAssetManager().get(pathToLoadedModel, Model.class));
		boundingBox = new BoundingBox();
		renderable.calculateBoundingBox(boundingBox);
		position = new Vector3();
		identity = new PhysicsIdentity(state.getAssetManager().get(pathToLoadedModel, Model.class), getDimensions(), primitive, mass);
		motion = new BulletMotion();
		motion.setTransform(renderable.transform);
		identity.getBody().setMotionState(motion);
	}
	
	public GameObject3 setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
		return this;
	}
	
	public GameObject3 setPosition(float x, float y, float z) {
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
		renderable.transform.rotate(axis, deg);
		resetPhysicsIdentity();
		return this;
	}
	
	public GameObject3 setPhysicsBodyType(byte flag) {
		identity.setBodyType(flag);
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
