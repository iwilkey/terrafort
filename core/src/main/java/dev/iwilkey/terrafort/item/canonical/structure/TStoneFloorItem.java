package dev.iwilkey.terrafort.item.canonical.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TBuilding;

/**
 * Simple stone flooring.
 * @author Ian Wilkey (iwilkey)
 */
public final class TStoneFloorItem extends TItemDefinition {
	
	public static final int STRENGTH = 4;

	public TStoneFloorItem() {
		super("Stone Tile Floor", 
			  "Basic stone flooring, arranged in neat tiles.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed to provide flooring for forts. Requires " + STRENGTH + " hits to break.\n\n"
			  + "To break, you must hold [PURPLE][CURSOR 2][] and attack simultaneously.",
			  256,
			  new TFrame(3, 13, 1, 1), 
			  TItemFunction.FORT,
			  25,
			  50);
	}
	
	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.STONE_TILE_FLOOR, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 4; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}
	
}
