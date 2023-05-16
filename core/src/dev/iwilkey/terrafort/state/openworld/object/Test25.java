package dev.iwilkey.terrafort.state.openworld.object;

import dev.iwilkey.terrafort.object.GameObject25;
import dev.iwilkey.terrafort.state.State;

public class Test25 extends GameObject25 {

	public Test25(State state, boolean shouldBillboard) {
		super(state, "texture/crosshair.png", shouldBillboard);
		setPosition(0, 2, 0);
		setScale(0.05f);
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
