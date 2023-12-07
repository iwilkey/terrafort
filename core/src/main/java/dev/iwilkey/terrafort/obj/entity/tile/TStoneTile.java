package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.obj.entity.element.ore.TCopper;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A stone tile. Blends with terrain.
 * @author Ian Wilkey (iwilkey)
 */
public final class TStoneTile extends TTile {
	
	public static final int MAX_HP = 6;

	public TStoneTile(TWorld world, int tileX, int tileY) {
		super(world, 
			  tileX, 
			  tileY, 
			  3, 
			  0,
			  1, 
			  1,
			  MAX_HP);
		shouldDraw = false;
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}

	@Override
	public void onInteraction(TLifeform interactee) {
		hurt(1);
		for(int i = 0; i < 4; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
	}

	@Override
	public void die() {
		world.setTileHeightAt(getTileX(), getTileY(), 1);
		for(int i = 0; i < 16; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
		if(Math.random() > 0.90f) {
			world.addObject(new TCopper(world, getTileX(), getTileY()));
		}
	}

}
