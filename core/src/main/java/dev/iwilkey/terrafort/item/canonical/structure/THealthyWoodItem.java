package dev.iwilkey.terrafort.item.canonical.structure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.TItemSpec;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TBuilding;
import dev.iwilkey.terrafort.obj.world.TBuilding.TMaterial;

/**
 * A decent building material. Forged from logs.
 * @author Ian Wilkey (iwilkey)
 */
public final class THealthyWoodItem extends TItemDefinition {
	
	public static final int STRENGTH = 8;
	
	public THealthyWoodItem() {
		super("Healthy Wood", 
			  "A decent building material.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to provide\n"
			  + "shelter from [RED]Bandits[]. Requires " + STRENGTH + " hits to break.",
			  256,
			  new TFrame(5, 2, 1, 1), 
			  TItemFunction.BUILDING,
			  new TItemSpec(TItem.WORN_WOOD, 2));
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.HEALTHY_WOOD, TMaterial.WOOD, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 8; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}

}
