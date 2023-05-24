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

import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletRigidbody;

public final class PhysicsIdentity implements Disposable {
	
	private final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
	private final btCollisionShape shape;
	private final BulletRigidbody rigidbody;
	private float mass;
	private byte bodyType;
	private Vector3 localInertia;
	
	public PhysicsIdentity(Model model, Vector3 dimensions, BulletPrimitive primitive, float mass) {
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
			default:;
				shape = new btCylinderShape(new Vector3(dimensions.x / 2f, dimensions.y / 2f, dimensions.z / 2f));
				break;
		}

		if(mass > 0f) shape.calculateLocalInertia(mass, localInertia);
		else localInertia.set(0, 0, 0);
		
		constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
		rigidbody = new BulletRigidbody(constructionInfo);
		constructionInfo.dispose();
		rigidbody.setTag(BulletPhysicsTag.DEFAULT);
		setBodyType(BulletWrapper.DYNAMIC_FLAG);
	}
	
	public void setBodyType(byte flag) {
	    bodyType = flag;
	    switch(bodyType) {
	        case BulletWrapper.STATIC_FLAG:
	            rigidbody.setMassProps(0, new Vector3(0, 0, 0));
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setContactCallbackFlag(BulletWrapper.STATIC_FLAG);
	            break;
	        case BulletWrapper.KINEMATIC_FLAG:
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setActivationState(Collision.DISABLE_DEACTIVATION);
	            rigidbody.setContactCallbackFlag(BulletWrapper.KINEMATIC_FLAG);
	            break;
	        case BulletWrapper.DYNAMIC_FLAG:
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() & ~btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
	            rigidbody.setCollisionFlags(rigidbody.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	            rigidbody.setContactCallbackFlag(BulletWrapper.DYNAMIC_FLAG);
	            rigidbody.setContactCallbackFilter(BulletWrapper.STATIC_FLAG | BulletWrapper.KINEMATIC_FLAG);
	            break;
	    }
	}
	
	public BulletRigidbody getBody() {
		return rigidbody;
	}
	
	public float getMass() {
		return mass;
	}
	
	public byte getBodyType() {
		return bodyType;
	}
	
	@Override
	public void dispose() {
		rigidbody.dispose();
		shape.dispose();
	}
	
}
