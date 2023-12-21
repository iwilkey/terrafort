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
import dev.iwilkey.terrafort.obj.world.TBuilding.TMaterial;

/**
 * A slightly structurally diminished building material.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWoodWallT2 extends TItemDefinition {
	
	public static final int STRENGTH = 4;
	
	public TWoodWallT2() {
		super("Wood Wall T2", 
			  "A slightly structurally diminished building"
			  + "\nmaterial.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to provide\n"
			  + "shelter from [RED]Bandits[]. Requires " + STRENGTH + " hits to break.",
			  256,
			  new TFrame(4, 2, 1, 1), 
			  TItemFunction.BULD,
			  65,
			  88);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.WORN_WOOD, TMaterial.WOOD, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 6; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}
	
}
