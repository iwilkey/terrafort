package dev.iwilkey.terrafort.state.game.object.truss;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.state.State;

public class WoodenTruss extends GameObject3 {
	public WoodenTruss(State state) {
		super(state, "wood_truss.txt", BulletPrimitive.CUBOID, 100.0f, (double)0.95f);
	}
}
