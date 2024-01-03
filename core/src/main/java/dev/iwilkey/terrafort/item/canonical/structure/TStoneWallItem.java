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

public class TStoneWallItem extends TItemDefinition {
	
	private static final long serialVersionUID = -4826721491245465317L;
	
	public static final int STRENGTH = 20;
	
	public TStoneWallItem() {
		super("Stone Wall", 
			  "A strong and sturdy building material.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to provide shelter from [RED]Bandits[]. Requires " + STRENGTH + " hits to break.",
			  256,
			  new TFrame(13, 0, 1, 1), 
			  TItemFunction.FORT,
			  63,
			  100);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.STONE_WALL, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 8; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.GRAY));
			return true;
	    }
		return false;
	}
	
}
