package dev.iwilkey.terrafort.obj.entity.element.ore;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TTerrain;
import dev.iwilkey.terrafort.obj.entity.element.TNaturalElement;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A natural element that bares valuable metals and minerals used to make or fuel important in-game
 * items. Found in Stone.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TOre extends TNaturalElement {

	public TOre(TWorld world, 
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
			  1, 
			  TTerrain.TERRAIN_TILE_WIDTH, 
			  TTerrain.TERRAIN_TILE_HEIGHT, 
			  TTerrain.TERRAIN_TILE_WIDTH / 4f, 
			  TTerrain.TERRAIN_TILE_HEIGHT / 4f, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  Color.WHITE.cpy(), 
			  maxHP);
	}

}
