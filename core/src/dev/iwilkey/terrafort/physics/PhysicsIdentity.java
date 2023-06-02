package dev.iwilkey.terrafort.physics;

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

import dev.iwilkey.terrafort.gfx.RenderableProvider3;


/**
 * Represents the physics identity of a game object.
 * It holds the collision shape, rigid body, mass, and body type.
 * @author iwilkey
 */
public final class PhysicsIdentity implements Disposable {
	
	private final RenderableProvider3 graphicalIdentity;
	private final PhysicsPrimitive primitive;
	private final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	private final TerrafortRigidbody rigidbody;
	private final Vector3 graphicalIdentityDimensions = new Vector3();
	private final Vector3 localInertia = new Vector3();
	
	private btCollisionShape shape = null;
	private float mass = 0.0f;
	private float physicsScale = 0.0f;
	private byte bodyType = 0b00000000;
	
	/**
	 * Constructs a PhysicsIdentity object for a game object with the given parameters.
	 * 
	 * @param model the 3D model of the game object
	 * @param dimensions the dimensions of the game object
	 * @param primitive the physics primitive type
	 * @param mass the mass of the game object
	 */
	public PhysicsIdentity(final RenderableProvider3 graphicalIdentity, final PhysicsPrimitive primitive, final float mass, final float physicsScale) {
		// Set identity properties.
		this.graphicalIdentity = graphicalIdentity;
		this.mass = mass;
		this.primitive = primitive;
		this.physicsScale = physicsScale;
		// Set the shape dimensions based on the graphical identity.
		setDimensions(graphicalIdentity);
		// Assign a rigidbody to the object.
		constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		rigidbody = new TerrafortRigidbody(constructionInfo);
		constructionInfo.dispose();
		// By default, it is tagged "DEFAULT".
		rigidbody.setTag(PhysicsTag.DEFAULT);
		// By default, it is a dynamic object.
		setBodyType(TerrafortPhysicsCore.DYNAMIC_FLAG);
	}
	
	public void setDimensions(final RenderableProvider3 graphicalIdentity) {
		// If the identity already had a shape, dispose of it.
		if(shape != null)
			shape.dispose();
		graphicalIdentityDimensions.set(graphicalIdentity.getDimensions().cpy().scl(physicsScale));
		switch (primitive) {
	        case CUBOID:
	            shape = new btBoxShape(new Vector3(graphicalIdentityDimensions.x / 2f, graphicalIdentityDimensions.y / 2f, graphicalIdentityDimensions.z / 2f));
	            break;
	        case SPHERE:
	            shape = new btSphereShape(graphicalIdentityDimensions.x / 2f);
	            break;
	        case CONE:
	            shape = new btConeShape(graphicalIdentityDimensions.y / 2f, graphicalIdentityDimensions.x);
	            break;
	        case CAPSULE:
	            shape = new btCapsuleShape(graphicalIdentityDimensions.x / 2f, graphicalIdentityDimensions.y / 2f);
	            break;
	        case MESH:
	            shape = Bullet.obtainStaticNodeShape(graphicalIdentity.getModelInstance().nodes);
	            break;
	        default:
	            shape = new btCylinderShape(new Vector3(graphicalIdentityDimensions.x / 2f, graphicalIdentityDimensions.y / 2f, graphicalIdentityDimensions.z / 2f));
	            break;
		}
	    if(mass > 0f) shape.calculateLocalInertia(mass, localInertia);
	    else localInertia.set(0, 0, 0);
	    // If the identity has a rigidbody assigned, change its properties.
	    if(rigidbody != null) {
		    rigidbody.setCollisionShape(shape);
		    rigidbody.setMassProps(mass, localInertia);
	    }
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
	 * 
	 * @return the rigid body
	 */
	public TerrafortRigidbody getBody() {
		return rigidbody;
	}
	
	/**
	 * Get the RenderableProvider3 identity.
	 * 
	 * @return the attached RenderableProvider3.
	 */
	public RenderableProvider3 getGraphicalIdentity() {
		return graphicalIdentity;
	}
	
	/**
	 * Returns the mass of the physics identity.
	 * 
	 * @return the mass
	 */
	public float getMass() {
		return mass;
	}
	
	/**
	 * Returns the body type of the physics identity.
	 * 
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
		if(shape != null)
			shape.dispose();
	}
}
