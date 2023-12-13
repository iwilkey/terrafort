package dev.iwilkey.terrafort.obj.entity.tile;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
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
			  tileX * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
			  tileY * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
			  0,
			  TTerrainRenderer.TERRAIN_TILE_WIDTH, 
			  TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
			  TTerrainRenderer.TERRAIN_TILE_WIDTH / 2f, 
			  TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2f, 
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
