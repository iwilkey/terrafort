package dev.iwilkey.terrafort.item.canonical.weapon;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * An accurate projectile that deals a great amount of damage.
 * @author Ian Wilkey (iwilkey)
 */
public final class TThrowingAxeItem extends TItemDefinition {
	
	public static final int   ENERGY_TO_THROW  = 10;
	public static final int   THROWING_FORCE   = 64;
	public static final int   COLLISION_DAMAGE = 16;
	public static final float DENSITY          = 1.0f;
	public static final int   ANGLE_SPREAD     = 10;
	
	public TThrowingAxeItem() {
		super("Throwing Axe", 
			  "An accurate projectile that deals a great amount of damage.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be thrown. Hits deal " + COLLISION_DAMAGE + " [PINK]damage[]. Requires " + ENERGY_TO_THROW + " [YELLOW]energy[] to throw.", 
			  256,
			  new TFrame(6, 10, 1, 1), 
			  TItemFunction.WPON,
			  807,
			  1004);
	}
	
	@Override
	public boolean use(TPlayer player) {
		if(player.getEnergyPoints() >= ENERGY_TO_THROW) {
			player.getWorld().addObject(new TProjectile(player.getWorld(), 
														player, 
														TItem.THROWING_AXE, 
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
