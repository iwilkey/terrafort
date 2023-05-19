package dev.iwilkey.terrafort.state.game;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.gfx.WorldEnvironment;
import dev.iwilkey.terrafort.state.game.object.Core;
import dev.iwilkey.terrafort.state.game.object.Crosshair;
import dev.iwilkey.terrafort.state.game.object.decal.Hitpoint;

public class SinglePlayerEngineState extends State {
	
	private Player player;
	private WorldEnvironment env;
	// private SinglePlayerGameState game;
	// private BuildingHandler builder;
	private long crosshair;
	private long core;

	public SinglePlayerEngineState(TerrafortEngine engine) {
		super(engine);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public long getCore() {
		return core;
	}
	
	@Override
	public void begin() {
		env = new WorldEnvironment();
		environment3 = env;
		crosshair = addGameObject(new Crosshair(this).setShouldRender(false));
		player = new Player(this, crosshair);
		camera3.setController(player);
		core = addGameObject(new Core(this).setPosition(0, 0, 0));
		addGameObject(new Hitpoint(this));
	}

	@Override
	public void tick() {
		// game.tick();
		// builder.tick();
	}

	@Override
	public void gui() {
		// game.render();
	}

	@Override
	public void end() {}

}
