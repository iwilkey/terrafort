package dev.iwilkey.terrafort.obj.entity.tile;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A structural tile with decent strength.
 * @author Ian Wilkey (iwilkey)
 */
public class TWoodTile extends TBuildingTile {
	
	public TWoodTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, maxHP);
	}
	
	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(4, 8); i++)
			interactee.getWorld().addObject(new TParticle(interactee.getWorld(), 
					                                      getTileX() * TTerrain.TILE_WIDTH, 
					                                      getTileY() * TTerrain.TILE_HEIGHT, 
					                                      Color.BROWN));
	}
	
}
