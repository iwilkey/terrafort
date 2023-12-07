package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A tile-sized physical object without life that can be placed or broken.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TTile extends TEntity {
	
	private int tx;
	private int ty;
	
	public TTile(TWorld world, 
				 int tileX, 
				 int tileY, 
				 int dataOffsetX, 
				 int dataOffsetY, 
				 int dataSelectionSquareWidth,
				 int dataSelectionSquareHeight, 
				 int maxHP) {
		super(world, 
			  false, 
			  tileX * TTerrain.TERRAIN_TILE_WIDTH, 
			  tileY * TTerrain.TERRAIN_TILE_HEIGHT, 
			  0,
			  TTerrain.TERRAIN_TILE_WIDTH, 
			  TTerrain.TERRAIN_TILE_HEIGHT, 
			  TTerrain.TERRAIN_TILE_WIDTH / 2f, 
			  TTerrain.TERRAIN_TILE_HEIGHT / 2f, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  Color.WHITE.cpy(), 
			  maxHP);
		tx = tileX;
		ty = tileY;
	}
	
	public final int getTileX() {
		return tx;
	}
	
	public final int getTileY() {
		return ty;
	}

}
