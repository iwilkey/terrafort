package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A simple flower :)
 * @author Ian Wilkey (iwilkey)
 */
public final class TFlower extends TNaturalElement {
	
	public static final int MAX_HP = 1;

	public TFlower(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrain.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrain.TERRAIN_TILE_HEIGHT, 
		      1, 
		      TTerrain.TERRAIN_TILE_WIDTH, 
		      TTerrain.TERRAIN_TILE_HEIGHT, 
		      TTerrain.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrain.TERRAIN_TILE_HEIGHT / 2f,
		      7, 
		      ThreadLocalRandom.current().nextInt(0, 6),
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setAsSensor();
	}

	@Override
	public void drops() {
		for(int i = 0; i < 1; i++)
			world.addObject(new TParticle(world, x, y, (Math.random() > 0.5f) ? Color.PINK : Color.YELLOW));
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
	}

}
