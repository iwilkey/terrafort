package dev.iwilkey.terrafort.math;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.particle.TParticle;

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
		if(objA instanceof TParticle || objB instanceof TParticle)
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
		if(objA instanceof TParticle || objB instanceof TParticle)
			return;
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
