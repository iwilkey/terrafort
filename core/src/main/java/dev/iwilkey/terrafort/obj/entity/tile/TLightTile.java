package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;

import box2dLight.Light;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * Any building tile that emits light.
 * @author Ian Wilkey (iwilkey)
 */
public final class TLightTile extends TBuildingTile {
	
	Light light;
	
	public TLightTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, maxHP);
		switch(item) {
			case CAMPFIRE:
				light = world.addPointLight(tileX * TTerrain.TILE_WIDTH, 
									tileY * TTerrain.TILE_HEIGHT, 
									32, 
									new Color().set(0xFFDF8Eaa));
				break;
			default:;
		}
	}

	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
	}
	
	@Override
	public void die() {
		super.die();
		light.remove(true);
	}

}
