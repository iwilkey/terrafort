package dev.iwilkey.terrafort.item.canonical.food;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.TItemSpec;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;

/**
 * A food that restores 1 nutrition.
 * @author Ian Wilkey (iwilkey)
 */
public final class TEscargotItem extends TItemDefinition {
	
	public TEscargotItem() {
		super("Escargot", 
			  "A \"[ORANGE]food[]\".\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Restores 1 [ORANGE]nutrition.[]", 
			  256,
			  new TFrame(7, 6, 1, 1), 
			  TItemFunction.FOOD,
			  new TItemSpec(TItem.SHELL, 16));
	}

	@Override
	public boolean use(TPlayer player) {
		if(player.getHungerPoints() + 1 <= TPlayer.PLAYER_MAX_HUNGER) {
			player.giveHungerPoints(1);
			return true;
		}
		return false;
	}
	
}
