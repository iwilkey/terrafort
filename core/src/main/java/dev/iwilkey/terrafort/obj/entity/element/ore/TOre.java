package dev.iwilkey.terrafort.obj.entity.element.ore;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.obj.entity.element.TNaturalElement;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A natural element that bares valuable metals and minerals used to make or fuel important in-game
 * items. Found in Stone.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TOre extends TNaturalElement {

	public TOre(TSinglePlayerWorld world, 
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
			  1, 
			  TTerrainRenderer.TERRAIN_TILE_WIDTH, 
			  TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
			  TTerrainRenderer.TERRAIN_TILE_WIDTH / 4f, 
			  TTerrainRenderer.TERRAIN_TILE_HEIGHT / 4f, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  Color.WHITE.cpy(), 
			  maxHP);
	}

}
