package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A 1x1 tile-sized physical object that can be placed or broken. Provides shelter. Blocks light.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TBuildingTile extends TEntity {
	
	private TItem item;
	private int   tx;
	private int   ty;
	
	public TBuildingTile(TWorld world, TItem item, int tileX, int tileY, int maxHP) {
		super(world, 
			  false, 
			  tileX * TTerrain.TILE_WIDTH, 
			  tileY * TTerrain.TILE_HEIGHT, 
			  0,
			  TTerrain.TILE_WIDTH, 
			  TTerrain.TILE_HEIGHT, 
			  TTerrain.TILE_WIDTH / 2f, 
			  TTerrain.TILE_HEIGHT / 2f, 
			  item.is().getIcon().getDataOffsetX(), 
			  item.is().getIcon().getDataOffsetY(),
			  item.is().getIcon().getDataSelectionWidth(), 
			  item.is().getIcon().getDataSelectionHeight(), 
			  Color.WHITE.cpy(), 
			  maxHP);
		this.item = item;
		tx        = tileX;
		ty        = tileY;
	}
	
	public final int getTileX() {
		return tx;
	}
	
	public final int getTileY() {
		return ty;
	}
	
	public final TItem getItem() {
		return item;
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

	@Override
	public void die() {

	}	

}
