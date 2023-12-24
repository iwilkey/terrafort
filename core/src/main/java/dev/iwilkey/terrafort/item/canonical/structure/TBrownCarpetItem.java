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

public final class TBrownCarpetItem extends TItemDefinition {
	
	public static final int STRENGTH = 1;

	public TBrownCarpetItem() {
		super("Brown Carpet", 
			  "Basic brown carpet.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed to provide flooring for forts. Requires " + STRENGTH + " hits to break.\n\n"
			  + "To break, you must hold [PURPLE][CURSOR 2][] and attack simultaneously.",
			  256,
			  new TFrame(4, 12, 1, 1), 
			  TItemFunction.FORT,
			  5,
			  10);
	}
	
	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.BROWN_CARPET, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 4; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}
	
}
