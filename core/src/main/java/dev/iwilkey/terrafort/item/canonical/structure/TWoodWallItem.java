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
 * A decent building material. Forged from logs.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWoodWallItem extends TItemDefinition {
	
	private static final long serialVersionUID = 6050882456974775060L;
	
	public static final int STRENGTH = 8;
	
	public TWoodWallItem() {
		super("Wood Wall", 
			  "A decent building material.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to provide shelter from [RED]Bandits[]. Requires " + STRENGTH + " hits to break.",
			  256,
			  new TFrame(5, 2, 1, 1), 
			  TItemFunction.FORT,
			  35,
			  50);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.WOOD_WALL, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 8; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}

}
