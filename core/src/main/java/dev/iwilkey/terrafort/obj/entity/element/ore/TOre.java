package dev.iwilkey.terrafort.obj.entity.element.ore;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.obj.entity.element.TNaturalElement;
import dev.iwilkey.terrafort.obj.world.TTerrain;
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
			  tileX * TTerrain.TILE_WIDTH, 
			  tileY * TTerrain.TILE_HEIGHT, 
			  1, 
			  TTerrain.TILE_WIDTH, 
			  TTerrain.TILE_HEIGHT, 
			  TTerrain.TILE_WIDTH / 4f, 
			  TTerrain.TILE_HEIGHT / 4f, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  Color.WHITE.cpy(), 
			  maxHP);
	}

}
