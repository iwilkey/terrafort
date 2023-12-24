package dev.iwilkey.terrafort.obj.entity.tile;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A decoration tile that provides a good foundation for a fort. Can be passed through.
 * @author Ian Wilkey (iwilkey)
 */
public class TFloorTile extends TBuildingTile {

	public TFloorTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, TTerrain.TILE_WIDTH / 3, TTerrain.TILE_HEIGHT / 3, maxHP);
		// floors should render first.
		z = 2;
		setAsSensor();
	}
	
	@Override
	public void onInteraction(TMob interactee) {
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			hurt(1);
			for(int i = 0; i < ThreadLocalRandom.current().nextInt(4, 8); i++)
				interactee.getWorld().addObject(new TParticle(interactee.getWorld(), 
						                                      getTileX() * TTerrain.TILE_WIDTH, 
						                                      getTileY() * TTerrain.TILE_HEIGHT, 
						                                      Color.BROWN));
		}
	}

	@Override
	public void task(float dt) {
		// TODO Auto-generated method stub
	}
}
