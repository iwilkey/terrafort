package dev.iwilkey.terrafort.obj.type;

import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;

/**
 * A more complex {@link TEntity}. A resource is any object that is found in nature and can be harvested. This class serves as an abstract 
 * representation of the harvestable's state, rather than it's physical presence.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class THarvestable extends TEntity {

	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = -2287216980856761088L;
	
	/**
	 * What are the color of the particles that are knocked off from this harvestable object?
	 */
	public int debrisColor;
	
	/**
	 * Creates a new harvestable object at a given pair of tile coordinates.
	 */
	public THarvestable(int tileX, int tileY) {
		this.currentTileX = tileX;
		this.currentTileY = tileY;
	}
	
	/**
	 * Returns the amount of particles that will be spawned the moment this {@link THarvestable} ceases to exist.
	 */
	public abstract int getParticleCountAtDeath();
	
	/*
	 * Shakes the harvestable object; causes particles to fall off of it!
	 */
	public final void shake(final TObjectRuntime concrete) {
		for(int i = 0; i < 1; i++)
			concrete.getWorld().addObject(new TParticle(worldX, worldY + (colliderHeight * 2), debrisColor));
	}
	
	@Override
	public void death(final TObjectRuntime concrete) {
		for(int i = 0; i < getParticleCountAtDeath(); i++)
			concrete.getWorld().addObject(new TParticle(worldX, worldY, debrisColor));
	}
	
}	
