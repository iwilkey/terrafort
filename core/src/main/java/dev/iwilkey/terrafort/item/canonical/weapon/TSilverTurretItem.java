package dev.iwilkey.terrafort.item.canonical.weapon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TBuilding;
import dev.iwilkey.terrafort.ui.containers.interfaces.TShopInterface;

/**
 * A silver turret that shoots silver pellets at Bandits.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSilverTurretItem extends TItemDefinition {
	
	private static final long serialVersionUID = 7596693657244140657L;
	
	public static final int   STRENGTH    = 16;
	public static final float RANGE       = 100.0f; // the range, in world units.
	public static final float SWEEP_SPEED = 2.0f; // the sweep speed of the turret in rad/sec
	public static final float FIRE_RATE   = 0.25f; // how many times a second can this turret shoot?
	public static final long  ROUND_VALUE = 50; // how much does this turret cost to shoot?
	
	public TSilverTurretItem() {
		super("Silver Turret", 
			  "A shiny, silver turret that fires silver rounds.\n\n"
			  + "Range: [YELLOW]" + RANGE + " [m][]\n"
			  + "Sweep speed: [YELLOW]" + SWEEP_SPEED + " [rad/s][]\n"
		  	  + "Fire rate: [YELLOW]" + FIRE_RATE + " [rounds/s][]\n"
		  	  + "Round value: [YELLOW]" + TShopInterface.currencyToString(ROUND_VALUE) + " [$/round][]\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to automatically\n"
			  + "aim and engage with [RED]Bandits[].\n"
			  + "Requires " + STRENGTH + " hits to break.",
			  16,
			  new TFrame(6, 11, 1, 1), 
			  TItemFunction.FORT,
			  25349,
			  45493);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.SILVER_TURRET, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 4; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}
	
}
