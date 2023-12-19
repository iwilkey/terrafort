package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A bush. Found in grasslands. Drops sticks.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBush extends TNaturalElement {
	
	public static final int MAX_HP = 1;
	
	public TBush(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrain.TILE_WIDTH, 
		      tileY * TTerrain.TILE_HEIGHT, 
		      0, 
		      TTerrain.TILE_WIDTH * 3f, 
		      TTerrain.TILE_HEIGHT * 3f, 
		      TTerrain.TILE_WIDTH / 1.5f, 
		      TTerrain.TILE_HEIGHT / 5f,
		      5, 
		      (int)TMath.equalPick(3.0f, 5.0f),
			  2,
			  2, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrain.TILE_HEIGHT * 0.5f);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 32; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height/ 2), Color.BROWN));
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(0, 8); i++)
			world.addObject(new TItemDrop(world, x, y, TItem.LOG));
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
