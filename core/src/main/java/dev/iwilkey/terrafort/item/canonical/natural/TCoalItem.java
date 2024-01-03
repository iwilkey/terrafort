package dev.iwilkey.terrafort.item.canonical.natural;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * You can throw it. I'd sell it. You will also be able to use it for machines and generators later.
 * @author Ian Wilkey (iwilkey).
 */
public final class TCoalItem extends TItemDefinition {

	private static final long serialVersionUID = 166646299729208327L;
	
	public static final int   ENERGY_TO_THROW  = 1;
	public static final int   THROWING_FORCE   = 64;
	public static final int   COLLISION_DAMAGE = 2;
	public static final float DENSITY          = 1.0f;
	public static final int   ANGLE_SPREAD     = 30;
	
	public TCoalItem() {
		super("Coal", 
			  "Found naturally in the debris of rock in mountainous regions.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be thrown. Hits deal " + COLLISION_DAMAGE + " [PINK]damage[]. Requires " + ENERGY_TO_THROW + " [YELLOW]energy[] to throw.",
			  256,
			  new TFrame(7, 7, 1, 1), 
			  TItemFunction.NTRL,
			  400,
			  450);
	}
	
	@Override
	public boolean use(TPlayer player) {
		if(player.getEnergyPoints() >= ENERGY_TO_THROW) {
			player.getWorld().addObject(new TProjectile(player.getWorld(), 
														player, 
														TItem.COAL, 
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
