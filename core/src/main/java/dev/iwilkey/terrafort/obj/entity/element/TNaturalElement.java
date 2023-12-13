package dev.iwilkey.terrafort.obj.entity.element;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A plant, tree, rock; any object found in nature that can be harvested.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TNaturalElement extends TEntity {

	public TNaturalElement(TWorld  world, 
			           boolean isDynamic, 
			           float   x, 
			           float   y, 
			           int     z, 
			           float   width, 
			           float   height,
			           float   colliderWidth, 
			           float   colliderHeight, 
			           int     dataOffsetX, 
			           int     dataOffsetY, 
			           int     dataSelectionSquareWidth,
			           int     dataSelectionSquareHeight, 
			           Color   renderTint, 
			           int     maxHP) {
		super(world, 
			  isDynamic, 
			  x, 
			  y, 
			  z, 
			  width, 
			  height, 
			  colliderWidth, 
			  colliderHeight, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  renderTint, 
			  maxHP);
	}
	
	/**
	 * Called when vegetation has been harvested and should drop items for collection.
	 */
	public abstract void drops();
	
	@Override
	public void die() {
		drops();
	}

}
