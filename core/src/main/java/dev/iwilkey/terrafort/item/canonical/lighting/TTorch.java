package dev.iwilkey.terrafort.item.canonical.lighting;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TBuilding;
import dev.iwilkey.terrafort.obj.world.TBuilding.TMaterial;

public final class TTorch extends TItemDefinition {

	public TTorch() {
		super("Torch", 
			  "Creates a dim, yellow light for those\ndark, cold nights.\n\n"
			  + "[YELLOW][ACTION][]\n"
			  + "Can be placed to create dim light.", 
			  256,
			  new TFrame(4, 11, 1, 1), 
			  TItemFunction.BULD,
			  50,
			  100);
	}

	@Override
	public boolean use(TPlayer player) {
		final Vector2 placed = TBuilding.place(player, TItem.TORCH, TMaterial.LIGHT, 1);
		if(placed != null) {
	    	for(int i = 0; i < 8; i++)
	    		player.getWorld().addObject(new TParticle(player.getWorld(), placed.x, placed.y, Color.ORANGE));
			return true;
	    }
		return false;
	}

}
