package dev.iwilkey.terrafort.math;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticulate;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * Efficiently facilitates the the Terrafort physical collision filtering and notification system.
 * @author Ian Wilkey (iwilkey)
 */
public final class TCollisionManifold implements ContactListener {
	
	public static final short IGNORE_GROUP = 0x0001;
	
	@Override
	public void beginContact(Contact contact) {
		final Body bodyA = contact.getFixtureA().getBody();
		final Body bodyB = contact.getFixtureB().getBody();
		final TObject objA = (TObject)(bodyA.getUserData());
		final TObject objB = (TObject)(bodyB.getUserData());
		// collisions between a projectile and an entity should trigger the hurt() event for the entity and finish
		// the projectile.
		if(objB instanceof TProjectile && objA instanceof TEntity) {
			final TProjectile proj = (TProjectile)objB;
			if(!proj.shouldHurt())
				return;
			final TEntity ent  = (TEntity)objA;
			ent.hurt(proj.getCollisionDamage());
			proj.setDone();
			return;
		}
		// collisions between a player and item drop trigger the transferTo() event.
		if(objA instanceof TPlayer && objB instanceof TItemDrop) {
			final TItemDrop drop   = (TItemDrop)objB;
			final TPlayer   player = (TPlayer)objA;
			drop.transferTo(player);
			return;
		}
		// otherwise, particles are insignificant and shouldn't go through the manifold.
		if(objA instanceof TParticulate || objB instanceof TParticulate) 
			return;
		objA.addToCollisionManifold(objB);
		objB.addToCollisionManifold(objA);
	}

	@Override
	public void endContact(Contact contact) {
		final Body bodyA = contact.getFixtureA().getBody();
		final Body bodyB = contact.getFixtureB().getBody();
		final TObject objA = (TObject)(bodyA.getUserData());
		final TObject objB = (TObject)(bodyB.getUserData());
		objA.removeFromCollisionManifold(objB);
		objB.removeFromCollisionManifold(objA);
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
	
}
