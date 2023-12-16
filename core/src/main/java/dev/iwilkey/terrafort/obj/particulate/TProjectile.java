package dev.iwilkey.terrafort.obj.particulate;

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
	
	public static final int WORLD_SIZE = 3;
	
	private int collisionDamage;
	
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
			  (int)originator.getActualX() + (TMath.DX[originator.getFacingDirection()] * (WORLD_SIZE * 3)), 
		      (int)originator.getActualY() + (-TMath.DY[originator.getFacingDirection()] * (WORLD_SIZE * 3)), 
		      WORLD_SIZE, 
		      WORLD_SIZE,
		      10.0f);
		this.collisionDamage = collisionDamage;
		this.dataOffsetX = item.is().getIcon().getDataOffsetX();
		this.dataOffsetY = item.is().getIcon().getDataOffsetY();
		this.dataSelectionSquareWidth = item.is().getIcon().getDataSelectionWidth();
		this.dataSelectionSquareHeight = item.is().getIcon().getDataSelectionHeight();
		randomDirectedImpulseForce(force, 
								   force + 1, 
								   new Vector2(TMath.DX[originator.getFacingDirection()], 
										       -TMath.DY[originator.getFacingDirection()]), 
								   angleSpreadDegrees);
		getPhysicalFixture().setDensity(density);
		getPhysicalBody().resetMassData();
		setAsSensor();
	}

	@Override
	public void behavior(float dt) {

	}
	
	/**
	 * The amount of damage the projectile does on a {@link TEntity}.
	 */
	public int getCollisionDamage() {
		return collisionDamage;
	}

}
