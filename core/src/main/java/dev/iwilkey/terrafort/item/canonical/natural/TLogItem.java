package dev.iwilkey.terrafort.item.canonical.natural;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * Dropped naturally from bushes or trees. Can be thrown.
 * @author Ian Wilkey (iwilkey)
 */
public final class TLogItem extends TItemDefinition {
	
	public static final int   ENERGY_TO_THROW  = 3;
	public static final int   THROWING_FORCE   = 64;
	public static final int   COLLISION_DAMAGE = 1;
	public static final float DENSITY          = 1.0f;
	public static final int   ANGLE_SPREAD     = 30;
	
	public TLogItem() {
		super("Log", 
			  "Dropped naturally from\n[GREEN]bushes[] and [GREEN]trees[].\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be thrown. Hits\ndeal " + COLLISION_DAMAGE + " [PINK]damage[]. Requires\n" + ENERGY_TO_THROW + " [YELLOW]energy[] to throw.", 
			  256,
			  new TFrame(3, 11, 1, 1), 
			  TItemFunction.NATURAL);
	}
	
	@Override
	public boolean use(TPlayer player) {
		// it takes ENERGY_TO_THROW amount of energy to actually throw the projectile.
		if(player.getEnergyPoints() >= ENERGY_TO_THROW) {
			player.getWorld().addObject(new TProjectile(player.getWorld(), 
														player, 
														TItem.LOG, 
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
