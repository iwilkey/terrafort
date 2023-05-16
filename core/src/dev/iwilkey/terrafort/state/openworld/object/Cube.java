package dev.iwilkey.terrafort.state.openworld.object;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.state.State;

public class Cube extends GameObject3 {

	public Cube(State state) {
		super(state, "vox/cube/cube.vox.obj");
		setPosition(0, 0, 0);
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
