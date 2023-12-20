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
 * A tree. Found in grasslands. Drops sticks.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTree extends TNaturalElement {
	
	public static final int MAX_HP = 4;

	public TTree(TWorld world, int tileX, int tileY) {
		super(world, 
		      false, 
		      tileX * TTerrain.TILE_WIDTH, 
		      tileY * TTerrain.TILE_HEIGHT, 
		      0, 
		      TTerrain.TILE_WIDTH * 4, 
		      TTerrain.TILE_HEIGHT * 4, 
		      TTerrain.TILE_WIDTH / 2f, 
		      TTerrain.TILE_HEIGHT / 3.5f,
		      3, 
		      (int)TMath.equalPick(3.0f, 5.0f),
			  2, 
			  2, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(0, TTerrain.TILE_HEIGHT * 1.75f);
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}
	
	@Override
	public void onInteraction(TMob interactee) {
		super.onInteraction(interactee);
		world.addObject(new TParticle(world, x, y, Color.BROWN));
	}

	@Override
	public void drops() {
		for(int i = 0; i < 64; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.BROWN));
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(16, 32); i++)
			world.addObject(new TItemDrop(world, x, y, TItem.LOG));
	}

}
