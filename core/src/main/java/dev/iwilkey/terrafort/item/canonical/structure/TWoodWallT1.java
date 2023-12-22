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

public final class TWoodWallT1 extends TItemDefinition {
	
	public static final int STRENGTH = 1;
	
	public TWoodWallT1() {
		super("Wood Wall T1", 
			  "A cheap, flimsy building material.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to provide\n"
			  + "shelter from [RED]Bandits[]. Requires " + STRENGTH + " hit to break.",
			  256,
			  new TFrame(3, 2, 1, 1), 
			  TItemFunction.FORT,
			  14,
			  20);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.WOOD_WALL_T1, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 4; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}

}
