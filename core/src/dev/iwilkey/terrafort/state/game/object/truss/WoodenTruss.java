package dev.iwilkey.terrafort.state.game.object.truss;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.state.State;

public class WoodenTruss extends GameObject3 {

	public WoodenTruss(State state) {
		super(state, "vox/truss/wooden_truss.vox.obj", BulletPrimitive.CUBOID, 1.0f);
	}

	@Override
	public void instantiation() {
		
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void dispose() {
		
	}

}
