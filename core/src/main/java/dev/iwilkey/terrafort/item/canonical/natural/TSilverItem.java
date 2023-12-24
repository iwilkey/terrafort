package dev.iwilkey.terrafort.item.canonical.natural;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * A hard, semi-valuable metal.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSilverItem extends TItemDefinition {
	
	public static final int   ENERGY_TO_THROW  = 3;
	public static final int   THROWING_FORCE   = 64;
	public static final int   COLLISION_DAMAGE = 5;
	public static final float DENSITY          = 1.0f;
	public static final int   ANGLE_SPREAD     = 30;
	
	public TSilverItem() {
		super("Silver", 
			  "A hard, semi-valuable metal.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be thrown. Hits deal " + COLLISION_DAMAGE + " [PINK]damage[]. Requires " + ENERGY_TO_THROW + " [YELLOW]energy[] to throw.", 
			  256,
			  new TFrame(7, 10, 1, 1), 
			  TItemFunction.NTRL,
			  932,
			  1024);
	}
	
	@Override
	public boolean use(TPlayer player) {
		if(player.getEnergyPoints() >= ENERGY_TO_THROW) {
			player.getWorld().addObject(new TProjectile(player.getWorld(), 
														player, 
														TItem.SILVER, 
														THROWING_FORCE, 
														COLLISION_DAMAGE,
														DENSITY, 
														ANGLE_SPREAD));
			player.takeEnergyPoints(ENERGY_TO_THROW);
			return true;
		}
		return false;
	}
	
}
