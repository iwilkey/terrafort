package dev.iwilkey.terrafort.state.game.object.truss;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public class Cube extends GameObject3 {

	public Cube(State state) {
		super(state, "cube.txt", BulletPrimitive.CUBOID, 100.0f);
	}

	@Override
	public void instantiation() {
		setPhysicsBodyType(BulletWrapper.DYNAMIC_FLAG);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void dispose() {
		
	}

}
