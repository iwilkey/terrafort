package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.physics.TerrafortPhysicsCore;
import dev.iwilkey.terrafort.physics.PhysicsPrimitive;
import dev.iwilkey.terrafort.physics.PhysicsTag;
import dev.iwilkey.terrafort.physics.TerrafortRigidbody;


/**
 * Represents the physics identity of a game object.
 * It holds the collision shape, rigid body, mass, and body type.
 * @author iwilkey
 */
public final class PhysicsIdentity implements Disposable {
	
	private final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	private final btCollisionShape shape;
	private final TerrafortRigidbody rigidbody;
	private float mass;
	private byte bodyType;
	private Vector3 localInertia;
	
	/**
	 * Constructs a PhysicsIdentity object for a game object with the given parameters.
	 * @param model the 3D model of the game object
	 * @param dimensions the dimensions of the game object
	 * @param primitive the physics primitive type
	 * @param mass the mass of the game object
	 */
	public PhysicsIdentity(Model model, Vector3 dimensions, PhysicsPrimitive primitive, float mass) {
		this.mass = mass;
		localInertia = new Vector3();

		switch (primitive) {
			case CUBOID:
				shape = new btBoxShape(new Vector3(dimensions.x / 2f, dimensions.y / 2f, dimensions.z / 2f));
				break;
			case SPHERE:
				shape = new btSphereShape(dimensions.x / 2f);
				break;
			case CONE:
				shape = new btConeShape(dimensions.y / 2f, dimensions.x);
				break;
			case CAPSULE:
				shape = new btCapsuleShape(dimensions.x / 2f, dimensions.y / 2f);
				break;
			case MESH:
				shape = Bullet.obtainStaticNodeShape(model.nodes);
				break;
			default:
				shape = new btCylinderShape(new Vector3(dimensions.x / 2f, dimensions.y / 2f, dimensions.z / 2f));
				break;
		}

		if(mass > 0f) shape.calculateLocalInertia(mass, localInertia);
		else localInertia.set(0, 0, 0);
		
		constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		rigidbody = new TerrafortRigidbody(constructionInfo);
		constructionInfo.dispose();
		rigidbody.setTag(PhysicsTag.DEFAULT);
		setBodyType(TerrafortPhysicsCore.DYNAMIC_FLAG);
	}
	
	/**
	 * Sets the body type of the physics identity.
	 * @param flag the body type flag
	 */
	public void setBodyType(byte flag) {
	    bodyType = flag;
	    switch(bodyType) {
	        case TerrafortPhysicsCore.STATIC_FLAG:
	            rigidbody.setMassProps(0, new Vector3(0, 0, 0));
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setContactCallbackFlag(TerrafortPhysicsCore.STATIC_FLAG);
	            break;
	        case TerrafortPhysicsCore.KINEMATIC_FLAG:
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setActivationState(Collision.DISABLE_DEACTIVATION);
	            rigidbody.setContactCallbackFlag(TerrafortPhysicsCore.KINEMATIC_FLAG);
	            break;
	        case TerrafortPhysicsCore.DYNAMIC_FLAG:
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	            rigidbody.setContactCallbackFlag(TerrafortPhysicsCore.DYNAMIC_FLAG);
	            rigidbody.setContactCallbackFilter(TerrafortPhysicsCore.STATIC_FLAG | TerrafortPhysicsCore.KINEMATIC_FLAG);
	            break;
	    }
	}
	
	/**
	 * Returns the rigid body associated with the physics identity.
	 * @return the rigid body
	 */
	public TerrafortRigidbody getBody() {
		return rigidbody;
	}
	
	/**
	 * Returns the mass of the physics identity.
	 * @return the mass
	 */
	public float getMass() {
		return mass;
	}
	
	/**
	 * Returns the body type of the physics identity.
	 * @return the body type
	 */
	public byte getBodyType() {
		return bodyType;
	}
	
	/**
	 * Disposes the physics identity and releases any resources.
	 */
	@Override
	public void dispose() {
		rigidbody.dispose();
		shape.dispose();
	}
}
