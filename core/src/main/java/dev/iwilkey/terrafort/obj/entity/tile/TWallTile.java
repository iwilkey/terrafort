package dev.iwilkey.terrafort.obj.entity.tile;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A structural tile with decent strength. Cannot be passed through.
 * @author Ian Wilkey (iwilkey)
 */
public class TWallTile extends TBuildingTile {
	
	private TFrame topHealthSprite;
	private TFrame midHealthSprite;
	private TFrame lowHealthSprite;
	private Color  particleColor;
	
	public TWallTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, item, tileX, tileY, TTerrain.TILE_WIDTH / 2, TTerrain.TILE_HEIGHT / 2, maxHP);
		particleColor = Color.WHITE.cpy();
		switch(item) {
			case WOOD_WALL:
				particleColor   = Color.BROWN.cpy();
				topHealthSprite = new TFrame(5, 2, 1, 1);
				midHealthSprite = new TFrame(4, 2, 1, 1);
				lowHealthSprite = new TFrame(3, 2, 1, 1);
				break;
			case STONE_WALL:
				particleColor   = Color.GRAY.cpy();
				topHealthSprite = new TFrame(13, 0, 1, 1);
				midHealthSprite = new TFrame(13, 1, 1, 1);
				lowHealthSprite = new TFrame(13, 2, 1, 1);
				break;
			default: throw new IllegalArgumentException("You cannot make a " + item.is().getName() + " that has wall tile properties!");
		}
	}
	
	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
		// look at health and change sprite based on health...
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(4, 8); i++)
			interactee.getWorld().addObject(new TParticle(interactee.getWorld(), 
					                                      getTileX() * TTerrain.TILE_WIDTH, 
					                                      getTileY() * TTerrain.TILE_HEIGHT, 
					                                      particleColor));
	}
	
	@Override
	public void task(float dt) {
		if(getHealthPercentage() >= ((2 / 3f) * 100.0f)) setSprite(topHealthSprite);
		else if(getHealthPercentage() >= ((1 / 3f) * 100.0f)) setSprite(midHealthSprite);
		else setSprite(lowHealthSprite);
	}
	
}
