package dev.iwilkey.terrafort.obj.entity.element.ore;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TWorld;

public final class TCopper extends TOre {
	
	public static final int MAX_HP = 1000;

	public TCopper(TWorld world, int tileX, int tileY) {
		super(world, 
			  tileX, 
			  tileY, 
			  11, 
			  0, 
			  1, 
			  1, 
			  MAX_HP);
	}

	@Override
	public void drops() {
		for(int i = 0; i < 16; i++)
			world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.ORANGE));
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {

	}

	@Override
	public void onInteraction(TMob interactee) {
		hurt(1);
		world.addObject(new TParticle(world, x, y + TMath.nextFloat(0.0f, height), Color.ORANGE));
	}

}
