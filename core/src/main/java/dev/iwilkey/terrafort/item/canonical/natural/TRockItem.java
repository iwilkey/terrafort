package dev.iwilkey.terrafort.item.canonical.natural;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TProjectile;

/**
 * A rock. Of course you can throw it.
 * @author Ian Wilkey (iwilkey)
 */
public final class TRockItem extends TItemDefinition {
	
	private static final long serialVersionUID = -4747675666879313009L;
	
	public static final int   ENERGY_TO_THROW  = 2;
	public static final int   THROWING_FORCE   = 64;
	public static final int   COLLISION_DAMAGE = 4;
	public static final float DENSITY          = 1.0f;
	public static final int   ANGLE_SPREAD     = 30;
	
	public TRockItem() {
		super("Rock", 
			  "A rock.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be thrown. Hits deal " + COLLISION_DAMAGE + " [PINK]damage[]. Requires " + ENERGY_TO_THROW + " [YELLOW]energy[] to throw.", 
			  256,
			  new TFrame(7, 8, 1, 1), 
			  TItemFunction.NTRL,
			  35,
			  50);
	}
	
	@Override
	public boolean use(TPlayer player) {
		if(player.getEnergyPoints() >= ENERGY_TO_THROW) {
			player.getWorld().addObject(new TProjectile(player.getWorld(), 
														player, 
														TItem.ROCK, 
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
