package dev.iwilkey.terrafort.state.game.object;

import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.object.GameObject2;
import dev.iwilkey.terrafort.state.State;

public class Crosshair extends GameObject2 {

	public Crosshair(State state) {
		super(state, "crosshair.png", 10, 10, Anchor.CENTER);
	}

	@Override
	public void instantiation() {}
	@Override
	public void tick() {}
	@Override
	public void dispose() {}

}
