package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A bush. Found in grasslands.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBush extends TNaturalElement {
	
	public static final int MAX_HP = 2;
	
	public TBush(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrain.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrain.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrain.TERRAIN_TILE_WIDTH * 3, 
		      TTerrain.TERRAIN_TILE_HEIGHT * 3, 
		      TTerrain.TERRAIN_TILE_WIDTH / 1.5f, 
		      TTerrain.TERRAIN_TILE_HEIGHT / 4f,
		      9, 
		      ThreadLocalRandom.current().nextInt(0, 2),
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrain.TERRAIN_TILE_HEIGHT * 0.75f);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 4; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.BROWN));
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
		world.addObject(new TParticle(world, x, y, Color.BROWN));
	}
	
}
