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
	
	public static final float TORCH_BASE_EMISSION_DISTANCE = 32.0f;
	
	private final float distance;
	private final Light light;
	
	public TLightTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, maxHP);
		switch(item) {
			case TORCH:
				distance = TORCH_BASE_EMISSION_DISTANCE;
				light = world.addPointLight(tileX * TTerrain.TILE_WIDTH, 
									tileY * TTerrain.TILE_HEIGHT, 
									distance, 
									new Color().set(0xFFDF8E77));
				break;
			default:
				throw new IllegalArgumentException("You cannot create a " + item.is().getName() + " tile that emits light.");
		}
	}
	
	float time = 0.0f;
	
	@Override
	public void task(float dt) {
		super.task(dt);
		time += dt;
		time %= 2 * Math.PI;
		light.setDistance(distance + (8.0f * (float)Math.sin(time)));
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
