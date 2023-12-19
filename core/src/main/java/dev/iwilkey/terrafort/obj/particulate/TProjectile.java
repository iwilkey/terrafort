package dev.iwilkey.terrafort.obj.particulate;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A rigid, physical {@link TItem} that flies through the world at a high velocity. Deals damage to
 * colliding bodies.
 * @author Ian Wilkey (iwilkey)
 */
public final class TProjectile extends TParticulate {
	
	public static final int   WORLD_SIZE = 4;
	public static final float HURT_SPEED_THRESHOLD = 10.0f; 
	
	private boolean           shouldHurt;
	private int               collisionDamage;
	
	/**
	 * Creates a new projectile with the given properties.
	 * @param world the world it exists in.
	 * @param originator the {@link TMob} that launched it.
	 * @param item the {@link TItem} it represents.
	 * @param force the amount of force, in Newtons, the projectile is launched with.
	 * @param collisionDamage the amount of damage it does during a successful collision with another body.
	 * @param density the density of the object.
	 * @param angleSpreadDegrees the spread, in degrees, from the {@link TMob} as it is launched.
	 */
	public TProjectile(TWorld world, 
			           TMob originator, 
			           TItem item, 
			           int force,
			           int collisionDamage, 
			           float density,
			           int angleSpreadDegrees) {
		super(world, 
			  (int)originator.getActualX() + (TMath.DX[originator.getFacingDirection()] * (WORLD_SIZE * 2)), 
		      (int)originator.getActualY() + ((originator.getFacingDirection() == TMath.SOUTH) ? 0 : (originator.getRenderHeight() / 5f)) + (-TMath.DY[originator.getFacingDirection()] * (WORLD_SIZE * 2)), 
		      WORLD_SIZE, 
		      WORLD_SIZE,
		      10.0f);
		this.collisionDamage           = collisionDamage;
		this.dataOffsetX               = item.is().getIcon().getDataOffsetX();
		this.dataOffsetY               = item.is().getIcon().getDataOffsetY();
		this.dataSelectionSquareWidth  = item.is().getIcon().getDataSelectionWidth();
		this.dataSelectionSquareHeight = item.is().getIcon().getDataSelectionHeight();
		randomDirectedImpulseForce(force, 
								   force + 1, 
								   new Vector2(TMath.DX[originator.getFacingDirection()], 
										       -TMath.DY[originator.getFacingDirection()]), 
								   angleSpreadDegrees);
		getPhysicalFixture().setDensity(density);
		getPhysicalBody().resetMassData();
		getPhysicalBody().applyTorque(ThreadLocalRandom.current().nextInt(5000, 500000), false);
		getPhysicalBody().setLinearDamping(density * 2);
		getPhysicalBody().setAngularDamping(density * 2);
		setAsSensor();
	}

	@Override
	public void behavior(float dt) {
		shouldHurt = speed() > HURT_SPEED_THRESHOLD;
	}
	
	/**
	 * The amount of damage the projectile does on a {@link TEntity}.
	 */
	public int getCollisionDamage() {
		return collisionDamage;
	}
	
	/**
	 * Returns whether or not a projectile should hurt on contact. If a projectile has stopped,
	 * why should it hurt?
	 */
	public boolean shouldHurt() {
		return shouldHurt;
	}
	
	/**
	 * Get the current magnitude of the linear velocity vector.
	 */
	public float speed() {
		final float x = getPhysicalBody().getLinearVelocity().x;
		final float y = getPhysicalBody().getLinearVelocity().y;
		return (float)Math.sqrt((x * x) + (y * y));
	}

}
