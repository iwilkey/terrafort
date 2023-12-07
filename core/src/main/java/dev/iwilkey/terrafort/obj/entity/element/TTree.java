package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.particle.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A tree. Found in grasslands.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTree extends TNaturalElement {
	
	public static final int MAX_HP = 4;

	public TTree(TWorld world, int tileX, int tileY) {
		super(world, 
		      false, 
		      tileX * TTerrain.TERRAIN_TILE_WIDTH, 
		      tileY * TTerrain.TERRAIN_TILE_HEIGHT, 
		      0, 
		      TTerrain.TERRAIN_TILE_WIDTH * 4, 
		      TTerrain.TERRAIN_TILE_HEIGHT * 4, 
		      TTerrain.TERRAIN_TILE_WIDTH / 2f, 
		      TTerrain.TERRAIN_TILE_HEIGHT / 2f,
		      8, 
		      ThreadLocalRandom.current().nextInt(0, 2),
			  1, 
			  1, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrain.TERRAIN_TILE_HEIGHT * 1.5f);
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

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.BROWN));
	}

}
