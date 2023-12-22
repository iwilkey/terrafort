package dev.iwilkey.terrafort.item.canonical.turret;

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
 * A turret that shoots copper pellets at Bandits.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBasicTurret extends TItemDefinition {
	
	public static final int   STRENGTH    = 8;
	public static final float RANGE       = 50.0f; // the range, in world units.
	public static final float SWEEP_SPEED = 1.0f; // the sweep speed of the turret in rad/sec
	public static final float FIRE_RATE   = 0.25f; // how many times a second can this turret shoot?
	public static final long  ROUND_VALUE = 0; // how much does this turret cost to shoot?
	
	public TBasicTurret() {
		super("Rusty Turret", 
			  "A basic turret that shoots copper rounds.\n\n"
			  + "Range: [YELLOW]" + RANGE + " [m][]\n"
			  + "Sweep speed: [YELLOW]" + SWEEP_SPEED + " [rad/s][]\n"
		  	  + "Fire rate: [YELLOW]" + FIRE_RATE + " [rounds/s][]\n"
		  	  + "Round value: [YELLOW]" + ROUND_VALUE + " [$/round][]\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed in the world to automatically\n"
			  + "aim and engage with [RED]Bandits[].\n"
			  + "Requires " + STRENGTH + " hit to break.",
			  16,
			  new TFrame(5, 10, 1, 1), 
			  TItemFunction.FORT,
			  5000,
			  10000);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.BASIC_TURRET, STRENGTH);
	    if(placed != null) {
	    	for(int i = 0; i < 4; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.BROWN));
			return true;
	    }
		return false;
	}
	
}
