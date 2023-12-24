package dev.iwilkey.terrafort.item.canonical.food;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;

/**
 * A savory chicken wing.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChickenWingItem extends TItemDefinition {
	
	public static final int NUTRITION_RESTORATION = 9;
	
	public TChickenWingItem() {
		super("Chicken Wing", 
			  "A savory chicken wing.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Restores " + NUTRITION_RESTORATION + " [ORANGE]nutrition[].", 
			  256,
			  new TFrame(5, 11, 1, 1), 
			  TItemFunction.FOOD,
			  200,
			  250);
	}

	@Override
	public boolean use(TPlayer player) {
		if(player.getHungerPoints() != TPlayer.PLAYER_MAX_HUNGER) {
			player.giveHungerPoints(NUTRITION_RESTORATION);
			return true;
		}
		return false;
	}
	
}
