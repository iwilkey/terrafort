package dev.iwilkey.terrafort.obj.entity.element;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TBandit;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A boulder. Found on asphalt and stone; near mountains.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBoulder extends TNaturalElement {
	
	public static final int MAX_HP = 16;

	public TBoulder(TWorld world, int tileX, int tileY) {
		super(world, 
		      false,
		      tileX * TTerrain.TILE_WIDTH, 
		      tileY * TTerrain.TILE_HEIGHT, 
		      0, 
		      TTerrain.TILE_WIDTH * 2, 
		      TTerrain.TILE_HEIGHT * 2, 
		      TTerrain.TILE_WIDTH / 1.5f, 
		      TTerrain.TILE_HEIGHT / 3f,
		      3, 
		      (int)TMath.equalPick(7.0f, 9.0f),
			  2, 
			  2, 
			  Color.WHITE.cpy(), 
			  MAX_HP);
		setGraphicsColliderOffset(-2, TTerrain.TILE_HEIGHT / 2f);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
		// there's a 20% chance the rock will spawn some valuable metal or mineral.
		if(Math.random() > 0.80f) {
			if(Math.random() > 0.5f)
				world.addObject(new TItemDrop(world, x, y, TItem.COAL));
			if(Math.random() > 0.25f)
				world.addObject(new TItemDrop(world, x, y, TItem.COPPER));
			if(Math.random() > 0.08f)
				world.addObject(new TItemDrop(world, x, y, TItem.SILVER));
			if(Math.random() > 0.01f)
				world.addObject(new TItemDrop(world, x, y, TItem.GOLD));
			
		}
		// otherwise, boulder just drop a lot of rocks.
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(8, 16); i++)
			world.addObject(new TItemDrop(world, x, y, TItem.ROCK));
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}
	
	@Override
	public void hurt(int amt) {
		super.hurt(amt);
		if(lastInteractee instanceof TBandit)
			return;
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
	}

	@Override
	public void onInteraction(TMob interactee) {
		super.onInteraction(interactee);
		if(interactee instanceof TBandit)
			return;
		for(int i = 0; i < 8; i++)
			world.addObject(new TParticle(world, x, y, Color.GRAY));
	}

}
