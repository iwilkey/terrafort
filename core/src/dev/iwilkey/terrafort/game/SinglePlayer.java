package dev.iwilkey.terrafort.game;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.registers.VoxelModels;
import dev.iwilkey.terrafort.input.KeyBinding;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.state.State;

public final class SinglePlayer extends State {

	public SinglePlayer(final TerrafortEngine engine) {
		super(engine);
	}

	@Override
	public void registerKeyBindings(final KeyBinding stateKeyBindings) {
		stateKeyBindings.bind("action", Buttons.LEFT);
		stateKeyBindings.bind("forward", Keys.W);
	}

	@Override
	public void begin() {
		addGameObject(new GameObject3(this, VoxelModels.SPITTER_MK1).setPosition(10, 2, 10));
		addGameObject(new Player(this));
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
