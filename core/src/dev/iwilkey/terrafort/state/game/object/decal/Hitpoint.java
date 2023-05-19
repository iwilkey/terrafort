package dev.iwilkey.terrafort.state.game.object.decal;

import dev.iwilkey.terrafort.object.GameObject25;
import dev.iwilkey.terrafort.state.State;

public final class Hitpoint extends GameObject25 {

	public Hitpoint(State state) {
		super(state, "crosshair.png", true);
		setPosition(0, 0, 0);
	}

	@Override
	public void instantiation() {
		setScale(0.01f);
	}
	@Override
	public void tick() {}
	@Override
	public void dispose() {}

}
