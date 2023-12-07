package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A boulder. Found in grasslands.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBoulder extends TNaturalElement {
	
	public static final int MAX_HP = 8;

	public TBoulder(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrain.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrain.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrain.TERRAIN_TILE_WIDTH * 2, 
		      TTerrain.TERRAIN_TILE_HEIGHT * 2, 
		      TTerrain.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrain.TERRAIN_TILE_HEIGHT / 4f,
		      10, 
		      ThreadLocalRandom.current().nextInt(0, 4),
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrain.TERRAIN_TILE_HEIGHT * 0.75f);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
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
		for(int i = 0; i < 2; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
	}

}
