package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
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
		      tileX * TTerrain.TILE_WIDTH, 
		      tileY * TTerrain.TILE_HEIGHT, 
		      1, 
		      TTerrain.TILE_WIDTH, 
		      TTerrain.TILE_HEIGHT, 
		      TTerrain.TILE_WIDTH / 2f, 
		      TTerrain.TILE_HEIGHT / 2f,
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
		world.addObject(new TParticle(world, x, y, (Math.random() > 0.5f) ? Color.PINK : Color.YELLOW));
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {
		
	}

	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
	}

}
