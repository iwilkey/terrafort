package dev.iwilkey.terrafort.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.game.object.Planet;
import dev.iwilkey.terrafort.input.KeyBinding;
import dev.iwilkey.terrafort.state.State;

public final class SinglePlayer extends State {

	public SinglePlayer(final TerrafortEngine engine) {
		super(engine);
	}

	@Override
	public void registerKeyBindings(final KeyBinding stateKeyBindings) {
		stateKeyBindings.bind("action", Buttons.LEFT);
		stateKeyBindings.bind("forward", Keys.W);
		stateKeyBindings.bind("backward", Keys.S);
		stateKeyBindings.bind("strafe_left", Keys.A);
		stateKeyBindings.bind("strafe_right", Keys.D);
		stateKeyBindings.bind("ascend", Keys.SPACE);
		stateKeyBindings.bind("descend", Keys.SHIFT_LEFT);
	}

	@Override
	public void begin() {
		addGameObject(new Player(this));
		addGameObject(new Planet(this).getTransform().positionAbsolute(0, 0, 0));
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void gui() {

	}

	@Override
	public void end() {
		
	}
	
}
